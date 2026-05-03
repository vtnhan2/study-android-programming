package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "import_orders")
public class ImportOrder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @DocumentId
    public String documentId;

    public String importCode;
    public String supplierDocumentId;
    public String supplierName;
    public String motorcycleDocumentId;
    public String motorcycleName;
    public int quantity;
    public double unitPrice;
    public double totalAmount;
    public long importDate;
    public String status; // Trạng thái đơn nhập
    public String note;

    // Id cũ dùng cho dữ liệu local nếu còn tham chiếu.
    public int supplierId;
    public int motorcycleId;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public ImportOrder() {}
}
