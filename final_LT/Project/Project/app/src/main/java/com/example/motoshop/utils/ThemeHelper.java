package com.example.motoshop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class ThemeHelper {
    private static final String PREF_NAME = "MotoShopPrefs";
    private static final String KEY_THEME = "theme_mode";

    // Áp dụng chế độ sáng hoặc tối đã lưu trước đó.
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    // Lưu chế độ giao diện người dùng chọn.
    public static void saveTheme(Context context, int themeMode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, themeMode).apply();
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    // Kiểm tra app hiện có đang ở chế độ tối không.
    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO) == AppCompatDelegate.MODE_NIGHT_YES;
    }
}
