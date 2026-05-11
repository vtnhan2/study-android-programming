package com.example.baitap5_5;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    TextView tvEmpty;
    DatabaseHelper db;
    ArrayList<Student> studentList;
    StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        tvEmpty = findViewById(R.id.tvEmpty);
        
        // Setup empty view
        listView.setEmptyView(tvEmpty);

        db = new DatabaseHelper(this);
        studentList = new ArrayList<>();

        loadData();

        adapter = new StudentAdapter(this, studentList);
        listView.setAdapter(adapter);

        // Handle item click to send data back to MainActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student selectedStudent = studentList.get(position);
                
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra("mssv", String.valueOf(selectedStudent.getMssv()));
                intent.putExtra("name", selectedStudent.getName());
                intent.putExtra("faculty", selectedStudent.getFaculty());
                intent.putExtra("department", selectedStudent.getDepartment());
                
                // Set flags to clear top so we don't create multiple instances of MainActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        Cursor cursor = db.getAllStudents();
        if (cursor.getCount() == 0) {
            // Toast removed since we have EmptyView now
        } else {
            while (cursor.moveToNext()) {
                int mssv = cursor.getInt(0);
                String name = cursor.getString(1);
                String faculty = cursor.getString(2);
                String department = cursor.getString(3);
                
                Student student = new Student(mssv, name, faculty, department);
                studentList.add(student);
            }
        }
    }
}
