package com.example.motoshop.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.firestore.DocumentId;

// Model lưu thông tin dữ liệu dùng trong app và Firebase.
@Entity(tableName = "customers")
public class Customer {
    @PrimaryKey(autoGenerate = true)
    public int id; // Giữ lại nếu còn dữ liệu local cũ

    @DocumentId
    public String documentId;

    public String name;
    public String phone;
    public String address;
    public String email;
    public String idCard;
    public long createdAt;
    public String note;
    public String avatarUrl; // Thêm trường lưu ảnh đại diện

    // Thông tin tài khoản tích điểm của khách hàng
    public String customerCode;
    public String accountStatus; // Trạng thái tài khoản
    public String memberRank;    // Hạng thành viên
    public int loyaltyPoints;
    public double totalSpent;

    // Constructor rỗng giúp Firebase chuyển dữ liệu sang object.
    public Customer() {}
}
