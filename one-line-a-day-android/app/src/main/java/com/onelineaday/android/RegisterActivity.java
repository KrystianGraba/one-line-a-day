package com.onelineaday.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextInputEditText etDisplayName = findViewById(R.id.etDisplayName);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String displayName = etDisplayName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Snackbar.make(v, getString(R.string.fill_all_fields), Snackbar.LENGTH_SHORT).show();
                return;
            }

            AnalyticsManager.logEvent(this, "register_attempt");

            ApiClient.getService(this).register(new ApiService.RegisterRequest(email, password, displayName))
                    .enqueue(new Callback<ApiService.AuthResponse>() {
                        @Override
                        public void onResponse(Call<ApiService.AuthResponse> call,
                                Response<ApiService.AuthResponse> response) {
                            if (response.isSuccessful()) {
                                AnalyticsManager.logEvent(RegisterActivity.this, "register_success");
                                Snackbar.make(v, getString(R.string.register_success), Snackbar.LENGTH_SHORT).show();

                                // Optional: Auto-login or redirect to login. Logic suggests redirect to login
                                // per strings/previous flow.
                                // But response contains token usually? If so we could auto login.
                                // For now, let's finish so they can login (or if backend returns token - we
                                // could use it).
                                // Backend AuthController.register CURRENTLY returns AuthResponse which has
                                // Token.
                                // So we can auto-login!

                                if (response.body() != null && response.body().token != null) {
                                    SecurePrefs.get(RegisterActivity.this).edit()
                                            .putString("token", response.body().token).apply();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finishAffinity(); // Close Auth stack
                                } else {
                                    finish(); // Fallback to login screen
                                }

                            } else {
                                AnalyticsManager.logEvent(RegisterActivity.this, "register_failed");
                                Snackbar.make(v, getString(R.string.register_failed), Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                            AnalyticsManager.logEvent(RegisterActivity.this, "register_error",
                                    java.util.Collections.singletonMap("error", t.getMessage()));
                            Snackbar.make(v, getString(R.string.network_error_msg, t.getMessage()),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
