# Hình 4: Sơ đồ Điều hướng / Flowchart (Navigation Diagram)

> Sơ đồ luồng màn hình ứng dụng MotoShop với cấu trúc phân cấp cây từ trên xuống dưới, mô tả quá trình khởi động ứng dụng và điều hướng giữa các màn hình.

```mermaid
flowchart TD
    OPEN["Mở app<br/>(Open app)"]
    SPLASH["SplashActivity"]
    LOGGEDIN{"Đã đăng nhập?<br/>(Logged in?)"}
    LOGIN["LoginActivity"]
    MAIN["MainActivity"]

    OPEN --> SPLASH
    SPLASH --> LOGGEDIN
    LOGGEDIN -->|"Chưa (No)"| LOGIN
    LOGGEDIN -->|"Rồi (Yes)"| MAIN
    LOGIN --> MAIN

    %% Cấp dưới MainActivity - 7 Fragment
    DASH["DashboardFragment"]
    INVF["InventoryFragment"]
    CUSTF["CustomerFragment"]
    SALESF["SalesFragment"]
    REPAIRF["RepairFragment"]
    AIF["AiChatFragment"]
    UPF["UserProfileFragment"]

    MAIN --> DASH
    MAIN --> INVF
    MAIN --> CUSTF
    MAIN --> SALESF
    MAIN --> REPAIRF
    MAIN --> AIF
    MAIN --> UPF

    %% Cấp chi tiết nhất - Activity con
    MDETAIL["MotorcycleDetailActivity"]
    ADDEDIT["AddEditMotorcycleActivity"]
    CUSTDETAIL["CustomerDetailActivity"]
    CREATESALE["CreateSaleActivity"]
    CREATEREPAIR["CreateRepairActivity"]
    REPAIRDETAIL["RepairDetailActivity"]

    INVF --> MDETAIL
    INVF --> ADDEDIT
    CUSTF --> CUSTDETAIL
    SALESF --> CREATESALE
    REPAIRF --> CREATEREPAIR
    REPAIRF --> REPAIRDETAIL

    classDef default fill:#f5f5f5,stroke:#999,stroke-width:1px,color:#333
    classDef decision fill:#fff8e1,stroke:#f9a825,stroke-width:2px,color:#333
    classDef main fill:#e3f2fd,stroke:#1976d2,stroke-width:2px,color:#333
    classDef fragment fill:#f3e5f5,stroke:#7b1fa2,stroke-width:1px,color:#333
    classDef detail fill:#e8f5e9,stroke:#388e3c,stroke-width:1px,color:#333

    class LOGGEDIN decision
    class MAIN main
    class DASH,INVF,CUSTF,SALESF,REPAIRF,AIF,UPF fragment
    class MDETAIL,ADDEDIT,CUSTDETAIL,CREATESALE,CREATEREPAIR,REPAIRDETAIL detail
```

## Mô tả luồng điều hướng

### Luồng khởi động
1. **Mở app** → `SplashActivity` (màn hình chờ với logo MotoShop)
2. Kiểm tra trạng thái đăng nhập:
   - **Chưa đăng nhập** → `LoginActivity` → `MainActivity`
   - **Đã đăng nhập** → `MainActivity` (bỏ qua login)

### Cấp Fragment (từ MainActivity)
| Fragment | Mô tả |
|----------|-------|
| DashboardFragment | Trang tổng quan / trang chủ khách hàng |
| InventoryFragment | Danh sách kho xe |
| CustomerFragment | Quản lý khách hàng |
| SalesFragment | Quản lý đơn bán hàng |
| RepairFragment | Quản lý sửa chữa |
| AiChatFragment | Trợ lý AI / tìm xe |
| UserProfileFragment | Hồ sơ cá nhân |

### Cấp Activity chi tiết
| Từ Fragment | Đến Activity | Chức năng |
|-------------|-------------|-----------|
| InventoryFragment | MotorcycleDetailActivity | Xem chi tiết xe |
| InventoryFragment | AddEditMotorcycleActivity | Thêm/sửa xe |
| CustomerFragment | CustomerDetailActivity | Chi tiết khách hàng |
| SalesFragment | CreateSaleActivity | Tạo đơn bán hàng |
| RepairFragment | CreateRepairActivity | Tạo phiếu sửa chữa |
| RepairFragment | RepairDetailActivity | Chi tiết phiếu sửa chữa |
