package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;
import java.util.List;
import java.util.Map;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "motorcycles")
public class Motorcycle {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @DocumentId
    public String documentId;

    public String brand;
    public String model;
    public String color;
    public int year;
    public double price;
    public double importPrice;
    public int quantity;
    public String status; // Trạng thái tồn kho xe
    public String description;
    public String imageUri;
    public long createdAt;

    // Thông tin chi tiết của xe
    public String shortDescription;
    public String longDescription;

    @Ignore
    public List<String> secondaryImages;

    @Ignore
    public Map<String, String> technicalSpecs;

    @Ignore
    public List<String> availableColors;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public Motorcycle() {}
}
