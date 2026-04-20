package com.example.custom_layout_21_04_26;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SubActivity extends AppCompatActivity {
    TextView txt_subphone;
    TextView txt_subprice;
    android.widget.ImageView img_subphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        // Ánh xạ các view
        txt_subphone = findViewById(R.id.txt_subphone);
        txt_subprice = findViewById(R.id.txt_subprice);
        img_subphone = findViewById(R.id.img_subphone);

        // Nhận dữ liệu từ Intent
        Intent myintent = getIntent();
        String namephone = myintent.getStringExtra("name");
        String price = myintent.getStringExtra("price");
        int imageRes = myintent.getIntExtra("image", 0);

        // Hiển thị dữ liệu
        txt_subphone.setText(namephone);
        txt_subprice.setText(price);
        if (imageRes != 0) {
            img_subphone.setImageResource(imageRes);
        }
    }
}
