package com.example.motoshop.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Customer;
import com.example.motoshop.viewmodel.CustomerViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

// Fragment hiển thị danh sách khách hàng - Đã sửa lỗi crash và đồng nhất điều hướng.
public class CustomerFragment extends Fragment {

    private CustomerViewModel viewModel;
    private CustomerAdapter adapter;
    private List<Customer> fullList = new ArrayList<>();
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        // Khởi tạo các thành phần giao diện một cách an toàn
        setupRecyclerView(view);
        setupSearchView(view);
        setupFAB(view);

        observeData();
    }

    private void setupRecyclerView(View v) {
        RecyclerView rv = v.findViewById(R.id.rvCustomers);
        if (rv == null) return;
        
        adapter = new CustomerAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(customer -> {
            if (customer != null && customer.documentId != null) {
                Intent intent = new Intent(getContext(), CustomerDetailActivity.class);
                intent.putExtra("CUSTOMER_DOC_ID", customer.documentId);
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
                    filter(newText);
                    return true;
                }
            });
        }
    }

    private void setupFAB(View v) {
        FloatingActionButton fab = v.findViewById(R.id.fabAddCustomer);
        if (fab != null) {
            fab.setOnClickListener(view -> showAddCustomerDialog());
        }
    }

    private void showAddCustomerDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_customer, null);

        TextInputLayout tilName = view.findViewById(R.id.tilName);
        TextInputLayout tilPhone = view.findViewById(R.id.tilPhone);
        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail);
        TextInputLayout tilAddress = view.findViewById(R.id.tilAddress);
        TextInputLayout tilIdCard = view.findViewById(R.id.tilIdCard);
        TextInputLayout tilNote = view.findViewById(R.id.tilNote);

        TextInputEditText etName = (TextInputEditText) tilName.getEditText();
        TextInputEditText etPhone = (TextInputEditText) tilPhone.getEditText();
        TextInputEditText etEmail = (TextInputEditText) tilEmail.getEditText();
        TextInputEditText etAddress = (TextInputEditText) tilAddress.getEditText();
        TextInputEditText etIdCard = (TextInputEditText) tilIdCard.getEditText();
        TextInputEditText etNote = (TextInputEditText) tilNote.getEditText();

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_customer_title)
                .setView(view)
                .setPositiveButton(R.string.btn_save, null)
                .setNegativeButton(R.string.btn_cancel, null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = getEditTextValue(etName);
            String phone = getEditTextValue(etPhone);
            String email = getEditTextValue(etEmail);
            String idCard = getEditTextValue(etIdCard);

            boolean isValid = true;
            if (TextUtils.isEmpty(name)) {
                tilName.setError("Vui lòng nhập họ tên");
                isValid = false;
            } else tilName.setError(null);

            if (TextUtils.isEmpty(phone)) {
                tilPhone.setError("Vui lòng nhập số điện thoại");
                isValid = false;
            } else if (!phone.matches("\\d{10,11}")) {
                tilPhone.setError("Số điện thoại không hợp lệ (10-11 số)");
                isValid = false;
            } else tilPhone.setError(null);

            if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Email không đúng định dạng");
                isValid = false;
            } else tilEmail.setError(null);

            if (!TextUtils.isEmpty(idCard) && !idCard.matches("\\d{9,12}")) {
                tilIdCard.setError("CCCD không hợp lệ (9-12 số)");
                isValid = false;
            } else tilIdCard.setError(null);

            if (!isValid) return;

            Customer c = new Customer();
            c.name = name;
            c.phone = phone;
            c.address = getEditTextValue(etAddress);
            c.email = email;
            c.idCard = idCard;
            c.note = getEditTextValue(etNote);

            viewModel.insert(c, new CustomerViewModel.OnActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), R.string.customer_added, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private String getEditTextValue(TextInputEditText et) {
        if (et == null) return "";
        Editable editable = et.getText();
        return editable != null ? editable.toString().trim() : "";
    }

    private void observeData() {
        if (viewModel != null) {
            viewModel.allCustomers.observe(getViewLifecycleOwner(), list -> {
                if (list != null) {
                    fullList = list;
                    adapter.setCustomers(list);
                    if (layoutEmpty != null) {
                        layoutEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
    }

    private void filter(String query) {
        if (query == null || adapter == null) return;
        List<Customer> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        for (Customer c : fullList) {
            if (c == null) continue;
            String name = (c.name != null) ? c.name.toLowerCase() : "";
            String phone = (c.phone != null) ? c.phone : "";
            if (name.contains(lowerQuery) || phone.contains(query)) {
                filtered.add(c);
            }
        }
        adapter.setCustomers(filtered);
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
