package com.example.custom_layout_21_04_26;

public class Phone {
    private String namephone;
    private int imagephone;
    private String price;  // Thêm thuộc tính giá bán

    // Constructor đầy đủ 3 tham số
    public Phone(String namephone, int imagephone, String price) {
        this.namephone = namephone;
        this.imagephone = imagephone;
        this.price = price;
    }

    // Getter & Setter cho namephone
    public String getNamephone() {
        return namephone;
    }

    public void setNamephone(String namephone) {
        this.namephone = namephone;
    }

    // Getter & Setter cho imagephone
    public int getImagephone() {
        return imagephone;
    }

    public void setImagephone(int imagephone) {
        this.imagephone = imagephone;
    }

    // Getter & Setter cho price (MỚI)
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
