package com.example.motoshop.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Customer;
import java.util.ArrayList;
import java.util.List;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    private List<Customer> customers = new ArrayList<>();
    private OnItemClickListener listener;

    // Interface báo ra ngoài khi người dùng bấm vào một item.
    public interface OnItemClickListener {
        void onItemClick(Customer customer);
    }

    // Nhận sự kiện click từ màn hình để xử lý khi bấm vào item.
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Gán giá trị mới cho dữ liệu trong object.
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.tvCustomerName.setText(customer.name);
        holder.tvCustomerPhone.setText(customer.phone);
        holder.tvCustomerAddress.setText(customer.address);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(customer);
        });
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() {
        return customers.size();
    }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;

        ViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
        }
    }
}
