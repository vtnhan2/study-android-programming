package com.example.motoshop.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.ui.inventory.MotorcycleDetailActivity;
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.MotorcycleViewModel;
import com.example.motoshop.viewmodel.SalesViewModel;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.example.motoshop.ui.sales.SalesOrderAdapter;
import com.example.motoshop.ui.repair.RepairOrderAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDashboardFragment extends Fragment {

    private UserSession session;
    private MotorcycleViewModel motorcycleViewModel;
    private SalesViewModel salesViewModel;
    private RepairViewModel repairViewModel;
    private BikeCardAdapter mostSearchedAdapter;
    private BikeCardAdapter recommendedAdapter;

    private TextView tvWelcome, tvRank;
    private RecyclerView rvVehicles, rvOrders, rvRepairs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new UserSession(requireContext());
        motorcycleViewModel = new ViewModelProvider(this).get(MotorcycleViewModel.class);
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);

        tvWelcome = view.findViewById(R.id.tvWelcomeUser);
        tvRank = view.findViewById(R.id.tvMemberRank);
        rvOrders = view.findViewById(R.id.rvRecentOrders);
        rvRepairs = view.findViewById(R.id.rvMyRepairs);

        tvWelcome.setText("Chào bạn, " + session.getUserName() + "!");

        View btnDrawerMenu = view.findViewById(R.id.btnDrawerMenu);
        if (btnDrawerMenu != null) {
            btnDrawerMenu.setOnClickListener(v -> {
                if (getActivity() instanceof com.example.motoshop.ui.main.MainActivity) {
                    ((com.example.motoshop.ui.main.MainActivity) getActivity()).openDrawer();
                }
            });
        }

        setupBikeRecyclerViews(view);
        setupOrderRecyclerViews();
        setupSeeAll(view);
        observeData();
    }

    private void setupBikeRecyclerViews(View v) {
        RecyclerView rvMostSearched = v.findViewById(R.id.rvMostSearched);
        mostSearchedAdapter = new BikeCardAdapter();
        rvMostSearched.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMostSearched.setAdapter(mostSearchedAdapter);
        mostSearchedAdapter.setOnBikeClickListener(this::openBikeDetail);

        RecyclerView rvRecommended = v.findViewById(R.id.rvRecommended);
        recommendedAdapter = new BikeCardAdapter();
        rvRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRecommended.setAdapter(recommendedAdapter);
        recommendedAdapter.setOnBikeClickListener(this::openBikeDetail);
    }

    private void setupOrderRecyclerViews() {
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRepairs.setLayoutManager(new LinearLayoutManager(getContext()));

        SalesOrderAdapter salesAdapter = new SalesOrderAdapter(null);
        rvOrders.setAdapter(salesAdapter);

        RepairOrderAdapter repairAdapter = new RepairOrderAdapter();
        rvRepairs.setAdapter(repairAdapter);
    }

    private void setupSeeAll(View v) {
        TextView tvSeeAll = v.findViewById(R.id.tvSeeAllBikes);
        tvSeeAll.setOnClickListener(view -> {
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                        getActivity().findViewById(R.id.bottom_nav);
                if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_inventory);
            }
        });
    }

    private void observeData() {
        motorcycleViewModel.allMotorcycles.observe(getViewLifecycleOwner(), list -> {
            if (list == null) return;
            List<Motorcycle> inStock = new ArrayList<>();
            for (Motorcycle m : list) {
                if (m.quantity > 0) inStock.add(m);
            }
            if (!inStock.isEmpty()) {
                List<Motorcycle> shuffled1 = new ArrayList<>(inStock);
                Collections.shuffle(shuffled1);
                mostSearchedAdapter.setBikes(shuffled1.subList(0, Math.min(5, shuffled1.size())));

                List<Motorcycle> shuffled2 = new ArrayList<>(inStock);
                Collections.shuffle(shuffled2);
                recommendedAdapter.setBikes(shuffled2.subList(0, Math.min(5, shuffled2.size())));
            }
        });

        String customerId = session.getCustomerDocId();
        if (customerId == null) return;

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("customers").document(customerId)
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        com.example.motoshop.data.model.Customer customer =
                                snapshot.toObject(com.example.motoshop.data.model.Customer.class);
                        if (customer != null && tvRank != null) {
                            tvRank.setText("Hạng: " + translateRank(customer.memberRank)
                                    + " | Điểm: " + customer.loyaltyPoints);
                        }
                    }
                });

        salesViewModel.getOrdersByCustomer(customerId).observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) salesAdapterFilter(orders);
        });

        repairViewModel.getRepairsByCustomer(customerId).observe(getViewLifecycleOwner(), repairs -> {
            if (repairs != null) repairAdapterFilter(repairs);
        });
    }

    private String translateRank(String rank) {
        if (rank == null || rank.equals("NORMAL")) return "Thành viên";
        switch (rank) {
            case "SILVER": return "Hạng Bạc";
            case "GOLD": return "Hạng Vàng";
            case "VIP": return "Hạng VIP";
            default: return rank;
        }
    }

    private void salesAdapterFilter(java.util.List<com.example.motoshop.data.model.SalesOrder> list) {
        String cid = session.getCustomerDocId();
        if (cid == null) return;
        java.util.List<com.example.motoshop.data.model.SalesOrder> filtered = new java.util.ArrayList<>();
        for (com.example.motoshop.data.model.SalesOrder o : list) {
            if (cid.equals(o.customerDocumentId)) filtered.add(o);
        }
        ((SalesOrderAdapter) rvOrders.getAdapter()).setOrders(filtered);
    }

    private void repairAdapterFilter(java.util.List<com.example.motoshop.data.model.RepairOrder> list) {
        String cid = session.getCustomerDocId();
        if (cid == null) return;
        java.util.List<com.example.motoshop.data.model.RepairOrder> filtered = new java.util.ArrayList<>();
        for (com.example.motoshop.data.model.RepairOrder r : list) {
            if (cid.equals(r.customerDocumentId)) filtered.add(r);
        }
        ((RepairOrderAdapter) rvRepairs.getAdapter()).setRepairs(filtered);
    }

    private void openBikeDetail(Motorcycle bike) {
        if (bike == null || bike.documentId == null) return;
        Intent intent = new Intent(getContext(), MotorcycleDetailActivity.class);
        intent.putExtra("MOTORCYCLE_DOC_ID", bike.documentId);
        startActivity(intent);
    }
}
