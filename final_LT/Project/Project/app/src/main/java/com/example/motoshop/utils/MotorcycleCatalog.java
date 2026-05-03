package com.example.motoshop.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
public class MotorcycleCatalog {
    // Lớp hỗ trợ xử lý một phần chức năng dùng lại trong app.
    public static class CatalogItem {
        public String brand, model, color;
        public int year;
        public double suggestedPrice;
        public String description;

        // Constructor tạo một dòng xe trong danh mục mẫu.
        public CatalogItem(String brand, String model, String color,
                          int year, double suggestedPrice, String description) {
            this.brand = brand; this.model = model; this.color = color;
            this.year = year; this.suggestedPrice = suggestedPrice;
            this.description = description;
        }

        // Hiển thị tên model và màu xe khi đưa vào spinner.
        @Override
        public String toString() {
            return model + " (" + color + ")";
        }
    }

    public static final List<CatalogItem> CATALOG = Arrays.asList(
        // Hãng Honda
        new CatalogItem("Honda","Wave Alpha","Đỏ đen",2024,18500000,"Xe số phổ thông, tiết kiệm xăng"),
        new CatalogItem("Honda","Wave RSX","Xanh đen",2024,21000000,"Xe số thể thao, phanh đĩa trước"),
        new CatalogItem("Honda","Future 125","Đen nhám",2024,30500000,"Xe số cao cấp, động cơ Fi"),
        new CatalogItem("Honda","Winner X","Đỏ đen",2024,46900000,"Xe số thể thao 150cc"),
        new CatalogItem("Honda","SH 125i","Xám bạc",2024,70900000,"Xe tay ga cao cấp"),
        new CatalogItem("Honda","SH 150i","Trắng",2024,85900000,"Xe tay ga cao cấp 150cc"),
        new CatalogItem("Honda","SH Mode","Đỏ đô",2024,53900000,"Xe tay ga thời trang"),
        new CatalogItem("Honda","Vision","Xanh pastel",2024,33900000,"Xe tay ga phổ thông"),
        new CatalogItem("Honda","Lead","Trắng bạc",2024,37900000,"Xe tay ga cốp rộng"),
        new CatalogItem("Honda","PCX 125","Đen bóng",2024,82900000,"Xe tay ga cao cấp nhập khẩu"),
        new CatalogItem("Honda","Air Blade","Xanh đen",2024,40900000,"Xe tay ga thể thao"),
        new CatalogItem("Honda","Blade","Đỏ trắng",2024,20500000,"Xe số giá rẻ"),
        // Hãng Yamaha
        new CatalogItem("Yamaha","Exciter 155","Xanh đen",2024,52900000,"Xe số thể thao 155cc VVA"),
        new CatalogItem("Yamaha","Exciter 150","Đỏ đen",2024,46900000,"Xe số thể thao 150cc"),
        new CatalogItem("Yamaha","NVX 155","Xanh lam",2024,55900000,"Xe tay ga thể thao 155cc"),
        new CatalogItem("Yamaha","NVX 125","Trắng",2024,47900000,"Xe tay ga thể thao 125cc"),
        new CatalogItem("Yamaha","Grande","Hồng pastel",2024,48900000,"Xe tay ga nữ cao cấp"),
        new CatalogItem("Yamaha","Freego","Xanh ngọc",2024,36900000,"Xe tay ga cốp lớn"),
        new CatalogItem("Yamaha","Janus","Tím than",2024,31900000,"Xe tay ga phổ thông"),
        new CatalogItem("Yamaha","Sirius","Đỏ đen",2024,20500000,"Xe số phổ thông bền bỉ"),
        new CatalogItem("Yamaha","Jupiter","Xanh trắng",2024,22500000,"Xe số gia đình"),
        new CatalogItem("Yamaha","Latte","Kem trắng",2024,35900000,"Xe tay ga phong cách Châu Âu"),
        // Hãng SYM
        new CatalogItem("SYM","Attila Elizabeth","Trắng bạc",2024,28500000,"Xe tay ga nữ cổ điển"),
        new CatalogItem("SYM","Star SR","Đen bóng",2024,22000000,"Xe tay ga phổ thông"),
        new CatalogItem("SYM","Galaxy","Xám titan",2024,32000000,"Xe tay ga cốp rộng"),
        // Hãng Piaggio
        new CatalogItem("Piaggio","Vespa LX 125","Xanh cổ điển",2024,72000000,"Xe tay ga cổ điển Ý"),
        new CatalogItem("Piaggio","Vespa Sprint","Đỏ Ý",2024,88000000,"Xe tay ga thể thao Ý"),
        new CatalogItem("Piaggio","Liberty","Trắng ngà",2024,55000000,"Xe tay ga thực dụng Ý"),
        new CatalogItem("Piaggio","Medley","Đen nhám",2024,95000000,"Xe tay ga cao cấp nhập khẩu"),
        // Hãng Suzuki
        new CatalogItem("Suzuki","Raider R150","Đen vàng",2024,49900000,"Xe số thể thao 150cc"),
        new CatalogItem("Suzuki","Address","Xanh bạc",2024,29900000,"Xe tay ga nhỏ gọn nhập khẩu"),
        new CatalogItem("Suzuki","Avenis","Trắng đỏ",2024,39900000,"Xe tay ga thể thao 125cc")
    );

    // Lấy danh sách hãng xe không trùng lặp
    public static List<String> getBrands() {
        List<String> brands = new ArrayList<>();
        for (CatalogItem item : CATALOG) {
            if (!brands.contains(item.brand)) brands.add(item.brand);
        }
        return brands;
    }

    // Lấy danh sách model theo hãng
    public static List<CatalogItem> getByBrand(String brand) {
        List<CatalogItem> result = new ArrayList<>();
        for (CatalogItem item : CATALOG) {
            if (item.brand.equals(brand)) result.add(item);
        }
        return result;
    }
}
