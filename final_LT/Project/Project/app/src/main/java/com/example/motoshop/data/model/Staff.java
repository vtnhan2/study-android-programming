package com.example.motoshop.data.model;

import com.google.firebase.firestore.DocumentId;

public class Staff {
    @DocumentId
    public String documentId;

    public String staffId;   // Ví dụ: AD01, SL01
    public String password;
    public String name;
    public String role;      // ADMIN, SALES, TECHNICIAN
    public String phone;
    public boolean active;

    public Staff() {}
}
