package com.application.application.activity.food;

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

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;
    private DatabaseHelper dbHelper;
    private FoodActivity foodActivity;

    // ✅ Sửa lỗi: List<Food> thay vì List<FoodListActivity>
    public FoodAdapter(Context context, List<Food> foodList, DatabaseHelper dbHelper, FoodActivity foodActivity) {
        this.context = context;
        this.foodList = foodList;
        this.dbHelper = dbHelper;
        this.foodActivity = foodActivity;
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

                try {
                    if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
                        Uri imageUri = Uri.parse(food.getImageUrl());
                        holder.foodImage.setImageURI(imageUri);
                    } else {
                        holder.foodImage.setImageResource(R.drawable.dialog_create_category_icon);
                    }
                } catch (Exception e) {
                    Log.e("FoodAdapter", "Error setting image: " + e.getMessage());
                    holder.foodImage.setImageResource(R.drawable.dialog_create_category_icon);
                }

                // Xử lý sự kiện chỉnh sửa sản phẩm
                if (foodActivity != null) {
                    holder.itemView.setOnClickListener(v -> {
                        foodActivity.showEditFoodDialog(food);
                    });
                }

                // Nút xoá sản phẩm
                holder.iconDelete.setOnClickListener(v -> {
                    dbHelper.softDeleteFood(food.getId());
                    foodList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, foodList.size());
                    Toast.makeText(v.getContext(), R.string.product_deleted, Toast.LENGTH_SHORT).show();
                });

                if (food.getStatus() != 0) {
                    holder.iconCart.setVisibility(View.GONE);
                } else {
                    holder.iconCart.setVisibility(View.VISIBLE);
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
