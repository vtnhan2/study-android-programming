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
        if (r.repairCode == null || r.repairCode.isEmpty()) {
            r.repairCode = com.example.motoshop.utils.DateUtils.generateCode("SC");
        }
        r.documentId = r.repairCode;
        db.collection("repair_orders").document(r.documentId).set(r);
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public void update(RepairOrder r) {
        if (r.documentId != null) {
            db.collection("repair_orders").document(r.documentId).set(r);
        }
    }
}
