package com.example.learnandroid.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.learnandroid.R;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class FormActivity extends AppCompatActivity {

    ImageButton btnBack;
    Button btnSave, btnCancel;
    EditText etName, etPhone, etEmail, etWork, etGroups, etAddress;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Get username to identify the profile
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) currentUsername = "default_user";

        initViews();
        loadProfileData();

        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            saveProfileData();
            Toast.makeText(this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etWork = findViewById(R.id.etWork);
        etGroups = findViewById(R.id.etGroups);
        etAddress = findViewById(R.id.etAddress);
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("Profile_" + currentUsername, MODE_PRIVATE);
        etName.setText(prefs.getString("name", ""));
        etPhone.setText(prefs.getString("phone", ""));
        etEmail.setText(prefs.getString("email", ""));
        etWork.setText(prefs.getString("work", ""));
        etGroups.setText(prefs.getString("groups", ""));
        etAddress.setText(prefs.getString("address", ""));
    }

    private void saveProfileData() {
        SharedPreferences prefs = getSharedPreferences("Profile_" + currentUsername, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", etName.getText().toString().trim());
        editor.putString("phone", etPhone.getText().toString().trim());
        editor.putString("email", etEmail.getText().toString().trim());
        editor.putString("work", etWork.getText().toString().trim());
        editor.putString("groups", etGroups.getText().toString().trim());
        editor.putString("address", etAddress.getText().toString().trim());
        editor.apply();
    }
}
