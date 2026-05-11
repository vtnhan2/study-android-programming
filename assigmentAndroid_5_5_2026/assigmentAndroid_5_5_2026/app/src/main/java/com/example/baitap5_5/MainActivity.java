package com.example.baitap5_5;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etMssv, etName, etFaculty, etDepartment;
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
                        Toast.makeText(MainActivity.this, "Thêm sinh viên thành công", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    } else {
                        Toast.makeText(MainActivity.this, "Thêm thất bại (MSSV đã tồn tại)", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    } else {
                        Toast.makeText(MainActivity.this, "Cập nhật thất bại (Không tìm thấy MSSV)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mssvStr = etMssv.getText().toString().trim();
                if (TextUtils.isEmpty(mssvStr)) {
                    etMssv.setError("Vui lòng nhập MSSV để xóa");
                    etMssv.requestFocus();
                    return;
                }

                // Show confirmation dialog before deleting
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa sinh viên mang MSSV " + mssvStr + " này không?")
                        .setPositiveButton("Có, xóa ngay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int mssv = Integer.parseInt(mssvStr);
                                boolean isDeleted = db.deleteStudent(mssv);
                                if (isDeleted) {
                                    Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    clearInputs();
                                } else {
                                    Toast.makeText(MainActivity.this, "Xóa thất bại (Không tìm thấy MSSV)", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        
        // Handle incoming intent if activity is already running
        handleIncomingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent != null && intent.hasExtra("mssv")) {
            etMssv.setText(intent.getStringExtra("mssv"));
            etName.setText(intent.getStringExtra("name"));
            etFaculty.setText(intent.getStringExtra("faculty"));
            etDepartment.setText(intent.getStringExtra("department"));
        }
    }

    private boolean validateInput() {
        boolean isValid = true;
        
        if (TextUtils.isEmpty(etMssv.getText().toString().trim())) {
            etMssv.setError("Không được để trống MSSV");
            isValid = false;
        }
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            etName.setError("Không được để trống Họ tên");
            isValid = false;
        }
        if (TextUtils.isEmpty(etFaculty.getText().toString().trim())) {
            etFaculty.setError("Không được để trống Khoa");
            isValid = false;
        }
        if (TextUtils.isEmpty(etDepartment.getText().toString().trim())) {
            etDepartment.setError("Không được để trống Bộ môn");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    private void clearInputs() {
        etMssv.setText("");
        etName.setText("");
        etFaculty.setText("");
        etDepartment.setText("");
        etMssv.requestFocus();
        etMssv.setError(null);
        etName.setError(null);
        etFaculty.setError(null);
        etDepartment.setError(null);
    }
}