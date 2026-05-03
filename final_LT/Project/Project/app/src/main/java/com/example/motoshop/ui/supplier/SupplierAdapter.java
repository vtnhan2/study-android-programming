package com.example.motoshop.ui.supplier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Supplier;
import java.util.ArrayList;
import java.util.List;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {
    private List<Supplier> suppliers = new ArrayList<>();

    // Gán giá trị mới cho dữ liệu trong object.
    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
        notifyDataSetChanged();
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Supplier supplier = suppliers.get(position);
        holder.tvSupplierName.setText(supplier.name);
        holder.tvSupplierPhone.setText(supplier.phone);
        holder.tvSupplierAddress.setText(supplier.address);
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() {
        return suppliers.size();
    }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSupplierName, tvSupplierPhone, tvSupplierAddress;

        ViewHolder(View itemView) {
            super(itemView);
            tvSupplierName = itemView.findViewById(R.id.tvSupplierName);
            tvSupplierPhone = itemView.findViewById(R.id.tvSupplierPhone);
            tvSupplierAddress = itemView.findViewById(R.id.tvSupplierAddress);
        }
    }
}
