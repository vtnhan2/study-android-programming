package com.example.motoshop.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.motoshop.data.model.Motorcycle;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

// ViewModel quản lý dữ liệu và cập nhật cho màn hình khi Firebase thay đổi.
public class MotorcycleViewModel extends BaseViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Motorcycle>> _motorcycles = new MutableLiveData<>();
    public final LiveData<List<Motorcycle>> allMotorcycles = _motorcycles;

    public final LiveData<Integer> totalStock;
    public final LiveData<Integer> lowStockCount;

    // Constructor khởi tạo object của class này.
    public MotorcycleViewModel(@NonNull Application app) {
        super(app);

        totalStock = Transformations.map(allMotorcycles, list -> {
            int total = 0;
            if (list != null) for (Motorcycle m : list) total += m.quantity;
            return total;
        });

        lowStockCount = Transformations.map(allMotorcycles, list -> {
            int count = 0;
            if (list != null) {
                for (Motorcycle m : list) if (m.quantity > 0 && m.quantity <= 3) count++;
            }
            return count;
        });

        listenToFirebase();
    }

    private void fixCorruptedData() {
        db.collection("motorcycles").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Motorcycle> allMotos = queryDocumentSnapshots.toObjects(Motorcycle.class);
            for (Motorcycle m : allMotos) {
                // Xoá các document bị lỗi sinh ra do timestamp (_1777...) hoặc mã ngẫu nhiên (không chứa "_")
                if (m.documentId != null && (m.documentId.contains("_1777") || !m.documentId.contains("_"))) {
                    // Xoá document rác
                    db.collection("motorcycles").document(m.documentId).delete()
                            .addOnSuccessListener(aVoid -> android.util.Log.d("FixData", "Deleted duplicate/garbage: " + m.documentId));
                    
                    // Nếu bạn muốn bảo toàn số lượng nhập vào (cộng dồn vào xe gốc):
                    // (Tuỳ chọn: Tìm xe gốc có cùng tên và năm, sau đó cộng dồn quantity)
                    String baseId = null;
                    if (m.model.contains("Air Blade 125 Đặc Biệt")) baseId = "honda_air_blade_125_dac_biet_2024";
                    else if (m.model.contains("Air Blade 125")) baseId = "honda_air_blade_125_2024";
                    else if (m.model.contains("Lead 125 Cao Cấp")) baseId = "honda_lead_125_cao_cap_2024";
                    else if (m.model.contains("Lead 125")) baseId = "honda_lead_125_2024";

                    if (baseId != null && m.quantity > 0) {
                        db.collection("motorcycles").document(baseId)
                                .update("quantity", com.google.firebase.firestore.FieldValue.increment(m.quantity));
                    }
                }
            }
        });
    }

    // Lấy hoặc lắng nghe dữ liệu từ Firebase để cập nhật giao diện.
    private void listenToFirebase() {
        registerListener(db.collection("motorcycles")
                .orderBy("brand", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("MotorcycleVM", "Listen failed.", error);
                        return;
                    }
                    if (value != null) {
                        _motorcycles.setValue(value.toObjects(Motorcycle.class));
                    }
                }));
    }

    // Trừ số lượng tồn kho của xe sau khi bán.
    public void decreaseStock(String docId, int qty) {
        if (docId != null) {
            db.collection("motorcycles").document(docId)
                    .update("quantity", FieldValue.increment(-qty));
        }
    }

    // Thêm hoặc lưu dữ liệu lên Firebase.
    public void insert(Motorcycle m) {
        m.createdAt = System.currentTimeMillis();
        if (m.documentId == null || m.documentId.isEmpty()) {
            String safeBrand = m.brand != null ? 
                com.example.motoshop.utils.VNCharacterUtils.removeAccents(m.brand.toLowerCase()).replaceAll("\\s+", "_") : "moto";
            String safeModel = m.model != null ? 
                com.example.motoshop.utils.VNCharacterUtils.removeAccents(m.model.toLowerCase()).replaceAll("\\s+", "_") : "model";
            m.documentId = safeBrand + "_" + safeModel + "_" + m.year;
        }
        db.collection("motorcycles").document(m.documentId).set(m);
    }

    // Cập nhật thông tin xe lên Firebase.
    public void update(Motorcycle m) {
        if (m.documentId != null) {
            db.collection("motorcycles").document(m.documentId).set(m);
        }
    }

    // Xóa hoặc bỏ dữ liệu theo thao tác của người dùng.
    public void delete(Motorcycle m) {
        if (m.documentId != null) {
            db.collection("motorcycles").document(m.documentId).delete();
        }
    }

    // Lấy danh sách xe hiện có trong LiveData.
    public List<Motorcycle> getAllSync() {
        List<Motorcycle> list = _motorcycles.getValue();
        return list != null ? list : new ArrayList<>();
    }

    // Tìm xe theo document id trên Firebase.
    public Motorcycle getByDocumentId(String docId) {
        List<Motorcycle> list = _motorcycles.getValue();
        if (list != null && docId != null) {
            for (Motorcycle m : list) {
                if (docId.equals(m.documentId)) return m;
            }
        }
        return null;
    }
}
