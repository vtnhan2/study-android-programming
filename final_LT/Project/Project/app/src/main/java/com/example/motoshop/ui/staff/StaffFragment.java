package com.example.motoshop.ui.staff;

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
import com.example.motoshop.viewmodel.StaffViewModel;

public class StaffFragment extends Fragment {

    private StaffViewModel viewModel;
    private StaffAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false); // Reuse fragment_inventory for simple list
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RecyclerView rv = view.findViewById(R.id.rvInventory); // Reuse ID from fragment_inventory
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StaffAdapter();
        rv.setAdapter(adapter);

        // Hide search and filters if not needed for now
        View searchView = view.findViewById(R.id.searchView);
        if (searchView != null) searchView.setVisibility(View.GONE);
        View filters = view.findViewById(R.id.scrollFilters);
        if (filters != null) filters.setVisibility(View.GONE);
        View fab = view.findViewById(R.id.fabAddMotorcycle);
        if (fab != null) fab.setVisibility(View.GONE);

        viewModel = new ViewModelProvider(this).get(StaffViewModel.class);
        viewModel.allStaff.observe(getViewLifecycleOwner(), list -> {
            adapter.setStaffList(list);
        });
    }
}
