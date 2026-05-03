package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @DocumentId
    public String documentId;

    public String name;
    public String contactPerson;
    public String phone;
    public String email;
    public String address;
    public String taxCode;
    public long createdAt;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public Supplier() {}
}
