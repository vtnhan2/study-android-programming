package com.example.motoshop.ui.repair;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.motoshop.R;
import com.example.motoshop.data.model.RepairOrder;
import com.example.motoshop.databinding.ActivityRepairDetailBinding;
import com.example.motoshop.databinding.ItemSpecRowBinding;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class RepairDetailActivity extends AppCompatActivity {

    private ActivityRepairDetailBinding binding;
    private RepairViewModel repairViewModel;
    private UserSession session;
    private RepairOrder currentOrder;

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRepairDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);
        session = new UserSession(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        String docId = getIntent().getStringExtra("REPAIR_DOC_ID");
        if (docId != null) {
            loadRepairDetail(docId);
        }

        binding.btnCompleteRepair.setOnClickListener(v -> completeOrder());
    }

    // Lấy dữ liệu cần thiết và đưa lên giao diện.
    private void loadRepairDetail(String docId) {
        FirebaseFirestore.getInstance().collection("repair_orders").document(docId).get()
                .addOnSuccessListener(doc -> {
                    currentOrder = doc.toObject(RepairOrder.class);
                    if (currentOrder != null) {
                        currentOrder.documentId = doc.getId();
                        displayData(currentOrder);
                        checkTechnicianPermission();
                    }
                });
    }

    // Kiểm tra dữ liệu trước khi tiếp tục xử lý.
    private void checkTechnicianPermission() {
        String role = session.getUserRole();
        String status = currentOrder.status != null ? currentOrder.status : "RECEIVED";

        // Chỉ hiện nút Hoàn thành nếu là TECHNICIAN và trạng thái là IN_PROGRESS
        if (UserSession.ROLE_TECHNICIAN.equals(role) && "IN_PROGRESS".equals(status)) {
            binding.btnCompleteRepair.setVisibility(View.VISIBLE);
        } else {
            binding.btnCompleteRepair.setVisibility(View.GONE);
        }
    }

    // Cập nhật phiếu sửa chữa sang trạng thái hoàn thành.
    private void completeOrder() {
        if (currentOrder == null) return;

        currentOrder.status = "DONE";
        currentOrder.completedDate = System.currentTimeMillis();
        currentOrder.technicianName = session.getUserName();

        FirebaseFirestore.getInstance().collection("repair_orders")
                .document(currentOrder.documentId)
                .set(currentOrder)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã hoàn thành sửa chữa", Toast.LENGTH_SHORT).show();
                    displayData(currentOrder);
                    checkTechnicianPermission();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void displayData(RepairOrder r) {
        binding.tvRepairCode.setText(r.repairCode);
        binding.tvDate.setText(String.format("Ngày lập: %s", new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(r.receivedDate))));

        applyStatusStyle(r.status != null ? r.status : "RECEIVED");

        String brand = r.motorcycleBrand != null ? r.motorcycleBrand : "";
        String model = r.motorcycleModel != null ? r.motorcycleModel : "";
        String vehicleInfo = (brand + " " + model).trim();

        setupRow(binding.rowCustomer, "Khách hàng", r.customerName != null ? r.customerName : "---");
        setupRow(binding.rowVehicle, "Xe", vehicleInfo.isEmpty() ? "---" : vehicleInfo);
        setupRow(binding.rowPlate, "Biển số", r.licensePlate != null && !r.licensePlate.isEmpty() ? r.licensePlate : "---");

        binding.tvIssue.setText(nonEmpty(r.issueDescription));
        binding.tvDiagnosis.setText(nonEmpty(r.diagnosis));

        setupRow(binding.rowLabor, "Tiền công", CurrencyFormatter.format(r.laborCost));
        setupRow(binding.rowParts, "Tiền phụ tùng", CurrencyFormatter.format(r.partsCost));
        binding.tvTotalCost.setText(CurrencyFormatter.format(r.totalCost));
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupRow(ItemSpecRowBinding itemBinding, String label, String value) {
        itemBinding.tvLabel.setText(label);
        itemBinding.tvValue.setText(value);
    }

    // Trả về dấu gạch nếu dữ liệu bị trống.
    private String nonEmpty(String val) {
        return (val == null || val.isEmpty()) ? "---" : val;
    }

    // Đổi màu trạng thái của phiếu sửa chữa.
    private void applyStatusStyle(String status) {
        switch (status) {
            case "DONE":
            case "DELIVERED":
                binding.chipStatus.setText("Hoàn thành");
                binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.status_green_bg)));
                binding.chipStatus.setTextColor(ContextCompat.getColor(this, R.color.status_green_text));
                break;
            case "IN_PROGRESS":
                binding.chipStatus.setText("Đang sửa");
                binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.status_orange_bg)));
                binding.chipStatus.setTextColor(ContextCompat.getColor(this, R.color.status_orange_text));
                break;
            default:
                binding.chipStatus.setText("Tiếp nhận");
                binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.status_blue_bg)));
                binding.chipStatus.setTextColor(ContextCompat.getColor(this, R.color.status_blue_text));
                break;
        }
    }
}
