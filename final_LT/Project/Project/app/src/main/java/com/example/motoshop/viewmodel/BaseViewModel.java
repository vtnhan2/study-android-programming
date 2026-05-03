package com.example.motoshop.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

// ViewModel quản lý dữ liệu và cập nhật cho màn hình khi Firebase thay đổi.
public class BaseViewModel extends AndroidViewModel {
    protected final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;
    
    // Danh sách các listener kết nối với Firebase
    protected final List<ListenerRegistration> listenerRegistrations = new ArrayList<>();

    // Constructor khởi tạo object của class này.
    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    // Đăng ký listener để tự động xoá khi ViewModel bị huỷ
    protected void registerListener(ListenerRegistration registration) {
        if (registration != null) {
            listenerRegistrations.add(registration);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Xoá toàn bộ listener để giải phóng RAM và chặn lỗi leak memory
        for (ListenerRegistration registration : listenerRegistrations) {
            registration.remove();
        }
        listenerRegistrations.clear();
    }

    // Xóa hoặc bỏ dữ liệu theo thao tác của người dùng.
    public void clearError() {
        _errorMessage.postValue(null);
    }
}
