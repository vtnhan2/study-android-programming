package com.example.motoshop.ui.sales;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.SalesOrder;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.ViewHolder> {
    private List<SalesOrder> orders = new ArrayList<>();
    private final OnOrderActionListener listener;

    // Interface xử lý các nút trong đơn bán hàng.
    public interface OnOrderActionListener {
        void onComplete(String documentId);
        void onCancel(SalesOrder order, String reason);
        void onViewDetail(SalesOrder order);
    }

    // Constructor khởi tạo object của class này.
    public SalesOrderAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    // Gán giá trị mới cho dữ liệu trong object.
    public void setOrders(List<SalesOrder> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_order, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SalesOrder order = orders.get(position);
        Context context = holder.itemView.getContext();

        holder.tvOrderCode.setText(order.orderCode);
        holder.tvCustomerName.setText("Khách hàng: " + order.customerName);
        holder.tvOrderDate.setText("Ngày tạo: " + DateUtils.formatDate(order.orderDate));
        holder.tvFinalAmount.setText(CurrencyFormatter.format(order.finalAmount));

        applyStatusStyle(holder.tvStatus, order.status, context);

        if ("PROCESSING".equals(order.status)) {
            holder.layoutActions.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        holder.btnComplete.setOnClickListener(v -> listener.onComplete(order.documentId));
        holder.btnCancel.setOnClickListener(v -> showStructuredCancelDialog(context, order));
        holder.itemView.setOnClickListener(v -> listener.onViewDetail(order));
    }

    // Hiển thị hộp thoại nhập lý do hủy đơn bán.
    private void applyStatusStyle(TextView tv, String status, Context context) {
        int colorBg, colorText;
        String text;

        if ("COMPLETED".equals(status)) {
            text = "Hoàn tất";
            colorBg = R.color.status_green_bg;
            colorText = R.color.status_green_text;
        } else if ("CANCELLED".equals(status)) {
            text = "Đã hủy";
            colorBg = R.color.status_red_bg;
            colorText = R.color.status_red_text;
        } else {
            text = "Đang xử lý";
            colorBg = R.color.status_orange_bg;
            colorText = R.color.status_orange_text;
        }

        tv.setText(text.toUpperCase());
        tv.setTextColor(ContextCompat.getColor(context, colorText));
        tv.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, colorBg)));
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showStructuredCancelDialog(Context context, SalesOrder order) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_order, null);
        RadioGroup rg = view.findViewById(R.id.rgCancelReasons);
        TextInputLayout tilOther = view.findViewById(R.id.tilOtherReason);
        TextInputEditText etOther = view.findViewById(R.id.etOtherReason);

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            tilOther.setVisibility(checkedId == R.id.rbOther ? View.VISIBLE : View.GONE);
        });

        new MaterialAlertDialogBuilder(context)
                .setTitle("Hủy đơn hàng")
                .setView(view)
                .setPositiveButton("Xác nhận hủy", (dialog, which) -> {
                    int checkedId = rg.getCheckedRadioButtonId();
                    if (checkedId == -1) return;

                    String reason;
                    RadioButton selectedRb = view.findViewById(checkedId);
                    if (checkedId == R.id.rbOther) {
                        reason = "Khác: " + etOther.getText().toString().trim();
                    } else {
                        reason = selectedRb.getText().toString();
                    }
                    listener.onCancel(order, reason);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() { return orders.size(); }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvCustomerName, tvOrderDate, tvFinalAmount, tvStatus;
        View layoutActions;
        MaterialButton btnComplete, btnCancel;

        ViewHolder(View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvFinalAmount = itemView.findViewById(R.id.tvFinalAmount);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
