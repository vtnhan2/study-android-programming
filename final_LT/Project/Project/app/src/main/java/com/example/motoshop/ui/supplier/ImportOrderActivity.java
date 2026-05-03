package com.example.motoshop.ui.supplier;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.motoshop.R;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class ImportOrderActivity extends AppCompatActivity {
    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_order);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Nhập hàng");
        }
    }

    // Mở màn hình khác bằng Intent hoặc điều hướng trong app.
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
