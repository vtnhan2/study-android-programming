package com.example.motoshop.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.ui.inventory.MotorcycleDetailActivity;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.MotorcycleViewModel;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.example.motoshop.viewmodel.SalesViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Fragment hiển thị màn hình Home Screen theo giao diện Bike App.
public class DashboardFragment extends Fragment {

    private MotorcycleViewModel motorcycleViewModel;
    private SalesViewModel salesViewModel;
    private RepairViewModel repairViewModel;
    private BikeCardAdapter mostSearchedAdapter;
    private BikeCardAdapter recommendedAdapter;
    private UserSession session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new UserSession(requireContext());

        setupGreeting(view);
        setupDrawerToggle(view);
        setupRecyclerViews(view);
        setupExploreButton(view);
        setupFab(view);
        initViewModels();
        setupManagementSummary(view);
        setupHeader(view);
        observeData();
    }

    private void setupFab(View view) {
        com.google.android.material.floatingactionbutton.FloatingActionButton fab = view.findViewById(R.id.fabAddOrder);
        if (fab == null) return;
        
        String role = session.getUserRole();
        if (UserSession.ROLE_USER.equals(role)) {
            fab.setVisibility(View.GONE);
            return;
        }

        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(v -> {
            if (UserSession.ROLE_TECHNICIAN.equals(role)) {
                startActivity(new Intent(getContext(), com.example.motoshop.ui.repair.CreateRepairActivity.class));
            } else {
                // Admin or Sales
                String[] options = {"Tạo đơn bán hàng", "Tạo phiếu sửa chữa"};
                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Chọn loại đơn")
                        .setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                startActivity(new Intent(getContext(), com.example.motoshop.ui.sales.CreateSaleActivity.class));
                            } else {
                                startActivity(new Intent(getContext(), com.example.motoshop.ui.repair.CreateRepairActivity.class));
                            }
                        })
                        .show();
            }
        });
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupGreeting(View v) {
        TextView tvGreeting = v.findViewById(R.id.tvGreeting);
        String userName = session.getUserName();
        tvGreeting.setText("Hi " + userName + "!");
    }

    // Mở DrawerLayout khi bấm nút menu.
    private void setupDrawerToggle(View v) {
        ImageView btnDrawer = v.findViewById(R.id.btnDrawerMenu);
        btnDrawer.setOnClickListener(view -> {
            if (getActivity() != null) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
                }
            }
        });
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupRecyclerViews(View v) {
        // Most Searched
        RecyclerView rvMostSearched = v.findViewById(R.id.rvMostSearched);
        mostSearchedAdapter = new BikeCardAdapter();
        rvMostSearched.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMostSearched.setAdapter(mostSearchedAdapter);
        mostSearchedAdapter.setOnBikeClickListener(new BikeCardAdapter.OnBikeClickListener() {
            @Override
            public void onBikeClick(Motorcycle bike) {
                openBikeDetail(bike);
            }

            @Override
            public void onFavoriteClick(Motorcycle bike) {
                // Hiển thị thông báo hoặc xử lý yêu thích cho nhân viên (nếu cần)
                android.widget.Toast.makeText(getContext(), "Tính năng yêu thích dành cho khách hàng", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Recommended
        RecyclerView rvRecommended = v.findViewById(R.id.rvRecommended);
        recommendedAdapter = new BikeCardAdapter();
        rvRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRecommended.setAdapter(recommendedAdapter);
        recommendedAdapter.setOnBikeClickListener(new BikeCardAdapter.OnBikeClickListener() {
            @Override
            public void onBikeClick(Motorcycle bike) {
                openBikeDetail(bike);
            }

            @Override
            public void onFavoriteClick(Motorcycle bike) {
                android.widget.Toast.makeText(getContext(), "Tính năng yêu thích dành cho khách hàng", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupExploreButton(View v) {
        View btnExplore = v.findViewById(R.id.btnExplore);
        btnExplore.setOnClickListener(view -> {
            if (getActivity() != null) {
                // Chuyển sang tab Inventory (Search)
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                        getActivity().findViewById(R.id.bottom_nav);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_inventory);
                }
            }
        });
    }

    // Khởi tạo ViewModel để lấy dữ liệu.
    private void initViewModels() {
        motorcycleViewModel = new ViewModelProvider(this).get(MotorcycleViewModel.class);
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);
    }

    private void setupManagementSummary(View v) {
        View layout = v.findViewById(R.id.layoutManagementSummary);
        if (UserSession.ROLE_ADMIN.equals(session.getUserRole()) || UserSession.ROLE_SALES.equals(session.getUserRole())) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    // Quan sát và cập nhật dữ liệu từ ViewModel vào giao diện.
    private void observeData() {
        motorcycleViewModel.allMotorcycles.observe(getViewLifecycleOwner(), list -> {
            if (list == null) return;
            
            List<Motorcycle> displayList = new ArrayList<>();
            for (Motorcycle m : list) {
                if (m.quantity > 0) displayList.add(m);
            }

            if (!displayList.isEmpty()) {
                List<Motorcycle> mostSearched = new ArrayList<>(displayList);
                Collections.shuffle(mostSearched);
                mostSearchedAdapter.setBikes(mostSearched.subList(0, Math.min(5, mostSearched.size())));

                List<Motorcycle> recommended = new ArrayList<>(displayList);
                Collections.shuffle(recommended);
                recommendedAdapter.setBikes(recommended.subList(0, Math.min(5, recommended.size())));
            }
        });

        salesViewModel.totalRevenue.observe(getViewLifecycleOwner(), total -> {
            TextView tvRevenue = getView().findViewById(R.id.tvTotalRevenue);
            if (tvRevenue != null && total != null) tvRevenue.setText(CurrencyFormatter.format(total));
        });

        salesViewModel.completedCount.observe(getViewLifecycleOwner(), count -> {
            TextView tvCount = getView().findViewById(R.id.tvOrderCount);
            if (tvCount != null && count != null) tvCount.setText(String.valueOf(count));
        });

        motorcycleViewModel.totalStock.observe(getViewLifecycleOwner(), stock -> {
            TextView tvStock = getView().findViewById(R.id.tvTotalStock);
            if (tvStock != null && stock != null) tvStock.setText(String.valueOf(stock));
        });

        repairViewModel.inProgressCount.observe(getViewLifecycleOwner(), count -> {
            TextView tvRepair = getView().findViewById(R.id.tvRepairCount);
            if (tvRepair != null && count != null) tvRepair.setText(String.valueOf(count));
        });

        salesViewModel.revenueByStaff.observe(getViewLifecycleOwner(), map -> {
            TextView tvLeaderboard = getView().findViewById(R.id.tvStaffLeaderboard);
            if (tvLeaderboard != null && map != null) {
                StringBuilder sb = new StringBuilder();
                List<String> staffNames = new ArrayList<>(map.keySet());
                Collections.sort(staffNames, (s1, s2) -> map.get(s2).compareTo(map.get(s1)));

                for (int i = 0; i < Math.min(3, staffNames.size()); i++) {
                    String name = staffNames.get(i);
                    sb.append((i + 1)).append(". ").append(name)
                      .append(": ").append(CurrencyFormatter.format(map.get(name))).append("\n");
                }
                tvLeaderboard.setText(sb.toString().trim());
            }
        });
    }

    // Mở màn hình chi tiết xe.
    private void openBikeDetail(Motorcycle bike) {
        if (bike == null || bike.documentId == null) return;
        Intent intent = new Intent(getContext(), MotorcycleDetailActivity.class);
        intent.putExtra("MOTORCYCLE_DOC_ID", bike.documentId);
        startActivity(intent);
    }

    private void setupHeader(View view) {
        View btnNotif = view.findViewById(R.id.btnNotification);
        if (btnNotif != null) {
            btnNotif.setOnClickListener(v -> {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Thông báo")
                        .setMessage("Tính năng thông báo đang được phát triển.")
                        .setPositiveButton("Đã hiểu", null)
                        .show();
            });
        }
    }
}
