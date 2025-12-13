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

    public void saveEntry(String date, String text, DataCallback<ApiService.JournalEntry> callback) {
        ApiService.EntryRequest request = new ApiService.EntryRequest(date, text);
        apiService.saveEntry(request).enqueue(new Callback<ApiService.JournalEntry>() {
            @Override
            public void onResponse(Call<ApiService.JournalEntry> call, Response<ApiService.JournalEntry> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.JournalEntry entry = response.body();
                    callback.onSuccess(entry);

                    // Save to local DB
                    executor.execute(() -> {
                        JournalEntryEntity entity = new JournalEntryEntity(entry.date, entry.text, entry.id,
                                entry.year);
                        db.journalDao().insert(entity);
                    });
                } else {
                    callback.onError("Failed to save entry: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiService.JournalEntry> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getTimeline(String date, DataCallback<List<ApiService.JournalEntry>> callback) {
        // Strategy: Network First -> Fallback to Local + Prefetch Neighbors
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
                        db.journalDao().insertAll(entities);
                    });

                    // PREFETCH NEXT/PREV DAYS silently
                    prefetchNeighbors(date);

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

    private void prefetchNeighbors(String currentDate) {
        // currentDate is YYYY-MM-DD
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.time.LocalDate current = java.time.LocalDate.parse(currentDate);
                prefetchDate(current.minusDays(1).toString());
                prefetchDate(current.plusDays(1).toString());
            }
        } catch (Exception e) {
            // Ignore prefetch errors
        }
    }

    private void prefetchDate(String date) {
        apiService.getTimeline(date).enqueue(new Callback<List<ApiService.JournalEntry>>() {
            @Override
            public void onResponse(Call<List<ApiService.JournalEntry>> call,
                    Response<List<ApiService.JournalEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        List<JournalEntryEntity> entities = new ArrayList<>();
                        for (ApiService.JournalEntry e : response.body()) {
                            entities.add(new JournalEntryEntity(e.date, e.text, e.id, e.year));
                        }
                        db.journalDao().insertAll(entities);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.JournalEntry>> call, Throwable t) {
            }
        });
    }

    private void loadFromDb(String date, DataCallback<List<ApiService.JournalEntry>> callback) {
        // Optimised: Use SQLite LIKE query to filter by "-MM-DD"
        executor.execute(() -> {
            // date is YYYY-MM-DD. We want pattern %-MM-DD.
            String targetMonthDay = date.substring(4); // -12-12
            String pattern = "%" + targetMonthDay;

            List<JournalEntryEntity> matches = db.journalDao().getEntriesByPattern(pattern);

            List<ApiService.JournalEntry> dtos = new ArrayList<>();
            for (JournalEntryEntity entity : matches) {
                ApiService.JournalEntry dto = new ApiService.JournalEntry();
                dto.date = entity.date;
                dto.text = entity.text;
                dto.id = entity.id;
                dto.year = entity.year;
                dtos.add(dto);
            }

            mainHandler.post(() -> {
                if (!dtos.isEmpty()) {
                    callback.onSuccess(dtos);
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
