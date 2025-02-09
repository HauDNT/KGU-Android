package com.application.application.activity.order.activity.detail_order;

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
import com.application.application.activity.order.activity.OrderActivityAdapter;
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

        if (orderInfo.getOrder().getStatus() == OrderStatus.DELIVERED || orderInfo.getOrder().getStatus() == OrderStatus.CANCELLED) {
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
        String getOrderId = args != null ? args.getString("order_id") : "";

        if (!getOrderId.equals("")) orderId = Integer.parseInt(getOrderId);
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

        // Lấy thông tin đơn hàng và các item bên trong đơn
        orderInfo = dbHelper.getOrder_OrderItems_Food(orderId);
        setOrderInfo(orderInfo.getOrder());
        Utils.setOrderStatus(orderInfo.getOrder().getStatus(), orderStatus);

        // Nếu có item trong đơn hàng
        if (orderInfo.getOrderItemList().size() > 0) {
            // Cập nhật tổng tiền toàn bộ đơn hàng
            setOrderAllTotalPrice(orderInfo.getOrderItemList());
            orderTotalPrice.setText(String.valueOf(orderAllTotalPrice));

            // Áp dụng adapter vào recycle view để render ra dữ liệu item trong đơn hàng
            detailOrderItemsAdapter = new DetailOrderItemsAdapter(requireActivity(), orderInfo.getOrderItemList());
        }  else {
            // Nếu không thì set list rỗng và tổng tiền là 0
            orderTotalPrice.setText(0);
            detailOrderItemsAdapter = new DetailOrderItemsAdapter(requireActivity(), new ArrayList<>());
        }

        orderItemsListRecycleView.setAdapter(detailOrderItemsAdapter);
    }

    private void setOrderInfo(Order orderInfo) {
        orderName.setText("Tên đơn hàng: " + orderInfo.getName());
        orderCreatedAt.setText("Thời gian tạo: " + orderInfo.getCreated_at());
        orderFinishedAt.setText("Ngày giao / huỷ: " + orderInfo.getDelivery_at());
    }

    private void setOrderAllTotalPrice(List<OrderItem> orderItemList) {
        for (OrderItem item : orderItemList) {
            orderAllTotalPrice += item.getTotalPrice();
        }
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

    // Tạo một interface update listener interface để ghi nhận việc cập nhật status order và gửi nó đến OrderActivity
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
