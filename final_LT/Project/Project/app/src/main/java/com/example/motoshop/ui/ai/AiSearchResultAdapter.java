package com.example.motoshop.ui.ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.utils.CurrencyFormatter;
import java.util.List;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class AiSearchResultAdapter extends RecyclerView.Adapter<AiSearchResultAdapter.ViewHolder> {
    private final List<Motorcycle> list;

    // Constructor khởi tạo object của class này.
    public AiSearchResultAdapter(List<Motorcycle> list) {
        this.list = list;
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_search_result, parent, false);
        return new ViewHolder(v);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Motorcycle m = list.get(position);
        holder.tvName.setText(m.brand + " " + m.model);
        holder.tvPrice.setText(CurrencyFormatter.format(m.price));
        holder.tvStock.setText("Kho: " + m.quantity + " - " + m.color);
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() { return list.size(); }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvStock;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvMotorName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvStock = v.findViewById(R.id.tvStock);
        }
    }
}
