package com.example.motoshop.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.motoshop.R;
import com.example.motoshop.ui.sales.CreateSaleActivity;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.MotorcycleViewModel;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.example.motoshop.viewmodel.SalesViewModel;

public class DashboardFragment extends Fragment {

    private SalesViewModel salesViewModel;
    private MotorcycleViewModel motorcycleViewModel;
    private RepairViewModel repairViewModel;
    private UserSession session;

    private TextView tvTotalRevenue, tvTotalStock, tvOrdersToday, tvRepairsActive, tvWelcome, tvShopNameDisplay;
    private View cardRevenue, cardStock, cardOrders, cardRepairs, layoutQuickActions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new UserSession(requireContext());

        initViews(view);
        setupQuickActions(view);
        applyRolePermissions();
        setupShopName();
        initViewModels();
        observeData();
    }

    private void initViews(View v) {
        tvWelcome = v.findViewById(R.id.tvWelcome);
        tvShopNameDisplay = v.findViewById(R.id.tvShopNameDisplay);
        tvTotalRevenue = v.findViewById(R.id.tvTotalRevenue);
        tvTotalStock = v.findViewById(R.id.tvTotalStock);
        tvOrdersToday = v.findViewById(R.id.tvOrdersToday);
        tvRepairsActive = v.findViewById(R.id.tvRepairsActive);

        cardRevenue = v.findViewById(R.id.cardRevenue);
        cardStock = v.findViewById(R.id.cardStock);
        cardOrders = v.findViewById(R.id.cardOrders);
        cardRepairs = v.findViewById(R.id.cardRepairs);
        layoutQuickActions = v.findViewById(R.id.layoutQuickActions);
    }

    private void setupQuickActions(View v) {
        View btnSale = v.findViewById(R.id.btnQuickAddSale);
        if (btnSale != null) btnSale.setOnClickListener(view -> startActivity(new Intent(requireContext(), CreateSaleActivity.class)));

        View btnCust = v.findViewById(R.id.btnQuickAddCustomer);
        if (btnCust != null) btnCust.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_customer));

        View btnInv = v.findViewById(R.id.btnQuickInventory);
        if (btnInv != null) btnInv.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_inventory));

        View btnAi = v.findViewById(R.id.btnQuickAi);
        if (btnAi != null) btnAi.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_ai));
    }

    private void applyRolePermissions() {
        String role = session.getUserRole();
        tvWelcome.setText("Chào buổi sáng, " + session.getUserName());

        cardRevenue.setVisibility(View.GONE);
        cardStock.setVisibility(View.GONE);
        cardOrders.setVisibility(View.GONE);
        cardRepairs.setVisibility(View.GONE);
        
        if (layoutQuickActions != null) layoutQuickActions.setVisibility(View.VISIBLE);

        if (UserSession.ROLE_ADMIN.equals(role)) {
            cardRevenue.setVisibility(View.VISIBLE);
            cardStock.setVisibility(View.VISIBLE);
            cardOrders.setVisibility(View.VISIBLE);
            cardRepairs.setVisibility(View.VISIBLE);
        } else if (UserSession.ROLE_SALES.equals(role)) {
            cardStock.setVisibility(View.VISIBLE);
            cardOrders.setVisibility(View.VISIBLE);
        } else if (UserSession.ROLE_TECHNICIAN.equals(role)) {
            cardRepairs.setVisibility(View.VISIBLE);
            if (layoutQuickActions != null) layoutQuickActions.setVisibility(View.GONE);
        }
    }

    private void setupShopName() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MotoShopPrefs", Context.MODE_PRIVATE);
        String shopName = prefs.getString("shop_name", "MotoShop");
        tvShopNameDisplay.setText(shopName);
    }

    private void initViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);
        salesViewModel = provider.get(SalesViewModel.class);
        motorcycleViewModel = provider.get(MotorcycleViewModel.class);
        repairViewModel = provider.get(RepairViewModel.class);
    }

    private void observeData() {
        salesViewModel.totalRevenue.observe(getViewLifecycleOwner(), revenue -> {
            if (cardRevenue.getVisibility() == View.VISIBLE)
                tvTotalRevenue.setText(CurrencyFormatter.format(revenue != null ? revenue : 0.0));
        });

        motorcycleViewModel.totalStock.observe(getViewLifecycleOwner(), stock -> {
            if (cardStock.getVisibility() == View.VISIBLE)
                tvTotalStock.setText(String.valueOf(stock != null ? stock : 0));
        });

        salesViewModel.completedCount.observe(getViewLifecycleOwner(), count -> {
            if (cardOrders.getVisibility() == View.VISIBLE)
                tvOrdersToday.setText(String.valueOf(count != null ? count : 0));
        });

        repairViewModel.inProgressCount.observe(getViewLifecycleOwner(), count -> {
            if (cardRepairs.getVisibility() == View.VISIBLE)
                tvRepairsActive.setText(String.valueOf(count != null ? count : 0));
        });
    }
}
