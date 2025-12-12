package com.onelineaday.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Room;

import com.onelineaday.android.db.AppDatabase;
import com.onelineaday.android.db.JournalEntryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalRepository {
    private final ApiService apiService;
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public JournalRepository(Context context) {
        this.apiService = ApiClient.getService(context);
        this.db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "journal-db").build();
    }

    public interface DataCallback<T> {
        void onSuccess(T data);

        void onError(String message);
    }

    public void getTimeline(String date, DataCallback<List<ApiService.JournalEntry>> callback) {
        // Strategy: Network First -> Fallback to Local
        apiService.getTimeline(date).enqueue(new Callback<List<ApiService.JournalEntry>>() {
            @Override
            public void onResponse(Call<List<ApiService.JournalEntry>> call,
                    Response<List<ApiService.JournalEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ApiService.JournalEntry> entries = response.body();
                    callback.onSuccess(entries);

                    // Save to DB in background
                    executor.execute(() -> {
                        List<JournalEntryEntity> entities = new ArrayList<>();
                        for (ApiService.JournalEntry e : entries) {
                            entities.add(new JournalEntryEntity(e.date, e.text, e.id, e.year));
                        }
                        // For simplicity, we might want to clear entries for this date/year or just
                        // insert.
                        // Since 'date' is PK in our simplified Entity (wait, JournalEntryEntity has
                        // date as PK?
                        // That means 1 entry per day per user? Yes. But Timeline returns entries for
                        // "Same Day, Different Years").
                        // AH! JournalEntryEntity PK should probably be ID, not Date, if we store
                        // multiple years.
                        // Let's check JournalEntryEntity.
                        // It defined @PrimaryKey public String date;
                        // ERROR: If timeline returns 2023-12-12 and 2022-12-12, they have DIFFERENT
                        // dates. So Date as PK is fine.
                        // Wait, PK is unique constraint. date="2023-12-12" != "2022-12-12". So it
                        // works.

                        db.journalDao().insertAll(entities);
                    });
                } else {
                    loadFromDb(date, callback);
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.JournalEntry>> call, Throwable t) {
                loadFromDb(date, callback);
            }
        });
    }

    private void loadFromDb(String date, DataCallback<List<ApiService.JournalEntry>> callback) {
        // We want entries for this day/month across all years.
        // Backend filters by "Month-Day".
        // Our DAO 'getEntryByDate' is exact match.
        // We need a DAO method to Get By Month/Day pattern or just getAll and filter in
        // Java (easier for now).
        // Or update DAO to use SQLite strftime (but Room supports it?).
        // Let's just fetch all and filter in Java for simplicity in this artifact.

        executor.execute(() -> {
            List<JournalEntryEntity> all = db.journalDao().getAllEntries();
            List<ApiService.JournalEntry> filtered = new ArrayList<>();
            // Target format YYYY-MM-DD. We want to match -MM-DD.
            // date input is YYYY-MM-DD.
            String targetMonthDay = date.substring(4); // -12-12

            for (JournalEntryEntity entity : all) {
                if (entity.date != null && entity.date.endsWith(targetMonthDay)) {
                    ApiService.JournalEntry dto = new ApiService.JournalEntry();
                    dto.date = entity.date;
                    dto.text = entity.text;
                    dto.id = entity.id;
                    dto.year = entity.year; // We stored year
                    filtered.add(dto);
                }
            }

            mainHandler.post(() -> {
                if (!filtered.isEmpty()) {
                    callback.onSuccess(filtered);
                } else {
                    callback.onError("No offline data");
                }
            });
        });
    }

    public void search(String query, DataCallback<List<ApiService.JournalEntry>> callback) {
        apiService.search(query).enqueue(new Callback<List<ApiService.JournalEntry>>() {
            @Override
            public void onResponse(Call<List<ApiService.JournalEntry>> call,
                    Response<List<ApiService.JournalEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    searchLocal(query, callback);
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.JournalEntry>> call, Throwable t) {
                searchLocal(query, callback);
            }
        });
    }

    private void searchLocal(String query, DataCallback<List<ApiService.JournalEntry>> callback) {
        executor.execute(() -> {
            List<JournalEntryEntity> all = db.journalDao().getAllEntries();
            List<ApiService.JournalEntry> matches = new ArrayList<>();
            String lowerQuery = query.toLowerCase();

            for (JournalEntryEntity entity : all) {
                if (entity.text != null && entity.text.toLowerCase().contains(lowerQuery)) {
                    ApiService.JournalEntry dto = new ApiService.JournalEntry();
                    dto.date = entity.date;
                    dto.text = entity.text;
                    dto.id = entity.id;
                    dto.year = entity.year;
                    matches.add(dto);
                }
            }

            mainHandler.post(() -> {
                if (!matches.isEmpty()) {
                    callback.onSuccess(matches);
                } else {
                    callback.onError("No matches found locally");
                }
            });
        });
    }
}
