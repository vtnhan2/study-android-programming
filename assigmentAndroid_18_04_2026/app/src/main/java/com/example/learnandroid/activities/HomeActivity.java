package com.example.learnandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.learnandroid.R;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    CardView cardProfile;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        cardProfile = findViewById(R.id.cardProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Get username from Intent
        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("Welcome back, " + username + "!");
        }

        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FormActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
