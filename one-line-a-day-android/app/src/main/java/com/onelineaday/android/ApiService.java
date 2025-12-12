package com.onelineaday.android;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @PUT("journal/entries")
    Call<JournalEntry> saveEntry(@Body EntryRequest request);

    @GET("journal/timeline")
    Call<List<JournalEntry>> getTimeline(@Query("date") String date);

    @GET("journal/search")
    Call<List<JournalEntry>> search(@Query("query") String query);

    @GET("journal/stats")
    Call<StatsSummary> getStats();

    @POST("analytics")
    Call<Void> trackEvent(@Body AnalyticsEventRequest request);

    // DTOs
    class LoginRequest {
        final String email;
        final String password;

        LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class RegisterRequest {
        final String email;
        final String password;
        final String displayName;

        RegisterRequest(String email, String password, String displayName) {
            this.email = email;
            this.password = password;
            this.displayName = displayName;
        }
    }

    class EntryRequest {
        final String date;
        final String text;

        EntryRequest(String date, String text) {
            this.date = date;
            this.text = text;
        }
    }

    class AnalyticsEventRequest {
        final String event;
        final java.util.Map<String, Object> metadata;

        AnalyticsEventRequest(String event, java.util.Map<String, Object> metadata) {
            this.event = event;
            this.metadata = metadata;
        }
    }

    class AuthResponse {
        String token;
    }

    class JournalEntry {
        String id;
        String date;
        String text;
        int year;
    }

    class StatsSummary {
        int totalEntries;
        long totalWords;
        int currentStreak;
        int longestStreak;
    }
}
