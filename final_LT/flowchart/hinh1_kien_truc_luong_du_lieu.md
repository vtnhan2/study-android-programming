# Hình 1: Sơ đồ Kiến trúc / Luồng dữ liệu (Data Flow Architecture)

> Sơ đồ mô tả luồng dữ liệu của ứng dụng MotoShop Android theo kiến trúc MVVM, sử dụng Firebase Firestore.

```mermaid
flowchart TD
    subgraph " "
        direction TB
        CSA["CreateSaleActivity"]
        SVM["SalesViewModel"]
        INV["Cập nhật tồn kho<br/>(update inventory)"]
        FF["Firebase Firestore"]
        SO1["sales_orders"]

        CSA --> SVM
        SVM --> INV
        SVM --> FF
        SVM --> SO1
        INV --> MOTO

        subgraph Collections["Firestore Collections"]
            MOTO["motorcycles"]
            CUST["customers"]
            SO2["sales_orders"]
            RO["repair_orders"]
            RS["repair_services"]
        end

        FF --> MOTO
        FF --> CUST
        FF --> SO2
        FF --> RO
        FF --> RS

        MJ["Model Java"]
        MOTO --> MJ
        CUST --> MJ
        SO2 --> MJ
        RO --> MJ
        RS --> MJ

        VM["ViewModel"]
        MJ --> VM

        FA["Fragment / Activity"]
        VM --> FA

        ADPT["Adapter"]
        AICRA["AiChatFragment /<br/>CreateRepairActivity"]
        FA --> ADPT
        FA --> AICRA

        RV["RecyclerView / UI"]
        ADPT --> RV

        GH["GeminiHelper"]
        AICRA --> RV
        AICRA --> GH
        GH --> AICRA
    end

    classDef default fill:#f5f5f5,stroke:#999,stroke-width:1px,color:#333
    classDef collection fill:#e8e8e8,stroke:#888,stroke-width:1px,color:#333
    class MOTO,CUST,SO2,RO,RS collection
```

## Giải thích luồng dữ liệu

| Bước | Mô tả |
|------|-------|
| 1 | `CreateSaleActivity` gửi yêu cầu tạo đơn hàng đến `SalesViewModel` |
| 2 | `SalesViewModel` thực hiện 3 tác vụ: cập nhật tồn kho, ghi vào Firestore, và tạo bản ghi `sales_orders` |
| 3 | Cập nhật tồn kho cập nhật collection `motorcycles` |
| 4 | `Firebase Firestore` quản lý 5 collections: motorcycles, customers, sales_orders, repair_orders, repair_services |
| 5 | Dữ liệu từ các collection được ánh xạ sang `Model Java` (POJO) |
| 6 | `Model Java` cung cấp dữ liệu cho `ViewModel` (LiveData) |
| 7 | `ViewModel` thông báo thay đổi đến `Fragment / Activity` |
| 8 | `Fragment / Activity` phân phối dữ liệu đến `Adapter` (danh sách) hoặc `AiChatFragment / CreateRepairActivity` |
| 9 | `Adapter` hiển thị trên `RecyclerView / UI` |
| 10 | `AiChatFragment / CreateRepairActivity` tương tác với `GeminiHelper` (vòng lặp request-response AI) |
