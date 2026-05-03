package com.example.motoshop.ui.repair;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Customer;
import com.example.motoshop.data.model.RepairOrder;
import com.example.motoshop.data.model.RepairService;
import com.example.motoshop.data.model.SalesOrderItem;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.DateUtils;
import com.example.motoshop.utils.GeminiHelper;
import com.example.motoshop.utils.MoneyInputTextWatcher;
import com.example.motoshop.viewmodel.CustomerViewModel;
import com.example.motoshop.viewmodel.RepairViewModel;
import com.example.motoshop.viewmodel.SalesViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình tạo phiếu sửa chữa với hỗ trợ phân tích lỗi bằng AI Gemini.
 */
public class CreateRepairActivity extends AppCompatActivity {

    private RepairViewModel repairViewModel;
    private CustomerViewModel customerViewModel;
    private SalesViewModel salesViewModel;

    private AutoCompleteTextView autoCompleteCustomer;
    private TextInputEditText etBrand, etModel, etLicensePlate, etIssueDescription;
    private TextView tvTotalCost;
    private MaterialButton btnSave, btnAiAnalyze, btnApprove, btnReject, btnAddManual;
    private ProgressBar pbAi;
    private View layoutDecision;
    private RecyclerView rvAiSuggestions, rvManualItems;

    private AiRepairSuggestionAdapter adapter;
    private ManualItemAdapter manualAdapter;
    private List<Customer> customerList = new ArrayList<>();
    private List<RepairService> allServices = new ArrayList<>();
    private List<ManualRepairItem> manualItems = new ArrayList<>();
    private Customer selectedCustomer;

    private double standardLabor = 0, standardParts = 0;
    private double manualTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repair);

        initViewModels();
        initViews();
        setupToolbar();
        setupCustomerAutoComplete();
        setupRecyclerViews();

        btnAiAnalyze.setOnClickListener(v -> performAiAnalysis());
        btnAddManual.setOnClickListener(v -> showAddManualItemDialog());
        btnApprove.setOnClickListener(v -> saveRepairOrder("IN_PROGRESS"));
        btnReject.setOnClickListener(v -> saveRepairOrder("CANCELLED"));
        btnSave.setOnClickListener(v -> saveRepairOrder("RECEIVED"));
    }

    private void initViewModels() {
        repairViewModel = new ViewModelProvider(this).get(RepairViewModel.class);
        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);

        repairViewModel.allServices.observe(this, services -> {
            if (services != null) {
                this.allServices = services;
                adapter.setSuggestions(services, null);
            }
        });
    }

    private void initViews() {
        autoCompleteCustomer = findViewById(R.id.autoCompleteCustomer);
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etLicensePlate = findViewById(R.id.etLicensePlate);
        etIssueDescription = findViewById(R.id.etIssueDescription);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        btnSave = findViewById(R.id.btnSave);
        btnAiAnalyze = findViewById(R.id.btnAiAnalyze);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        btnAddManual = findViewById(R.id.btnAddManual);
        pbAi = findViewById(R.id.pbAi);
        layoutDecision = findViewById(R.id.layoutDecision);
        rvAiSuggestions = findViewById(R.id.rvAiSuggestions);
        rvManualItems = findViewById(R.id.rvManualItems);
    }

    private void setupRecyclerViews() {
        adapter = new AiRepairSuggestionAdapter();
        rvAiSuggestions.setLayoutManager(new LinearLayoutManager(this));
        rvAiSuggestions.setAdapter(adapter);
        adapter.setOnSelectionChangeListener((labor, parts) -> {
            standardLabor = labor;
            standardParts = parts;
            updateTotalUI();
        });

        manualAdapter = new ManualItemAdapter();
        rvManualItems.setLayoutManager(new LinearLayoutManager(this));
        rvManualItems.setAdapter(manualAdapter);
    }

    private void showAddManualItemDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 30, 60, 0);

        TextInputLayout tilName = new TextInputLayout(this, null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        tilName.setHint("Tên hạng mục phát sinh");
        TextInputEditText etName = new TextInputEditText(tilName.getContext());
        tilName.addView(etName);
        layout.addView(tilName);

        TextInputLayout tilPrice = new TextInputLayout(this, null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        tilPrice.setHint("Thành tiền (VNĐ)");
        tilPrice.setPadding(0, 20, 0, 0);
        TextInputEditText etPrice = new TextInputEditText(tilPrice.getContext());
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        tilPrice.addView(etPrice);
        layout.addView(tilPrice);

        TextView tvWords = new TextView(this);
        tvWords.setPadding(10, 10, 10, 30);
        tvWords.setTextSize(12);
        tvWords.setTextColor(ContextCompat.getColor(this, R.color.gray_dark));
        layout.addView(tvWords);

        etPrice.addTextChangedListener(new MoneyInputTextWatcher(etPrice, tvWords));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Thêm hạng mục phát sinh")
                .setView(layout)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    double price = MoneyInputTextWatcher.getRawValue(etPrice);
                    if (!TextUtils.isEmpty(name) && price > 0) {
                        manualItems.add(new ManualRepairItem(name, price));
                        manualAdapter.notifyDataSetChanged();
                        updateTotalUI();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateTotalUI() {
        manualTotal = 0;
        for (ManualRepairItem item : manualItems) manualTotal += item.price;
        double total = standardLabor + standardParts + manualTotal;
        tvTotalCost.setText(CurrencyFormatter.format(total));
        boolean hasItems = (adapter != null && adapter.hasSelection()) || !manualItems.isEmpty();
        layoutDecision.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(hasItems ? View.GONE : View.VISIBLE);
    }

    private void performAiAnalysis() {
        String description = etIssueDescription.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            etIssueDescription.setError("Nhập mô tả lỗi để AI phân tích");
            return;
        }
        if (allServices.isEmpty()) {
            Toast.makeText(this, "Đang tải dữ liệu dịch vụ...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        pbAi.setVisibility(View.VISIBLE);
        btnAiAnalyze.setEnabled(false);
        
        List<RepairService> minimalServices = new ArrayList<>();
        for (RepairService s : allServices) {
            RepairService min = new RepairService();
            min.documentId = s.documentId;
            min.name = s.name;
            min.description = s.description;
            minimalServices.add(min);
        }
        
        String servicesJson = new Gson().toJson(minimalServices);
        GeminiHelper.suggestRepairs(description, servicesJson, new GeminiHelper.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    pbAi.setVisibility(View.GONE);
                    btnAiAnalyze.setEnabled(true);
                    try {
                        String clean = response.trim();
                        int start = clean.indexOf("[");
                        int end = clean.lastIndexOf("]");
                        if (start != -1 && end != -1) {
                            clean = clean.substring(start, end + 1);
                            List<String> recommendedDocIds = new Gson().fromJson(clean, new TypeToken<List<String>>(){}.getType());
                            if (recommendedDocIds != null && !recommendedDocIds.isEmpty()) {
                                adapter.setSuggestions(allServices, recommendedDocIds);
                                rvAiSuggestions.scrollToPosition(0);
                                Toast.makeText(CreateRepairActivity.this, "AI đã gợi ý các dịch vụ phù hợp", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        suggestByKeywords(description);
                    } catch (Exception e) {
                        Log.e("AI_REPAIR", "Error parsing AI response: " + response, e);
                        suggestByKeywords(description);
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("AI_REPAIR", "AI Error: " + error);
                    suggestByKeywords(description);
                });
            }
        });
    }

    private void suggestByKeywords(String description) {
        pbAi.setVisibility(View.GONE);
        btnAiAnalyze.setEnabled(true);
        List<String> suggestedIds = getRecommendedServiceIdsByKeywords(description);
        adapter.setSuggestions(allServices, suggestedIds);
        if (!suggestedIds.isEmpty()) {
            rvAiSuggestions.scrollToPosition(0);
            Toast.makeText(this, "Đã gợi ý dựa trên từ khóa quan trọng", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không tìm thấy dịch vụ tương ứng, vui lòng chọn thủ công", Toast.LENGTH_LONG).show();
        }
    }

    private List<String> getRecommendedServiceIdsByKeywords(String description) {
        List<String> recommended = new ArrayList<>();
        for (RepairService service : allServices) {
            if (isMatchingService(description, service)) {
                recommended.add(service.documentId);
            }
        }
        return recommended;
    }

    private boolean isMatchingService(String desc, RepairService service) {
        String d = desc.toLowerCase();
        String sName = service.name != null ? service.name.toLowerCase() : "";
        String sDesc = service.description != null ? service.description.toLowerCase() : "";
        
        if (containsAny(sName, "nhớt", "dầu") && containsAny(d, "nhớt", "dầu", "thay", "định kỳ")) return true;
        if (containsAny(sName, "phanh", "thắng") && containsAny(d, "phanh", "thắng", "không ăn", "kêu", "rít")) return true;
        if (containsAny(sName, "lốp", "vỏ") && containsAny(d, "lốp", "vỏ", "thủng", "xì", "săm", "bánh", "mòn")) return true;
        if (sName.contains("nồi") && containsAny(d, "nồi", "rung", "giật", "vệ sinh", "lá côn")) return true;
        if (containsAny(sName, "điện", "bình", "ắc quy", "đèn") && 
            containsAny(d, "điện", "đèn", "còi", "ắc quy", "bình", "sạc", "không nổ", "đề", "cháy")) return true;
        
        return sName.contains(d) || d.contains(sName) || sDesc.contains(d);
    }

    private boolean containsAny(String text, String... keywords) {
        if (text == null) return false;
        for (String keyword : keywords) if (text.contains(keyword)) return true;
        return false;
    }

    private void saveRepairOrder(String status) {
        String custName = autoCompleteCustomer.getText().toString().trim();
        String brand = etBrand.getText().toString().trim();
        if (TextUtils.isEmpty(custName) || TextUtils.isEmpty(brand)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin khách và xe", Toast.LENGTH_SHORT).show();
            return;
        }
        RepairOrder r = new RepairOrder();
        r.repairCode = DateUtils.generateCode("SC");
        r.customerDocumentId = selectedCustomer != null ? selectedCustomer.documentId : null;
        r.customerName = custName;
        r.motorcycleBrand = brand;
        r.motorcycleModel = etModel.getText().toString().trim();
        r.licensePlate = etLicensePlate.getText().toString().trim();
        r.issueDescription = etIssueDescription.getText().toString().trim();
        StringBuilder diag = new StringBuilder(adapter.getSelectedServicesText());
        for (ManualRepairItem item : manualItems) {
            if (diag.length() > 0) diag.append(", ");
            diag.append(item.name);
        }
        r.diagnosis = diag.toString();
        r.laborCost = standardLabor + manualTotal;
        r.partsCost = standardParts;
        r.totalCost = standardLabor + standardParts + manualTotal;
        r.status = status;
        r.receivedDate = System.currentTimeMillis();
        repairViewModel.insert(r);
        Toast.makeText(this, "Đã lưu phiếu sửa chữa", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupCustomerAutoComplete() {
        customerViewModel.allCustomers.observe(this, list -> {
            if (list != null) {
                customerList = list;
                List<String> names = new ArrayList<>();
                for (Customer c : list) names.add(c.name);
                autoCompleteCustomer.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names));
            }
        });
        autoCompleteCustomer.setOnItemClickListener((parent, view, position, id) -> {
            String name = (String) parent.getItemAtPosition(position);
            selectedCustomer = null;
            for (Customer c : customerList) if (c.name.equals(name)) { selectedCustomer = c; break; }
            resetVehicleInfo();
            if (selectedCustomer != null) loadPurchasedMotorcycles(selectedCustomer.documentId);
        });
    }

    private void resetVehicleInfo() { etBrand.setText(""); etModel.setText(""); etLicensePlate.setText(""); }

    private void loadPurchasedMotorcycles(String customerDocumentId) {
        salesViewModel.getPurchasedItemsByCustomer(customerDocumentId).observe(this, items -> {
            if (items != null && !items.isEmpty()) showMotorcycleSelectionDialog(items);
        });
    }

    private void showMotorcycleSelectionDialog(List<SalesOrderItem> items) {
        String[] bikeNames = new String[items.size()];
        for (int i = 0; i < items.size(); i++) bikeNames[i] = items.get(i).motorcycleName;
        new AlertDialog.Builder(this).setTitle("Chọn xe khách đã mua").setItems(bikeNames, (dialog, which) -> autoFillVehicleInfo(items.get(which))).setNegativeButton("Nhập tay", (dialog, which) -> resetVehicleInfo()).show();
    }

    private void autoFillVehicleInfo(SalesOrderItem item) {
        resetVehicleInfo();
        String fullName = item.motorcycleName;
        if (fullName.contains(" ")) {
            int firstSpace = fullName.indexOf(" ");
            etBrand.setText(fullName.substring(0, firstSpace).trim());
            etModel.setText(fullName.substring(firstSpace).trim());
        } else etBrand.setText(fullName);
        etLicensePlate.requestFocus();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo phiếu sửa chữa");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private static class ManualRepairItem {
        String name; double price;
        ManualRepairItem(String name, double price) { this.name = name; this.price = price; }
    }

    private class ManualItemAdapter extends RecyclerView.Adapter<ManualItemAdapter.ViewHolder> {
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_manual_repair_item, p, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int p) {
            ManualRepairItem item = manualItems.get(p);
            h.tvName.setText(item.name);
            h.tvPrice.setText(CurrencyFormatter.format(item.price));
            h.btnRemove.setOnClickListener(v -> {
                int pos = h.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    manualItems.remove(pos); notifyItemRemoved(pos); updateTotalUI();
                }
            });
        }
        @Override public int getItemCount() { return manualItems.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice; ImageButton btnRemove;
            ViewHolder(View v) { super(v); tvName = v.findViewById(R.id.tvManualName); tvPrice = v.findViewById(R.id.tvManualPrice); btnRemove = v.findViewById(R.id.btnRemoveManual); }
        }
    }
}
