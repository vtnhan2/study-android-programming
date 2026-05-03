package com.example.motoshop.ui.repair;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.RepairOrder;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class RepairOrderAdapter extends RecyclerView.Adapter<RepairOrderAdapter.ViewHolder> {
    private List<RepairOrder> repairs = new ArrayList<>();
    private OnItemClickListener listener;

    // Interface báo ra ngoài khi người dùng bấm vào một item.
    public interface OnItemClickListener {
        void onItemClick(RepairOrder repair);
    }

    // Nhận sự kiện click từ màn hình để xử lý khi bấm vào item.
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Xử lý nội dung AI hoặc tin nhắn trả về cho người dùng.
    public void setRepairs(List<RepairOrder> repairs) {
        this.repairs = repairs;
        notifyDataSetChanged();
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_order, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepairOrder repair = repairs.get(position);
        Context context = holder.itemView.getContext();

        holder.tvRepairCode.setText(repair.repairCode);
        holder.tvCustomerName.setText("Khách hàng: " + repair.customerName);
        holder.tvMotorInfo.setText("Xe: " + repair.motorcycleBrand + " - " + repair.licensePlate);

        applyStatusStyle(holder.chipStatus, repair.status != null ? repair.status : "RECEIVED", context);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(repair);
        });
    }

    // Đổi màu trạng thái của phiếu sửa chữa trong danh sách.
    private void applyStatusStyle(Chip chip, String status, Context context) {
        switch (status) {
            case "DONE":
            case "DELIVERED":
                chip.setText("Hoàn thành");
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_green_bg)));
                chip.setTextColor(ContextCompat.getColor(context, R.color.status_green_text));
                break;
            case "IN_PROGRESS":
                chip.setText("Đang sửa");
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_orange_bg)));
                chip.setTextColor(ContextCompat.getColor(context, R.color.status_orange_text));
                break;
            default:
                chip.setText("Tiếp nhận");
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_blue_bg)));
                chip.setTextColor(ContextCompat.getColor(context, R.color.status_blue_text));
                break;
        }
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() {
        return repairs.size();
    }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRepairCode, tvCustomerName, tvMotorInfo;
        Chip chipStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvRepairCode = itemView.findViewById(R.id.tvRepairCode);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvMotorInfo = itemView.findViewById(R.id.tvMotorInfo);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
