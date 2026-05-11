package com.example.baitap5_5;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper db;
    ArrayList<String> studentList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        db = new DatabaseHelper(this);
        studentList = new ArrayList<>();

        loadData();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        listView.setAdapter(adapter);
    }

    private void loadData() {
        Cursor cursor = db.getAllStudents();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String mssv = cursor.getString(0);
                String name = cursor.getString(1);
                String faculty = cursor.getString(2);
                String department = cursor.getString(3);
                
                String info = "MSSV: " + mssv + "\nHọ tên: " + name + "\nKhoa: " + faculty + "\nBộ môn: " + department;
                studentList.add(info);
            }
        }
    }
}
