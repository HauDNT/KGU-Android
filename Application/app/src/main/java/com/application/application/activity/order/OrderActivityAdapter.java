package com.application.application.activity.order;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.application.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
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
        setOrderStatus(orderItem.getStatus(), holder.order_status);

        //Nếu đơn hàng quá khứ (DELIVERED hoặc CANCELLED) thì ẩn nút xoá.
        if (orderItem.getStatus() == OrderStatus.DELIVERED || orderItem.getStatus() == OrderStatus.CANCELLED) {
            holder.btn_delete_order.setVisibility(View.GONE);
        } else {
            holder.btn_delete_order.setVisibility(View.VISIBLE);
            holder.btn_delete_order.setOnClickListener(v -> removeOrderDB(position, orderItem.getId()));
        }

        //Khi click vào item, mở dialog sửa đơn hàng
        holder.order_bound.setOnClickListener(v -> {
            EditOrderDialogFragment fragment = EditOrderDialogFragment.newInstance(orderItem);
            fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "EditOrderDialog");
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
        public ImageView btn_delete_order;

        OrderViewHolder(View itemView) {
            super(itemView);
            order_title = itemView.findViewById(R.id.order_item_title);
            order_created_at = itemView.findViewById(R.id.order_item_created_at);
            order_status = itemView.findViewById(R.id.order_item_status);
            order_bound = itemView.findViewById(R.id.order_item_bound);
            btn_delete_order = itemView.findViewById(R.id.order_item_btn_delete);
        }
    }

    //Hiển thị trạng thái đơn hàng (với text và màu sắc)
    public void setOrderStatus(OrderStatus orderStatus, TextView statusField) {
        switch (orderStatus) {
            case PENDING:
                statusField.setText("Đang xử lý");
                statusField.setTextColor(Color.BLUE);
                break;
            case DELIVERED:
                statusField.setText("Đã giao");
                statusField.setTextColor(Color.GREEN);
                break;
            case CANCELLED:
                statusField.setText("Đã huỷ");
                statusField.setTextColor(Color.RED);
                break;
            default:
                statusField.setText("Không xác định");
                break;
        }
    }

    // Xoá đơn hàng khỏi CSDL và cập nhật UI
    public void removeOrderDB(int position, int id) {
        long result = dbHelper.deleteOrder(id);
        if (result != 0) {
            removeOrderUI(position);
            Toast.makeText(context, "Xoá đơn hàng thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Xoá đơn hàng thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    // Xoá đơn hàng khỏi danh sách hiển thị
    public void removeOrderUI(int position) {
        if (position >= 0 && position < orderList.size()) {
            orderList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderList.size());
        }
    }
}
