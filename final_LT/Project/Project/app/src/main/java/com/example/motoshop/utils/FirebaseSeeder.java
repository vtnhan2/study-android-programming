package com.example.motoshop.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.example.motoshop.data.model.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FirebaseSeeder {
    private static final String TAG = "FirebaseSeeder";

    public static void uploadSeedData(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            String json = loadJSONFromAsset(context, "seed_data.json");
            if (json == null) {
                Toast.makeText(context, "Không tìm thấy file seed_data.json", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject root = new JSONObject(json);

            // Import từng collection
            importMotorcycles(db, root.optJSONArray("motorcycles"));
            importCustomers(db, root.optJSONArray("customers"));
            importSuppliers(db, root.optJSONArray("suppliers"));
            importRepairServices(db, root.optJSONArray("repair_services"));
            importCustomerVehicles(db, root.optJSONArray("customer_vehicles"));
            importSalesOrders(db, root.optJSONArray("sales_orders"));
            importRepairOrders(db, root.optJSONArray("repair_orders"));

            Toast.makeText(context, "Đang import dữ liệu mẫu...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi import: " + e.getMessage());
            Toast.makeText(context, "Lỗi import dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    private static void importMotorcycles(FirebaseFirestore db, JSONArray array) throws Exception {
        if (array == null) return;
        WriteBatch batch = db.batch();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Motorcycle m = new Motorcycle();
            m.brand = obj.optString("brand");
            m.model = obj.optString("model");
            m.color = obj.optString("color");
            m.year = obj.optInt("year");
            m.price = obj.optDouble("price");
            m.importPrice = obj.optDouble("importPrice");
            m.quantity = obj.optInt("quantity");
            m.status = obj.optString("status");
            m.description = obj.optString("description");
            m.imageUri = obj.optString("imageUri", "");
            m.createdAt = System.currentTimeMillis();
            
            String id = obj.optString("id", (m.brand + "_" + m.model + "_" + m.year).toLowerCase().replace(" ", "_"));
            m.documentId = id;
            batch.set(db.collection("motorcycles").document(id), m);
        }
        batch.commit();
    }

    private static void importCustomers(FirebaseFirestore db, JSONArray array) throws Exception {
        if (array == null) return;
        WriteBatch batch = db.batch();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Customer c = new Customer();
            c.name = obj.optString("name");
            c.phone = obj.optString("phone");
            c.address = obj.optString("address");
            c.email = obj.optString("email");
            c.memberRank = obj.optString("memberRank", "NORMAL");
            c.loyaltyPoints = obj.optInt("loyaltyPoints", 0);
            c.totalSpent = obj.optDouble("totalSpent", 0);
            c.createdAt = System.currentTimeMillis();
            
            String id = obj.optString("id", "KH_" + c.phone);
            c.customerCode = id;
            c.documentId = id;
            batch.set(db.collection("customers").document(id), c);
        }
        batch.commit();
    }

    private static void importSuppliers(FirebaseFirestore db, JSONArray array) throws Exception {
        if (array == null) return;
        WriteBatch batch = db.batch();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Supplier s = new Supplier();
            s.name = obj.optString("name");
            s.contactPerson = obj.optString("contactPerson");
            s.phone = obj.optString("phone");
            s.email = obj.optString("email");
            s.address = obj.optString("address");
            s.taxCode = obj.optString("taxCode");
            s.createdAt = System.currentTimeMillis();
            
            String safeName = s.name.replaceAll("[^a-zA-Z0-9\\s]", "").trim().replaceAll("\\s+", "_").toUpperCase();
            String id = obj.optString("id", "NCC_" + safeName);
            s.documentId = id;
            batch.set(db.collection("suppliers").document(id), s);
        }
        batch.commit();
    }

    private static void importRepairServices(FirebaseFirestore db, JSONArray array) throws Exception {
        if (array == null) return;
        WriteBatch batch = db.batch();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            RepairService s = new RepairService();
            s.name = obj.optString("name");
            s.description = obj.optString("description");
            s.defaultLaborCost = obj.optDouble("defaultLaborCost");
            s.defaultPartsCost = obj.optDouble("defaultPartsCost");
            s.category = obj.optString("category");
            
            String id = obj.optString("id", "DV_" + i);
            s.documentId = id;
            batch.set(db.collection("repair_services").document(id), s);
        }
        batch.commit();
    }

    private static void importCustomerVehicles(FirebaseFirestore db, JSONArray array) throws Exception {
        if (array == null) return;
        WriteBatch batch = db.batch();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            CustomerVehicle v = new CustomerVehicle();
            v.customerDocumentId = obj.optString("customerDocumentId");
            v.customerName = obj.optString("customerName");
            v.brand = obj.optString("brand");
            v.model = obj.optString("model");
            v.licensePlate = obj.optString("licensePlate");
            v.year = obj.optInt("year");
            v.color = obj.optString("color");
            v.createdAt = System.currentTimeMillis();
            
            String id = obj.optString("id", v.licensePlate != null ? v.licensePlate.replaceAll("[^a-zA-Z0-9]", "").toUpperCase() : ("CV_" + i));
            v.documentId = id;
            batch.set(db.collection("customer_vehicles").document(id), v);
        }
        batch.commit();
    }

    private static void importSalesOrders(FirebaseFirestore db, JSONArray array) throws Exception {
        if (array == null) return;
        WriteBatch batch = db.batch();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            SalesOrder o = new SalesOrder();
            o.orderCode = obj.optString("orderCode");
            o.customerDocumentId = obj.optString("customerDocumentId");
            o.customerName = obj.optString("customerName");
            o.totalAmount = obj.optDouble("totalAmount");
            o.discount = obj.optDouble("discount");
            o.finalAmount = obj.optDouble("finalAmount");
            o.paymentMethod = obj.optString("paymentMethod");
            o.status = obj.optString("status");
            o.note = obj.optString("note");
            o.orderDate = System.currentTimeMillis() - (long)(Math.random() * 864000000L); // Random trong 10 ngày qua

            JSONArray itemsArray = obj.optJSONArray("items");
            if (itemsArray != null) {
                List<SalesOrderItem> items = new ArrayList<>();
                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemObj = itemsArray.getJSONObject(j);
                    SalesOrderItem item = new SalesOrderItem();
                    item.motorcycleDocumentId = itemObj.optString("motorcycleDocumentId");
                    item.motorcycleName = itemObj.optString("motorcycleName");
                    item.quantity = itemObj.optInt("quantity", 1);
                    item.unitPrice = itemObj.optDouble("unitPrice");
                    item.subtotal = itemObj.optDouble("subtotal");
                    items.add(item);
                }
                o.items = items;
            }
            
            String id = obj.optString("id", o.orderCode);
            o.documentId = id;
            batch.set(db.collection("sales_orders").document(id), o);
        }
        batch.commit();
    }

    private static void importRepairOrders(FirebaseFirestore db, JSONArray array) throws Exception {
        if (array == null) return;
        WriteBatch batch = db.batch();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            RepairOrder r = new RepairOrder();
            r.repairCode = obj.optString("repairCode");
            r.customerDocumentId = obj.optString("customerDocumentId");
            r.customerName = obj.optString("customerName");
            r.motorcycleBrand = obj.optString("motorcycleBrand");
            r.motorcycleModel = obj.optString("motorcycleModel");
            r.licensePlate = obj.optString("licensePlate");
            r.issueDescription = obj.optString("issueDescription");
            r.diagnosis = obj.optString("diagnosis");
            r.laborCost = obj.optDouble("laborCost");
            r.partsCost = obj.optDouble("partsCost");
            r.totalCost = obj.optDouble("totalCost");
            r.status = obj.optString("status");
            r.technicianName = obj.optString("technicianName");
            r.receivedDate = System.currentTimeMillis() - (long)(Math.random() * 864000000L);
            
            String id = obj.optString("id", r.repairCode);
            r.documentId = id;
            batch.set(db.collection("repair_orders").document(id), r);
        }
        batch.commit();
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }
}
