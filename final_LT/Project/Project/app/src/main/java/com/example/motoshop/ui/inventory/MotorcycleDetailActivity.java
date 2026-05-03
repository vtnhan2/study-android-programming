package com.example.motoshop.ui.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.utils.AppConfig;
import com.example.motoshop.utils.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

// Màn hình hiển thị chi tiết xe theo giao diện Bike App template.
public class MotorcycleDetailActivity extends AppCompatActivity {

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcycle_detail);

        setupToolbar();

        String docId = getIntent().getStringExtra("MOTORCYCLE_DOC_ID");
        if (docId != null) {
            loadMotorcycleDetail(docId);
        }

        // Nút Pre-book
        MaterialButton btnPrebook = findViewById(R.id.btnPrebook);
        btnPrebook.setOnClickListener(v -> showContactOptions());
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupToolbar() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageView btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Xem thử chiếc xe này trên MotoShop nhé!");
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
        });
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

    // Hiển thị dữ liệu xe lên giao diện.
    private void displayData(Motorcycle m) {
        TextView tvMotorName = findViewById(R.id.tvMotorName);
        TextView tvMotorBrand = findViewById(R.id.tvMotorBrand);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvVariants = findViewById(R.id.tvVariants);
        TextView tvDescription = findViewById(R.id.tvDescription);
        ImageView ivMotorMain = findViewById(R.id.ivMotorMain);
        ImageView btnFavorite = findViewById(R.id.btnFavorite);

        // Tên xe (model)
        tvMotorName.setText(m.model != null ? m.model : "Không xác định");
        // Hãng xe
        tvMotorBrand.setText("Hãng: " + (m.brand != null ? m.brand : "Không xác định"));
        // Giá
        tvPrice.setText(CurrencyFormatter.format(m.price));
        // Phiên bản (dùng quantity)
        tvVariants.setText(m.quantity + " Phiên bản");

        // Mô tả
        String desc = (m.longDescription != null && !m.longDescription.isEmpty()) ? m.longDescription : m.description;
        if (desc == null || desc.isEmpty()) {
            desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
        }
        tvDescription.setText(desc);

        // Hình ảnh xe
        int resId = getMotorcycleImageResId(m.imageUri);
        if (resId != 0) {
            Glide.with(this).load(resId).into(ivMotorMain);
        } else {
            ivMotorMain.setImageResource(R.drawable.ic_inventory);
        }

        // Toggle favorite
        com.example.motoshop.utils.UserSession session = new com.example.motoshop.utils.UserSession(this);
        String customerId = session.getCustomerDocId();
        final boolean[] isFavorite = {false};

        if (customerId != null) {
            FirebaseFirestore.getInstance().collection("customers").document(customerId).get()
                    .addOnSuccessListener(doc -> {
                        java.util.List<String> favorites = (java.util.List<String>) doc.get("favoriteBikeIds");
                        if (favorites != null && favorites.contains(m.documentId)) {
                            isFavorite[0] = true;
                            btnFavorite.setColorFilter(getResources().getColor(R.color.notification_red));
                        }
                    });
        }

        btnFavorite.setOnClickListener(v -> {
            if (customerId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để yêu thích", Toast.LENGTH_SHORT).show();
                return;
            }
            isFavorite[0] = !isFavorite[0];
            if (isFavorite[0]) {
                btnFavorite.setColorFilter(getResources().getColor(R.color.notification_red));
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                btnFavorite.setColorFilter(getResources().getColor(R.color.heart_green));
                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            }

            // Update Firestore
            FirebaseFirestore.getInstance().collection("customers").document(customerId).get()
                    .addOnSuccessListener(doc -> {
                        java.util.List<String> favorites = (java.util.List<String>) doc.get("favoriteBikeIds");
                        if (favorites == null) favorites = new java.util.ArrayList<>();
                        
                        if (isFavorite[0]) {
                            if (!favorites.contains(m.documentId)) favorites.add(m.documentId);
                        } else {
                            favorites.remove(m.documentId);
                        }
                        FirebaseFirestore.getInstance().collection("customers").document(customerId)
                                .update("favoriteBikeIds", favorites);
                    });
        });
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

    // Hiển thị hộp thoại liên hệ.
    private void showContactOptions() {
        String[] options = {"Gọi Hotline: " + AppConfig.HOTLINE, "Xem website cửa hàng"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Liên hệ tư vấn")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
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
