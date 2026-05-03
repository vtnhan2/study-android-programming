package com.example.motoshop.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.MotorcycleViewModel;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách kho xe máy.
 * Đã sửa lỗi crash do Toolbar null và tối ưu an toàn dữ liệu.
 */
public class InventoryFragment extends Fragment {

    private MotorcycleViewModel viewModel;
    private MotorcycleAdapter adapter;
    private List<Motorcycle> fullList = new ArrayList<>();
    private String currentQuery = "";
    private int currentFilterId = R.id.chipAll;
    private View layoutEmpty;
    private UserSession session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new UserSession(requireContext());

        viewModel = new ViewModelProvider(this).get(MotorcycleViewModel.class);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        // Khởi tạo các thành phần giao diện một cách an toàn
        setupRecyclerView(view);
        setupSearchView(view);
        setupFilters(view);
        setupFAB(view);

        observeData();
    }

    private void setupRecyclerView(View v) {
        RecyclerView rv = v.findViewById(R.id.rvInventory);
        if (rv == null) return;
        
        adapter = new MotorcycleAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(motor -> {
            if (motor == null || motor.documentId == null) return;
            
            if (UserSession.ROLE_USER.equals(session.getUserRole())) {
                Intent intent = new Intent(getContext(), MotorcycleDetailActivity.class);
                intent.putExtra("MOTORCYCLE_DOC_ID", motor.documentId);
                startActivity(intent);
            } else if (UserSession.ROLE_ADMIN.equals(session.getUserRole())) {
                Intent intent = new Intent(getContext(), AddEditMotorcycleActivity.class);
                intent.putExtra("EXTRA_BRAND", motor.brand);
                intent.putExtra("EXTRA_MODEL", motor.model);
                intent.putExtra("EXTRA_COLOR", motor.color);
                intent.putExtra("EXTRA_YEAR", motor.year);
                startActivity(intent);
            }
        });
    }

    private void setupSearchView(View v) {
        SearchView searchView = v.findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) { return false; }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentQuery = (newText != null) ? newText.toLowerCase().trim() : "";
                    applyFilter();
                    return true;
                }
            });
        }
    }

    private void setupFilters(View v) {
        ChipGroup chipGroup = v.findViewById(R.id.chipGroupFilter);
        if (chipGroup != null) {
            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds != null && !checkedIds.isEmpty()) {
                    currentFilterId = checkedIds.get(0);
                } else {
                    currentFilterId = R.id.chipAll;
                }
                applyFilter();
            });
        }
    }

    private void setupFAB(View v) {
        FloatingActionButton fab = v.findViewById(R.id.fabAddMotorcycle);
        if (fab != null) {
            if (UserSession.ROLE_USER.equals(session.getUserRole()) || UserSession.ROLE_SALES.equals(session.getUserRole())) {
                fab.setVisibility(View.GONE);
            } else {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(view -> {
                    startActivity(new Intent(getContext(), AddEditMotorcycleActivity.class));
                });
            }
        }
    }

    private void observeData() {
        if (viewModel != null) {
            viewModel.allMotorcycles.observe(getViewLifecycleOwner(), list -> {
                if (list != null) {
                    fullList = list;
                    applyFilter();
                }
            });
        }
    }

    private void applyFilter() {
        if (adapter == null) return;
        
        List<Motorcycle> filteredList = new ArrayList<>();
        for (Motorcycle m : fullList) {
            if (m == null) continue;

            String brand = (m.brand != null) ? m.brand : "";
            String model = (m.model != null) ? m.model : "";
            String fullName = (brand + " " + model).toLowerCase();
            
            boolean matchesSearch = fullName.contains(currentQuery);
            boolean matchesChip = false;

            if (currentFilterId == R.id.chipAll) {
                matchesChip = true;
            } else if (currentFilterId == R.id.chipInStock) {
                matchesChip = m.quantity > 0;
            } else if (currentFilterId == R.id.chipLowStock) {
                matchesChip = m.quantity > 0 && m.quantity <= 3;
            } else if (currentFilterId == R.id.chipSoldOut) {
                matchesChip = m.quantity == 0;
            }

            if (matchesSearch && matchesChip) {
                filteredList.add(m);
            }
        }
        adapter.setMotors(filteredList);
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
