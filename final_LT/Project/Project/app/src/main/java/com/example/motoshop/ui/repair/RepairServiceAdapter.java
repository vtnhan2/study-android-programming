package com.example.motoshop.ui.repair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.RepairService;
import com.example.motoshop.utils.CurrencyFormatter;
import java.util.ArrayList;
import java.util.List;

public class RepairServiceAdapter extends RecyclerView.Adapter<RepairServiceAdapter.ServiceViewHolder> {

    private List<RepairService> serviceList = new ArrayList<>();
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onEditClick(RepairService service);
        void onDeleteClick(RepairService service);
    }

    public void setListener(OnServiceClickListener listener) {
        this.listener = listener;
    }

    public void setServices(List<RepairService> list) {
        this.serviceList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        RepairService s = serviceList.get(position);
        holder.tvName.setText(s.name);
        holder.tvPrice.setText("Phí dịch vụ: " + CurrencyFormatter.format(s.defaultLaborCost));
        holder.tvDesc.setText(s.description);
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(s);
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(s);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDesc, btnEdit, btnDelete;
        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
            tvDesc = itemView.findViewById(R.id.tvServiceDesc);
            btnEdit = itemView.findViewById(R.id.btnEditService);
            btnDelete = itemView.findViewById(R.id.btnDeleteService);
        }
    }
}
