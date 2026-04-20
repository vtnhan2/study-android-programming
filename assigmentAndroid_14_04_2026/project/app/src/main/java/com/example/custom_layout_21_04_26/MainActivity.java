package com.example.custom_layout_21_04_26;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Mảng tên điện thoại
    String namephone[] = {
            "Điện thoại iPhone 13",
            "Điện thoại Samsung",
            "Điện thoại HTC Desire",
            "Điện thoại LG V30",
            "Điện thoại Oppo A73",
            "Điện thoại Xiaomi 15"
    };

    // Mảng ảnh điện thoại (tham chiếu drawable)
    int imagephone[] = {
            R.drawable.iphone, R.drawable.samsung, R.drawable.htc_desire,
            R.drawable.lg, R.drawable.oppo, R.drawable.xiaomi
    };

    // Mảng giá bán (THÊM MỚI cho bài tập)
    String pricephone[] = {
            "25.000.000 VNĐ",
            "18.000.000 VNĐ",
            "5.500.000 VNĐ",
            "6.990.000 VNĐ",
            "8.000.000 VNĐ",
            "3.290.000 VNĐ"
    };

    ArrayList<Phone> mylist;       // Danh sách đối tượng Phone
    MyArrayAdapter myadapter;      // Custom Adapter
    ListView lv;                   // ListView hiển thị

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ ListView từ layout
        lv = findViewById(R.id.lv);

        // Khởi tạo danh sách và thêm dữ liệu
        mylist = new ArrayList<>();
        for (int i = 0; i < namephone.length; i++) {
            mylist.add(new Phone(namephone[i], imagephone[i], pricephone[i]));
        }

        // Khởi tạo Adapter với custom layout
        myadapter = new MyArrayAdapter(this, R.layout.layout_listview, mylist);
        lv.setAdapter(myadapter);

        // Sự kiện click vào item trong ListView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Tạo Intent để mở SubActivity
                Intent myintent = new Intent(MainActivity.this, SubActivity.class);
                // Truyền tên điện thoại
                myintent.putExtra("name", namephone[position]);
                // Truyền giá bán (THÊM MỚI)
                myintent.putExtra("price", pricephone[position]);
                // Truyền hình ảnh
                myintent.putExtra("image", imagephone[position]);
                startActivity(myintent);
            }
        });
    }
}