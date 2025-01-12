package com.application.application.activity.food;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Food;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<Food> foodList;
    private DatabaseHelper dbHelper;

    public FoodAdapter(List<Food> foodList, DatabaseHelper dbHelper) {
        this.foodList = foodList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item_list, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        //Kiểm tra xem foodList có rỗng hay không
        if (foodList != null && position < foodList.size()) {
            Food food = foodList.get(position);
            if (food != null) {  //Kiểm tra food có phải là null không
                holder.foodTitle.setText(food.getName() != null ? food.getName() : "Không có tên");
                holder.foodPrice.setText("Đơn giá: " +
                        (food.getPrice() != null ? food.getPrice() : 0.0) + " VND");
                holder.foodDescription.setText("Mô tả: " + (food.getDescription() != null ? food.getDescription() : "Không có mô tả"));
                holder.foodCode.setText("Mã sản phẩm: " + food.getId());

                //Hiển thị trạng thái
                String status = food.getStatus() == 0 ? "Còn hàng" : "Hết hàng"; //0 là còn hàng, 1 là hết hàng
                holder.foodStatus.setText(status);
                holder.foodStatus.setTextColor(food.getStatus() == 0 ? Color.GREEN : Color.RED);

                try {
                    if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
                        Uri imageUri = Uri.parse(food.getImageUrl());
                        holder.foodImage.setImageURI(imageUri);
                    } else {
                        holder.foodImage.setImageResource(R.drawable.dialog_create_category_icon); //Hình ảnh mặc định
                    }
                } catch (Exception e) {
                    Log.e("FoodAdapter", "Error setting image: " + e.getMessage());
                    holder.foodImage.setImageResource(R.drawable.dialog_create_category_icon);
                }
                holder.iconDelete.setOnClickListener(v -> {
                    //Xóa sản phẩm
                    dbHelper.deleteFood(food.getId());
                    foodList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, foodList.size());
                    Toast.makeText(v.getContext(), R.string.product_deleted, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodTitle, foodPrice, foodDescription, foodCode, foodStatus;
        ImageView foodImage, iconDelete;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodTitle = itemView.findViewById(R.id.food_title);
            foodPrice = itemView.findViewById(R.id.food_price);
            foodDescription = itemView.findViewById(R.id.food_description);
            foodCode = itemView.findViewById(R.id.food_code);
            iconDelete = itemView.findViewById(R.id.icon_delete);
            foodStatus = itemView.findViewById(R.id.food_status);
        }
    }
}