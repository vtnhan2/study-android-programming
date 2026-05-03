package com.example.motoshop.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class DateUtils {
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi"));
    private static final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi"));

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public static String formatDate(long millis) {
        if (millis <= 0) return "";
        return DATE_FMT.format(new Date(millis));
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public static String formatDateTime(long millis) {
        if (millis <= 0) return "";
        return DATETIME_FMT.format(new Date(millis));
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    public static long parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return 0;
        try {
            Date date = DATE_FMT.parse(dateStr);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            return 0;
        }
    }

    // Lấy thời điểm bắt đầu của một ngày.
    public static long startOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // Lấy thời điểm bắt đầu của một tháng.
    public static long startOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1, 0, 0, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // Lấy thời điểm cuối cùng của một tháng.
    public static long endOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1, 0, 0, 0);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59);
        return c.getTimeInMillis();
    }

    // Tạo mã đơn giản theo ngày hiện tại và số thứ tự (STT).
    public static String generateCode(String prefix, int sequence) {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return prefix + "_" + f.format(new Date()) + "_" + String.format(Locale.getDefault(), "%03d", sequence);
    }

    // Tạo mã đơn giản theo ngày hiện tại và số ngẫu nhiên (dùng tạm khi chưa có STT).
    public static String generateCode(String prefix) {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return prefix + "_" + f.format(new Date()) + "_" + (int)(Math.random() * 900 + 100);
    }
}
