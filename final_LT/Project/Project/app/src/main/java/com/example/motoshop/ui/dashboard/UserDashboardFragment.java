package com.example.motoshop.ui.dashboard;

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
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.SalesViewModel;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.example.motoshop.ui.sales.SalesOrderAdapter;
import com.example.motoshop.ui.repair.RepairOrderAdapter;

public class UserDashboardFragment extends Fragment {

    private UserSession session;
    private SalesViewModel salesViewModel;
    private RepairViewModel repairViewModel;
    
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
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);

        tvWelcome = view.findViewById(R.id.tvWelcomeUser);
        tvRank = view.findViewById(R.id.tvMemberRank);
        rvOrders = view.findViewById(R.id.rvRecentOrders);
        rvRepairs = view.findViewById(R.id.rvMyRepairs);

        tvWelcome.setText("Chào bạn, " + session.getUserName() + "!");
        
        setupRecyclerViews();
        observeData();
    }

    private void setupRecyclerViews() {
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRepairs.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Use existing adapters
        SalesOrderAdapter salesAdapter = new SalesOrderAdapter(null);
        rvOrders.setAdapter(salesAdapter);

        RepairOrderAdapter repairAdapter = new RepairOrderAdapter();
        rvRepairs.setAdapter(repairAdapter);
    }

    private void observeData() {
        String customerId = session.getCustomerDocId();
        if (customerId == null) return;

        // Fetch Customer Profile for Rank & Points
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("customers").document(customerId)
            .addSnapshotListener((snapshot, e) -> {
                if (snapshot != null && snapshot.exists()) {
                    com.example.motoshop.data.model.Customer customer = snapshot.toObject(com.example.motoshop.data.model.Customer.class);
                    if (customer != null && tvRank != null) {
                        tvRank.setText("Hạng: " + (customer.memberRank != null ? customer.memberRank : "Standard") + 
                                      " | Điểm: " + customer.loyaltyPoints);
                    }
                }
            });

        salesViewModel.allOrders.observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) salesAdapterFilter(orders);
        });

        repairViewModel.allRepairs.observe(getViewLifecycleOwner(), repairs -> {
            if (repairs != null) repairAdapterFilter(repairs);
        });
    }

    private void salesAdapterFilter(java.util.List<com.example.motoshop.data.model.SalesOrder> list) {
        String cid = session.getCustomerDocId();
        java.util.List<com.example.motoshop.data.model.SalesOrder> filtered = new java.util.ArrayList<>();
        for (com.example.motoshop.data.model.SalesOrder o : list) {
            if (cid.equals(o.customerDocumentId)) filtered.add(o);
        }
        ((SalesOrderAdapter)rvOrders.getAdapter()).setOrders(filtered);
    }

    private void repairAdapterFilter(java.util.List<com.example.motoshop.data.model.RepairOrder> list) {
        String cid = session.getCustomerDocId();
        java.util.List<com.example.motoshop.data.model.RepairOrder> filtered = new java.util.ArrayList<>();
        for (com.example.motoshop.data.model.RepairOrder r : list) {
            if (cid.equals(r.customerDocumentId)) filtered.add(r);
        }
        ((RepairOrderAdapter)rvRepairs.getAdapter()).setRepairs(filtered);
    }
}
