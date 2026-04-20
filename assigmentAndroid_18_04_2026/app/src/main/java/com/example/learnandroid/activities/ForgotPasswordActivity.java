package com.example.learnandroid.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnandroid.R;
import com.example.learnandroid.utils.HashUtils;

import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    LinearLayout layout1, layout2, layout3;
    EditText etEmail, etOTP, etNewPass, etConfirmNewPass;
    Button btnSend, btnVerify, btnReset;
    TextView tvBack;
    
    String targetUsername = null;
    final String DEFAULT_OTP = "22200114";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Find username by email
            targetUsername = findUsernameByEmail(email);
            if (targetUsername != null) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Email not found in our records", Toast.LENGTH_SHORT).show();
            }
        });

        btnVerify.setOnClickListener(v -> {
            String otp = etOTP.getText().toString().trim();
            if (otp.equals(DEFAULT_OTP)) {
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Invalid OTP code", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmNewPass.getText().toString().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Fill both password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update password
            String hashPass = HashUtils.hashPassword(newPass);
            SharedPreferences userPrefs = getSharedPreferences("MyUsers", MODE_PRIVATE);
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putString(targetUsername, hashPass);
            if (editor.commit()) {
                Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });

        tvBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        layout1 = findViewById(R.id.layoutPhase1);
        layout2 = findViewById(R.id.layoutPhase2);
        layout3 = findViewById(R.id.layoutPhase3);

        etEmail = findViewById(R.id.etForgotEmail);
        etOTP = findViewById(R.id.etOTP);
        etNewPass = findViewById(R.id.etNewPassword);
        etConfirmNewPass = findViewById(R.id.etConfirmNewPassword);

        btnSend = findViewById(R.id.btnSendOTP);
        btnVerify = findViewById(R.id.btnVerifyOTP);
        btnReset = findViewById(R.id.btnResetPassword);

        tvBack = findViewById(R.id.tvBackToLogin);
    }

    private String findUsernameByEmail(String email) {
        SharedPreferences emailPrefs = getSharedPreferences("UserEmails", MODE_PRIVATE);
        Map<String, ?> allEntries = emailPrefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (email.equals(entry.getValue().toString())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
