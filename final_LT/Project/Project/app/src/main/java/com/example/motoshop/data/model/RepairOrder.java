package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "repair_orders")
public class RepairOrder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @DocumentId
    public String documentId;

    public String repairCode;
    public String customerDocumentId;
    public String customerName;
    public String motorcycleBrand;
    public String motorcycleModel;
    public String licensePlate;
    public String issueDescription;
    public String diagnosis;
    public double laborCost;
    public double partsCost;
    public double totalCost;
    public String status; // Trạng thái phiếu sửa chữa
    public String technicianName;
    public long receivedDate;
    public long completedDate;
    public String note;

    // Id cũ dùng cho dữ liệu local nếu còn tham chiếu.
    public int customerId;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public RepairOrder() {}
}
