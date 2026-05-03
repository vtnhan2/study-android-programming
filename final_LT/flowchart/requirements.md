Prompt cho Hình 1: Sơ đồ Kiến trúc/Luồng dữ liệu 
"Vẽ một sơ đồ kiến trúc kỹ thuật chi tiết theo phong cách sơ đồ luồng dữ liệu, sử dụng các hộp hình chữ nhật màu xám nhẹ trên nền trắng, với các đường kết nối và mũi tên rõ ràng. Sơ đồ mô tả luồng dữ liệu của một ứng dụng Android theo kiến trúc MVVM, sử dụng Firebase.

Cấu trúc: Sơ đồ bắt đầu với một hộp ở trên cùng là CreateSaleActivity.

Kết nối:

CreateSaleActivity kết nối một chiều đến SalesViewModel.

SalesViewModel kết nối một chiều đến ba hộp: Cập nhật tồn kho (update inventory), Firebase Firestore, và sales_orders.

Cập nhật tồn kho kết nối một chiều đến motorcycles.

Firebase Firestore kết nối một chiều đến năm hộp: motorcycles, customers, sales_orders, repair_orders, và repair_services.

Tất cả năm hộp (motorcycles, customers, sales_orders, repair_orders, repair_services) kết nối một chiều đến Model Java.

Model Java kết nối một chiều đến ViewModel.

ViewModel kết nối một chiều đến Fragment / Activity.

Fragment / Activity kết nối một chiều đến hai hộp: Adapter và AiChatFragment / CreateRepairActivity.

Adapter kết nối một chiều đến RecyclerView / UI.

AiChatFragment / CreateRepairActivity kết nối một chiều đến hai hộp: RecyclerView / UI và GeminiHelper.

GeminiHelper kết nối một chiều trở lại AiChatFragment / CreateRepairActivity (tạo một vòng lặp).

Chi tiết nhãn: Đảm bảo sử dụng chính xác tên các hộp và viết hoa theo đúng hình ảnh (ví dụ: CreateSaleActivity, ViewModel, GeminiHelper), và giữ nguyên các nhãn tiếng Việt như Cập nhật tồn kho."

Prompt cho Hình 2: Biểu đồ Use Case 
"Vẽ một biểu đồ Use Case chuẩn UML, sử dụng ký hiệu hình người cho Tác nhân (Actor) và hình elip/hộp bo góc cho Chức năng (Use Case). Sử dụng tông màu xám nhẹ cho các hộp trên nền trắng.

Tác nhân: Đặt 4 tác nhân theo chiều dọc ở bên trái: Admin, Nhân viên bán hàng (Sales Staff), Kỹ thuật viên (Technician), và Khách hàng (Customer).

Chức năng: Đặt 9 chức năng theo chiều dọc ở bên phải: Quản lý đơn bán xe (Manage motorcycle sales order), Quản lý khách hàng (Manage customers), Quản lý sửa chữa (Manage repairs), Xem Dashboard (View Dashboard), Import dữ liệu mẫu (Import sample data), Chat AI / tìm xe (AI Chat / Find vehicles), Quản lý xe (Manage vehicles), Đăng nhập (Login), và Xem hồ sơ cá nhân (View personal profile).

Kết nối (Mối quan hệ Association):

Admin: Kết nối đến tất cả 9 chức năng.

Nhân viên bán hàng: Kết nối đến Xem Dashboard, Chat AI / tìm xe, Quản lý xe, Đăng nhập.

Kỹ thuật viên: Kết nối đến Xem Dashboard, Chat AI / tìm xe, Quản lý xe, Đăng nhập, và Xem hồ sơ cá nhân.

Khách hàng: Kết nối đến Chat AI / tìm xe, Quản lý xe, Đăng nhập, và Xem hồ sơ cá nhân.

Chi tiết: Sử dụng đường nối liền mảnh giữa tác nhân và chức năng. Đảm bảo tất cả nhãn tiếng Việt được viết chính xác như trong hình."

Prompt cho Hình 3: Biểu đồ Lớp
"Vẽ một biểu đồ Lớp UML chi tiết, kỹ thuật, sử dụng các hộp ba ngăn (Tên lớp, Thuộc tính, Phương thức) trên nền trắng. Các hộp có viền xám nhẹ.

Lớp: Vẽ tất cả 16 lớp với các thuộc tính và phương thức sau (sử dụng dấu '+' cho public):

MainActivity (+setupNavigation(), +filterBottomMenu())

InventoryFragment (+observeData(), +applyFilter())

CustomerFragment (+observeData(), +showAddCustomerDialog())

SalesFragment (+observeData(), +showOrderDetails())

RepairFragment (+observeData(), +applyFilter())

AiChatFragment (+sendMessage(), +performAISearch())

MotorcycleViewModel (+allMotorcycles, +insert(), +update(), +delete())

CustomerViewModel (+allCustomers, +insert(), +update(), +delete())

SalesViewModel (+allOrders, +createOrder(), +cancelOrder())

RepairViewModel (+allRepairs, +allServices, +insert(), +update())

GeminiHelper (+chat(), +searchMotorcycle(), +suggestRepairs())

Motorcycle (+documentId, +brand, +model, +price, +quantity)

Firestore (+motorcycles, +customers, +sales_orders, +repair_orders)

Customer (+documentId, +name, +phone, +customerCode, +totalSpent)

SalesOrder (+documentId, +orderCode, +customerDocumentId, +finalAmount, +status)

RepairOrder (+documentId, +repairCode, +customerDocumentId, +issueDescription, +status)

Kết nối (Mối quan hệ UML):

MainActivity kế thừa từ tất cả 5 Fragment (mũi tên rỗng).

InventoryFragment dùng MotorcycleViewModel (mũi tên rỗng).

CustomerFragment dùng CustomerViewModel (mũi tên rỗng).

SalesFragment dùng SalesViewModel (mũi tên rỗng).

RepairFragment dùng RepairViewModel (mũi tên rỗng).

AiChatFragment dùng GeminiHelper (mũi tên rỗng).

MotorcycleViewModel dùng Motorcycle và Firestore (mũi tên rỗng).

CustomerViewModel dùng Customer và Firestore (mũi tên rỗng).

SalesViewModel dùng SalesOrder và Firestore (mũi tên rỗng).

RepairViewModel dùng RepairOrder và Firestore (mũi tên rỗng).

GeminiHelper dùng MotorcycleViewModel, CustomerViewModel, SalesViewModel, và RepairViewModel (mũi tên rỗng).

Firestore phụ thuộc vào Motorcycle, Customer, SalesOrder, và RepairOrder (mũi tên rỗng).

Chi tiết: Sử dụng ký hiệu rỗng ở đầu mũi tên cho mối quan hệ kế thừa hoặc phụ thuộc. Đảm bảo tất cả tên lớp, thuộc tính và phương thức được viết chính xác."

Prompt cho Hình 4: Sơ đồ Điều hướng/Flowchart 
"Vẽ một sơ đồ luồng màn hình ứng dụng (Flowchart/Navigation Diagram) với cấu trúc phân cấp cây lộn ngược. Luồng đi xuống. Sử dụng các hộp hình chữ nhật màu xám nhẹ trên nền trắng, trừ hình thoi cho điều kiện quyết định.

Luồng chính (ở trên): Sơ đồ bắt đầu với hộp Mở app (Open app), kết nối một chiều đến SplashActivity, kết nối một chiều đến một hình thoi quyết định Đã đăng nhập? (Logged in?).

Rẽ nhánh tại hình thoi:

Nhánh "Chưa" (No) kết nối một chiều đến LoginActivity, kết nối một chiều đến MainActivity.

Nhánh "Rồi" (Yes) kết nối một chiều trực tiếp đến MainActivity.

Cấp dưới MainActivity: Từ MainActivity, tạo 7 kết nối một chiều đến các hộp cấp dưới (đặt theo chiều ngang): DashboardFragment, InventoryFragment, CustomerFragment, SalesFragment, RepairFragment, AiChatFragment, và UserProfileFragment.

Cấp chi tiết nhất:

InventoryFragment kết nối một chiều đến hai hộp cấp dưới: MotorcycleDetailActivity và AddEditMotorcycleActivity.

CustomerFragment kết nối một chiều đến một hộp cấp dưới: CustomerDetailActivity.

SalesFragment kết nối một chiều đến một hộp cấp dưới: CreateSaleActivity.

RepairFragment kết nối một chiều đến hai hộp cấp dưới: CreateRepairActivity và RepairDetailActivity.

Chi tiết: Đảm bảo tất cả tên các hộp Activity/Fragment được viết hoa chính xác. Giữ nguyên các nhãn tiếng Việt như Mở app, Đã đăng nhập?, "Rồi", "Chưa" với đúng vị trí của chúng."