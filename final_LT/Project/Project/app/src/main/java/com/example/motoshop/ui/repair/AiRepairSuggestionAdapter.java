package com.example.motoshop.ui.repair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.RepairService;
import com.example.motoshop.utils.CurrencyFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class AiRepairSuggestionAdapter extends RecyclerView.Adapter<AiRepairSuggestionAdapter.ViewHolder> {

    private List<RepairService> suggestions = new ArrayList<>();
    private final Set<String> selectedDocIds = new HashSet<>();
    private OnSelectionChangeListener listener;

    // Interface báo lại tổng tiền khi chọn hoặc bỏ chọn dịch vụ.
    public interface OnSelectionChangeListener {
        void onSelectionChanged(double totalLabor, double totalParts);
    }

    // Lấy dữ liệu cần thiết và đưa lên giao diện.
    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.listener = listener;
    }

    // Gán giá trị mới cho dữ liệu trong object.
    public void setSuggestions(List<RepairService> allServices, List<String> aiRecommendedDocIds) {
        this.selectedDocIds.clear();
        if (aiRecommendedDocIds != null) {
            this.selectedDocIds.addAll(aiRecommendedDocIds);
        }

        // Đưa dịch vụ AI gợi ý lên đầu danh sách
        List<RepairService> sortedList = new ArrayList<>(allServices);
        Collections.sort(sortedList, (s1, s2) -> {
            boolean r1 = selectedDocIds.contains(s1.documentId);
            boolean r2 = selectedDocIds.contains(s2.documentId);
            if (r1 && !r2) return -1;
            if (!r1 && r2) return 1;
            return s1.name.compareTo(s2.name);
        });

        this.suggestions = sortedList;
        notifyDataSetChanged();
        notifyPriceChange();
    }

    // Tính lại tổng tiền khi chọn hoặc bỏ chọn dịch vụ sửa chữa.
    private void notifyPriceChange() {
        if (listener != null) {
            double labor = 0;
            double parts = 0;
            for (RepairService s : suggestions) {
                if (selectedDocIds.contains(s.documentId)) {
                    labor += s.defaultLaborCost;
                    parts += s.defaultPartsCost;
                }
            }
            listener.onSelectionChanged(labor, parts);
        }
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public String getSelectedServicesText() {
        StringBuilder sb = new StringBuilder();
        for (RepairService s : suggestions) {
            if (selectedDocIds.contains(s.documentId)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(s.name);
            }
        }
        return sb.toString();
    }

    // Kiểm tra người dùng đã chọn ít nhất một dịch vụ chưa.
    public boolean hasSelection() {
        return !selectedDocIds.isEmpty();
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_repair_suggestion, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepairService service = suggestions.get(position);
        holder.tvName.setText(service.name);
        holder.tvDesc.setText(service.description);
        holder.tvPrice.setText(CurrencyFormatter.format(service.defaultLaborCost + service.defaultPartsCost));

        // Làm nổi bật dịch vụ được gợi ý
        if (selectedDocIds.contains(service.documentId)) {
            holder.itemView.setAlpha(1.0f);
            holder.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ai, 0);
        } else {
            holder.itemView.setAlpha(0.7f);
            holder.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(selectedDocIds.contains(service.documentId));

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedDocIds.add(service.documentId);
            else selectedDocIds.remove(service.documentId);
            notifyPriceChange();
        });

        holder.itemView.setOnClickListener(v -> holder.cbSelect.performClick());
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        TextView tvName, tvDesc, tvPrice;
        ViewHolder(View v) {
            super(v);
            cbSelect = v.findViewById(R.id.cbSelect);
            tvName = v.findViewById(R.id.tvServiceName);
            tvDesc = v.findViewById(R.id.tvDescription);
            tvPrice = v.findViewById(R.id.tvPrice);
        }
    }
}
