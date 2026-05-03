package com.example.motoshop.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.motoshop.data.model.SalesOrder;
import com.example.motoshop.data.model.SalesOrderItem;
import com.example.motoshop.data.model.Motorcycle;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

// ViewModel quản lý dữ liệu và cập nhật cho màn hình khi Firebase thay đổi.
public class SalesViewModel extends BaseViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<SalesOrder>> _firebaseOrders = new MutableLiveData<>();
    public final LiveData<List<SalesOrder>> allOrders = _firebaseOrders;

    public final LiveData<Double> totalRevenue;
    public final LiveData<Integer> completedCount;
    public final LiveData<List<SalesOrder>> recentOrders;
    public final LiveData<java.util.Map<String, Double>> revenueByStaff;

    // Callback báo kết quả khi tạo hoặc cập nhật đơn bán.
    public interface OrderCallback {
        void onSuccess();
        void onError(String message);
    }

    // Constructor khởi tạo object của class này.
    public SalesViewModel(@NonNull Application app) {
        super(app);

        totalRevenue = Transformations.map(_firebaseOrders, list -> {
            double total = 0;
            if (list != null) {
                for (SalesOrder o : list) {
                    if ("COMPLETED".equals(o.status)) total += o.finalAmount;
                }
            }
            return total;
        });

        completedCount = Transformations.map(_firebaseOrders, list -> {
            int count = 0;
            if (list != null) {
                for (SalesOrder o : list) if ("COMPLETED".equals(o.status)) count++;
            }
            return count;
        });

        recentOrders = Transformations.map(_firebaseOrders, list -> {
            if (list == null) return new ArrayList<>();
            if (list.size() > 5) return list.subList(0, 5);
            return list;
        });

        revenueByStaff = Transformations.map(_firebaseOrders, list -> {
            java.util.Map<String, Double> map = new java.util.HashMap<>();
            if (list != null) {
                for (SalesOrder o : list) {
                    if ("COMPLETED".equals(o.status) && o.createdByStaffName != null) {
                        double current = map.getOrDefault(o.createdByStaffName, 0.0);
                        map.put(o.createdByStaffName, current + o.finalAmount);
                    }
                }
            }
            return map;
        });

        listenToOrders();
    }

    // Lấy hoặc lắng nghe dữ liệu từ Firebase để cập nhật giao diện.
    private void listenToOrders() {
        registerListener(db.collection("sales_orders")
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("SalesViewModel", "Listen failed.", error);
                        return;
                    }
                    if (value != null) {
                        _firebaseOrders.setValue(value.toObjects(SalesOrder.class));
                    }
                }));
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public void updateStatus(String documentId, String status) {
        if (documentId == null) return;
        db.collection("sales_orders").document(documentId).update("status", status);
    }

    /**
     * Hoàn tất đơn hàng và tự động cộng điểm tích lũy cho khách hàng.
     * Cập nhật hạng thành viên dựa trên tổng chi tiêu.
     */
    public void completeOrderWithLoyalty(SalesOrder order, OrderCallback callback) {
        if (order == null || order.documentId == null || order.customerDocumentId == null) {
            if (callback != null) callback.onError("Dữ liệu không hợp lệ");
            return;
        }

        db.runTransaction(transaction -> {
            DocumentReference orderRef = db.collection("sales_orders").document(order.documentId);
            DocumentReference customerRef = db.collection("customers").document(order.customerDocumentId);

            // 1. Đọc thông tin khách hàng hiện tại
            com.example.motoshop.data.model.Customer customer = transaction.get(customerRef).toObject(com.example.motoshop.data.model.Customer.class);
            if (customer == null) throw new RuntimeException("Không tìm thấy khách hàng");

            // 2. Cập nhật trạng thái đơn hàng
            transaction.update(orderRef, "status", "COMPLETED");

            // 3. Tính toán điểm và hạng mới
            // Giả sử: 1,000,000đ = 1 điểm.
            int newPoints = (int) (order.finalAmount / 1000000);
            double newTotalSpent = customer.totalSpent + order.finalAmount;
            
            String newRank = "Standard";
            if (newTotalSpent >= 500000000) newRank = "Diamond";
            else if (newTotalSpent >= 200000000) newRank = "Gold";
            else if (newTotalSpent >= 50000000) newRank = "Silver";

            transaction.update(customerRef, 
                "loyaltyPoints", customer.loyaltyPoints + newPoints,
                "totalSpent", newTotalSpent,
                "memberRank", newRank
            );

            return null;
        }).addOnSuccessListener(aVoid -> {
            if (callback != null) callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e("SalesViewModel", "Complete order with loyalty failed", e);
            if (callback != null) callback.onError(e.getMessage());
        });
    }

    // Xóa hoặc bỏ dữ liệu theo thao tác của người dùng.
    public void cancelOrder(SalesOrder order, String reason, OrderCallback callback) {
        if (order == null || order.documentId == null) {
            if (callback != null) callback.onError("Dữ liệu đơn hàng không hợp lệ");
            return;
        }

        db.runTransaction(transaction -> {
            DocumentReference orderRef = db.collection("sales_orders").document(order.documentId);
            
            // Trong Firestore Transaction, TẤT CẢ các lệnh đọc (get) phải thực hiện TRƯỚC các lệnh ghi (update/set).
            // 1. Đọc dữ liệu xe trước
            List<Motorcycle> motos = new ArrayList<>();
            if (order.items != null) {
                for (SalesOrderItem item : order.items) {
                    if (item.motorcycleDocumentId != null) {
                        DocumentReference motoRef = db.collection("motorcycles").document(item.motorcycleDocumentId);
                        motos.add(transaction.get(motoRef).toObject(Motorcycle.class));
                    } else {
                        motos.add(null);
                    }
                }
            }

            // 2. Thực hiện cập nhật sau khi đã đọc xong
            if (order.items != null) {
                for (int i = 0; i < order.items.size(); i++) {
                    SalesOrderItem item = order.items.get(i);
                    Motorcycle moto = motos.get(i);
                    if (moto != null && item.motorcycleDocumentId != null) {
                        DocumentReference motoRef = db.collection("motorcycles").document(item.motorcycleDocumentId);
                        transaction.update(motoRef, "quantity", moto.quantity + item.quantity);
                    }
                }
            }
            
            transaction.update(orderRef, "status", "CANCELLED", "cancelReason", reason);
            return null;
        }).addOnSuccessListener(aVoid -> {
            if (callback != null) callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e("SalesViewModel", "Cancel order failed", e);
            if (callback != null) callback.onError(e.getMessage());
        });
    }

    // Thêm hoặc lưu dữ liệu lên Firebase.
    public void createOrder(SalesOrder order, OrderCallback callback) {
        if (order == null || order.items == null || order.items.isEmpty()) {
            if (callback != null) callback.onError("Dữ liệu đơn hàng không hợp lệ");
            return;
        }

        db.runTransaction(transaction -> {
            // 1. Đọc dữ liệu tồn kho trước
            List<Motorcycle> currentMotos = new ArrayList<>();
            for (SalesOrderItem item : order.items) {
                DocumentReference motoRef = db.collection("motorcycles").document(item.motorcycleDocumentId);
                Motorcycle moto = transaction.get(motoRef).toObject(Motorcycle.class);
                if (moto == null || moto.quantity < item.quantity) {
                    throw new RuntimeException("Không đủ tồn kho cho xe: " + (moto != null ? moto.model : item.motorcycleName));
                }
                currentMotos.add(moto);
            }

            // 2. Cập nhật sau khi kiểm tra xong
            for (int i = 0; i < order.items.size(); i++) {
                SalesOrderItem item = order.items.get(i);
                Motorcycle moto = currentMotos.get(i);
                DocumentReference motoRef = db.collection("motorcycles").document(item.motorcycleDocumentId);
                transaction.update(motoRef, "quantity", moto.quantity - item.quantity);
            }

            DocumentReference newOrderRef = db.collection("sales_orders").document(order.orderCode);
            transaction.set(newOrderRef, order);
            return null;
        }).addOnSuccessListener(aVoid -> {
            if (callback != null) callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e("SalesViewModel", "Create order failed", e);
            if (callback != null) callback.onError(e.getMessage());
        });
    }

    // Cập nhật lại dữ liệu đang có.
    public LiveData<List<SalesOrderItem>> getPurchasedItemsByCustomer(String customerDocumentId) {
        MutableLiveData<List<SalesOrderItem>> result = new MutableLiveData<>();
        if (customerDocumentId == null) return result;

        db.collection("sales_orders")
                .whereEqualTo("customerDocumentId", customerDocumentId)
                .whereEqualTo("status", "COMPLETED")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SalesOrderItem> allItems = new ArrayList<>();
                    List<SalesOrder> orders = queryDocumentSnapshots.toObjects(SalesOrder.class);
                    for (SalesOrder order : orders) {
                        if (order.items != null) {
                            allItems.addAll(order.items);
                        }
                    }
                    result.setValue(allItems);
                });
        return result;
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public Double getRevenueInRange(long start, long end) {
        double total = 0;
        List<SalesOrder> list = _firebaseOrders.getValue();
        if (list != null) {
            for (SalesOrder o : list) {
                if ("COMPLETED".equals(o.status) && o.orderDate >= start && o.orderDate <= end) {
                    total += o.finalAmount;
                }
            }
        }
        return total;
    }
}
