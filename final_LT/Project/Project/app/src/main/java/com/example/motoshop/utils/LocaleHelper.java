package com.example.motoshop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class LocaleHelper {
    private static final String PREF_NAME = "MotoShopPrefs";
    private static final String KEY_LANG = "language_code";

    // Áp dụng ngôn ngữ đã lưu khi màn hình được tạo.
    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, "vi");
        return setLocale(context, lang);
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public static String getLanguage(Context context) {
        return getPersistedData(context, "vi");
    }

    // Lưu ngôn ngữ mới và cập nhật lại tài nguyên giao diện.
    public static Context setLocale(Context context, String language) {
        persist(context, language);
        return updateResources(context, language);
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_LANG, defaultLanguage);
    }

    // Lưu mã ngôn ngữ vào SharedPreferences.
    private static void persist(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_LANG, language).apply();
    }

    // Cập nhật lại cấu hình ngôn ngữ cho app.
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }
}
