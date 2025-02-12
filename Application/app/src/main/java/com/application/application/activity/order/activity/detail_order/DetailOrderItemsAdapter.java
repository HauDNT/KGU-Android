package com.application.application.activity.order.activity.detail_order;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.OrderItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailOrderItemsAdapter extends RecyclerView.Adapter<DetailOrderItemsAdapter.DetailOrderItemsViewHolder> {
    private Context context;
    private List<OrderItem> orderItemsList;
    private DatabaseHelper dbHelper;
    private int status; // Trạng thái đơn hàng tổng thể

    // Getter cho status (nếu cần)
    public int getStatus() {
        return status;
    }

    // Setter cho status (nếu cần)
    public void setStatus(int status) {
        this.status = status;
    }

    // Interface để callback khi một item bị xoá
    public interface OnOrderItemDeletedListener {
        void onOrderItemDeleted();
    }

    // Field cho interface callback
    private OnOrderItemDeletedListener onOrderItemDeletedListener;

    // Setter cho listener
    public void setOnOrderItemDeletedListener(OnOrderItemDeletedListener listener) {
        this.onOrderItemDeletedListener = listener;
    }

    public DetailOrderItemsAdapter(Context context, List<OrderItem> orderItemsList, int orderStatus) {
        this.context = context;
        this.orderItemsList = orderItemsList;
        this.dbHelper = new DatabaseHelper(context);
        this.status = orderStatus;
    }

    @NonNull
    @Override
    public DetailOrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_detail_order_item_list, parent, false);
        return new DetailOrderItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailOrderItemsViewHolder holder, int position) {
        // Kiểm tra list có null hoặc rỗng để tránh crash
        if (orderItemsList == null || orderItemsList.isEmpty()) {
            return;
        }

        // Lấy đối tượng order item theo vị trí
        OrderItem orderItem = orderItemsList.get(position);
        if (orderItem == null) return;

        int foodId = orderItem.getFood_id();
        String foodName = dbHelper.getFoodNameById(foodId);

        holder.orderItemName.setText(foodName);
        holder.orderItemQuantity.setText(String.valueOf(orderItem.getQuantity()));
        holder.orderItemTotal.setText(String.valueOf(orderItem.getTotalPrice()));
        // Tính đơn giá (nếu quantity khác 0)
        if (orderItem.getQuantity() != 0) {
            holder.orderItemPrice.setText(String.valueOf(orderItem.getTotalPrice() / orderItem.getQuantity()));
        } else {
            holder.orderItemPrice.setText("0");
        }

        // Xử lý sự kiện xoá item
        // Trong onBindViewHolder
        if (status == OrderStatus.DELIVERED.getStatusValue() ||
                status == OrderStatus.CANCELLED.getStatusValue()) {
            // Nếu đơn hàng đã giao hoặc đã huỷ, ẩn nút xóa
            holder.orderItemBtnDelete.setVisibility(View.GONE);
        } else {
            // Nếu không, hiển thị nút xóa và thiết lập sự kiện xoá item
            holder.orderItemBtnDelete.setVisibility(View.VISIBLE);
            holder.orderItemBtnDelete.setOnClickListener(v -> {
                long result = dbHelper.deleteOrderItem(orderItem.getId());
                if (result > 0) {
                    orderItemsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, orderItemsList.size());
                    Toast.makeText(context, "Xoá item thành công", Toast.LENGTH_SHORT).show();
                    if (onOrderItemDeletedListener != null) {
                        onOrderItemDeletedListener.onOrderItemDeleted();
                    }
                } else {
                    Toast.makeText(context, "Xoá item thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderItemsList != null ? orderItemsList.size() : 0;
    }

    class DetailOrderItemsViewHolder extends RecyclerView.ViewHolder {
        private TextView orderItemName, orderItemQuantity, orderItemPrice, orderItemTotal;
        private ImageView orderItemBtnDelete;

        DetailOrderItemsViewHolder(View view) {
            super(view);
            orderItemName = view.findViewById(R.id.order_detail_item_name);
            orderItemQuantity = view.findViewById(R.id.order_detail_item_quantity);
            orderItemPrice = view.findViewById(R.id.order_detail_item_price);
            orderItemTotal = view.findViewById(R.id.order_detail_item_total_price);
            orderItemBtnDelete = view.findViewById(R.id.order_detail_item_btn_delete);
        }
    }
}
