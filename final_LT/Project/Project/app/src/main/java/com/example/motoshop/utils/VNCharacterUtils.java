package com.example.motoshop.utils;

public class VNCharacterUtils {
    private static final String[] VIETNAMESE_SIGNS = new String[] {
            "aAeEoOuUiIdDyY",
            "áàạảãâấầậẩẫăắằặẳẵ",
            "ÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴ",
            "éèẹẻẽêếềệểễ",
            "ÉÈẸẺẼÊẾỀỆỂỄ",
            "óòọỏõôốồộổỗơớờợởỡ",
            "ÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠ",
            "úùụủũưứừựửữ",
            "ÚÙỤỦŨƯỨỪỰỬỮ",
            "íìịỉĩ",
            "ÍÌỊỈĨ",
            "đ",
            "Đ",
            "ýỳỵỷỹ",
            "ÝỲỴỶỸ"
    };

    public static String removeAccents(String str) {
        if (str == null) return null;
        for (int i = 1; i < VIETNAMESE_SIGNS.length; i++) {
            for (int j = 0; j < VIETNAMESE_SIGNS[i].length(); j++) {
                str = str.replace(VIETNAMESE_SIGNS[i].charAt(j), VIETNAMESE_SIGNS[0].charAt(i - 1));
            }
        }
        return str;
    }
}
