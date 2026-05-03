package com.example.motoshop.utils;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class VietnameseMoneyTextFormatter {
    private static final String[] digits = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};

    // Chuyển số tiền thành chữ tiếng Việt.
    public static String toWords(double amount) {
        if (amount <= 0) return "Không đồng";
        if (amount > 999_999_999_999L) return "Số quá lớn";

        long number = (long) amount;
        String res = readGroup(number);
        res = res.trim();
        if (res.isEmpty()) return "Không đồng";

        // Viết hoa chữ cái đầu cho đẹp hơn
        res = res.substring(0, 1).toUpperCase() + res.substring(1) + " đồng";
        return res.replaceAll("\\s+", " ");
    }

    // Đọc số tiền theo nhóm tỷ, triệu, nghìn và đơn vị.
    private static String readGroup(long n) {
        if (n == 0) return "";

        String res = "";
        long bill = n / 1_000_000_000;
        long million = (n % 1_000_000_000) / 1_000_000;
        long thousand = (n % 1_000_000) / 1_000;
        long unit = n % 1_000;

        if (bill > 0) res += readThreeDigits((int) bill) + " tỷ ";
        if (million > 0) res += readThreeDigits((int) million) + " triệu ";
        else if (bill > 0 && (thousand > 0 || unit > 0)) res += "không triệu ";

        if (thousand > 0) res += readThreeDigits((int) thousand) + " nghìn ";
        else if ((bill > 0 || million > 0) && unit > 0) res += "không nghìn ";

        if (unit > 0) res += readThreeDigits((int) unit);

        return res;
    }

    // Đọc một nhóm gồm ba chữ số.
    private static String readThreeDigits(int n) {
        int hundred = n / 100;
        int ten = (n % 100) / 10;
        int unit = n % 10;
        String res = "";

        if (hundred > 0) res += digits[hundred] + " trăm ";

        if (ten > 1) {
            res += digits[ten] + " mươi ";
            if (unit == 1) res += "mốt";
            else if (unit == 5) res += "lăm";
            else if (unit > 0) res += digits[unit];
        } else if (ten == 1) {
            res += "mười ";
            if (unit == 5) res += "lăm";
            else if (unit > 0) res += digits[unit];
        } else if (hundred > 0 && unit > 0) {
            res += "linh " + digits[unit];
        } else if (unit > 0) {
            res += digits[unit];
        }

        return res;
    }
}
