Đóng vai trò là một Product Manager kiêm Software Architect dày dặn kinh nghiệm. Tôi đang xây dựng một dự án phần mềm có tên là [Tên dự án/App của bạn, ví dụ: Hệ thống quản lý CRM cho công ty cung cấp thiết bị IoT].

Yêu cầu cốt lõi của dự án là phải phân quyền và tách lớp rõ ràng cho 4 roles: Admin, Sale, Tech, User.

Dựa trên thông tin tôi cung cấp bên dưới, hãy giúp tôi hoàn thiện 3 yêu cầu sau:

1. THIẾT KẾ PHÂN QUYỀN VÀ TÁCH LỚP (RBAC & Architecture):

Liệt kê chi tiết quyền hạn (CRUD - Create, Read, Update, Delete) của từng role đối với các thực thể dữ liệu chính.

Đề xuất cách tách lớp (Frontend, Backend, Database) để đảm bảo bảo mật, role nào không được phép gọi API của role kia.

2. ĐỀ XUẤT GIAO DIỆN (UI/UX Layout):

Mô tả cấu trúc UI (Menu/Sidebar/Dashboard) tối ưu cho từng role.

Dashboard của mỗi role cần hiển thị những chỉ số (Metrics/Widgets) nào để phù hợp với công việc đặc thù của họ?

3. HOÀN THIỆN TÍNH NĂNG CÒN THIẾU:

Dựa trên danh sách tính năng tôi đã có, hãy chỉ ra những lỗ hổng logic hoặc những tính năng thiết yếu cần bổ sung để luồng nghiệp vụ giữa các role được liền mạch.

--- THÔNG TIN DỰ ÁN CỦA TÔI ---

Mục tiêu chính của app: Cửa hàng bán xe máy chuyên nghiệp.

Mô tả các thực thể (Entities) chính: Khách hàng, Đơn hàng, Xe máy, Ticket hỗ trợ, Nhân viên (Admin, Sale, Tech)

Danh sách tính năng tôi đã nghĩ ra (Luồng hiện tại):

Admin: Quản lý tài khoản nội bộ (tạo tài khoản sale, tech), xem báo cáo doanh thu tổng.

Sale: Tạo thông tin khách hàng, tạo đơn hàng.

Tech: Xem danh sách thiết bị cần lắp đặt.

User (Khách hàng): Đăng nhập xem danh sách thiết bị của mình.

Hãy phân tích luồng trên và thực hiện 3 yêu cầu tôi đã nêu. Trình bày output dưới dạng Markdown, sử dụng bảng để thể hiện phân quyền cho dễ nhìn.