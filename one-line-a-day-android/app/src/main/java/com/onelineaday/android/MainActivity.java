package com.onelineaday.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TimelineAdapter adapter;
    private TextInputEditText etEntry;
    private LocalDate today = LocalDate.now();
    private JournalRepository repository; // Added missing field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new JournalRepository(this); // Initialize repository

        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText("Today, " + today.format(DateTimeFormatter.ofPattern("MMMM d")));

        etEntry = findViewById(R.id.etEntry);
        Button btnSave = findViewById(R.id.btnSave);
        RecyclerView rvTimeline = findViewById(R.id.rvTimeline);

        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimelineAdapter();
        rvTimeline.setAdapter(adapter);

        // Analytics: App Open
        AnalyticsManager.logEvent(this, "app_opened");

        // Fetch Timeline
        fetchTimeline();

        btnSave.setOnClickListener(v -> {
            String text = etEntry.getText().toString().trim();
            if (text.isEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(v, getString(R.string.empty_msg),
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (text.length() > 500) {
                com.google.android.material.snackbar.Snackbar.make(v, getString(R.string.long_msg),
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                return;
            }

            repository.saveEntry(today.toString(), text, new JournalRepository.DataCallback<ApiService.JournalEntry>() {
                @Override
                public void onSuccess(ApiService.JournalEntry data) {
                    com.google.android.material.snackbar.Snackbar.make(v, getString(R.string.saved_msg),
                            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                    AnalyticsManager.logEvent(MainActivity.this, "entry_saved");
                    fetchTimeline();
                }

                @Override
                public void onError(String message) {
                    com.google.android.material.snackbar.Snackbar.make(v, message,
                            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                    AnalyticsManager.logEvent(MainActivity.this, "entry_save_failed",
                            java.util.Collections.singletonMap("error", message));
                }
            });
        });

        // Navigation
        View btnPrev = findViewById(R.id.btnPrev);
        View btnNext = findViewById(R.id.btnNext);

        btnPrev.setOnClickListener(v -> updateDate(today.minusDays(1)));
        btnNext.setOnClickListener(v -> updateDate(today.plusDays(1)));

        tvDate.setOnClickListener(v -> showDatePicker());
    }

    private void updateDate(LocalDate newDate) {
        this.today = newDate;
        TextView tvDate = findViewById(R.id.tvDate);
        String pattern = getString(R.string.today_date_format);
        tvDate.setText(today.format(DateTimeFormatter.ofPattern(pattern, java.util.Locale.getDefault()))); // Use Locale

        // Clear input to prevent saving to wrong day accidentally
        etEntry.setText("");

        // Fetch new data
        fetchTimeline();
    }

    private void showDatePicker() {
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            updateDate(LocalDate.of(year, month + 1, dayOfMonth));
        }, today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()).show();
    }

    private void fetchTimeline() {
        repository.getTimeline(today.toString(), new JournalRepository.DataCallback<List<ApiService.JournalEntry>>() {
            @Override
            public void onSuccess(List<ApiService.JournalEntry> entries) {
                adapter.setEntries(entries);
                // Check if we have an entry for today to populate the EditText
                for (ApiService.JournalEntry entry : entries) {
                    if (entry.date.equals(today.toString())) {
                        etEntry.setText(entry.text);
                        break;
                    }
                }
            }

            @Override
            public void onError(String message) {
                com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.failed_load_msg) + " (" + message + ")",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    static class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
        // ... (unchanged adapter code)
        private List<ApiService.JournalEntry> entries = new java.util.ArrayList<>();

        public void setEntries(List<ApiService.JournalEntry> entries) {
            this.entries = entries;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ApiService.JournalEntry entry = entries.get(position);
            holder.text1.setText(entry.year + ": " + entry.text); // Show Year + Text
            holder.text2.setText(entry.date);
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView text1, text2;

            ViewHolder(android.view.View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }

    // --- Menu Handling ---
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            showSearchDialog();
            return true;
        } else if (id == R.id.action_stats) {
            startActivity(new Intent(this, StatsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            SecurePrefs.get(this).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearchDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Search memories...");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Search")
                .setView(input)
                .setPositiveButton("Search", (dialog, which) -> {
                    String query = input.getText().toString();
                    performSearch(query);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performSearch(String query) {
        repository.search(query, new JournalRepository.DataCallback<List<ApiService.JournalEntry>>() {
            @Override
            public void onSuccess(List<ApiService.JournalEntry> entries) {
                adapter.setEntries(entries);
                com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content),
                        "Found " + entries.size() + " matches",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content),
                        "Search failed: " + message,
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
