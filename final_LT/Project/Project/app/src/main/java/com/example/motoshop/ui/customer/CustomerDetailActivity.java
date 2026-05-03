package com.example.motoshop.ui.customer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Customer;
import com.example.motoshop.data.model.CustomerVehicle;
import com.example.motoshop.data.model.RepairOrder;
import com.example.motoshop.data.model.SalesOrder;
import com.example.motoshop.data.model.SalesOrderItem;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.DateUtils;
import com.example.motoshop.viewmodel.CustomerVehicleViewModel;
import com.example.motoshop.viewmodel.CustomerViewModel;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.example.motoshop.viewmodel.SalesViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class CustomerDetailActivity extends AppCompatActivity {

    private String customerDocId;
    private Customer currentCustomer;
    private TextView tvName, tvPhone, tvEmail, tvAddress, tvIdCard, tvNote;
    private TextView tvCountSales, tvCountRepairs, tvTotalSpending;
    private TextView tvCustomerCode, tvAccountStatus, tvMemberRank, tvLoyaltyPoints;
    private RecyclerView rvVehicles, rvSalesHistory, rvRepairHistory;

    private CustomerViewModel customerViewModel;
    private CustomerVehicleViewModel vehicleViewModel;
    private SalesViewModel salesViewModel;
    private RepairViewModel repairViewModel;

    private double totalSales = 0;
    private double totalRepairs = 0;
    private boolean isUpdatingLoyalty = false;

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        customerDocId = getIntent().getStringExtra("CUSTOMER_DOC_ID");
        if (customerDocId == null) {
            Toast.makeText(this, "Không tìm thấy khách hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        initViewModels();
        observeData();
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvIdCard = findViewById(R.id.tvIdCard);
        tvNote = findViewById(R.id.tvNote);

        tvCustomerCode = findViewById(R.id.tvCustomerCode);
        tvAccountStatus = findViewById(R.id.tvAccountStatus);
        tvMemberRank = findViewById(R.id.tvMemberRank);
        tvLoyaltyPoints = findViewById(R.id.tvLoyaltyPoints);

        tvCountSales = findViewById(R.id.tvCountSales);
        tvCountRepairs = findViewById(R.id.tvCountRepairs);
        tvTotalSpending = findViewById(R.id.tvTotalSpending);

        rvVehicles = findViewById(R.id.rvVehicles);
        rvSalesHistory = findViewById(R.id.rvSalesHistory);
        rvRepairHistory = findViewById(R.id.rvRepairHistory);

        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvSalesHistory.setLayoutManager(new LinearLayoutManager(this));
        rvRepairHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết khách hàng");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_detail, menu);
        return true;
    }

    // Xử lý khi người dùng bấm các nút trên thanh menu.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            if (currentCustomer != null) showEditCustomerDialog();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void initViewModels() {
        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        vehicleViewModel = new ViewModelProvider(this).get(CustomerVehicleViewModel.class);
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);
    }

    private final List<com.google.firebase.firestore.ListenerRegistration> listeners = new ArrayList<>();

    // Lấy dữ liệu cần thiết và đưa lên giao diện.
    private void observeData() {
        customerViewModel.allCustomers.observe(this, customers -> {
            for (Customer c : customers) {
                if (customerDocId.equals(c.documentId)) {
                    currentCustomer = c;
                    bindCustomer(c);
                    break;
                }
            }
        });

        vehicleViewModel.getByCustomer(customerDocId).observe(this, vehicles -> {
            rvVehicles.setAdapter(new VehicleAdapter(vehicles, this::showEditMaintenanceDialog));
        });

        listeners.add(FirebaseFirestore.getInstance().collection("sales_orders")
                .whereEqualTo("customerDocumentId", customerDocId)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<SalesOrder> orders = value.toObjects(SalesOrder.class);
                        rvSalesHistory.setAdapter(new SalesHistoryAdapter(orders));

                        int count = 0;
                        totalSales = 0;
                        for (SalesOrder o : orders) {
                            if ("COMPLETED".equals(o.status)) {
                                count++;
                                totalSales += o.finalAmount;
                            }
                        }
                        tvCountSales.setText(String.valueOf(count));
                        syncLoyaltyInfo();
                    }
                }));

        listeners.add(FirebaseFirestore.getInstance().collection("repair_orders")
                .whereEqualTo("customerDocumentId", customerDocId)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<RepairOrder> repairs = value.toObjects(RepairOrder.class);
                        rvRepairHistory.setAdapter(new RepairHistoryAdapter(repairs));

                        int count = 0;
                        totalRepairs = 0;
                        for (RepairOrder r : repairs) {
                            if ("DONE".equals(r.status) || "DELIVERED".equals(r.status)) {
                                count++;
                                totalRepairs += r.totalCost;
                            }
                        }
                        tvCountRepairs.setText(String.valueOf(count));
                        syncLoyaltyInfo();
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (com.google.firebase.firestore.ListenerRegistration reg : listeners) {
            reg.remove();
        }
        listeners.clear();
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void bindCustomer(Customer c) {
        tvName.setText(c.name);
        tvPhone.setText(c.phone);
        tvEmail.setText("Email: " + (c.email != null && !c.email.isEmpty() ? c.email : "---"));
        tvAddress.setText("Địa chỉ: " + (c.address != null && !c.address.isEmpty() ? c.address : "---"));
        tvIdCard.setText("CCCD: " + (c.idCard != null && !c.idCard.isEmpty() ? c.idCard : "---"));
        tvNote.setText("Ghi chú: " + (c.note != null && !c.note.isEmpty() ? c.note : "---"));

        tvCustomerCode.setText("Mã KH: " + (c.customerCode != null ? c.customerCode : "---"));
        tvAccountStatus.setText("ACTIVE".equals(c.accountStatus) ? "Đang hoạt động" : "Tạm khóa");
        tvAccountStatus.setTextColor("ACTIVE".equals(c.accountStatus) ? 0xFF4CAF50 : 0xFFF44336);
        tvMemberRank.setText(c.memberRank != null ? c.memberRank : "NORMAL");
        tvLoyaltyPoints.setText(String.valueOf(c.loyaltyPoints));

        tvTotalSpending.setText(CurrencyFormatter.format(totalSales + totalRepairs));
    }

    // Cập nhật lại điểm tích lũy và hạng thành viên của khách hàng.
    private void syncLoyaltyInfo() {
        double currentTotal = totalSales + totalRepairs;
        tvTotalSpending.setText(CurrencyFormatter.format(currentTotal));

        if (!isUpdatingLoyalty) {
            isUpdatingLoyalty = true;
            customerViewModel.updateLoyalty(customerDocId, currentTotal);
            tvTotalSpending.postDelayed(() -> isUpdatingLoyalty = false, 2000);
        }
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showEditCustomerDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_customer, null);

        TextInputLayout tilName = view.findViewById(R.id.tilName);
        TextInputLayout tilPhone = view.findViewById(R.id.tilPhone);
        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail);
        TextInputLayout tilAddress = view.findViewById(R.id.tilAddress);
        TextInputLayout tilIdCard = view.findViewById(R.id.tilIdCard);
        TextInputLayout tilNote = view.findViewById(R.id.tilNote);

        TextInputEditText etName = (TextInputEditText) tilName.getEditText();
        TextInputEditText etPhone = (TextInputEditText) tilPhone.getEditText();
        TextInputEditText etEmail = (TextInputEditText) tilEmail.getEditText();
        TextInputEditText etAddress = (TextInputEditText) tilAddress.getEditText();
        TextInputEditText etIdCard = (TextInputEditText) tilIdCard.getEditText();
        TextInputEditText etNote = (TextInputEditText) tilNote.getEditText();

        // Lấy dữ liệu hiện tại để đưa vào form sửa
        etName.setText(currentCustomer.name);
        etPhone.setText(currentCustomer.phone);
        etEmail.setText(currentCustomer.email);
        etAddress.setText(currentCustomer.address);
        etIdCard.setText(currentCustomer.idCard);
        etNote.setText(currentCustomer.note);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Sửa thông tin khách hàng")
                .setView(view)
                .setPositiveButton("Cập nhật", null)
                .setNegativeButton("Hủy", null)
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String idCard = etIdCard.getText().toString().trim();

            boolean isValid = true;
            if (TextUtils.isEmpty(name)) { tilName.setError("Nhập tên"); isValid = false; } else tilName.setError(null);
            if (TextUtils.isEmpty(phone)) { tilPhone.setError("Nhập SĐT"); isValid = false; } else tilPhone.setError(null);
            if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Email sai"); isValid = false; } else tilEmail.setError(null);

            if (!isValid) return;

            currentCustomer.name = name;
            currentCustomer.phone = phone;
            currentCustomer.email = email;
            currentCustomer.address = etAddress.getText().toString().trim();
            currentCustomer.idCard = idCard;
            currentCustomer.note = etNote.getText().toString().trim();

            customerViewModel.update(currentCustomer, new CustomerViewModel.OnActionListener() {
                @Override public void onSuccess() {
                    Toast.makeText(CustomerDetailActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                @Override public void onFailure(String msg) { Toast.makeText(CustomerDetailActivity.this, msg, Toast.LENGTH_LONG).show(); }
            });
        });
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showDeleteConfirmDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khách hàng này? Mọi dữ liệu liên quan sẽ không thể khôi phục.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    customerViewModel.delete(customerDocId, new CustomerViewModel.OnActionListener() {
                        @Override public void onSuccess() {
                            Toast.makeText(CustomerDetailActivity.this, "Đã xóa khách hàng", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override public void onFailure(String msg) { Toast.makeText(CustomerDetailActivity.this, msg, Toast.LENGTH_LONG).show(); }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showEditMaintenanceDialog(CustomerVehicle v) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_maintenance, null);
        TextInputEditText etPurchase = view.findViewById(R.id.etPurchaseDate);
        TextInputEditText etWarranty = view.findViewById(R.id.etWarrantyDate);
        TextInputEditText etNextMain = view.findViewById(R.id.etNextMaintenance);
        TextInputEditText etNote = view.findViewById(R.id.etMaintenanceNote);

        if (v.purchaseDate > 0) etPurchase.setText(DateUtils.formatDate(v.purchaseDate));
        if (v.warrantyEndDate > 0) etWarranty.setText(DateUtils.formatDate(v.warrantyEndDate));
        if (v.nextMaintenanceDate > 0) etNextMain.setText(DateUtils.formatDate(v.nextMaintenanceDate));
        etNote.setText(v.maintenanceNote);

        setupDatePicker(etPurchase);
        setupDatePicker(etWarranty);
        setupDatePicker(etNextMain);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Cập nhật bảo hành & bảo dưỡng")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    v.purchaseDate = DateUtils.parseDate(etPurchase.getText().toString());
                    v.warrantyEndDate = DateUtils.parseDate(etWarranty.getText().toString());
                    v.nextMaintenanceDate = DateUtils.parseDate(etNextMain.getText().toString());
                    v.maintenanceNote = etNote.getText().toString().trim();
                    vehicleViewModel.update(v);
                    Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupDatePicker(TextInputEditText et) {
        et.setFocusable(false);
        et.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                cal.set(year, month, dayOfMonth);
                et.setText(DateUtils.formatDate(cal.getTimeInMillis()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    // Các adapter nhỏ dùng cho màn hình chi tiết khách hàng

    private static class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VH> {
        private List<CustomerVehicle> list;
        private final OnEditClickListener editListener;

        // Interface xử lý khi bấm nút sửa xe của khách.
        interface OnEditClickListener { void onEdit(CustomerVehicle v); }

        VehicleAdapter(List<CustomerVehicle> list, OnEditClickListener editListener) {
            this.list = list;
            this.editListener = editListener;
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_customer_bike, p, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int p) {
            CustomerVehicle v = list.get(p);
            h.name.setText(v.brand + " " + v.model);
            h.plate.setText("Biển số: " + (v.licensePlate != null && !v.licensePlate.isEmpty() ? v.licensePlate : "Chưa có"));
            h.info.setText(v.color + " - " + v.year);

            long now = System.currentTimeMillis();
            if (v.warrantyEndDate > 0) {
                if (now <= v.warrantyEndDate) {
                    h.warranty.setText("CÒN BẢO HÀNH");
                    h.warranty.setBackgroundResource(R.drawable.bg_status_badge);
                } else {
                    h.warranty.setText("HẾT BẢO HÀNH");
                    h.warranty.setBackgroundColor(0xFFE0E0E0);
                }
            } else {
                h.warranty.setText("CHƯA CÓ BH");
                h.warranty.setBackgroundColor(0xFFF5F5F5);
            }

            if (v.nextMaintenanceDate > 0) {
                String dateStr = DateUtils.formatDate(v.nextMaintenanceDate);
                long diff = v.nextMaintenanceDate - now;
                long sevenDays = 7L * 24 * 60 * 60 * 1000;

                if (diff < 0) {
                    h.maintenance.setText("Quá hạn bảo dưỡng: " + dateStr);
                    h.maintenance.setTextColor(0xFFD32F2F);
                } else if (diff <= sevenDays) {
                    h.maintenance.setText("Sắp đến hạn: " + dateStr);
                    h.maintenance.setTextColor(0xFFE65100);
                } else {
                    h.maintenance.setText("Ngày bảo dưỡng: " + dateStr);
                    h.maintenance.setTextColor(0xFF757575);
                }
            } else {
                h.maintenance.setText("Chưa có lịch bảo dưỡng");
                h.maintenance.setTextColor(0xFF9E9E9E);
            }

            h.btnEdit.setOnClickListener(view -> editListener.onEdit(v));
        }

        // Trả về số lượng item đang có trong danh sách.
        @Override public int getItemCount() { return list != null ? list.size() : 0; }
        static class VH extends RecyclerView.ViewHolder {
            TextView name, plate, info, warranty, maintenance;
            ImageButton btnEdit;
            VH(View v) {
                super(v);
                name=v.findViewById(R.id.tvBikeName);
                plate=v.findViewById(R.id.tvBikePlate);
                info=v.findViewById(R.id.tvBikeInfo);
                warranty=v.findViewById(R.id.tvWarrantyStatus);
                maintenance=v.findViewById(R.id.tvMaintenanceStatus);
                btnEdit=v.findViewById(R.id.btnEditVehicle);
            }
        }
    }

    // Adapter dùng để đưa dữ liệu lên RecyclerView.
    private static class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.VH> {
        private List<SalesOrder> list;
        SalesHistoryAdapter(List<SalesOrder> list) { this.list = list; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_history_simple, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int p) {
            SalesOrder o = list.get(p);
            h.code.setText(o.orderCode);
            h.date.setText(DateUtils.formatDate(o.orderDate));
            h.amount.setText(CurrencyFormatter.format(o.finalAmount));
            h.status.setText(o.status);
            StringBuilder summary = new StringBuilder();
            if (o.items != null) for (SalesOrderItem item : o.items) {
                if (summary.length() > 0) summary.append(", ");
                summary.append(item.motorcycleName);
            }
            h.desc.setText(summary.toString());
        }
        // Trả về số lượng item đang có trong danh sách.
        @Override public int getItemCount() { return list != null ? list.size() : 0; }
        static class VH extends RecyclerView.ViewHolder {
            TextView code, date, amount, status, desc;
            VH(View v) { super(v); code=v.findViewById(R.id.tvCode); date=v.findViewById(R.id.tvDate); amount=v.findViewById(R.id.tvAmount); status=v.findViewById(R.id.tvStatus); desc=v.findViewById(R.id.tvDesc); }
        }
    }

    // Adapter dùng để đưa dữ liệu lên RecyclerView.
    private static class RepairHistoryAdapter extends RecyclerView.Adapter<RepairHistoryAdapter.VH> {
        private List<RepairOrder> list;
        RepairHistoryAdapter(List<RepairOrder> list) { this.list = list; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_history_simple, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int p) {
            RepairOrder r = list.get(p);
            h.code.setText(r.repairCode);
            h.date.setText(DateUtils.formatDate(r.receivedDate));
            h.amount.setText(CurrencyFormatter.format(r.totalCost));
            h.status.setText(r.status);
            h.desc.setText(r.issueDescription != null ? r.issueDescription : r.diagnosis);
        }
        // Trả về số lượng item đang có trong danh sách.
        @Override public int getItemCount() { return list != null ? list.size() : 0; }
        static class VH extends RecyclerView.ViewHolder {
            TextView code, date, amount, status, desc;
            VH(View v) { super(v); code=v.findViewById(R.id.tvCode); date=v.findViewById(R.id.tvDate); amount=v.findViewById(R.id.tvAmount); status=v.findViewById(R.id.tvStatus); desc=v.findViewById(R.id.tvDesc); }
        }
    }
}
