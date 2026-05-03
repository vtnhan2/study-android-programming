package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "repair_services")
public class RepairService {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @DocumentId
    public String documentId;

    public String name;
    public String description;
    public double defaultLaborCost;
    public double defaultPartsCost;
    public String category; // Nhóm dịch vụ sửa chữa

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public RepairService() {}

    // Constructor dùng khi tạo nhanh dịch vụ sửa chữa trong code.
    public RepairService(String name, String description, double labor, double parts, String category) {
        this.name = name;
        this.description = description;
        this.defaultLaborCost = labor;
        this.defaultPartsCost = parts;
        this.category = category;
    }
}
