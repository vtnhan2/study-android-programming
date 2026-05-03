package com.example.motoshop.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.motoshop.data.model.RepairOrder;
import com.example.motoshop.data.model.RepairService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;

// ViewModel quản lý dữ liệu và cập nhật cho màn hình khi Firebase thay đổi.
public class RepairViewModel extends BaseViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<RepairOrder>> _firebaseRepairs = new MutableLiveData<>();
    public final LiveData<List<RepairOrder>> allRepairs = _firebaseRepairs;

    private final MutableLiveData<List<RepairService>> _firebaseServices = new MutableLiveData<>();
    public final LiveData<List<RepairService>> allServices = _firebaseServices;

    public final LiveData<Integer> inProgressCount;

    public final LiveData<List<RepairOrder>> getRepairsByCustomer(String customerId) {
        MutableLiveData<List<RepairOrder>> customerRepairs = new MutableLiveData<>();
        if (customerId == null) return customerRepairs;

        db.collection("repair_orders")
                .whereEqualTo("customerDocumentId", customerId)
                .orderBy("receivedDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        customerRepairs.setValue(value.toObjects(RepairOrder.class));
                    }
                });
        return customerRepairs;
    }

    // Constructor khởi tạo object của class này.
    public RepairViewModel(@NonNull Application app) {
        super(app);

        inProgressCount = Transformations.map(_firebaseRepairs, list -> {
            int count = 0;
            if (list != null) {
                for (RepairOrder r : list) {
                    if ("RECEIVED".equals(r.status) || "IN_PROGRESS".equals(r.status)) {
                        count++;
                    }
                }
            }
            return count;
        });

        listenToRepairs();
        listenToServices();
    }

    // Lấy hoặc lắng nghe dữ liệu từ Firebase để cập nhật giao diện.
    private void listenToRepairs() {
        registerListener(db.collection("repair_orders")
                .orderBy("receivedDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        _firebaseRepairs.setValue(value.toObjects(RepairOrder.class));
                    }
                }));
    }

    // Lấy hoặc lắng nghe dữ liệu từ Firebase để cập nhật giao diện.
    private void listenToServices() {
        registerListener(db.collection("repair_services")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        _firebaseServices.setValue(value.toObjects(RepairService.class));
                    }
                }));
    }

    // Thêm hoặc lưu dữ liệu lên Firebase.
    public void insert(RepairOrder r) {
        // Tự động sinh mã nếu chưa có
        int countToday = 1;
        List<RepairOrder> all = _firebaseRepairs.getValue();
        if (all != null) {
            long todayStart = com.example.motoshop.utils.DateUtils.startOfDay(System.currentTimeMillis());
            for (RepairOrder o : all) {
                if (o.receivedDate >= todayStart) countToday++;
            }
        }
        r.repairCode = com.example.motoshop.utils.DateUtils.generateCode("SC", countToday);
        r.documentId = r.repairCode;
        db.collection("repair_orders").document(r.documentId).set(r);
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public void update(RepairOrder r) {
        if (r.documentId != null) {
            db.collection("repair_orders").document(r.documentId).set(r);
        }
    }

    public void saveService(RepairService service) {
        if (service.documentId == null || service.documentId.isEmpty()) {
            String safeName = service.name != null ? 
                com.example.motoshop.utils.VNCharacterUtils.removeAccents(service.name).toUpperCase().replaceAll("\\s+", "_") : 
                "SERVICE_" + System.currentTimeMillis();
            
            // Nếu là phụ tùng (có giá nhập/tồn kho trong tương lai hoặc dựa trên tên)
            if (safeName.contains("LOC_") || safeName.contains("LOP_") || safeName.contains("NHOT_") || safeName.contains("BUGI_")) {
                service.documentId = "PT_" + safeName;
            } else {
                service.documentId = "DV_" + safeName;
            }
        }
        db.collection("repair_services").document(service.documentId).set(service);
    }

    public void deleteService(RepairService service) {
        if (service.documentId != null) {
            db.collection("repair_services").document(service.documentId).delete();
        }
    }
}
