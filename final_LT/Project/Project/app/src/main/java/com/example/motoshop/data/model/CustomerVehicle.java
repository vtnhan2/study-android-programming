package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "customer_vehicles")
public class CustomerVehicle {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @DocumentId
    public String documentId;

    public String customerDocumentId;
    public String customerName;
    public String brand;
    public String model;
    public String licensePlate;
    public int year;
    public String color;
    public String engineNumber;
    public String frameNumber;
    public String note;
    public long createdAt;

    // Thông tin bảo dưỡng và bảo hành
    public long purchaseDate;
    public long warrantyEndDate;
    public long lastServiceDate;
    public long nextMaintenanceDate;
    public String maintenanceNote;

    // Id cũ dùng cho dữ liệu local nếu còn tham chiếu.
    public int customerId;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public CustomerVehicle() {}
}
