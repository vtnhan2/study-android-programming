package com.example.motoshop.ui.supplier;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Supplier;
import com.example.motoshop.viewmodel.SupplierViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

// Fragment hiển thị một phần giao diện và xử lý dữ liệu cho màn hình này.
public class SupplierFragment extends Fragment {

    private SupplierViewModel viewModel;
    private SupplierAdapter adapter;
    private View layoutEmpty;

    // Tạo giao diện cho Fragment.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supplier, container, false);
    }

    // Ánh xạ view và chuẩn bị dữ liệu sau khi giao diện được tạo.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SupplierViewModel.class);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        setupRecyclerView(view);
        setupFAB(view);

        observeData();
    }

    // Chuẩn bị RecyclerView và adapter để hiển thị danh sách.
    private void setupRecyclerView(View v) {
        RecyclerView rv = v.findViewById(R.id.rvSuppliers);
        adapter = new SupplierAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupFAB(View v) {
        FloatingActionButton fab = v.findViewById(R.id.fabAddSupplier);
        fab.setOnClickListener(view -> showAddSupplierDialog());
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showAddSupplierDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 20, 48, 0);

        TextInputLayout tilName = createTextInputLayout("Tên nhà cung cấp (*)");
        TextInputEditText etName = (TextInputEditText) tilName.getEditText();
        layout.addView(tilName);

        TextInputLayout tilContact = createTextInputLayout("Người liên hệ");
        TextInputEditText etContact = (TextInputEditText) tilContact.getEditText();
        layout.addView(tilContact);

        TextInputLayout tilPhone = createTextInputLayout("Số điện thoại (*)");
        TextInputEditText etPhone = (TextInputEditText) tilPhone.getEditText();
        layout.addView(tilPhone);

        TextInputLayout tilEmail = createTextInputLayout("Email");
        TextInputEditText etEmail = (TextInputEditText) tilEmail.getEditText();
        layout.addView(tilEmail);

        TextInputLayout tilAddress = createTextInputLayout("Địa chỉ");
        TextInputEditText etAddress = (TextInputEditText) tilAddress.getEditText();
        layout.addView(tilAddress);

        TextInputLayout tilTax = createTextInputLayout("Mã số thuế");
        TextInputEditText etTax = (TextInputEditText) tilTax.getEditText();
        layout.addView(tilTax);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm nhà cung cấp")
                .setView(layout)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                        Toast.makeText(getContext(), "Vui lòng nhập tên và số điện thoại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Supplier s = new Supplier();
                    s.name = name;
                    s.contactPerson = etContact.getText().toString().trim();
                    s.phone = phone;
                    s.email = etEmail.getText().toString().trim();
                    s.address = etAddress.getText().toString().trim();
                    s.taxCode = etTax.getText().toString().trim();
                    s.createdAt = System.currentTimeMillis();

                    viewModel.insert(s);
                    Toast.makeText(getContext(), "Đã thêm nhà cung cấp", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private TextInputLayout createTextInputLayout(String hint) {
        TextInputLayout til = new TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        til.setHint(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        til.setLayoutParams(params);
        TextInputEditText et = new TextInputEditText(til.getContext());
        til.addView(et);
        return til;
    }

    // Lấy dữ liệu cần thiết và đưa lên giao diện.
    private void observeData() {
        viewModel.allSuppliers.observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                adapter.setSuppliers(list);
                layoutEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }
}
