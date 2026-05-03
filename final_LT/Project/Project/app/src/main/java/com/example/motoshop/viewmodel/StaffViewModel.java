package com.example.motoshop.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.motoshop.data.model.Staff;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;

public class StaffViewModel extends BaseViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Staff>> _staffList = new MutableLiveData<>();
    public final LiveData<List<Staff>> allStaff = _staffList;

    public StaffViewModel(@NonNull Application app) {
        super(app);
        listenToStaff();
    }

    private void listenToStaff() {
        registerListener(db.collection("staff")
                .orderBy("role", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        _staffList.setValue(value.toObjects(Staff.class));
                    }
                }));
    }

    public void insert(Staff s) {
        if (s.staffId != null) {
            db.collection("staff").document(s.staffId).set(s);
        }
    }

    public void delete(Staff s) {
        if (s.documentId != null) {
            db.collection("staff").document(s.documentId).delete();
        }
    }
}
