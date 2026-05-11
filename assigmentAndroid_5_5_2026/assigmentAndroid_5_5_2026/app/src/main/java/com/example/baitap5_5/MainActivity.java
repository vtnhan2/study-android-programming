package com.example.baitap5_5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etMssv, etName, etFaculty, etDepartment;
    Button btnCreate, btnUpdate, btnDelete, btnQuery;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMssv = findViewById(R.id.etMssv);
        etName = findViewById(R.id.etName);
        etFaculty = findViewById(R.id.etFaculty);
        etDepartment = findViewById(R.id.etDepartment);

        btnCreate = findViewById(R.id.btnCreate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnQuery = findViewById(R.id.btnQuery);

        db = new DatabaseHelper(this);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    int mssv = Integer.parseInt(etMssv.getText().toString().trim());
                    String name = etName.getText().toString().trim();
                    String faculty = etFaculty.getText().toString().trim();
                    String department = etDepartment.getText().toString().trim();

                    boolean isInserted = db.insertStudent(mssv, name, faculty, department);
                    if (isInserted) {
                        Toast.makeText(MainActivity.this, "Tạo thành công", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    } else {
                        Toast.makeText(MainActivity.this, "Tạo thất bại (Có thể MSSV đã tồn tại)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    int mssv = Integer.parseInt(etMssv.getText().toString().trim());
                    String name = etName.getText().toString().trim();
                    String faculty = etFaculty.getText().toString().trim();
                    String department = etDepartment.getText().toString().trim();

                    boolean isUpdated = db.updateStudent(mssv, name, faculty, department);
                    if (isUpdated) {
                        Toast.makeText(MainActivity.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    } else {
                        Toast.makeText(MainActivity.this, "Sửa thất bại (Không tìm thấy MSSV)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mssvStr = etMssv.getText().toString().trim();
                if (TextUtils.isEmpty(mssvStr)) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập MSSV để xóa", Toast.LENGTH_SHORT).show();
                    return;
                }
                int mssv = Integer.parseInt(mssvStr);
                boolean isDeleted = db.deleteStudent(mssv);
                if (isDeleted) {
                    Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    clearInputs();
                } else {
                    Toast.makeText(MainActivity.this, "Xóa thất bại (Không tìm thấy MSSV)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(etMssv.getText().toString().trim()) ||
            TextUtils.isEmpty(etName.getText().toString().trim()) ||
            TextUtils.isEmpty(etFaculty.getText().toString().trim()) ||
            TextUtils.isEmpty(etDepartment.getText().toString().trim())) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearInputs() {
        etMssv.setText("");
        etName.setText("");
        etFaculty.setText("");
        etDepartment.setText("");
    }
}