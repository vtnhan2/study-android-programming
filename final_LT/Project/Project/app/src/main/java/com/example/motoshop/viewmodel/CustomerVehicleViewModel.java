package com.example.motoshop.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.motoshop.data.model.CustomerVehicle;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;

// ViewModel quản lý dữ liệu và cập nhật cho màn hình khi Firebase thay đổi.
public class CustomerVehicleViewModel extends BaseViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<CustomerVehicle>> _vehicles = new MutableLiveData<>();
    public final LiveData<List<CustomerVehicle>> allVehicles = _vehicles;

    // Constructor khởi tạo object của class này.
    public CustomerVehicleViewModel(@NonNull Application application) {
        super(application);
        listenToVehicles();
    }

    // Lấy hoặc lắng nghe dữ liệu từ Firebase để cập nhật giao diện.
    private void listenToVehicles() {
        registerListener(db.collection("customer_vehicles")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("CustomerVehicleVM", "Listen failed.", error);
                        return;
                    }
                    if (value != null) {
                        _vehicles.setValue(value.toObjects(CustomerVehicle.class));
                    }
                }));
    }

    public void insert(CustomerVehicle v) {
        v.createdAt = System.currentTimeMillis();
        if (v.documentId == null || v.documentId.isEmpty()) {
            String phone = "UNKNOWN";
            if (v.customerDocumentId != null && v.customerDocumentId.contains("_")) {
                String[] parts = v.customerDocumentId.split("_");
                if (parts.length > 1) phone = parts[1];
            }
            if (v.licensePlate != null && !v.licensePlate.trim().isEmpty()) {
                v.documentId = phone + "_" + v.licensePlate.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            } else {
                v.documentId = "VEHICLE_" + System.currentTimeMillis();
            }
        }
        db.collection("customer_vehicles").document(v.documentId).set(v);
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public void update(CustomerVehicle v) {
        if (v.documentId != null) {
            db.collection("customer_vehicles").document(v.documentId).set(v);
        }
    }

    // Xóa hoặc bỏ dữ liệu theo thao tác của người dùng.
    public void delete(CustomerVehicle v) {
        if (v.documentId != null) {
            db.collection("customer_vehicles").document(v.documentId).delete();
        }
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public LiveData<List<CustomerVehicle>> getByCustomer(String customerDocumentId) {
        MutableLiveData<List<CustomerVehicle>> result = new MutableLiveData<>();
        if (customerDocumentId == null) return result;

        registerListener(db.collection("customer_vehicles")
                .whereEqualTo("customerDocumentId", customerDocumentId)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        result.setValue(value.toObjects(CustomerVehicle.class));
                    }
                }));
        return result;
    }
}
