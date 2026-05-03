# 📜 Log Triển Khai Sprint 1 - MotoShop RBAC & Logic Fix

## [2026-05-03] - Sprint 1 Implementation

### ✅ 1. Tách lớp & Phân quyền (RBAC)
- **Tạo Staff Model**: Đã thêm file `Staff.java` để quản lý thông tin nhân viên (ID, Password, Role, Name).
- **Dynamic Login**: Cập nhật `LoginActivity.java` để truy vấn collection `staff` trên Firestore. Loại bỏ hardcode tài khoản.
- **Dữ liệu mẫu**: Cập nhật `seed_data.json` và `FirebaseSeeder.java` để hỗ trợ import tài khoản nhân viên mẫu (`AD01`, `SL01`, `KT01`).

### ✅ 2. Tracking & Audit Log
- **SalesOrder Update**: Thêm trường `createdByStaffId` và `createdByStaffName` vào model.
- **RepairOrder Update**: Thêm trường `createdByStaffId` và `createdByStaffName` vào model.
- **Logic Stamp**: Cập nhật `CreateSaleActivity.java` để tự động gán thông tin nhân viên đang đăng nhập vào đơn hàng khi tạo mới.

### ✅ 3. Tối ưu quy trình cho Technician
- **Repair Status Flow**: Cập nhật `RepairDetailActivity.java`.
    - Thêm nút **"Bắt đầu sửa chữa"** khi phiếu ở trạng thái `RECEIVED`.
    - Chuyển trạng thái sang `IN_PROGRESS` và gán tên kỹ thuật viên đảm nhận.
    - Duy trì nút **"Hoàn tất sửa chữa"** để chuyển sang `DONE`.

### ✅ 4. Giao diện cho Khách hàng (User Role)
- **UserDashboardFragment**: Đã tạo Layout và Class Java cho màn hình Dashboard riêng của khách hàng (hiển thị xe, đơn hàng cá nhân).
- **Routing**: Cập nhật `MainActivity` để tự động chuyển Start Destination sang `UserDashboardFragment` nếu role là `USER`.

---
*Ghi chú: Đã kiểm tra build thành công sau các thay đổi.*

## [2026-05-03] - Sprint 2 Implementation

### ✅ 1. Bảo mật giao diện (UI Security)
- **Role-based Sidebar**: Cập nhật `MainActivity.java` để ẩn các mục như "Import dữ liệu" đối với Technician và Customer.
- **Conditional Dashboard**: Cập nhật `DashboardFragment.java` để chỉ hiển thị phần "Tổng quan kinh doanh" (Doanh thu, Đơn hàng) cho Admin và Sales.

### ✅ 2. Phân tích hiệu suất (Performance Tracking)
- **Staff Leaderboard**: Bổ sung logic tính toán doanh thu theo từng nhân viên trong `SalesViewModel`.
- **Dashboard Stats**: Hiển thị bảng xếp hạng Top 3 nhân viên xuất sắc nhất ngay trên màn hình chính của quản lý.

### ✅ 3. Toàn vẹn dữ liệu (Data Integrity)
- **Transaction Validation**: Đã kiểm tra và xác nhận logic `createOrder` trong `SalesViewModel` sử dụng `runTransaction` để kiểm tra tồn kho và trừ số lượng xe một cách nguyên tử (atomic), tránh tình trạng over-selling.

