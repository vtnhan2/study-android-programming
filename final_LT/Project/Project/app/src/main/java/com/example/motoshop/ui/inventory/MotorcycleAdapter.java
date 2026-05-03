package com.example.motoshop.ui.inventory;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.utils.CurrencyFormatter;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class MotorcycleAdapter extends RecyclerView.Adapter<MotorcycleAdapter.ViewHolder> {
    private List<Motorcycle> motors = new ArrayList<>();
    private OnItemClickListener listener;

    // Interface báo ra ngoài khi người dùng bấm vào một item.
    public interface OnItemClickListener {
        void onItemClick(Motorcycle motor);
    }

    // Nhận sự kiện click từ màn hình để xử lý khi bấm vào item.
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Gán danh sách xe mới cho Adapter và lọc bớt dữ liệu trùng.
    public void setMotors(List<Motorcycle> list) {
        List<Motorcycle> distinctList = new ArrayList<>();
        if (list != null) {
            Set<String> seen = new HashSet<>();
            for (Motorcycle m : list) {
                // Dùng đủ hãng + mẫu + màu + năm để tránh nhầm các xe cùng mẫu nhưng khác màu.
                String key = getMotorKey(m);
                if (!seen.contains(key)) {
                    distinctList.add(m);
                    seen.add(key);
                }
            }
        }
        this.motors = distinctList;
        notifyDataSetChanged();
    }

    // Tạo khóa đơn giản để kiểm tra xe trùng trong danh sách.
    private String getMotorKey(Motorcycle motor) {
        if (motor == null) return "";

        return cleanText(motor.brand) + "_"
                + cleanText(motor.model) + "_"
                + cleanText(motor.color) + "_"
                + motor.year;
    }

    // Chuẩn hóa chữ trước khi so sánh để tránh lệch do viết hoa hoặc khoảng trắng.
    private String cleanText(String text) {
        if (text == null) return "";
        return text.toLowerCase().trim();
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_motorcycle, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Motorcycle motor = motors.get(position);
        Context context = holder.itemView.getContext();

        holder.tvMotorName.setText(motor.brand + " " + motor.model);
        holder.tvMotorInfo.setText("Màu: " + motor.color + " | Năm: " + motor.year);
        holder.tvMotorPrice.setText(CurrencyFormatter.format(motor.price));
        holder.tvQuantityBadge.setText("Tồn: " + motor.quantity);

        applyStatusStyle(holder.chipStatus, motor.quantity, context);
        // Lấy ảnh xe bằng đúng tên resource trong imageUri.
        int resId = getMotorcycleImageResId(context, motor.imageUri);
        if (resId != 0) {
            Glide.with(context).load(resId).into(holder.ivMotorcycle);
        } else {
            holder.ivMotorcycle.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(motor);
        });
    }

    // Xử lý hình ảnh cần hiển thị trên màn hình.
    private int getMotorcycleImageResId(Context context, String imageUri) {
        if (imageUri == null || imageUri.trim().isEmpty()) return 0;

        String exactName = imageUri.trim();
        int resId = context.getResources().getIdentifier(exactName, "drawable", context.getPackageName());
        if (resId != 0) return resId;

        String safeName = exactName.toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return context.getResources().getIdentifier(safeName, "drawable", context.getPackageName());
    }

    // Đổi màu trạng thái dựa trên số lượng tồn kho.
    private void applyStatusStyle(Chip chip, int qty, Context context) {
        if (qty == 0) {
            chip.setText(context.getString(R.string.status_sold_out));
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_red_bg)));
            chip.setTextColor(ContextCompat.getColor(context, R.color.status_red_text));
        } else if (qty <= 3) {
            chip.setText(context.getString(R.string.status_low_stock));
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_orange_bg)));
            chip.setTextColor(ContextCompat.getColor(context, R.color.status_orange_text));
        } else {
            chip.setText(context.getString(R.string.status_in_stock));
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_green_bg)));
            chip.setTextColor(ContextCompat.getColor(context, R.color.status_green_text));
        }
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() { return motors.size(); }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMotorcycle;
        TextView tvMotorName, tvMotorInfo, tvMotorPrice, tvQuantityBadge;
        Chip chipStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ivMotorcycle = itemView.findViewById(R.id.ivMotorcycle);
            tvMotorName = itemView.findViewById(R.id.tvMotorName);
            tvMotorInfo = itemView.findViewById(R.id.tvMotorInfo);
            tvMotorPrice = itemView.findViewById(R.id.tvMotorPrice);
            tvQuantityBadge = itemView.findViewById(R.id.tvQuantityBadge);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
