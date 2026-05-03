package com.example.motoshop.ui.repair;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.RepairService;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RepairServicesFragment extends Fragment {

    private RepairViewModel viewModel;
    private RepairServiceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reuse inventory layout for simplicity, it has a RecyclerView and a FAB
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide unused search/filters
        View searchView = view.findViewById(R.id.searchView);
        if (searchView != null) searchView.setVisibility(View.GONE);
        View filters = view.findViewById(R.id.scrollFilters);
        if (filters != null) filters.setVisibility(View.GONE);

        RecyclerView rv = view.findViewById(R.id.rvInventory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RepairServiceAdapter();
        rv.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(RepairViewModel.class);
        viewModel.allServices.observe(getViewLifecycleOwner(), list -> {
            adapter.setServices(list);
            View empty = view.findViewById(R.id.layoutEmpty);
            if (empty != null) {
                empty.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        adapter.setListener(new RepairServiceAdapter.OnServiceClickListener() {
            @Override
            public void onEditClick(RepairService service) {
                showServiceDialog(service);
            }

            @Override
            public void onDeleteClick(RepairService service) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xóa dịch vụ")
                        .setMessage("Bạn có chắc muốn xóa dịch vụ " + service.name + "?")
                        .setPositiveButton("Xóa", (dialog, which) -> viewModel.deleteService(service))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fabAddMotorcycle);
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> showServiceDialog(null));
        }
    }

    private void showServiceDialog(@Nullable RepairService service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(service == null ? "Thêm dịch vụ mới" : "Chỉnh sửa dịch vụ");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etName = new EditText(requireContext());
        etName.setHint("Tên dịch vụ");
        if (service != null) etName.setText(service.name);
        layout.addView(etName);

        final EditText etDesc = new EditText(requireContext());
        etDesc.setHint("Mô tả");
        if (service != null) etDesc.setText(service.description);
        layout.addView(etDesc);

        final EditText etPrice = new EditText(requireContext());
        etPrice.setHint("Phí dịch vụ (VNĐ)");
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (service != null) etPrice.setText(String.valueOf(service.defaultLaborCost));
        layout.addView(etPrice);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ tên và giá", Toast.LENGTH_SHORT).show();
                return;
            }

            RepairService s = service == null ? new RepairService() : service;
            s.name = name;
            s.description = desc;
            s.defaultLaborCost = Double.parseDouble(priceStr);

            viewModel.saveService(s);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
