package com.example.motoshop.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.SalesOrder;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.ViewHolder> {
    private List<SalesOrder> orders = new ArrayList<>();

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
        holder.tvOrderCode.setText(order.orderCode);
        holder.tvCustomerName.setText(order.customerName);
        holder.tvOrderDate.setText(DateUtils.formatDate(order.orderDate));
        holder.tvFinalAmount.setText(CurrencyFormatter.format(order.finalAmount));
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() {
        return orders.size();
    }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvCustomerName, tvOrderDate, tvFinalAmount;

        ViewHolder(View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvFinalAmount = itemView.findViewById(R.id.tvFinalAmount);
        }
    }
}
