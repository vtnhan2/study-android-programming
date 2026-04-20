package com.example.learnandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnandroid.R;
import com.example.learnandroid.utils.HashUtils;

public class RegisterActivity extends AppCompatActivity {

    EditText etUser, etPass, etConfirmPass, etEmail;
    Button btnReg;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUser = findViewById(R.id.etRegUsername);
        etPass = findViewById(R.id.etRegPassword);
        etConfirmPass = findViewById(R.id.etRegConfirmPassword);
        etEmail = findViewById(R.id.etRegEmail);
        btnReg = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvGoToLogin);

        btnReg.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashPass = HashUtils.hashPassword(pass);
            
            // Save to SharedPreferences (mock DB)
            SharedPreferences userPrefs = getSharedPreferences("MyUsers", MODE_PRIVATE);
            SharedPreferences emailPrefs = getSharedPreferences("UserEmails", MODE_PRIVATE);
            
            SharedPreferences.Editor userEditor = userPrefs.edit();
            SharedPreferences.Editor emailEditor = emailPrefs.edit();
            
            userEditor.putString(user, hashPass);
            emailEditor.putString(user, email);
            
            if (userEditor.commit() && emailEditor.commit()) {
                Toast.makeText(this, "Registered: " + user, Toast.LENGTH_SHORT).show();
                finish(); 
            } else {
                Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}
