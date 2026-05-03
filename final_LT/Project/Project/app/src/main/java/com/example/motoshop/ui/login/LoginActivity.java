package com.example.motoshop.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.motoshop.databinding.ActivityLoginBinding;
import com.example.motoshop.ui.main.MainActivity;
import com.example.motoshop.utils.FirebaseSeeder;
import com.example.motoshop.utils.UserSession;
import com.google.firebase.firestore.FirebaseFirestore;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserSession session;
    private FirebaseFirestore db;

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new UserSession(this);
        db = FirebaseFirestore.getInstance();

        if (session.isLoggedIn()) {
            navigateToMain();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> performLogin());

        // Tài khoản mẫu để đăng nhập nhanh
        binding.chipAdmin.setOnClickListener(v -> setDemoAccount("AD01", "1111"));
        binding.chipSales.setOnClickListener(v -> setDemoAccount("SL01", "2222"));
        binding.chipTech.setOnClickListener(v -> setDemoAccount("KT01", "3333"));

        // Chuyển sang đăng nhập khách hàng
        binding.chipUser.setOnClickListener(v -> switchToCustomerMode());

        // Kiểm tra Firestore — nếu chưa có dữ liệu thì hiện nút khởi tạo
        checkAndShowSeedButton();
    }

    // Kiểm tra Firestore — nếu collection staff trống thì hiện nút seed.
    private void checkAndShowSeedButton() {
        db.collection("staff").limit(1).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        binding.btnSeedData.setVisibility(View.VISIBLE);
                        binding.btnSeedData.setOnClickListener(v -> {
                            binding.btnSeedData.setEnabled(false);
                            binding.btnSeedData.setText("Đang khởi tạo...");
                            FirebaseSeeder.uploadSeedData(this);
                            // Sau 3 giây ẩn nút đi (dữ liệu đã được ghi)
                            binding.btnSeedData.postDelayed(() -> {
                                binding.btnSeedData.setVisibility(View.GONE);
                                Toast.makeText(this, "Khởi tạo xong! Bạn có thể đăng nhập.", Toast.LENGTH_LONG).show();
                            }, 3000);
                        });
                    }
                });
    }

    // Gán giá trị mới cho dữ liệu trong object.
    private void setDemoAccount(String id, String pass) {
        binding.etUserId.setText(id);
        binding.etPassword.setText(pass);
        binding.tilUserId.setHint("Mã nhân viên / Số điện thoại");
    }

    // Chuyển form đăng nhập sang chế độ khách hàng.
    private void switchToCustomerMode() {
        binding.etUserId.setText("");
        binding.etPassword.setText("123");
        binding.tilUserId.setHint("Số điện thoại khách hàng");
        binding.etUserId.requestFocus();
    }

    // Kiểm tra tài khoản và chuyển vào màn hình chính nếu đăng nhập đúng.
    private void performLogin() {
        String id = binding.etUserId.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();

        if (id.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ưu tiên kiểm tra Staff trước
        db.collection("staff")
                .whereEqualTo("staffId", id)
                .whereEqualTo("password", pass)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        String name = doc.getString("name");
                        String role = doc.getString("role");
                        session.createLoginSession(id, name, role);
                        navigateToMain();
                    } else {
                        // Nếu không phải staff, kiểm tra xem có phải khách hàng đăng nhập bằng pass mặc định không
                        if (pass.equals("123")) {
                            checkCustomerLogin(id);
                        } else {
                            Toast.makeText(this, "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Kiểm tra dữ liệu trước khi tiếp tục xử lý.
    private void checkCustomerLogin(String phone) {
        db.collection("customers")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String docId = doc.getId();
                        String name = doc.getString("name");
                        session.createCustomerSession(docId, name, phone);
                        navigateToMain();
                    } else {
                        Toast.makeText(this, "Không tìm thấy khách hàng với số điện thoại này", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Mở màn hình khác bằng Intent hoặc điều hướng trong app.
    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
