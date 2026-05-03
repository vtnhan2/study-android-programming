package com.example.motoshop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.motoshop.R;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class UserSession {
    private static final String PREF_NAME = "MotoShopUserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_CUSTOMER_DOC_ID = "customerDocId";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SALES = "SALES";
    public static final String ROLE_TECHNICIAN = "TECHNICIAN";
    public static final String ROLE_USER = "USER";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    // Constructor khởi tạo object của class này.
    public UserSession(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    public void createLoginSession(String userId, String name, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_ROLE, role);
        editor.commit();
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    public void createCustomerSession(String docId, String name, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, phone);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_ROLE, ROLE_USER);
        editor.putString(KEY_CUSTOMER_DOC_ID, docId);
        editor.commit();
    }

    // Kiểm tra trạng thái đúng hoặc sai của dữ liệu.
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "");
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public String getCustomerDocId() {
        return pref.getString(KEY_CUSTOMER_DOC_ID, null);
    }

    // Hiển thị thông tin hoặc hộp thoại cho người dùng.
    public String getRoleDisplayName() {
        String role = getUserRole();
        if (ROLE_ADMIN.equals(role)) return "Quản trị viên";
        if (ROLE_SALES.equals(role)) return "Nhân viên bán hàng";
        if (ROLE_TECHNICIAN.equals(role)) return "Kỹ thuật viên";
        if (ROLE_USER.equals(role)) return "Khách hàng";
        return role;
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "User");
    }

    // Lấy ID người dùng hiện tại.
    public String getUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    // Mở màn hình khác bằng Intent hoặc điều hướng trong app.
    public void logout() {
        editor.clear();
        editor.commit();
    }
}
