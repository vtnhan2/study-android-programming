package com.example.motoshop.utils;

import java.text.NumberFormat;
import java.util.Locale;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class CurrencyFormatter {
    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public static String format(double amount) {
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return fmt.format(amount) + " đ";
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public static String formatMillions(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1f tỷ đ", amount / 1_000_000_000);
        if (amount >= 1_000_000) return String.format("%.1f triệu đ", amount / 1_000_000);
        return format(amount);
    }
}
