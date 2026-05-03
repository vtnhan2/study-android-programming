package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "sales_order_items")
public class SalesOrderItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Dữ liệu chi tiết từng xe trong đơn hàng
    public String motorcycleDocumentId;
    public String motorcycleName;
    public int quantity;
    public double unitPrice;
    public double subtotal;

    // Id cũ dùng cho dữ liệu local nếu còn tham chiếu.
    public int orderId;
    public int motorcycleId;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public SalesOrderItem() {}
}
