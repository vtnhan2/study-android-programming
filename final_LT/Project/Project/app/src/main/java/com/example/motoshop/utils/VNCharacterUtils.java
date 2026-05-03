package com.example.motoshop.utils;

public class VNCharacterUtils {
    private static final String VIETNAMESE_CHARS = 
            "àáạảãâầấậẩẫăằắặẳẵ" +
            "èéẹẻẽêềếệểễ" +
            "ìíịỉĩ" +
            "òóọỏõôồốộổỗơờớợởỡ" +
            "ùúụủũưừứựửữ" +
            "ỳýỵỷỹ" +
            "đ" +
            "ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ" +
            "ÈÉẸẺẼÊỀẾỆỂỄ" +
            "ÌÍỊỈĨ" +
            "ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ" +
            "ÙÚỤỦŨƯỪỨỰỬỮ" +
            "ỲÝỴỶỸ" +
            "Đ";

    private static final String REPLACEMENT_CHARS = 
            "aaaaaaaaaaaaaaaaa" +
            "eeeeeeeeeee" +
            "iiiii" +
            "ooooooooooooooooo" +
            "uuuuuuuuuuu" +
            "yyyyy" +
            "d" +
            "AAAAAAAAAAAAAAAAA" +
            "EEEEEEEEEEE" +
            "IIIII" +
            "OOOOOOOOOOOOOOOOO" +
            "UUUUUUUUUUU" +
            "YYYYY" +
            "D";

    public static String removeAccents(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            int index = VIETNAMESE_CHARS.indexOf(c);
            if (index >= 0) {
                sb.setCharAt(i, REPLACEMENT_CHARS.charAt(index));
            }
        }
        return sb.toString();
    }
}
