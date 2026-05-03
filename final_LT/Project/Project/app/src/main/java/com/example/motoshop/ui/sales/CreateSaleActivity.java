package com.example.motoshop.ui.sales;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Customer;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.data.model.SalesOrder;
import com.example.motoshop.data.model.SalesOrderItem;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.DateUtils;
import com.example.motoshop.utils.MoneyInputTextWatcher;
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.CustomerViewModel;
import com.example.motoshop.viewmodel.MotorcycleViewModel;
import com.example.motoshop.viewmodel.SalesViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class CreateSaleActivity extends AppCompatActivity {

    private SalesViewModel salesViewModel;
    private MotorcycleViewModel motorViewModel;
    private CustomerViewModel customerViewModel;
    private UserSession session;

    private AutoCompleteTextView spinnerCustomer, spinnerPaymentMethod;
    private RecyclerView rvSelectedVehicles;
    private TextInputEditText etDiscount, etNote;
    private TextView tvTotalAmount, tvDiscountDisplay, tvFinalAmount, tvDiscountInWords;
    private MaterialButton btnAddVehicle, btnConfirm;

    private SelectedMotorAdapter adapter;
    private List<Customer> customerList = new ArrayList<>();
    private List<Motorcycle> inventoryList = new ArrayList<>();
    private List<SalesOrderItem> selectedItems = new ArrayList<>();
    private Customer selectedCustomer;

    private double totalAmount = 0;
    private double discount = 0;
    private double finalAmount = 0;

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sale);

        initViewModels();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSpinners();
        observeData();
        session = new UserSession(this);

        btnAddVehicle.setOnClickListener(v -> showAddVehicleDialog());
        btnConfirm.setOnClickListener(v -> confirmOrder());

        etDiscount.addTextChangedListener(new MoneyInputTextWatcher(etDiscount, tvDiscountInWords) {
            // Xử lý lại dữ liệu sau khi người dùng nhập xong.
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                calculateSummary();
            }
        });
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void initViewModels() {
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);
        motorViewModel = new ViewModelProvider(this).get(MotorcycleViewModel.class);
        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void initViews() {
        spinnerCustomer = findViewById(R.id.spinnerCustomer);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        rvSelectedVehicles = findViewById(R.id.rvSelectedVehicles);
        etDiscount = findViewById(R.id.etDiscount);
        etNote = findViewById(R.id.etNote);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvDiscountDisplay = findViewById(R.id.tvDiscountDisplay);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        tvDiscountInWords = findViewById(R.id.tvDiscountInWords);
        btnAddVehicle = findViewById(R.id.btnAddVehicle);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.create_sale_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Chuẩn bị RecyclerView và adapter để hiển thị danh sách.
    private void setupRecyclerView() {
        adapter = new SelectedMotorAdapter();
        rvSelectedVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvSelectedVehicles.setAdapter(adapter);
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupSpinners() {
        String[] paymentMethods = {"Tiền mặt", "Chuyển khoản", "Trả góp"};
        ArrayAdapter<String> pmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, paymentMethods);
        spinnerPaymentMethod.setAdapter(pmAdapter);
        spinnerPaymentMethod.setText(paymentMethods[0], false);
    }

    // Lấy dữ liệu cần thiết và đưa lên giao diện.
    private void observeData() {
        customerViewModel.allCustomers.observe(this, list -> {
            if (list != null) {
                customerList = list;
                List<String> names = new ArrayList<>();
                for (Customer c : list) names.add(c.name + " (" + c.phone + ")");
                ArrayAdapter<String> cAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
                spinnerCustomer.setAdapter(cAdapter);
            }
        });

        spinnerCustomer.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < customerList.size()) {
                selectedCustomer = customerList.get(position);
            }
        });

        motorViewModel.allMotorcycles.observe(this, list -> {
            if (list != null) inventoryList = list;
        });
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showAddVehicleDialog() {
        List<Motorcycle> available = new ArrayList<>();
        for (Motorcycle m : inventoryList) {
            if (m.quantity > 0) available.add(m);
        }

        if (available.isEmpty()) {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] motorNames = new String[available.size()];
        for (int i = 0; i < available.size(); i++) {
            Motorcycle m = available.get(i);
            motorNames[i] = m.brand + " " + m.model + " (" + m.color + ") - Kho: " + m.quantity;
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn xe từ kho")
                .setItems(motorNames, (dialog, which) -> {
                    addVehicleToOrder(available.get(which));
                })
                .show();
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private void addVehicleToOrder(Motorcycle m) {
        if (m == null) return;

        for (SalesOrderItem item : selectedItems) {
            if (item.motorcycleDocumentId.equals(m.documentId)) {
                if (item.quantity < m.quantity) {
                    item.quantity++;
                    item.subtotal = item.quantity * item.unitPrice;
                    adapter.notifyDataSetChanged();
                    calculateSummary();
                } else {
                    Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        SalesOrderItem item = new SalesOrderItem();
        item.motorcycleDocumentId = m.documentId;
        item.motorcycleName = m.brand + " " + m.model;
        item.quantity = 1;
        item.unitPrice = m.price;
        item.subtotal = m.price;
        selectedItems.add(item);
        adapter.notifyDataSetChanged();
        calculateSummary();
    }

    // Tính tổng tiền, giảm giá và số tiền cuối cùng của đơn bán.
    private void calculateSummary() {
        totalAmount = 0;
        for (SalesOrderItem item : selectedItems) {
            totalAmount += item.subtotal;
        }

        discount = MoneyInputTextWatcher.getRawValue(etDiscount);
        finalAmount = Math.max(0, totalAmount - discount);

        tvTotalAmount.setText(CurrencyFormatter.format(totalAmount));
        tvDiscountDisplay.setText("-" + CurrencyFormatter.format(discount));
        tvFinalAmount.setText(CurrencyFormatter.format(finalAmount));
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private void confirmOrder() {
        if (selectedCustomer == null) {
            spinnerCustomer.setError(getString(R.string.err_select_customer));
            Toast.makeText(this, R.string.err_select_customer, Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.err_select_vehicle, Toast.LENGTH_SHORT).show();
            return;
        }

        SalesOrder order = new SalesOrder();
        order.orderCode = DateUtils.generateCode("DH");
        order.customerDocumentId = selectedCustomer.documentId;
        order.customerName = selectedCustomer.name;
        order.totalAmount = totalAmount;
        order.discount = discount;
        order.finalAmount = finalAmount;
        order.paymentMethod = spinnerPaymentMethod.getText().toString();
        order.status = "PROCESSING"; // Luôn bắt đầu bằng PROCESSING
        order.orderDate = System.currentTimeMillis();
        order.note = etNote.getText().toString().trim();
        order.items = new ArrayList<>(selectedItems);
        order.createdByStaffId   = session.getUserId();
        order.createdByStaffName = session.getUserName();

        // Hiển thị loading/vô hiệu hóa nút để tránh click nhiều lần
        btnConfirm.setEnabled(false);

        salesViewModel.createOrder(order, new SalesViewModel.OrderCallback() {
            // Xử lý khi thao tác bất đồng bộ trả về thành công.
            @Override
            public void onSuccess() {
                Toast.makeText(CreateSaleActivity.this, R.string.sale_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            // Xử lý khi thao tác bất đồng bộ bị lỗi.
            @Override
            public void onError(String message) {
                btnConfirm.setEnabled(true);
                Toast.makeText(CreateSaleActivity.this, "Lỗi tạo đơn hàng: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Adapter dùng để đưa dữ liệu lên RecyclerView.
    private class SelectedMotorAdapter extends RecyclerView.Adapter<SelectedMotorAdapter.ViewHolder> {
        // Tạo view cho từng item trong RecyclerView.
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_motorcycle, parent, false);
            return new ViewHolder(view);
        }

        // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SalesOrderItem item = selectedItems.get(position);
            holder.tvName.setText(item.motorcycleName);
            holder.tvDetails.setText("SL: " + item.quantity + " x " + CurrencyFormatter.format(item.unitPrice));
            holder.tvSubtotal.setText(CurrencyFormatter.format(item.subtotal));
            holder.btnRemove.setOnClickListener(v -> {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    selectedItems.remove(adapterPos);
                    notifyItemRemoved(adapterPos);
                    notifyItemRangeChanged(adapterPos, selectedItems.size());
                    calculateSummary();
                }
            });
        }

        // Trả về số lượng item đang có trong danh sách.
        @Override
        public int getItemCount() { return selectedItems.size(); }

        // ViewHolder giữ các view của một item để RecyclerView dùng lại.
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDetails, tvSubtotal;
            ImageButton btnRemove;
            ViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvMotorName);
                tvDetails = v.findViewById(R.id.tvDetails);
                tvSubtotal = v.findViewById(R.id.tvSubtotal);
                btnRemove = v.findViewById(R.id.btnRemove);
            }
        }
    }
}
