package com.example.motoshop.ui.inventory;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.databinding.ActivityMotorcycleDetailBinding;
import com.example.motoshop.utils.AppConfig;
import com.example.motoshop.utils.CurrencyFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class MotorcycleDetailActivity extends AppCompatActivity {

    private ActivityMotorcycleDetailBinding binding;

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMotorcycleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        String docId = getIntent().getStringExtra("MOTORCYCLE_DOC_ID");
        if (docId != null) {
            loadMotorcycleDetail(docId);
        }

        binding.btnContact.setOnClickListener(v -> showContactOptions());
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        // Đổi màu nút back sang màu Primary để nhìn rõ trên nền trắng
        binding.toolbar.setNavigationIconTint(AppConfig.COLOR_PRIMARY);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Lấy dữ liệu cần thiết và đưa lên giao diện.
    private void loadMotorcycleDetail(String docId) {
        FirebaseFirestore.getInstance().collection("motorcycles").document(docId).get()
                .addOnSuccessListener(doc -> {
                    Motorcycle m = doc.toObject(Motorcycle.class);
                    if (m != null) {
                        displayData(m);
                    }
                });
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void displayData(Motorcycle m) {
        binding.tvMotorName.setText(String.format("%s %s", m.brand, m.model));
        binding.tvMotorPrice.setText(CurrencyFormatter.format(m.price));

        String desc = (m.longDescription != null && !m.longDescription.isEmpty()) ? m.longDescription : m.description;
        binding.tvDescription.setText(desc != null ? desc : "Đang cập nhật...");

        // Hiển thị trạng thái xe bằng chip
        if (m.quantity > 0) {
            binding.chipStatus.setText("Còn hàng");
            binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.status_green_bg)));
            binding.chipStatus.setTextColor(ContextCompat.getColor(this, R.color.status_green_text));
        } else {
            binding.chipStatus.setText("Hết hàng");
            binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.status_red_bg)));
            binding.chipStatus.setTextColor(ContextCompat.getColor(this, R.color.status_red_text));
        }
        // Hiển thị hình ảnh xe
        int resId = getMotorcycleImageResId(m.imageUri);
        if (resId != 0) {
            Glide.with(this).load(resId).placeholder(R.drawable.ic_inventory).into(binding.ivMotorMain);
        } else {
            Glide.with(this).load(R.drawable.ic_inventory).into(binding.ivMotorMain);
        }

        // Hiển thị thông số kỹ thuật của xe
        binding.layoutSpecs.removeAllViews();
        if (m.technicalSpecs != null && !m.technicalSpecs.isEmpty()) {
            for (Map.Entry<String, String> entry : m.technicalSpecs.entrySet()) {
                addSpecRow(entry.getKey(), entry.getValue());
            }
        } else {
            addSpecRow("Thương hiệu", m.brand);
            addSpecRow("Đời xe", String.valueOf(m.year));
            addSpecRow("Màu sắc", m.color);
        }
    }

    // Xử lý hình ảnh cần hiển thị trên màn hình.
    private int getMotorcycleImageResId(String imageUri) {
        if (imageUri == null || imageUri.trim().isEmpty()) return 0;

        String exactName = imageUri.trim();
        int resId = getResources().getIdentifier(exactName, "drawable", getPackageName());
        if (resId != 0) return resId;

        String safeName = exactName.toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return getResources().getIdentifier(safeName, "drawable", getPackageName());
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private void addSpecRow(String label, String value) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_spec_row, binding.layoutSpecs, false);
        TextView tvLabel = row.findViewById(R.id.tvLabel);
        TextView tvValue = row.findViewById(R.id.tvValue);
        tvLabel.setText(label);
        tvValue.setText(value);
        binding.layoutSpecs.addView(row);
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showContactOptions() {
        String[] options = {"Gọi Hotline: " + AppConfig.HOTLINE, "Xem website cửa hàng"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Liên hệ tư vấn")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        // Chuẩn hóa số điện thoại bỏ khoảng trắng để intent dial đọc đúng
                        String cleanPhone = AppConfig.HOTLINE.replace(" ", "");
                        callIntent.setData(Uri.parse("tel:" + cleanPhone));
                        startActivity(callIntent);
                    } else {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW);
                        webIntent.setData(Uri.parse(AppConfig.WEBSITE_URL));
                        startActivity(webIntent);
                    }
                })
                .show();
    }
}
