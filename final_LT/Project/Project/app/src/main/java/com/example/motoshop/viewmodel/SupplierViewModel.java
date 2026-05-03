package com.example.motoshop.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.motoshop.data.model.Supplier;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;

// ViewModel quản lý dữ liệu và cập nhật cho màn hình khi Firebase thay đổi.
public class SupplierViewModel extends BaseViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Supplier>> _suppliers = new MutableLiveData<>();
    public final LiveData<List<Supplier>> allSuppliers = _suppliers;

    // Constructor khởi tạo object của class này.
    public SupplierViewModel(@NonNull Application app) {
        super(app);
        listenToSuppliers();
    }

    // Lấy hoặc lắng nghe dữ liệu từ Firebase để cập nhật giao diện.
    private void listenToSuppliers() {
        registerListener(db.collection("suppliers")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("SupplierViewModel", "Listen failed.", error);
                        return;
                    }
                    if (value != null) {
                        _suppliers.setValue(value.toObjects(Supplier.class));
                    }
                }));
    }

    public void insert(Supplier s) {
        s.createdAt = System.currentTimeMillis();
        if (s.documentId == null || s.documentId.isEmpty()) {
            if (s.name != null && !s.name.trim().isEmpty()) {
                String safeName = s.name.replaceAll("[^a-zA-Z0-9À-ỹ\\s]", "").trim().replaceAll("\\s+", "_").toUpperCase();
                safeName = com.example.motoshop.utils.VNCharacterUtils.removeAccents(safeName);
                if (safeName.contains("HONDA")) safeName = "HONDA";
                else if (safeName.contains("YAMAHA")) safeName = "YAMAHA";
                else if (safeName.contains("SUZUKI")) safeName = "SUZUKI";
                else if (safeName.contains("PIAGGIO")) safeName = "PIAGGIO";
                else if (safeName.contains("SYM")) safeName = "SYM";
                
                s.documentId = "NCC_" + safeName + "_VN";
            } else {
                s.documentId = "NCC_" + System.currentTimeMillis() + "_VN";
            }
        }
        db.collection("suppliers").document(s.documentId).set(s)
                .addOnFailureListener(e -> _errorMessage.postValue("Lỗi thêm: " + e.getMessage()));
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public void update(Supplier s) {
        if (s.documentId != null) {
            db.collection("suppliers").document(s.documentId).set(s)
                    .addOnFailureListener(e -> _errorMessage.postValue("Lỗi cập nhật: " + e.getMessage()));
        }
    }

    // Xóa hoặc bỏ dữ liệu theo thao tác của người dùng.
    public void delete(Supplier s) {
        if (s.documentId != null) {
            db.collection("suppliers").document(s.documentId).delete()
                    .addOnFailureListener(e -> _errorMessage.postValue("Lỗi xóa: " + e.getMessage()));
        }
    }
}
