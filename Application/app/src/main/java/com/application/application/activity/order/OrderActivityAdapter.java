package com.application.application.activity.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.application.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.database.DatabaseHelper;
import com.application.application.model.Order;

import java.util.List;

public class OrderActivityAdapter extends RecyclerView.Adapter<OrderActivityAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private DatabaseHelper dbHelper;

    public OrderActivityAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_activity_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order orderItem = orderList.get(position);
        holder.order_title.setText(orderItem.getName());
        holder.order_created_at.setText(orderItem.getCreated_at());

        // Xoá bỏ 1 đơn hàng
        holder.btn_delete_order.setOnClickListener(v -> {
            removeOrderDB(position, orderItem.getId());
        });

        // Click vào để xem thông tin đơn hàng
        holder.order_bound.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void refreshOrdersList() {
        this.orderList = dbHelper.getOrdersList();
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView order_title;
        public TextView order_created_at;
        public LinearLayout order_bound;
        public ImageView btn_delete_order;

        OrderViewHolder(View itemView) {
            super(itemView);
            order_title = itemView.findViewById(R.id.order_item_title);
            order_created_at = itemView.findViewById(R.id.order_item_created_at);
            order_bound = itemView.findViewById(R.id.order_item_bound);
            btn_delete_order = itemView.findViewById(R.id.order_item_btn_delete);
        }
    }

    // Hàm xoá 1 đơn hàng khỏi Database và cập nhật lại giao diện
    public void removeOrderDB(int position, int id) {
        long result = dbHelper.deleteOrder(id);
        if (result != 0) {
            removeOrderUI(position);
            Toast.makeText(context, "Xoá đơn hàng thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Xoá đơn hàng thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm xoá giỏ hàng khỏi giao diện danh sách
    public void removeOrderUI(int position) {
        if (position >= 0 && position < orderList.size()) {
            orderList.remove(position);
            notifyItemChanged(position);
        }
    }
}
