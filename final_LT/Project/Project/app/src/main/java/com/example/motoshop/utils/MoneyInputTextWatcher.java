package com.example.motoshop.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class MoneyInputTextWatcher implements TextWatcher {

    private final EditText editText;
    private final TextView tvWords;
    private String current = "";

    // Constructor khởi tạo object của class này.
    public MoneyInputTextWatcher(EditText editText, TextView tvWords) {
        this.editText = editText;
        this.tvWords = tvWords;
    }

    // Hàm bắt sự kiện thay đổi chữ trong ô nhập liệu.
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    // Hàm bắt sự kiện thay đổi chữ trong ô nhập liệu.
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    // Xử lý lại dữ liệu sau khi người dùng nhập xong.
    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().equals(current)) {
            editText.removeTextChangedListener(this);

            String cleanString = s.toString().replaceAll("[^\\d]", "");

            if (cleanString.isEmpty()) {
                current = "";
                editText.setText("");
                if (tvWords != null) tvWords.setText("");
            } else {
                try {
                    double parsed = Double.parseDouble(cleanString);

                    // Định dạng số tiền: 1.000.000
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("vi", "VN"));
                    formatter.applyPattern("#,###");
                    String formatted = formatter.format(parsed).replace(",", ".");

                    current = formatted;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());

                    // Hiển thị số tiền bằng chữ
                    if (tvWords != null) {
                        tvWords.setText(VietnameseMoneyTextFormatter.toWords(parsed));
                    }
                } catch (NumberFormatException e) {
                    // Nếu nhập lỗi thì giữ nguyên, không làm app bị crash
                }
            }

            editText.addTextChangedListener(this);
        }
    }

    // Lấy giá trị dữ liệu đang được lưu trong object.
    public static double getRawValue(EditText et) {
        String s = et.getText().toString().replaceAll("[^\\d]", "");
        if (s.isEmpty()) return 0;
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }
}
