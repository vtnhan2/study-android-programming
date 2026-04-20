package com.example.learnandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnandroid.R;
import com.example.learnandroid.utils.HashUtils;

public class MainActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin;
    TextView tvReg, tvForgotPassword;
    CheckBox cbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvReg = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        cbRemember = findViewById(R.id.cbRemember);

        // Load Remember Me state
        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = loginPrefs.getBoolean("rememberMe", false);
        if (isRemembered) {
            etUser.setText(loginPrefs.getString("username", ""));
            cbRemember.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences userPrefs = getSharedPreferences("MyUsers", MODE_PRIVATE);
            String savedHash = userPrefs.getString(user, null);

            if (savedHash == null) {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            } else {
                String inputHash = HashUtils.hashPassword(pass);
                if (savedHash.equals(inputHash)) {
                    // Handle Remember Me
                    SharedPreferences.Editor editor = loginPrefs.edit();
                    if (cbRemember.isChecked()) {
                        editor.putBoolean("rememberMe", true);
                        editor.putString("username", user);
                    } else {
                        editor.clear();
                    }
                    editor.apply();

                    Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("username", user);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvReg.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}
