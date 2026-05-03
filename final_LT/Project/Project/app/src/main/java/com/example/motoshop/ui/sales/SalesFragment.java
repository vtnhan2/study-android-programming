package com.example.motoshop.ui.sales;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import com.example.motoshop.data.model.SalesOrder;
import com.example.motoshop.data.model.SalesOrderItem;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.DateUtils;
import com.example.motoshop.utils.UserSession;
import com.example.motoshop.viewmodel.SalesViewModel;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách đơn bán hàng.
 * Đã khôi phục chức năng nút Thêm (+) nổi cam.
 */
public class SalesFragment extends Fragment {

    private SalesViewModel viewModel;
    private SalesOrderAdapter adapter;
    private String currentFilterStatus = "ALL";
    private UserSession session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales, container, false);
        session = new UserSession(requireContext());
        viewModel = new ViewModelProvider(this).get(SalesViewModel.class);

        RecyclerView rv = v.findViewById(R.id.rvSales);
        adapter = new SalesOrderAdapter(new SalesOrderAdapter.OnOrderActionListener() {
            @Override
            public void onComplete(String documentId) {
                viewModel.updateStatus(documentId, "COMPLETED");
                Toast.makeText(getContext(), "Đã cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SalesOrder order, String reason) {
                viewModel.cancelOrder(order, reason, new SalesViewModel.OrderCallback() {
                    @Override
                    public void onSuccess() {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi khi hủy đơn: " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onViewDetail(SalesOrder order) {
                showOrderDetails(order);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        ChipGroup filterGroup = v.findViewById(R.id.chipGroupFilter);
        if (filterGroup != null) {
            filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.contains(R.id.chipAll)) {
                    currentFilterStatus = "ALL";
                } else if (checkedIds.contains(R.id.chipProcessing)) {
                    currentFilterStatus = "PROCESSING";
                } else if (checkedIds.contains(R.id.chipCompleted)) {
                    currentFilterStatus = "COMPLETED";
                } else if (checkedIds.contains(R.id.chipCancelled)) {
                    currentFilterStatus = "CANCELLED";
                }
                applyFilter();
            });
        }

        // Gắn lại sự kiện cho nút Thêm đơn hàng (+)
        setupFAB(v);

        viewModel.allOrders.observe(getViewLifecycleOwner(), orders -> {
            applyFilter();
        });

        return v;
    }

    private void setupFAB(View v) {
        FloatingActionButton fab = v.findViewById(R.id.fabAddSale);
        if (fab != null) {
            // Nút này chỉ hiện cho Admin và Sale
            if (UserSession.ROLE_TECHNICIAN.equals(session.getUserRole()) || UserSession.ROLE_USER.equals(session.getUserRole())) {
                fab.setVisibility(View.GONE);
            } else {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(view -> {
                    Intent intent = new Intent(getContext(), CreateSaleActivity.class);
                    startActivity(intent);
                });
            }
        }
    }

    private void applyFilter() {
        if (adapter == null) return;
        List<SalesOrder> all = viewModel.allOrders.getValue();
        if (all == null) return;

        if ("ALL".equals(currentFilterStatus)) {
            adapter.setOrders(all);
        } else {
            List<SalesOrder> filtered = new ArrayList<>();
            for (SalesOrder o : all) {
                if (currentFilterStatus.equals(o.status)) filtered.add(o);
            }
            adapter.setOrders(filtered);
        }
    }

    private void showOrderDetails(SalesOrder order) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_details, null);
        
        TextView tvOrderCode = view.findViewById(R.id.tvOrderCode);
        TextView tvCustomerInfo = view.findViewById(R.id.tvCustomerInfo);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        TextView tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        TextView tvCancelReason = view.findViewById(R.id.tvCancelReason);
        TextView tvNote = view.findViewById(R.id.tvNote);
        TextView tvStatus = view.findViewById(R.id.tvStatus);

        if (tvOrderCode != null) tvOrderCode.setText(order.orderCode);
        if (tvCustomerInfo != null) tvCustomerInfo.setText("Khách hàng: " + order.customerName);
        if (tvDate != null) tvDate.setText("Ngày: " + DateUtils.formatDate(order.orderDate));
        if (tvPaymentMethod != null) tvPaymentMethod.setText("Thanh toán: " + order.paymentMethod);
        if (tvTotalAmount != null) tvTotalAmount.setText(CurrencyFormatter.format(order.finalAmount));
        
        if (tvStatus != null) {
            String statusText = order.status;
            if ("COMPLETED".equals(order.status)) statusText = "HOÀN TẤT";
            else if ("CANCELLED".equals(order.status)) statusText = "ĐÃ HỦY";
            else statusText = "ĐANG XỬ LÝ";
            tvStatus.setText(statusText);
        }

        if (tvCancelReason != null) {
            if ("CANCELLED".equals(order.status)) {
                tvCancelReason.setVisibility(View.VISIBLE);
                tvCancelReason.setText("Lý do hủy: " + (order.cancelReason != null ? order.cancelReason : "Không có lý do"));
            } else {
                tvCancelReason.setVisibility(View.GONE);
            }
        }

        if (tvNote != null) {
            if (order.note != null && !order.note.trim().isEmpty()) {
                tvNote.setVisibility(View.VISIBLE);
                tvNote.setText("Ghi chú: " + order.note);
            } else {
                tvNote.setVisibility(View.GONE);
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Chi tiết đơn hàng")
                .setView(view)
                .setPositiveButton("Đóng", null)
                .show();
    }
}
