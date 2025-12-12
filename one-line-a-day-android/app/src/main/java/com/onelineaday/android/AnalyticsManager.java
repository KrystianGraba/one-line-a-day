package com.onelineaday.android;

import android.content.Context;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Collections;
import java.util.Map;

public class AnalyticsManager {
    private static final String TAG = "AnalyticsManager";

    public static void logEvent(Context context, String event) {
        logEvent(context, event, Collections.emptyMap());
    }

    public static void logEvent(Context context, String event, Map<String, Object> metadata) {
        Log.d(TAG, "Logging event: " + event);
        ApiClient.getService(context).trackEvent(new ApiService.AnalyticsEventRequest(event, metadata))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Log.w(TAG, "Failed to send analytics: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Network error sending analytics: " + t.getMessage());
                    }
                });
    }
}
