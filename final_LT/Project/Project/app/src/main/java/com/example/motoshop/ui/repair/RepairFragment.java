package com.example.motoshop.ui.repair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.RepairOrder;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách phiếu sửa chữa.
 * Đã dọn dẹp Toolbar cũ để dùng Toolbar chung của Activity, tránh lỗi crash NullPointerException.
 */
public class RepairFragment extends Fragment {

    private RepairViewModel viewModel;
    private RepairOrderAdapter adapter;
    private List<RepairOrder> fullList = new ArrayList<>();
    private String currentFilterStatus = "ALL";
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repair, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RepairViewModel.class);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        // Khởi tạo giao diện an toàn
        setupRecyclerView(view);
        setupFilters(view);
        setupFAB(view);

        observeData();
    }

    private void setupRecyclerView(View v) {
        RecyclerView rv = v.findViewById(R.id.rvRepairs);
        if (rv == null) return;

        adapter = new RepairOrderAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(repair -> {
            if (repair != null && repair.documentId != null) {
                Intent intent = new Intent(getContext(), RepairDetailActivity.class);
                intent.putExtra("REPAIR_DOC_ID", repair.documentId);
                startActivity(intent);
            }
        });
    }

    private void setupFilters(View v) {
        ChipGroup chipGroup = v.findViewById(R.id.chipGroupFilter);
        if (chipGroup != null) {
            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.contains(R.id.chipAll)) {
                    currentFilterStatus = "ALL";
                } else if (checkedIds.contains(R.id.chipInProgress)) {
                    currentFilterStatus = "IN_PROGRESS";
                } else if (checkedIds.contains(R.id.chipDone)) {
                    currentFilterStatus = "DONE";
                }
                applyFilter();
            });
        }
    }

    private void setupFAB(View v) {
        FloatingActionButton fab = v.findViewById(R.id.fabAddRepair);
        if (fab != null) {
            fab.setOnClickListener(view -> {
                startActivity(new Intent(getContext(), CreateRepairActivity.class));
            });
        }
    }

    private void observeData() {
        if (viewModel != null) {
            viewModel.allRepairs.observe(getViewLifecycleOwner(), list -> {
                if (list != null) {
                    fullList = list;
                    applyFilter();
                }
            });
        }
    }

    private void applyFilter() {
        if (adapter == null) return;

        List<RepairOrder> filtered = new ArrayList<>();
        if ("ALL".equals(currentFilterStatus)) {
            filtered.addAll(fullList);
        } else {
            for (RepairOrder r : fullList) {
                if (r != null && currentFilterStatus.equals(r.status)) {
                    filtered.add(r);
                }
            }
        }
        adapter.setRepairs(filtered);
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
