package com.application.application.fragment.order;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.fragment.order.detail_order.DetailOrderDialogFragment;
import com.application.application.fragment.order.edit_order.EditOrderDialogFragment;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.Order;

import java.util.List;

public class OrderFragmentAdapter extends RecyclerView.Adapter<OrderFragmentAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private DatabaseHelper dbHelper;

    public OrderFragmentAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_activity_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order orderItem = orderList.get(position);
        holder.order_title.setText(orderItem.getName());
        holder.order_created_at.setText(orderItem.getCreated_at());
        Utils.setOrderStatus(orderItem.getStatus(), holder.order_status);

        //Nếu đơn hàng quá khứ (DELIVERED hoặc CANCELLED) thì ẩn nút xoá.
        if (orderItem.getStatus() == OrderStatus.DELIVERED || orderItem.getStatus() == OrderStatus.CANCELLED) {
            holder.btn_delete_order.setVisibility(View.GONE);
        } else {
            holder.btn_delete_order.setVisibility(View.VISIBLE);
            holder.btn_delete_order.setOnClickListener(v -> removeOrderDB(position, orderItem.getId()));
        }
        // Xoá bỏ 1 đơn hàng
        holder.btn_delete_order.setOnClickListener(v -> removeOrderDB(position, orderItem.getId()));

        // Xem/chỉnh sửa thông tin đơn hàng
        holder.btn_edit_order.setOnClickListener(v -> {
            EditOrderDialogFragment dialogFragment = EditOrderDialogFragment.newInstance(orderItem);

            dialogFragment.setOnOrderUpdatedListener(() -> {
                refreshOrdersList();
            });

            dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "EditOrderDialog");
        });

        //Khi click vào item, mở dialog chi tiết đơn hàng
        holder.order_bound.setOnClickListener(v -> {
            DetailOrderDialogFragment dialogFragment = new DetailOrderDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("order_id", String.valueOf(orderItem.getId()));
            dialogFragment.setArguments(bundle);
            dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "Detail Order Dialog");
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
        public TextView order_created_at, order_status;
        public LinearLayout order_bound;
        public ImageView btn_delete_order, btn_edit_order;

        OrderViewHolder(View itemView) {
            super(itemView);
            order_title = itemView.findViewById(R.id.order_item_title);
            order_created_at = itemView.findViewById(R.id.order_item_created_at);
            order_status = itemView.findViewById(R.id.order_item_status);
            order_bound = itemView.findViewById(R.id.order_item_bound);
            btn_delete_order = itemView.findViewById(R.id.order_item_btn_delete);
            btn_edit_order = itemView.findViewById(R.id.order_item_btn_update);
        }
    }

    // Xoá đơn hàng khỏi CSDL và cập nhật UI
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

    public void removeOrderUI(int position) {
        if (position >= 0 && position < orderList.size()) {
            orderList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderList.size());
        }
    }
}
