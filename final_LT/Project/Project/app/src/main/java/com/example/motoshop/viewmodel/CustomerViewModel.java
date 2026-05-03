package com.example.motoshop.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.motoshop.data.model.Customer;
import com.example.motoshop.utils.DateUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;

// ViewModel quản lý dữ liệu và cập nhật cho màn hình khi Firebase thay đổi.
public class CustomerViewModel extends BaseViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Customer>> _firebaseCustomers = new MutableLiveData<>();
    public final LiveData<List<Customer>> allCustomers = _firebaseCustomers;

    // Interface báo kết quả khi thêm hoặc sửa dữ liệu khách hàng.
    public interface OnActionListener {
        void onSuccess();
        void onFailure(String message);
    }

    // Constructor khởi tạo object của class này.
    public CustomerViewModel(@NonNull Application app) {
        super(app);
        listenToCustomers();
    }

    // Lấy hoặc lắng nghe dữ liệu từ Firebase để cập nhật giao diện.
    private void listenToCustomers() {
        registerListener(db.collection("customers")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        _errorMessage.postValue("Lỗi khách hàng Online: " + error.getMessage());
                        return;
                    }
                    if (value != null) {
                        List<Customer> list = value.toObjects(Customer.class);
                        _firebaseCustomers.setValue(list);
                    }
                }));
    }

    // Thêm hoặc lưu dữ liệu lên Firebase.
    public void insert(Customer c, OnActionListener listener) {
        // Kiểm tra trùng số điện thoại trước khi thêm
        db.collection("customers")
                .whereEqualTo("phone", c.phone)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        if (listener != null) listener.onFailure("Số điện thoại này đã tồn tại trên hệ thống!");
                    } else {
                        c.createdAt = System.currentTimeMillis();
                        String safeName = c.name.replaceAll("[^a-zA-Z0-9À-ỹ\\s]", "").trim().replaceAll("\\s+", "_").toUpperCase();
                        safeName = com.example.motoshop.utils.VNCharacterUtils.removeAccents(safeName); // Assuming I create this
                        if (c.customerCode == null) c.customerCode = "KH_" + c.phone + "_" + safeName;
                        c.documentId = c.customerCode;
                        c.accountStatus = "ACTIVE";
                        c.memberRank = "NORMAL";
                        c.loyaltyPoints = 0;
                        c.totalSpent = 0;

                        db.collection("customers").document(c.documentId).set(c)
                                .addOnSuccessListener(aVoid -> {
                                    if (listener != null) listener.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    if (listener != null) listener.onFailure("Lỗi Firestore: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure("Lỗi kiểm tra trùng: " + e.getMessage());
                });
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public void update(Customer c, OnActionListener listener) {
        if (c.documentId != null) {
            // Kiểm tra trùng số điện thoại (trừ chính nó)
            db.collection("customers")
                    .whereEqualTo("phone", c.phone)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        boolean duplicate = false;
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                            if (!doc.getId().equals(c.documentId)) {
                                duplicate = true;
                                break;
                            }
                        }

                        if (duplicate) {
                            if (listener != null) listener.onFailure("Số điện thoại này đã được sử dụng bởi khách hàng khác!");
                        } else {
                            db.collection("customers").document(c.documentId).set(c)
                                    .addOnSuccessListener(aVoid -> {
                                        if (listener != null) listener.onSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        if (listener != null) listener.onFailure("Lỗi cập nhật Firestore: " + e.getMessage());
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onFailure("Lỗi kiểm tra số điện thoại: " + e.getMessage());
                    });
        }
    }

    // Xóa hoặc bỏ dữ liệu theo thao tác của người dùng.
    public void delete(String docId, OnActionListener listener) {
        if (docId != null) {
            db.collection("customers").document(docId).delete()
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onFailure("Lỗi xóa: " + e.getMessage());
                    });
        }
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public void updateLoyalty(String docId, double totalSpent) {
        if (docId == null) return;

        String rank = "NORMAL";
        if (totalSpent >= 100000000) rank = "VIP";
        else if (totalSpent >= 50000000) rank = "GOLD";
        else if (totalSpent >= 20000000) rank = "SILVER";

        int points = (int) (totalSpent / 100000);

        db.collection("customers").document(docId)
                .update("totalSpent", totalSpent,
                        "memberRank", rank,
                        "loyaltyPoints", points);
    }
}
