# Hình 3: Biểu đồ Lớp (Class Diagram)

> Biểu đồ Lớp UML mô tả 16 lớp chính của hệ thống MotoShop với các thuộc tính, phương thức và mối quan hệ giữa chúng.

```mermaid
classDiagram
    direction TB

    class MainActivity {
        +setupNavigation()
        +filterBottomMenu()
    }

    class InventoryFragment {
        +observeData()
        +applyFilter()
    }

    class CustomerFragment {
        +observeData()
        +showAddCustomerDialog()
    }

    class SalesFragment {
        +observeData()
        +showOrderDetails()
    }

    class RepairFragment {
        +observeData()
        +applyFilter()
    }

    class AiChatFragment {
        +sendMessage()
        +performAISearch()
    }

    class MotorcycleViewModel {
        +allMotorcycles
        +insert()
        +update()
        +delete()
    }

    class CustomerViewModel {
        +allCustomers
        +insert()
        +update()
        +delete()
    }

    class SalesViewModel {
        +allOrders
        +createOrder()
        +cancelOrder()
    }

    class RepairViewModel {
        +allRepairs
        +allServices
        +insert()
        +update()
    }

    class GeminiHelper {
        +chat()
        +searchMotorcycle()
        +suggestRepairs()
    }

    class Motorcycle {
        +documentId
        +brand
        +model
        +price
        +quantity
    }

    class Firestore {
        +motorcycles
        +customers
        +sales_orders
        +repair_orders
    }

    class Customer {
        +documentId
        +name
        +phone
        +customerCode
        +totalSpent
    }

    class SalesOrder {
        +documentId
        +orderCode
        +customerDocumentId
        +finalAmount
        +status
    }

    class RepairOrder {
        +documentId
        +repairCode
        +customerDocumentId
        +issueDescription
        +status
    }

    %% MainActivity chứa các Fragment
    MainActivity o-- InventoryFragment
    MainActivity o-- CustomerFragment
    MainActivity o-- SalesFragment
    MainActivity o-- RepairFragment
    MainActivity o-- AiChatFragment

    %% Fragment dùng ViewModel
    InventoryFragment ..> MotorcycleViewModel
    CustomerFragment ..> CustomerViewModel
    SalesFragment ..> SalesViewModel
    RepairFragment ..> RepairViewModel
    AiChatFragment ..> GeminiHelper

    %% ViewModel dùng Model và Firestore
    MotorcycleViewModel ..> Motorcycle
    MotorcycleViewModel ..> Firestore
    CustomerViewModel ..> Customer
    CustomerViewModel ..> Firestore
    SalesViewModel ..> SalesOrder
    SalesViewModel ..> Firestore
    RepairViewModel ..> RepairOrder
    RepairViewModel ..> Firestore

    %% GeminiHelper dùng các ViewModel
    GeminiHelper ..> MotorcycleViewModel
    GeminiHelper ..> CustomerViewModel
    GeminiHelper ..> SalesViewModel
    GeminiHelper ..> RepairViewModel

    %% Firestore phụ thuộc Model
    Firestore ..> Motorcycle
    Firestore ..> Customer
    Firestore ..> SalesOrder
    Firestore ..> RepairOrder
```

## Mô tả mối quan hệ

| Quan hệ | Từ | Đến | Loại |
|---------|-----|------|------|
| Chứa (Aggregation) | MainActivity | 5 Fragment | `o--` |
| Phụ thuộc (Dependency) | InventoryFragment | MotorcycleViewModel | `..>` |
| Phụ thuộc (Dependency) | CustomerFragment | CustomerViewModel | `..>` |
| Phụ thuộc (Dependency) | SalesFragment | SalesViewModel | `..>` |
| Phụ thuộc (Dependency) | RepairFragment | RepairViewModel | `..>` |
| Phụ thuộc (Dependency) | AiChatFragment | GeminiHelper | `..>` |
| Phụ thuộc (Dependency) | ViewModel → Model | Motorcycle, Customer, SalesOrder, RepairOrder | `..>` |
| Phụ thuộc (Dependency) | ViewModel → Firestore | Firestore | `..>` |
| Phụ thuộc (Dependency) | GeminiHelper | 4 ViewModel | `..>` |
| Phụ thuộc (Dependency) | Firestore | 4 Model | `..>` |
