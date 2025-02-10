package com.application.application.activity.order.choose_order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.Order;

import java.util.List;

public class ChooseOrderDialogAdapter extends RecyclerView.Adapter<ChooseOrderDialogAdapter.OrderViewHolder> {
    public Context context;
    public List<Order> orderList;
    public int orderSelectedId;
    private OnOrderSelectedListener orderSelectedListener;

    public ChooseOrderDialogAdapter(Context context, List<Order> orderList, OnOrderSelectedListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.orderSelectedId = 0;
        this.orderSelectedListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_choose_order_item_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order orderItem = orderList.get(position);

        holder.order_id.setText(String.valueOf(orderItem.getId()));
        holder.order_title.setText(orderItem.getName());
        holder.order_created_at.setText(orderItem.getCreated_at());

        // Kiểm tra trạng thái đơn hàng: nếu là DELIVERED hoặc CANCELLED, ẩn CheckBox
        if (orderItem.getStatus() == OrderStatus.DELIVERED ||
                orderItem.getStatus() == OrderStatus.CANCELLED) {
            holder.order_select.setVisibility(View.GONE);
        } else {
            holder.order_select.setVisibility(View.VISIBLE);
            holder.order_select.setChecked(orderItem.getId() == orderSelectedId);
            holder.order_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    orderSelectedId = orderItem.getId();
                    orderSelectedListener.onOrderSelected(orderItem.getId(), true);
                } else {
                    // Nếu uncheck, đảm bảo checkbox giữ trạng thái false
                    holder.order_select.setChecked(false);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void updateOrderListUI(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView order_id, order_title;
        public TextView order_created_at;
        public LinearLayout order_bound;
        public CheckBox order_select;

        OrderViewHolder(View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.choose_order_item_id);
            order_title = itemView.findViewById(R.id.choose_order_item_title);
            order_created_at = itemView.findViewById(R.id.choose_order_item_created_at);
            order_bound = itemView.findViewById(R.id.choose_order_item_bound);
            order_select = itemView.findViewById(R.id.choose_order_item_select);
        }
    }

    public interface OnOrderSelectedListener {
        void onOrderSelected(int orderId, boolean isChecked);
    }
}
