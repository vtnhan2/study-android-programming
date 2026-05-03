package com.example.motoshop.ui.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.utils.MoneyInputTextWatcher;
import com.example.motoshop.utils.MotorcycleCatalog;
import com.example.motoshop.viewmodel.MotorcycleViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class AddEditMotorcycleActivity extends AppCompatActivity {

    private MotorcycleViewModel viewModel;
    private ImageView ivMotorcycle;
    private TextInputEditText etBrand, etModel, etColor, etYear, etPrice, etImportPrice, etQuantity, etDescription;
    private TextView tvPriceInWords, tvImportPriceInWords;
    private AutoCompleteTextView spinnerStatus;
    private Button btnSave;
    private MaterialButton btnSelectFromCatalog;

    private String brandParam, modelParam, colorParam;
    private int yearParam;
    private boolean isEditMode = false;
    private Uri selectedImageUri;
    private Motorcycle currentMotorcycle;

    private final String[] statusVn = {"Còn hàng", "Sắp hết", "Hết hàng"};
    private final String[] statusEn = {"IN_STOCK", "LOW_STOCK", "SOLD_OUT"};

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivMotorcycle.setImageURI(selectedImageUri);
                }
            }
    );

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_motorcycle);

        viewModel = new ViewModelProvider(this).get(MotorcycleViewModel.class);

        initViews();
        setupToolbar();
        setupStatusSpinner();
        setupMoneyFormatting();

        checkEditMode();

        ivMotorcycle.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSelectFromCatalog.setOnClickListener(v -> showBrandSelectionDialog());
        btnSave.setOnClickListener(v -> saveMotorcycle());
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void initViews() {
        ivMotorcycle = findViewById(R.id.ivMotorcycle);
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etColor = findViewById(R.id.etColor);
        etYear = findViewById(R.id.etYear);
        etPrice = findViewById(R.id.etPrice);
        etImportPrice = findViewById(R.id.etImportPrice);
        tvPriceInWords = findViewById(R.id.tvPriceInWords);
        tvImportPriceInWords = findViewById(R.id.tvImportPriceInWords);
        etQuantity = findViewById(R.id.etQuantity);
        etDescription = findViewById(R.id.etDescription);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSave = findViewById(R.id.btnSave);
        btnSelectFromCatalog = findViewById(R.id.btnSelectFromCatalog);
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupMoneyFormatting() {
        etPrice.addTextChangedListener(new MoneyInputTextWatcher(etPrice, tvPriceInWords));
        etImportPrice.addTextChangedListener(new MoneyInputTextWatcher(etImportPrice, tvImportPriceInWords));
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupStatusSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusVn);
        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setText(statusVn[0], false);
    }

    // Kiểm tra dữ liệu trước khi tiếp tục xử lý.
    private void checkEditMode() {
        brandParam = getIntent().getStringExtra("EXTRA_BRAND");
        modelParam = getIntent().getStringExtra("EXTRA_MODEL");

        if (brandParam != null && modelParam != null) {
            isEditMode = true;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Chỉnh sửa xe");
            }
            loadMotorcycleData();
        }
    }

    // Lấy dữ liệu cần thiết và đưa lên giao diện.
    private void loadMotorcycleData() {
        // Lắng nghe dữ liệu từ LiveData để đảm bảo lấy được xe từ Firebase
        viewModel.allMotorcycles.observe(this, list -> {
            if (list != null) {
                for (Motorcycle m : list) {
                    if (m.brand.equals(brandParam) && m.model.equals(modelParam)) {
                        currentMotorcycle = m;
                        displayMotorcycle(m);
                        break;
                    }
                }
            }
        });
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void displayMotorcycle(Motorcycle m) {
        etBrand.setText(m.brand);
        etModel.setText(m.model);
        etColor.setText(m.color);
        etYear.setText(String.valueOf(m.year));
        etPrice.setText(formatPlain(m.price));
        etImportPrice.setText(formatPlain(m.importPrice));
        etQuantity.setText(String.valueOf(m.quantity));
        etDescription.setText(m.description);

        String currentVn = statusVn[0];
        for(int i=0; i<statusEn.length; i++) {
            if(statusEn[i].equals(m.status)) { currentVn = statusVn[i]; break; }
        }
        spinnerStatus.setText(currentVn, false);
        int resId = getMotorcycleImageResId(m.imageUri);
        if (resId != 0) {
            Glide.with(this).load(resId).into(ivMotorcycle);
        } else {
            ivMotorcycle.setImageResource(R.drawable.ic_inventory);
        }
    }

    // Xử lý hình ảnh cần hiển thị trên màn hình.
    private int getMotorcycleImageResId(String imageUri) {
        if (imageUri == null || imageUri.trim().isEmpty()) return 0;

        String exactName = imageUri.trim();
        int resId = getResources().getIdentifier(exactName, "drawable", getPackageName());
        if (resId != 0) return resId;

        String safeName = exactName.toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return getResources().getIdentifier(safeName, "drawable", getPackageName());
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    private void showBrandSelectionDialog() {
        List<Motorcycle> existing = viewModel.getAllSync();
        List<String> options = new ArrayList<>();
        options.add("--- CHỌN TỪ MẪU CÓ SẴN ---");
        for (Motorcycle m : existing) {
            options.add(m.brand + " " + m.model);
        }

        String[] optionsArray = options.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Nhập thêm xe")
                .setItems(optionsArray, (dialog, which) -> {
                    if (which > 0) applyExistingMotor(existing.get(which - 1));
                }).show();
    }

    private void applyExistingMotor(Motorcycle m) {
        currentMotorcycle = m;
        isEditMode = true;

        etBrand.setText(m.brand);
        etModel.setText(m.model);
        etColor.setText(m.color);
        etYear.setText(String.valueOf(m.year));
        etPrice.setText(formatPlain(m.price));
        etImportPrice.setText(formatPlain(m.importPrice));
        etDescription.setText(m.description);
        etQuantity.setText(String.valueOf(m.quantity));

        String currentVn = statusVn[0];
        for (int i = 0; i < statusEn.length; i++) {
            if (statusEn[i].equals(m.status)) {
                currentVn = statusVn[i];
                break;
            }
        }
        spinnerStatus.setText(currentVn, false);

        int resId = getMotorcycleImageResId(m.imageUri);
        if (resId != 0) {
            Glide.with(this).load(resId).into(ivMotorcycle);
        } else {
            ivMotorcycle.setImageResource(R.drawable.ic_inventory);
        }
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    private String formatPlain(double value) {
        return String.format(Locale.US, "%.0f", value);
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private void saveMotorcycle() {
        if (!validate()) return;

        Motorcycle m = isEditMode ? currentMotorcycle : new Motorcycle();
        m.brand = etBrand.getText().toString().trim();
        m.model = etModel.getText().toString().trim();
        m.color = etColor.getText().toString().trim();
        m.year = Integer.parseInt(etYear.getText().toString());
        m.price = MoneyInputTextWatcher.getRawValue(etPrice);
        m.importPrice = MoneyInputTextWatcher.getRawValue(etImportPrice);
        m.quantity = Integer.parseInt(etQuantity.getText().toString());
        m.description = etDescription.getText().toString().trim();

        String selectedVn = spinnerStatus.getText().toString();
        for(int i=0; i<statusVn.length; i++) {
            if(statusVn[i].equals(selectedVn)) { m.status = statusEn[i]; break; }
        }

        if (isEditMode) {
            viewModel.update(m);
            Toast.makeText(this, "Đã cập nhật lên Firebase Online", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(m);
            Toast.makeText(this, "Đã thêm xe mới lên Firebase Online", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // Kiểm tra dữ liệu trước khi tiếp tục xử lý.
    private boolean validate() {
        if (TextUtils.isEmpty(etBrand.getText())) { etBrand.setError("Nhập hãng xe"); return false; }
        if (TextUtils.isEmpty(etModel.getText())) { etModel.setError("Nhập model xe"); return false; }
        return true;
    }
}
