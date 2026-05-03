package com.example.motoshop.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.motoshop.ui.login.LoginActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Customer;
import com.example.motoshop.data.model.RepairOrder;
import com.example.motoshop.data.model.SalesOrder;
import com.example.motoshop.data.model.Staff;
import com.example.motoshop.databinding.FragmentUserProfileBinding;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.UserSession;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private FirebaseFirestore db;
    private UserSession session;
    private Customer currentCustomer;
    private UserHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        session = new UserSession(requireContext());

        binding.btnLogout.setOnClickListener(v -> confirmLogout());
        binding.ivAvatar.setOnClickListener(null);
        binding.fabEditAvatar.setVisibility(View.GONE);

        if (UserSession.ROLE_USER.equals(session.getUserRole())) {
            setupCustomerMode();
        } else {
            setupStaffMode();
        }
    }

    private void setupCustomerMode() {
        setupRecyclerView();
        loadCustomerData();
        loadHistory();
        binding.btnUpdate.setOnClickListener(v -> showEditDialog());
        binding.btnUpdate.setVisibility(View.VISIBLE);
    }

    private void setupStaffMode() {
        // Hide customer-only sections
        binding.cardLoyalty.setVisibility(View.GONE);
        binding.tvMaintenanceSectionLabel.setVisibility(View.GONE);
        binding.cardMaintenance.setVisibility(View.GONE);
        binding.tvHistoryLabel.setVisibility(View.GONE);
        binding.rvUserHistory.setVisibility(View.GONE);
        binding.btnUpdate.setVisibility(View.GONE);

        loadStaffData();
    }

    private void loadStaffData() {
        String staffId = session.getUserId();
        db.collection("staff")
                .whereEqualTo("staffId", staffId)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        Staff staff = snap.getDocuments().get(0).toObject(Staff.class);
                        if (staff != null) showStaffUI(staff);
                    } else {
                        // Fallback: show from session data
                        binding.tvHeaderName.setText(session.getUserName());
                        binding.tvHeaderPhone.setText(session.getRoleDisplayName());
                        binding.layoutInfo.tvValue.setText(session.getUserName());
                        binding.layoutInfo.tvDetailPhone.setText("Mã NV: " + staffId);
                        binding.layoutInfo.tvDetailEmail.setText(session.getRoleDisplayName());
                        binding.layoutInfo.tvDetailAddress.setText("---");
                        binding.layoutInfo.tvDetailIdCard.setVisibility(View.GONE);
                    }
                });
    }

    private void showStaffUI(Staff staff) {
        binding.tvHeaderName.setText(staff.name);
        binding.tvHeaderPhone.setText(session.getRoleDisplayName());
        binding.layoutInfo.tvValue.setText(staff.name);
        binding.layoutInfo.tvDetailPhone.setText(nonEmpty(staff.phone));
        binding.layoutInfo.tvDetailEmail.setText("Mã NV: " + staff.staffId);
        binding.layoutInfo.tvDetailAddress.setText(session.getRoleDisplayName());
        binding.layoutInfo.tvDetailIdCard.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new UserHistoryAdapter();
        binding.rvUserHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserHistory.setAdapter(adapter);
    }

    private void loadCustomerData() {
        String docId = session.getCustomerDocId();
        if (docId == null) return;

        db.collection("customers").document(docId).get()
                .addOnSuccessListener(doc -> {
                    currentCustomer = doc.toObject(Customer.class);
                    if (currentCustomer != null) {
                        currentCustomer.documentId = doc.getId();
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        if (currentCustomer == null) return;
        binding.tvHeaderName.setText(currentCustomer.name);
        binding.tvHeaderPhone.setText(currentCustomer.phone);

        if (currentCustomer.avatarUrl != null && !currentCustomer.avatarUrl.isEmpty()) {
            Glide.with(this).load(currentCustomer.avatarUrl).circleCrop().into(binding.ivAvatar);
        }

        binding.layoutInfo.tvValue.setText(currentCustomer.name);
        binding.layoutInfo.tvDetailPhone.setText(currentCustomer.phone);
        binding.layoutInfo.tvDetailEmail.setText(nonEmpty(currentCustomer.email));
        binding.layoutInfo.tvDetailAddress.setText(nonEmpty(currentCustomer.address));
        binding.layoutInfo.tvDetailIdCard.setText(nonEmpty(currentCustomer.idCard));

        binding.tvPoints.setText(String.valueOf(currentCustomer.loyaltyPoints));
        binding.tvRank.setText(translateRank(currentCustomer.memberRank));

        calculateMaintenance();
    }

    private String translateRank(String rank) {
        if (rank == null || rank.equals("NORMAL")) return "Thành viên";
        switch (rank) {
            case "SILVER": return "Hạng Bạc";
            case "GOLD": return "Hạng Vàng";
            case "VIP": return "Hạng VIP";
            default: return rank;
        }
    }

    private static String translateStatus(String status) {
        if (status == null) return "---";
        switch (status) {
            case "RECEIVED": return "Tiếp nhận";
            case "IN_PROGRESS": return "Đang sửa";
            case "DONE":
            case "COMPLETED": return "Hoàn tất";
            case "DELIVERED": return "Đã giao";
            case "CANCELLED": return "Đã hủy";
            case "PENDING": return "Chờ xử lý";
            case "PROCESSING": return "Đang xử lý";
            default: return status;
        }
    }

    private String nonEmpty(String val) {
        return (val == null || val.isEmpty()) ? "---" : val;
    }

    private void calculateMaintenance() {
        if (currentCustomer == null) return;
        long baseDate = currentCustomer.createdAt > 0 ? currentCustomer.createdAt : System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(baseDate);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        StringBuilder schedule = new StringBuilder("Dự kiến bảo dưỡng:\n");
        int[] months = {1, 3, 6};
        for (int m : months) {
            Calendar mCal = (Calendar) cal.clone();
            mCal.add(Calendar.MONTH, m);
            schedule.append("• ").append(m).append(" tháng: ").append(sdf.format(mCal.getTime())).append("\n");
        }
        binding.tvMaintenanceSchedule.setText(schedule.toString().trim());
    }

    private void showEditDialog() {
        if (currentCustomer == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_customer, null);

        TextInputLayout tilName = view.findViewById(R.id.tilName);
        TextInputLayout tilPhone = view.findViewById(R.id.tilPhone);
        TextInputEditText etName = (TextInputEditText) tilName.getEditText();
        TextInputEditText etPhone = (TextInputEditText) tilPhone.getEditText();

        if (etName != null) etName.setText(currentCustomer.name);
        if (etPhone != null) etPhone.setText(currentCustomer.phone);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cập nhật thông tin")
                .setView(view)
                .setPositiveButton(R.string.btn_save, (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || !phone.matches("\\d{10,11}")) {
                        Toast.makeText(getContext(), "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentCustomer.name = name;
                    currentCustomer.phone = phone;
                    db.collection("customers").document(currentCustomer.documentId).set(currentCustomer)
                            .addOnSuccessListener(aVoid -> {
                                session.createCustomerSession(currentCustomer.documentId, currentCustomer.name, currentCustomer.phone);
                                updateUI();
                            });
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void loadHistory() {
        String docId = session.getCustomerDocId();
        if (docId == null) return;
        List<HistoryItem> historyList = new ArrayList<>();

        db.collection("sales_orders").whereEqualTo("customerDocumentId", docId).get()
                .addOnSuccessListener(salesDocs -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : salesDocs) {
                        SalesOrder o = doc.toObject(SalesOrder.class);
                        if (o != null) historyList.add(new HistoryItem("MUA XE", o.orderDate, o.orderCode, o.finalAmount, o.status));
                    }
                    sortAndNotify(historyList);
                });

        db.collection("repair_orders").whereEqualTo("customerDocumentId", docId).get()
                .addOnSuccessListener(repairDocs -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : repairDocs) {
                        RepairOrder r = doc.toObject(RepairOrder.class);
                        if (r != null) historyList.add(new HistoryItem("SỬA CHỮA", r.receivedDate, r.repairCode, r.totalCost, r.status));
                    }
                    sortAndNotify(historyList);
                });
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    session.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void sortAndNotify(List<HistoryItem> list) {
        list.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
        if (adapter != null) adapter.setItems(list);
    }

    private static class HistoryItem {
        String type, code, status; long timestamp; double amount;
        HistoryItem(String type, long timestamp, String code, double amount, String status) {
            this.type = type; this.timestamp = timestamp; this.code = code; this.amount = amount; this.status = status;
        }
    }

    private static class UserHistoryAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<UserHistoryAdapter.ViewHolder> {
        private List<HistoryItem> items = new ArrayList<>();
        void setItems(List<HistoryItem> items) { this.items = items; notifyDataSetChanged(); }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_history, parent, false);
            return new ViewHolder(v);
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = items.get(position);
            holder.tvType.setText(item.type);
            holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(item.timestamp)));
            holder.tvContent.setText(item.code);
            holder.tvAmount.setText(CurrencyFormatter.format(item.amount));
            holder.chipStatus.setText(translateStatus(item.status));
        }
        @Override public int getItemCount() { return items.size(); }
        static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            android.widget.TextView tvType, tvDate, tvContent, tvAmount;
            com.google.android.material.chip.Chip chipStatus;
            ViewHolder(View v) {
                super(v);
                tvType = v.findViewById(com.example.motoshop.R.id.tvType); tvDate = v.findViewById(com.example.motoshop.R.id.tvDate);
                tvContent = v.findViewById(com.example.motoshop.R.id.tvContent); tvAmount = v.findViewById(com.example.motoshop.R.id.tvAmount);
                chipStatus = v.findViewById(com.example.motoshop.R.id.chipStatus);
            }
        }
    }
}
