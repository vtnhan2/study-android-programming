# 📖 Tài liệu Phân tích Dự án MotoShop

> **Mục đích**: Tài liệu tổng hợp để hiểu toàn bộ project, phục vụ presentation và bảo trì. Kết hợp với 4 sơ đồ kỹ thuật trong cùng thư mục.

---

## 1. Tổng quan dự án

**MotoShop** là ứng dụng Android quản lý cửa hàng xe máy, hỗ trợ:
- Quản lý kho xe, bán hàng, sửa chữa, khách hàng
- Phân quyền 4 vai trò (Admin, Sales, Technician, Customer)
- Tích hợp AI Gemini để phân tích lỗi xe và tư vấn
- Chương trình tích điểm thành viên tự động

| Thông tin | Chi tiết |
|-----------|----------|
| **Ngôn ngữ** | Java (Android) |
| **Kiến trúc** | MVVM (Model - View - ViewModel) |
| **Database** | Firebase Firestore (realtime sync) |
| **AI Engine** | Google Gemini 2.5 Flash (REST API) |
| **Min SDK** | Android 7.0 (API 24) |
| **Ngôn ngữ UI** | Tiếng Việt thuần |

---

## 2. Sơ đồ tham chiếu

| Sơ đồ | File | Nội dung |
|-------|------|----------|
| Hình 1 | `hinh1_kien_truc_luong_du_lieu.md` | Kiến trúc MVVM & luồng dữ liệu |
| Hình 2 | `hinh2_bieu_do_use_case.md` | Biểu đồ Use Case — 4 tác nhân, 9 chức năng |
| Hình 3 | `hinh3_bieu_do_lop.md` | Biểu đồ Lớp UML — 16 class chính |
| Hình 4 | `hinh4_so_do_dieu_huong.md` | Sơ đồ điều hướng màn hình |

> 💡 Mở bằng VS Code + extension **Markdown Preview Mermaid Support** (Ctrl+Shift+V)

---

## 3. Cấu trúc thư mục mã nguồn

```
com.example.motoshop/
├── MotoShopApplication.java          ← Entry point
├── data/model/                       ← 10 POJO (Firestore mapping)
│   ├── Motorcycle.java
│   ├── Customer.java
│   ├── SalesOrder.java / SalesOrderItem.java
│   ├── RepairOrder.java / RepairService.java
│   ├── CustomerVehicle.java
│   ├── Supplier.java / ImportOrder.java
│   └── Staff.java
├── viewmodel/                        ← 4 ViewModel + BaseViewModel
│   ├── BaseViewModel.java            ← Quản lý Firestore listener lifecycle
│   ├── MotorcycleViewModel.java
│   ├── CustomerViewModel.java
│   ├── SalesViewModel.java
│   └── RepairViewModel.java
├── ui/                               ← 11 package giao diện
│   ├── splash/   → SplashActivity
│   ├── login/    → LoginActivity
│   ├── main/     → MainActivity (NavHost)
│   ├── dashboard/ → DashboardFragment + UserDashboardFragment
│   ├── inventory/ → InventoryFragment, MotorcycleDetailActivity, AddEditMotorcycleActivity
│   ├── customer/  → CustomerFragment, CustomerDetailActivity
│   ├── sales/     → SalesFragment, CreateSaleActivity
│   ├── repair/    → RepairFragment, CreateRepairActivity, RepairDetailActivity
│   ├── ai/        → AiChatFragment
│   ├── staff/     → (quản lý nhân viên)
│   └── supplier/  → (quản lý nhà cung cấp)
└── utils/                            ← 12 lớp tiện ích
    ├── UserSession.java              ← SharedPreferences auth
    ├── GeminiHelper.java             ← Gemini AI REST client
    ├── FirebaseSeeder.java           ← Import dữ liệu mẫu
    ├── CurrencyFormatter.java        ← Định dạng tiền VNĐ
    ├── DateUtils.java                ← Sinh mã đơn, format ngày
    ├── LocaleHelper.java             ← Hỗ trợ đa ngôn ngữ
    ├── ThemeHelper.java              ← Dark/Light mode
    └── VNCharacterUtils.java         ← Xử lý ký tự tiếng Việt
```

---

## 4. Luồng hoạt động chính

### 4.1. Luồng khởi động & đăng nhập

```mermaid
flowchart LR
    A["Mở app"] --> B["SplashActivity"]
    B --> C{"Đã đăng nhập?"}
    C -->|Chưa| D["LoginActivity"]
    C -->|Rồi| E["MainActivity"]
    D --> F{"Staff hay\nKhách hàng?"}
    F -->|Staff| G["Query: staff collection\n(staffId + password)"]
    F -->|Khách| H["Query: customers collection\n(phone + pass mặc định '123')"]
    G --> I["createLoginSession()"]
    H --> J["createCustomerSession()"]
    I --> E
    J --> E
```

**Code core — Xác thực đăng nhập**:
- `LoginActivity.java` → method `performLogin()` (dòng 86-118): Query Firestore collection `staff`, fallback sang `customers`
- `UserSession.java` → method `createLoginSession()` / `createCustomerSession()`: Lưu session vào SharedPreferences

### 4.2. Luồng phân quyền giao diện

Sau đăng nhập, `MainActivity` quyết định giao diện dựa trên `UserSession.getUserRole()`:

```mermaid
flowchart TD
    MA["MainActivity.onCreate()"] --> CHK{"getUserRole()"}
    CHK -->|ADMIN| ADM["menu_admin.xml\n5 tab: Dashboard, Kho xe,\nKhách hàng, Bán hàng, Sửa chữa"]
    CHK -->|SALES| SL["menu_sales.xml\n3 tab: Dashboard, Kho xe, Bán hàng"]
    CHK -->|TECHNICIAN| TH["menu_tech.xml\n3 tab: Dashboard, Kho xe, Sửa chữa"]
    CHK -->|USER| US["menu_user.xml\n2 tab: Trang chủ, Kho xe\n+ UserDashboardFragment"]
```

**Code core — Phân quyền menu**:
- `MainActivity.java` → dòng 64-73: `bottomNav.inflateMenu()` theo role
- `MainActivity.java` → dòng 56-62: Chọn `startDestination` (DashboardFragment vs UserDashboardFragment)
- `MainActivity.java` → method `setupMenuByRole()` (dòng 144-164): Ẩn/hiện drawer menu items

### 4.3. Luồng tạo đơn bán hàng

```mermaid
flowchart TD
    A["CreateSaleActivity"] --> B["Chọn khách hàng\n(AutoComplete từ Firestore)"]
    B --> C["Chọn xe từ kho\n(lọc quantity > 0)"]
    C --> D["Tính tổng tiền\ncalculateSummary()"]
    D --> E["Nhập giảm giá + ghi chú"]
    E --> F["confirmOrder()"]
    F --> G["SalesViewModel.createOrder()"]
    G --> H["Firestore Transaction"]
    H --> I["1. Kiểm tra tồn kho"]
    H --> J["2. Trừ quantity xe"]
    H --> K["3. Ghi sales_orders"]
```

**Code core — Transaction bán hàng**:
- `SalesViewModel.java` → method `createOrder()` (dòng 215-262): Firestore Transaction đảm bảo atomic (kiểm tra tồn kho → trừ kho → ghi đơn)
- `CreateSaleActivity.java` → method `confirmOrder()` (dòng 230-273): Build SalesOrder object, gọi ViewModel

### 4.4. Luồng tạo phiếu sửa chữa (có AI)

```mermaid
flowchart TD
    A["CreateRepairActivity"] --> B["Chọn khách hàng"]
    B --> C["Auto-fill xe đã mua\n(từ sales_orders)"]
    C --> D["Mô tả lỗi xe"]
    D --> E{"Nhấn 'AI Phân tích'"}
    E --> F["GeminiHelper.suggestRepairs()"]
    F --> G["Gemini API trả về\nJSON array documentId"]
    G --> H["Highlight dịch vụ\nđược AI gợi ý"]
    H --> I["Kỹ thuật viên duyệt\n+ thêm hạng mục phát sinh"]
    I --> J["Lưu RepairOrder\n(status: RECEIVED/IN_PROGRESS)"]
    E -->|Lỗi AI| K["Fallback: gợi ý\nbằng keyword matching"]
    K --> H
```

**Code core — Tích hợp AI Gemini**:
- `GeminiHelper.java` → method `suggestRepairs()` (dòng 120-126): Xây prompt chuyên gia kỹ thuật, gửi danh sách dịch vụ dạng JSON
- `GeminiHelper.java` → method `callApi()` (dòng 93-118): OkHttp POST đến Gemini REST API, parse response
- `CreateRepairActivity.java` → method `performAiAnalysis()` (dòng 195-254): Gọi AI, parse JSON response, highlight dịch vụ gợi ý
- `CreateRepairActivity.java` → method `isMatchingService()` (dòng 279-292): Fallback keyword matching khi AI lỗi

### 4.5. Luồng tích điểm thành viên

```mermaid
flowchart LR
    A["Hoàn tất đơn hàng"] --> B["completeOrderWithLoyalty()"]
    B --> C["Firestore Transaction"]
    C --> D["Tính điểm:\nfinalAmount / 100,000"]
    D --> E{"Tổng chi tiêu?"}
    E -->|"≥ 100tr"| F["VIP"]
    E -->|"≥ 50tr"| G["GOLD"]
    E -->|"≥ 20tr"| H["SILVER"]
    E -->|"< 20tr"| I["NORMAL"]
```

**Code core — Loyalty system**:
- `SalesViewModel.java` → method `completeOrderWithLoyalty()` (dòng 129-169): Transaction cập nhật đồng thời status đơn hàng + điểm + hạng thành viên

---

## 5. Vai trò và Chức năng chi tiết

### 5.1. 👑 Admin (Quản trị viên)

| Chức năng | Mô tả | Code vị trí |
|-----------|-------|-------------|
| Dashboard tổng quan | Doanh thu, số đơn, tồn kho, leaderboard nhân viên | `DashboardFragment.java` → `observeData()` |
| Quản lý kho xe (CRUD) | Thêm/sửa/xóa xe, lọc theo hãng/giá/trạng thái | `InventoryFragment.java`, `AddEditMotorcycleActivity.java` |
| Quản lý khách hàng | Thêm/sửa/xóa KH, xem lịch sử mua + sửa | `CustomerFragment.java`, `CustomerDetailActivity.java` |
| Quản lý đơn bán hàng | Tạo đơn, hoàn tất (+ tích điểm), hủy (+ hoàn kho) | `CreateSaleActivity.java`, `SalesFragment.java` |
| Quản lý sửa chữa | Tạo phiếu, phân tích AI, duyệt/từ chối | `CreateRepairActivity.java`, `RepairDetailActivity.java` |
| Import dữ liệu mẫu | Upload seed_data.json lên Firestore | `FirebaseSeeder.java` → `uploadSeedData()` |
| Chat AI | Hỏi đáp tự do với Gemini | `AiChatFragment.java` |
| FAB tạo đơn nhanh | Chọn tạo đơn bán hoặc phiếu sửa | `DashboardFragment.java` → `setupFab()` |

### 5.2. 💼 Nhân viên bán hàng (Sales)

| Chức năng | Mô tả | Code vị trí |
|-----------|-------|-------------|
| Dashboard | Doanh thu cá nhân, leaderboard | `DashboardFragment.java` |
| Xem kho xe | Duyệt và xem chi tiết xe | `InventoryFragment.java`, `MotorcycleDetailActivity.java` |
| Tạo đơn bán | Chọn KH → chọn xe → tính tiền → xác nhận | `CreateSaleActivity.java` |
| Chat AI | Tìm xe phù hợp cho khách | `AiChatFragment.java` |

### 5.3. 🔧 Kỹ thuật viên (Technician)

| Chức năng | Mô tả | Code vị trí |
|-----------|-------|-------------|
| Dashboard | Số phiếu đang xử lý | `DashboardFragment.java` |
| Xem kho xe | Tra cứu thông tin xe | `InventoryFragment.java` |
| Tạo phiếu sửa chữa | Nhập lỗi → AI gợi ý → duyệt dịch vụ | `CreateRepairActivity.java` |
| Chat AI | Hỏi AI về cách sửa lỗi kỹ thuật | `AiChatFragment.java` |
| Hồ sơ cá nhân | Xem thông tin tài khoản | `UserProfileFragment.java` |

### 5.4. 👤 Khách hàng (Customer / User)

| Chức năng | Mô tả | Code vị trí |
|-----------|-------|-------------|
| Trang chủ riêng | Chào hỏi, hạng thành viên, điểm tích lũy | `UserDashboardFragment.java` → `observeData()` |
| Duyệt kho xe | Xem danh sách xe, chi tiết, chia sẻ | `InventoryFragment.java`, `MotorcycleDetailActivity.java` |
| Yêu thích xe | Toggle favorite, lưu vào Firestore | `UserDashboardFragment.java` → `onFavoriteClicked()` |
| Lịch sử mua hàng | Xem đơn hàng của mình | `UserDashboardFragment.java` → `salesAdapterFilter()` |
| Lịch sử sửa chữa | Xem phiếu sửa chữa của mình | `UserDashboardFragment.java` → `repairAdapterFilter()` |
| Chat AI | Tìm xe, hỏi tư vấn | `AiChatFragment.java` |

---

## 6. Bảng phân quyền tổng hợp

| Chức năng | Admin | Sales | Tech | Customer |
|-----------|:-----:|:-----:|:----:|:--------:|
| Dashboard (quản lý) | ✅ | ✅ | ✅ | ❌ |
| Dashboard (khách hàng) | ❌ | ❌ | ❌ | ✅ |
| Xem kho xe | ✅ | ✅ | ✅ | ✅ |
| Thêm/Sửa/Xóa xe | ✅ | ❌ | ❌ | ❌ |
| Quản lý khách hàng | ✅ | ❌ | ❌ | ❌ |
| Tạo đơn bán hàng | ✅ | ✅ | ❌ | ❌ |
| Hoàn tất/Hủy đơn | ✅ | ✅ | ❌ | ❌ |
| Tạo phiếu sửa chữa | ✅ | ❌ | ✅ | ❌ |
| Cập nhật phiếu sửa | ✅ | ❌ | ✅ | ❌ |
| Yêu thích xe | ❌ | ❌ | ❌ | ✅ |
| Xem lịch sử cá nhân | ❌ | ❌ | ❌ | ✅ |
| Import dữ liệu mẫu | ✅ | ❌ | ❌ | ❌ |
| Chat AI Gemini | ✅ | ✅ | ✅ | ✅ |
| Hồ sơ cá nhân | ✅ | ❌ | ✅ | ✅ |

---

## 7. Firebase Firestore Collections

```mermaid
erDiagram
    staff {
        string staffId PK
        string name
        string role
        string password
        string phone
    }
    customers {
        string documentId PK
        string name
        string phone
        string memberRank
        int loyaltyPoints
        double totalSpent
    }
    motorcycles {
        string documentId PK
        string brand
        string model
        double price
        int quantity
        string imageUri
    }
    sales_orders {
        string documentId PK
        string orderCode
        string customerDocumentId FK
        double finalAmount
        string status
        long orderDate
    }
    repair_orders {
        string documentId PK
        string repairCode
        string customerDocumentId FK
        string issueDescription
        string status
        double totalCost
    }
    repair_services {
        string documentId PK
        string name
        double defaultLaborCost
        double defaultPartsCost
    }
    customer_vehicles {
        string documentId PK
        string customerDocumentId FK
        string licensePlate
        string brand
    }
    suppliers {
        string documentId PK
        string name
        string taxCode
    }

    customers ||--o{ sales_orders : "mua hàng"
    customers ||--o{ repair_orders : "sửa chữa"
    customers ||--o{ customer_vehicles : "sở hữu"
    motorcycles ||--o{ sales_orders : "được bán"
    repair_services ||--o{ repair_orders : "áp dụng"
```

### Quy tắc đặt Document ID

| Collection | Format | Ví dụ |
|------------|--------|-------|
| staff | `staffId` gốc | `AD01`, `SL01`, `KT01` |
| customers | `KH_[phone]` | `KH_0901234567` |
| motorcycles | `[brand]_[model]_[year]` | `honda_air_blade_125_2024` |
| sales_orders | `DH_[yyMMdd]_[stt]` | `DH_260503_001` |
| repair_orders | `SC_[yyMMdd]_[stt]` | `SC_260503_001` |
| repair_services | `DV_[TEN]` hoặc `PT_[TEN]` | `DV_THAY_NHOT`, `PT_LOC_GIO` |
| suppliers | `NCC_[TEN]` | `NCC_HONDA_VIET_NAM` |

---

## 8. Tính năng nổi bật (Điểm nhấn Presentation)

### 🤖 8.1. Tích hợp AI Gemini

- **Chat tự do**: `AiChatFragment` + `GeminiHelper.chat()` — lưu lịch sử hội thoại
- **Phân tích lỗi xe**: `GeminiHelper.suggestRepairs()` — prompt chuyên gia, trả về JSON service IDs
- **Tìm xe thông minh**: `GeminiHelper.searchMotorcycle()` — mô tả tự nhiên → gợi ý xe phù hợp
- **Fallback an toàn**: Khi AI lỗi/timeout → dùng keyword matching cục bộ

> 📍 **Core file**: `utils/GeminiHelper.java` (133 dòng)

### 💎 8.2. Hệ thống tích điểm thành viên

- Tự động tính khi hoàn tất đơn: `100,000đ = 1 điểm`
- 4 hạng: NORMAL → SILVER (20tr) → GOLD (50tr) → VIP (100tr)
- Sử dụng **Firestore Transaction** đảm bảo tính nhất quán

> 📍 **Core file**: `viewmodel/SalesViewModel.java` → `completeOrderWithLoyalty()` (dòng 129-169)

### 🔄 8.3. Realtime Sync với Firestore

- Tất cả ViewModel dùng `addSnapshotListener` — dữ liệu cập nhật realtime
- `BaseViewModel` quản lý lifecycle listener tự động (tránh memory leak)

> 📍 **Core file**: `viewmodel/BaseViewModel.java`

### 🛡️ 8.4. Transaction an toàn

- **Bán hàng**: Kiểm tra tồn kho → trừ kho → ghi đơn (atomic)
- **Hủy đơn**: Hoàn kho tự động + ghi lý do hủy
- **Tích điểm**: Cập nhật đồng thời đơn hàng + khách hàng

> 📍 **Core file**: `viewmodel/SalesViewModel.java` → `createOrder()`, `cancelOrder()`, `completeOrderWithLoyalty()`

### 🎨 8.5. Giao diện phân quyền động

- **4 bộ menu** Bottom Navigation khác nhau theo role
- **2 Dashboard** riêng biệt: nhân viên (quản lý) vs khách hàng (tiêu dùng)
- **Drawer menu** ẩn/hiện theo quyền

> 📍 **Core file**: `ui/main/MainActivity.java` → `onCreate()` (dòng 56-73), `setupMenuByRole()` (dòng 144-164)

---

## 9. Tài khoản demo đăng nhập

| Vai trò | Mã đăng nhập | Mật khẩu |
|---------|-------------|----------|
| Admin | `AD01` | `1111` |
| Nhân viên bán hàng | `SL01` | `2222` |
| Kỹ thuật viên | `KT01` | `3333` |
| Khách hàng | `[SĐT trong DB]` | `123` |

> 📍 **Core file**: `ui/login/LoginActivity.java` → `setDemoAccount()` (dòng 71-75), `switchToCustomerMode()` (dòng 78-83)

---

## 10. Tóm tắt Core Code quan trọng cho Presentation

| # | Chủ đề | File | Method/Dòng |
|---|--------|------|-------------|
| 1 | Đăng nhập & phân quyền | `LoginActivity.java` | `performLogin()` (L86-118) |
| 2 | Phân quyền giao diện | `MainActivity.java` | `onCreate()` (L56-73) |
| 3 | Kiến trúc MVVM | `BaseViewModel.java` | `registerListener()` |
| 4 | Realtime data | `MotorcycleViewModel.java` | `listenToFirebase()` (L66-80) |
| 5 | Tạo đơn bán (Transaction) | `SalesViewModel.java` | `createOrder()` (L215-262) |
| 6 | Hủy đơn + hoàn kho | `SalesViewModel.java` | `cancelOrder()` (L171-213) |
| 7 | Tích điểm thành viên | `SalesViewModel.java` | `completeOrderWithLoyalty()` (L129-169) |
| 8 | AI phân tích lỗi xe | `GeminiHelper.java` | `suggestRepairs()` (L120-126) |
| 9 | AI chat đa lượt | `GeminiHelper.java` | `chat()` (L60-80) |
| 10 | Xử lý AI response | `CreateRepairActivity.java` | `performAiAnalysis()` (L195-254) |
| 11 | Fallback keyword | `CreateRepairActivity.java` | `isMatchingService()` (L279-292) |
| 12 | Seed dữ liệu mẫu | `FirebaseSeeder.java` | `uploadSeedData()` (L21-46) |
| 13 | Session management | `UserSession.java` | Toàn bộ file (92 dòng) |
| 14 | Dashboard khách hàng | `UserDashboardFragment.java` | `observeData()` (L174-215) |
| 15 | Yêu thích xe | `UserDashboardFragment.java` | `onFavoriteClicked()` (L127-148) |

---

## 11. 👥 Phân chia Nội dung Thuyết trình — Chia File Cụ thể

> **Tổng cộng: 69 file Java · 46 layout · 48 drawable · 10 menu · 1 nav_graph · 7 values**
> Mỗi file được gán cho đúng 1 người, không bỏ sót file nào.

---

### 🛡️ Thành viên 1: Trí — Backend & AI Engineer (Nhóm trưởng)

**Trình bày**: Kiến trúc Firebase, ViewModel lõi, tích hợp AI Gemini, seed dữ liệu.

#### Java files (20 file)

| Package | File |
|---------|------|
| *(root)* | `MotoShopApplication.java` |
| `data/model` | `Motorcycle.java` · `Customer.java` · `Staff.java` · `Supplier.java` · `ImportOrder.java` |
| `viewmodel` | `BaseViewModel.java` · `MotorcycleViewModel.java` · `SalesViewModel.java` · `StaffViewModel.java` · `SupplierViewModel.java` |
| `ui/ai` | `AiChatFragment.java` · `AiSearchResultAdapter.java` · `ChatAdapter.java` · `ChatMessage.java` |
| `utils` | `GeminiHelper.java` · `FirebaseSeeder.java` · `UserSession.java` · `AppConfig.java` · `MotorcycleCatalog.java` |

#### Layout files (3 file)

`fragment_ai_chat.xml` · `item_ai_search_result.xml` · `item_chat_message.xml`

---

### 🎨 Thành viên 2: Quân — Frontend & UI/UX Designer

**Trình bày**: Navigation, màn hình khởi động/đăng nhập, Dashboard, Thông báo, toàn bộ tài nguyên giao diện.

#### Java files (14 file)

| Package | File |
|---------|------|
| `ui/splash` | `SplashActivity.java` |
| `ui/login` | `LoginActivity.java` |
| `ui/main` | `MainActivity.java` |
| `ui/dashboard` | `DashboardFragment.java` · `UserDashboardFragment.java` · `BikeCardAdapter.java` · `RecentOrderAdapter.java` · `RevenueChartFragment.java` |
| `ui/notifications` | `NotificationsFragment.java` · `NewsAdapter.java` · `NewsItem.java` |
| `utils` | `ThemeHelper.java` · `LocaleHelper.java` · `VNCharacterUtils.java` |

#### Layout files (11 file)

`activity_splash.xml` · `activity_login.xml` · `activity_main.xml` · `fragment_dashboard.xml` · `fragment_user_dashboard.xml` · `fragment_revenue_chart.xml` · `fragment_notifications.xml` · `item_news.xml` · `item_bike_card.xml` · `nav_header_drawer.xml` · `dialog_settings.xml`

#### Resource files (tất cả — 65 file)

**Drawable (48 file)**:
`bg_avatar_placeholder.xml` · `bg_explore_button.xml` · `bg_gradient_chart.xml` · `bg_gradient_green.xml` · `bg_gradient_navy.xml` · `bg_gradient_orange.xml` · `bg_gradient_purple.xml` · `bg_heart_circle.xml` · `bg_hero_banner.xml` · `bg_info_card.xml` · `bg_notification_dot.xml` · `bg_quantity_badge.xml` · `bg_search_view.xml` · `bg_stat_card.xml` · `bg_stat_card_total.xml` · `bg_status_badge.xml` · `ic_add.xml` · `ic_ai.xml` · `ic_back.xml` · `ic_best_deal.xml` · `ic_camera.xml` · `ic_currency.xml` · `ic_customer.xml` · `ic_dashboard.xml` · `ic_heart_outline.xml` · `ic_help_white.xml` · `ic_home.xml` · `ic_home_white.xml` · `ic_inventory.xml` · `ic_launcher_background.xml` · `ic_launcher_foreground.xml` · `ic_logout.xml` · `ic_menu_drawer.xml` · `ic_motorcycle_logo.xml` · `ic_notification_bell.xml` · `ic_notification_white.xml` · `ic_profile.xml` · `ic_rate_white.xml` · `ic_repair.xml` · `ic_revenue.xml` · `ic_sales.xml` · `ic_search.xml` · `ic_settings.xml` · `ic_share.xml` · `ic_signout_white.xml` · `ic_star.xml` · `ic_supplier.xml` · `ic_wheel.xml`

**Menu (10 file)**:
`bottom_nav_menu.xml` · `drawer_menu.xml` · `main_top_menu.xml` · `menu_admin.xml` · `menu_customer_detail.xml` · `menu_dashboard.xml` · `menu_inventory.xml` · `menu_sales.xml` · `menu_tech.xml` · `menu_user.xml`

**Navigation (1 file)**:
`nav_graph.xml`

**Values (7 file)**:
`values/colors.xml` · `values/dimens.xml` · `values/strings.xml` · `values/themes.xml` · `values-night/colors.xml` · `values-night/themes.xml` · `values-en/strings.xml`

---

### 📦 Thành viên 3: Thịnh — Sales & Inventory Specialist

**Trình bày**: Module kho xe, module bán hàng, quản lý nhân viên, tiện ích tài chính.

#### Java files (16 file)

| Package | File |
|---------|------|
| `data/model` | `SalesOrder.java` · `SalesOrderItem.java` |
| `viewmodel` | `CustomerVehicleViewModel.java` |
| `ui/inventory` | `InventoryFragment.java` · `MotorcycleDetailActivity.java` · `AddEditMotorcycleActivity.java` · `MotorcycleAdapter.java` |
| `ui/sales` | `SalesFragment.java` · `CreateSaleActivity.java` · `SalesOrderAdapter.java` |
| `ui/staff` | `StaffFragment.java` · `StaffAdapter.java` |
| `utils` | `CurrencyFormatter.java` · `MoneyInputTextWatcher.java` · `VietnameseMoneyTextFormatter.java` · `DateUtils.java` |

#### Layout files (13 file)

`fragment_inventory.xml` · `activity_add_edit_motorcycle.xml` · `activity_motorcycle_detail.xml` · `fragment_sales.xml` · `activity_create_sale.xml` · `item_motorcycle.xml` · `item_spec_row.xml` · `item_sales_order.xml` · `item_order_detail_bike.xml` · `item_selected_motorcycle.xml` · `item_history_simple.xml` · `dialog_cancel_order.xml` · `dialog_order_details.xml`

---

### 🛠️ Thành viên 4: Nhân — Services & Customer Specialist

**Trình bày**: Module sửa chữa (+ AI gợi ý dịch vụ), module khách hàng, module nhà cung cấp, hệ thống tích điểm.

#### Java files (19 file)

| Package | File |
|---------|------|
| `data/model` | `RepairOrder.java` · `RepairService.java` · `CustomerVehicle.java` |
| `viewmodel` | `CustomerViewModel.java` · `RepairViewModel.java` |
| `ui/repair` | `RepairFragment.java` · `CreateRepairActivity.java` · `RepairDetailActivity.java` · `RepairOrderAdapter.java` · `RepairServiceAdapter.java` · `RepairServicesFragment.java` · `AiRepairSuggestionAdapter.java` |
| `ui/customer` | `CustomerFragment.java` · `CustomerDetailActivity.java` · `CustomerAdapter.java` · `UserProfileFragment.java` |
| `ui/supplier` | `SupplierFragment.java` · `SupplierAdapter.java` · `ImportOrderActivity.java` |

#### Layout files (19 file)

`fragment_repair.xml` · `activity_create_repair.xml` · `activity_repair_detail.xml` · `fragment_customer.xml` · `activity_customer_detail.xml` · `fragment_user_profile.xml` · `fragment_supplier.xml` · `activity_import_order.xml` · `item_repair_order.xml` · `item_repair_service.xml` · `item_ai_repair_suggestion.xml` · `item_manual_repair_item.xml` · `item_customer.xml` · `item_customer_bike.xml` · `item_supplier.xml` · `item_user_detail_row.xml` · `item_user_history.xml` · `dialog_add_customer.xml` · `dialog_edit_maintenance.xml`

---

### 📊 Tổng kết phân chia

| | TV1 (Backend/AI) | TV2 (UI/UX) | TV3 (Sales/Kho) | TV4 (Sửa chữa/KH) | **Tổng** |
|---|:---:|:---:|:---:|:---:|:---:|
| **Java files** | 20 | 14 | 16 | 19 | **69** |
| **Layout XML** | 3 | 11 | 13 | 19 | **46** |
| **Drawable XML** | — | 48 | — | — | **48** |
| **Menu XML** | — | 10 | — | — | **10** |
| **Navigation** | — | 1 | — | — | **1** |
| **Values XML** | — | 7 | — | — | **7** |
| **Tổng cộng** | **23** | **91** | **29** | **38** | **181** |

---

> 📅 Cập nhật lần cuối: 09/05/2026
