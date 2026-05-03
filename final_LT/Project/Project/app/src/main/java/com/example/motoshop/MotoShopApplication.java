package com.example.motoshop;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

public class MotoShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        
        // Cấu hình Firestore Offline Cache
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }
}
