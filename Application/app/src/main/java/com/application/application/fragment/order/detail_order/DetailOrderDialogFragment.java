package com.application.application.fragment.order.detail_order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.Order;
import com.application.application.model.OrderItem;
import com.application.application.model.OrderWithItems;

import java.util.ArrayList;
import java.util.List;

public class DetailOrderDialogFragment extends DialogFragment {
    private DatabaseHelper dbHelper;
    private OnOrderUpdatedListener onOrderUpdatedListener;
    private int orderId = -1;
    private int orderAllTotalPrice = 0;
    private OrderWithItems orderInfo;
    private TextView orderName, orderCreatedAt, orderFinishedAt, orderStatus, orderTotalPrice;
    private Button btnPayment, btnCancel;
    private RecyclerView orderItemsListRecycleView;
    private DetailOrderItemsAdapter detailOrderItemsAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_detail_order_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dbHelper = new DatabaseHelper(requireActivity());
        initElements(dialog);

        // Nếu đơn hàng có trạng thái là DELIVERED hoặc CANCELLED, ẩn các nút thanh toán và huỷ.
        if (orderInfo != null && orderInfo.getOrder() != null &&
                (orderInfo.getOrder().getStatus() == OrderStatus.DELIVERED ||
                        orderInfo.getOrder().getStatus() == OrderStatus.CANCELLED)) {
            btnPayment.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        } else {
            paymentOrderBtnClick();
            cancelOrderBtnClick();
        }
        return dialog;
    }

    private void getOrderIdInBundle() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("order_id")) {
            try {
                orderId = Integer.parseInt(args.getString("order_id"));
            } catch (NumberFormatException e) {
                orderId = -1;
            }
        }
    }

    private void initElements(Dialog dialog) {
        getOrderIdInBundle();

        orderName = dialog.findViewById(R.id.dialog_detail_order_name);
        orderCreatedAt = dialog.findViewById(R.id.dialog_detail_order_created_at);
        orderFinishedAt = dialog.findViewById(R.id.dialog_detail_order_finish_at);
        orderStatus = dialog.findViewById(R.id.dialog_detail_order_status);
        orderTotalPrice = dialog.findViewById(R.id.dialog_detail_order_total_price);
        btnPayment = dialog.findViewById(R.id.dialog_detail_order_btn_payment);
        btnCancel = dialog.findViewById(R.id.dialog_detail_order_btn_cancel);

        initOrderRecycleView(dialog);
    }

    private void initOrderRecycleView(Dialog dialog) {
        // Khởi tạo recycle view
        orderItemsListRecycleView = dialog.findViewById(R.id.dialog_detail_order_list);
        orderItemsListRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // Lấy thông tin đơn hàng kèm các category item theo trạng thái đơn hàng
        orderInfo = dbHelper.getOrder_OrderItems_Food(orderId);

        if (orderInfo == null || orderInfo.getOrder() == null) {
            Toast.makeText(requireActivity(), "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        setOrderInfo(orderInfo.getOrder());
        Utils.setOrderStatus(orderInfo.getOrder().getStatus(), orderStatus);

        if (orderInfo.getOrderItemList() != null && !orderInfo.getOrderItemList().isEmpty()) {
            setOrderAllTotalPrice(orderInfo.getOrderItemList());
            orderTotalPrice.setText(String.valueOf(orderAllTotalPrice));

            detailOrderItemsAdapter = new DetailOrderItemsAdapter(
                    requireActivity(),
                    orderInfo.getOrderItemList(),
                    orderInfo.getOrder().getStatus().getStatusValue()
            );
        } else {
            orderTotalPrice.setText("0");
            detailOrderItemsAdapter = new DetailOrderItemsAdapter(
                    requireActivity(),
                    new ArrayList<>(),
                    orderInfo.getOrder().getStatus().getStatusValue()
            );
        }

        // Đặt listener callback để refresh lại thông tin khi một item bị xoá
        detailOrderItemsAdapter.setOnOrderItemDeletedListener(() -> refreshOrderDetails());
        orderItemsListRecycleView.setAdapter(detailOrderItemsAdapter);
    }

    private void setOrderInfo(Order orderInfo) {
        orderName.setText("Tên đơn hàng: " + orderInfo.getName());
        orderCreatedAt.setText("Thời gian tạo: " + orderInfo.getCreated_at());
        String finishedAt = (orderInfo.getDelivery_at() == null || orderInfo.getDelivery_at().trim().isEmpty())
                ? "Chưa cập nhật" : orderInfo.getDelivery_at();
        orderFinishedAt.setText("Ngày giao / huỷ: " + finishedAt);
    }

    private void setOrderAllTotalPrice(List<OrderItem> orderItemList) {
        orderAllTotalPrice = 0;
        for (OrderItem item : orderItemList) {
            orderAllTotalPrice += item.getTotalPrice();
        }
    }

    // Phương thức refreshOrderDetails được gọi thông qua callback của adapter khi item bị xoá
    private void refreshOrderDetails() {
        // Reset tổng tiền về 0 trước khi tính lại
        orderAllTotalPrice = 0;
        orderInfo = dbHelper.getOrder_OrderItems_Food(orderId);
        if (orderInfo == null || orderInfo.getOrder() == null) {
            Toast.makeText(requireActivity(), "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        setOrderInfo(orderInfo.getOrder());
        Utils.setOrderStatus(orderInfo.getOrder().getStatus(), orderStatus);
        if (orderInfo.getOrderItemList() != null && !orderInfo.getOrderItemList().isEmpty()) {
            setOrderAllTotalPrice(orderInfo.getOrderItemList());
            orderTotalPrice.setText(String.valueOf(orderAllTotalPrice));
        } else {
            orderTotalPrice.setText("0");
        }
        // Tạo adapter mới và đặt lại cho RecyclerView
        detailOrderItemsAdapter = new DetailOrderItemsAdapter(
                requireActivity(),
                orderInfo.getOrderItemList(),
                orderInfo.getOrder().getStatus().getStatusValue()
        );
        detailOrderItemsAdapter.setOnOrderItemDeletedListener(() -> refreshOrderDetails());
        orderItemsListRecycleView.setAdapter(detailOrderItemsAdapter);
    }

    public void paymentOrderBtnClick() {
        btnPayment.setOnClickListener(v -> {
            long result = dbHelper.updateOrderStatus(orderId, OrderStatus.DELIVERED);

            if (result > 0) {
                if (onOrderUpdatedListener != null) {
                    onOrderUpdatedListener.onOrderUpdated();
                }

                Toast.makeText(requireActivity(), "Đơn hàng đã được thanh toán", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            } else {
                Toast.makeText(requireActivity(), "Thanh toán đơn hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cancelOrderBtnClick() {
        btnCancel.setOnClickListener(v -> {
            long result = dbHelper.updateOrderStatus(orderId, OrderStatus.CANCELLED);

            if (result > 0) {
                if (onOrderUpdatedListener != null) {
                    onOrderUpdatedListener.onOrderUpdated();
                }

                Toast.makeText(requireActivity(), "Đơn hàng đã được huỷ bỏ", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            } else {
                Toast.makeText(requireActivity(), "Huỷ đơn hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tạo một interface update listener interface để ghi nhận việc cập nhật status category và gửi nó đến OrderActivity
    public interface OnOrderUpdatedListener {
        void onOrderUpdated();
    }

    // Override method onAttach để OrderActivity implements interface này
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnOrderUpdatedListener) {
            onOrderUpdatedListener = (OnOrderUpdatedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnOrderUpdatedListener");
        }
    }
}
