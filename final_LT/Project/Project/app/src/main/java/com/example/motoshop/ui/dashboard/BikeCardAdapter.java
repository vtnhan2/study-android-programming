package com.example.motoshop.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.utils.CurrencyFormatter;
import java.util.ArrayList;
import java.util.List;

// Adapter hiển thị xe dạng card ngang trên Home Screen.
public class BikeCardAdapter extends RecyclerView.Adapter<BikeCardAdapter.ViewHolder> {

    private List<Motorcycle> bikes = new ArrayList<>();
    private OnBikeClickListener listener;

    // Interface báo ra ngoài khi người dùng bấm vào một item.
    public interface OnBikeClickListener {
        void onBikeClick(Motorcycle bike);
        void onFavoriteClick(Motorcycle bike);
    }

    // Nhận sự kiện click từ màn hình để xử lý khi bấm vào item.
    public void setOnBikeClickListener(OnBikeClickListener listener) {
        this.listener = listener;
    }

    // Gán danh sách xe mới cho Adapter.
    public void setBikes(List<Motorcycle> list) {
        this.bikes = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bike_card, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Motorcycle bike = bikes.get(position);
        Context context = holder.itemView.getContext();

        holder.tvBikeName.setText(bike.brand + " " + bike.model);
        holder.tvBikePrice.setText(CurrencyFormatter.format(bike.price));
        holder.tvBikeSubInfo.setText("Giá tham khảo");

        // Xử lý hình ảnh cần hiển thị trên màn hình.
        int resId = getMotorcycleImageResId(context, bike.imageUri);
        if (resId != 0) {
            Glide.with(context).load(resId).into(holder.ivBike);
        } else {
            holder.ivBike.setImageResource(R.drawable.ic_inventory);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBikeClick(bike);
        });

        holder.ivHeart.setOnClickListener(v -> {
            if (listener != null) listener.onFavoriteClick(bike);
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

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() {
        return bikes.size();
    }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBike, ivHeart;
        TextView tvBikeName, tvBikePrice, tvBikeSubInfo;

        ViewHolder(View itemView) {
            super(itemView);
            ivBike = itemView.findViewById(R.id.ivBike);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvBikePrice = itemView.findViewById(R.id.tvBikePrice);
            tvBikeSubInfo = itemView.findViewById(R.id.tvBikeSubInfo);
        }
    }
}
