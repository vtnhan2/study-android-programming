# Hình 2: Biểu đồ Use Case

> Biểu đồ Use Case UML mô tả mối quan hệ giữa 4 tác nhân và 9 chức năng chính của hệ thống MotoShop.

```mermaid
flowchart LR
    subgraph Actors["Tác nhân"]
        direction TB
        A1(["👤 Admin"])
        A2(["👤 Nhân viên bán hàng<br/>(Sales Staff)"])
        A3(["👤 Kỹ thuật viên<br/>(Technician)"])
        A4(["👤 Khách hàng<br/>(Customer)"])
    end

    subgraph UseCases["Chức năng hệ thống MotoShop"]
        direction TB
        UC1(["Quản lý đơn bán xe<br/>(Manage motorcycle sales order)"])
        UC2(["Quản lý khách hàng<br/>(Manage customers)"])
        UC3(["Quản lý sửa chữa<br/>(Manage repairs)"])
        UC4(["Xem Dashboard<br/>(View Dashboard)"])
        UC5(["Import dữ liệu mẫu<br/>(Import sample data)"])
        UC6(["Chat AI / tìm xe<br/>(AI Chat / Find vehicles)"])
        UC7(["Quản lý xe<br/>(Manage vehicles)"])
        UC8(["Đăng nhập<br/>(Login)"])
        UC9(["Xem hồ sơ cá nhân<br/>(View personal profile)"])
    end

    %% Admin - tất cả 9 chức năng
    A1 --- UC1
    A1 --- UC2
    A1 --- UC3
    A1 --- UC4
    A1 --- UC5
    A1 --- UC6
    A1 --- UC7
    A1 --- UC8
    A1 --- UC9

    %% Nhân viên bán hàng - 4 chức năng
    A2 --- UC4
    A2 --- UC6
    A2 --- UC7
    A2 --- UC8

    %% Kỹ thuật viên - 5 chức năng
    A3 --- UC4
    A3 --- UC6
    A3 --- UC7
    A3 --- UC8
    A3 --- UC9

    %% Khách hàng - 4 chức năng
    A4 --- UC6
    A4 --- UC7
    A4 --- UC8
    A4 --- UC9

    classDef actor fill:#ffffff,stroke:#555,stroke-width:2px,color:#333
    classDef usecase fill:#f0f0f0,stroke:#888,stroke-width:1px,color:#333,rx:20,ry:20
    class A1,A2,A3,A4 actor
    class UC1,UC2,UC3,UC4,UC5,UC6,UC7,UC8,UC9 usecase
```

## Bảng phân quyền chức năng

| Chức năng | Admin | NV Bán hàng | Kỹ thuật viên | Khách hàng |
|-----------|:-----:|:-----------:|:-------------:|:----------:|
| Quản lý đơn bán xe | ✅ | ❌ | ❌ | ❌ |
| Quản lý khách hàng | ✅ | ❌ | ❌ | ❌ |
| Quản lý sửa chữa | ✅ | ❌ | ❌ | ❌ |
| Xem Dashboard | ✅ | ✅ | ✅ | ❌ |
| Import dữ liệu mẫu | ✅ | ❌ | ❌ | ❌ |
| Chat AI / tìm xe | ✅ | ✅ | ✅ | ✅ |
| Quản lý xe | ✅ | ✅ | ✅ | ✅ |
| Đăng nhập | ✅ | ✅ | ✅ | ✅ |
| Xem hồ sơ cá nhân | ✅ | ❌ | ✅ | ✅ |
