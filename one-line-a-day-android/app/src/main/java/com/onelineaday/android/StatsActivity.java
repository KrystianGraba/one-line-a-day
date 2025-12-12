package com.onelineaday.android;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        TextView tvTotalEntries = findViewById(R.id.tvTotalEntries);
        TextView tvCurrentStreak = findViewById(R.id.tvCurrentStreak);
        TextView tvLongestStreak = findViewById(R.id.tvLongestStreak);

        ApiClient.getService(this).getStats().enqueue(new Callback<ApiService.StatsSummary>() {
            @Override
            public void onResponse(Call<ApiService.StatsSummary> call, Response<ApiService.StatsSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.StatsSummary stats = response.body();
                    tvTotalEntries.setText(String.valueOf(stats.totalEntries));
                    tvCurrentStreak.setText(stats.currentStreak + " days");
                    tvLongestStreak.setText(stats.longestStreak + " days");
                }
            }

            @Override
            public void onFailure(Call<ApiService.StatsSummary> call, Throwable t) {
                Toast.makeText(StatsActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
