package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;
import java.util.List;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "sales_orders")
public class SalesOrder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @DocumentId
    public String documentId;

    public String orderCode;
    public String customerDocumentId;
    public String customerName;
    public double totalAmount;
    public double discount;
    public double finalAmount;
    public String paymentMethod;
    public String status; // Trạng thái đơn bán
    public long orderDate;
    public String note;
    public String cancelReason;

    @Ignore
    public List<SalesOrderItem> items;

    // Id cũ dùng cho dữ liệu local nếu còn tham chiếu.
    public int customerId;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public SalesOrder() {}
}
