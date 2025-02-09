package com.application.application.activity.order.activity.detail_order;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.OrderItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailOrderItemsAdapter extends RecyclerView.Adapter<DetailOrderItemsAdapter.DetailOrderItemsViewHolder> {
    private Context context;
    private List<OrderItem> orderItemsList;
    private DatabaseHelper dbHelper;

    public DetailOrderItemsAdapter(Context context, List<OrderItem> orderItemsList) {
        this.context = context;
        this.orderItemsList = orderItemsList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public DetailOrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_detail_order_item_list, parent, false);
        return new DetailOrderItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailOrderItemsViewHolder holder, int position) {
        OrderItem orderItem = orderItemsList.get(position);
        int foodId = orderItem.getFood_id();
        String foodName = dbHelper.getFoodNameById(foodId);

        holder.orderItemName.setText(foodName);
        holder.orderItemQuantity.setText(String.valueOf(orderItem.getQuantity()));
        holder.orderItemTotal.setText(String.valueOf(orderItem.getTotalPrice()));
        holder.orderItemPrice.setText(String.valueOf(orderItem.getTotalPrice() / orderItem.getQuantity()));

        holder.orderItemBtnDelete.setOnClickListener(v -> {
            Log.d("Delete order item id", String.valueOf(orderItem.getOrder_id()));
        });
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
