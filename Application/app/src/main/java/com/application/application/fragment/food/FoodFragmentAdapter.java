package com.application.application.fragment.food;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.activity.order.choose_order.ChooseOrderDialogFragment;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Food;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodFragmentAdapter extends RecyclerView.Adapter<FoodFragmentAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;
    private DatabaseHelper dbHelper;
    private FoodFragment foodFragment;

    public FoodFragmentAdapter(Context context, List<Food> foodList, DatabaseHelper dbHelper, FoodFragment foodFragment) {
        this.context = context;
        this.foodList = foodList;
        this.dbHelper = dbHelper;
        this.foodFragment = foodFragment;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_list, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        if (foodList != null && position < foodList.size()) {
            Food food = foodList.get(position);
            if (food != null) {
                // Hiển thị thông tin sản phẩm
                holder.foodTitle.setText(food.getName() != null ? food.getName() : "Không có tên");
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String formattedPrice = currencyFormat.format(food.getPrice());
                holder.foodPrice.setText("Đơn giá: " + formattedPrice);
                holder.foodDescription.setText("Mô tả: " + (food.getDescription() != null ? food.getDescription() : "Không có mô tả"));
                holder.foodCode.setText("Mã sản phẩm: " + food.getId());

                List<String> categories = dbHelper.getCategoriesForFood(food.getId());
                holder.foodType.setText("Loại sản phẩm: " + String.join(", ", categories));

                String status = food.getStatus() == 0 ? "Còn hàng" : "Hết hàng";
                holder.foodStatus.setText(status);
                holder.foodStatus.setTextColor(food.getStatus() == 0 ? Color.GREEN : Color.RED);

                // Xử lý hình ảnh
                try {
                    if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
                        Uri imageUri = Uri.parse(food.getImageUrl());
                        holder.foodImage.setImageURI(imageUri);
                    } else {
                        holder.foodImage.setImageResource(R.drawable.dialog_create_category_icon);
                    }
                } catch (Exception e) {
                    Log.e("FoodFragmentAdapter", "Error setting image: " + e.getMessage());
                    holder.foodImage.setImageResource(R.drawable.dialog_create_category_icon);
                }

                // Sự kiện chỉnh sửa sản phẩm
                holder.itemView.setOnClickListener(v -> {
                    foodFragment.showEditFoodDialog(food);
                });

                // Nút xoá sản phẩm
                holder.iconDelete.setOnClickListener(v -> {
                    dbHelper.deleteFood(food.getId());
                    foodList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, foodList.size());
                    Toast.makeText(v.getContext(), R.string.product_deleted, Toast.LENGTH_SHORT).show();
                });

                // Kiểm tra trạng thái sản phẩm: nếu hết hàng thì ẩn nút thêm vào giỏ hàng
                if (food.getStatus() != 0) {
                    holder.iconCart.setVisibility(View.GONE);
                } else {
                    holder.iconCart.setVisibility(View.VISIBLE);
                    // Sự kiện thêm sản phẩm vào giỏ hàng
                    holder.iconCart.setOnClickListener(v -> {
                        ChooseOrderDialogFragment dialogFragment = new ChooseOrderDialogFragment();
                        Bundle dialogBundle = new Bundle();
                        dialogBundle.putString("food_id", String.valueOf(food.getId()));
                        dialogFragment.setArguments(dialogBundle);
                        dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "Choose Cart Dialog");
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodTitle, foodPrice, foodDescription, foodCode, foodStatus, foodType;
        ImageView foodImage, iconDelete, iconCart;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodTitle = itemView.findViewById(R.id.food_title);
            foodPrice = itemView.findViewById(R.id.food_price);
            foodDescription = itemView.findViewById(R.id.food_description);
            foodCode = itemView.findViewById(R.id.food_code);
            iconDelete = itemView.findViewById(R.id.icon_delete);
            iconCart = itemView.findViewById(R.id.icon_cart);
            foodStatus = itemView.findViewById(R.id.food_status);
            foodType = itemView.findViewById(R.id.food_type);
        }
    }
}
