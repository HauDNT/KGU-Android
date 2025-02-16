package com.application.application.fragment.dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.application.application.fragment.order.choose_order.ChooseOrderDialogFragment;
import com.application.application.model.Food;
import com.application.application.model.OrderItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PopularFoodAdapter extends RecyclerView.Adapter<PopularFoodAdapter.ViewHolder> {
    private Context context;
    private List<OrderItem> popularItems;

    public PopularFoodAdapter(Context context, List<OrderItem> popularItems) {
        this.context = context;
        this.popularItems = popularItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = popularItems.get(position);
        holder.foodNameTextView.setText(item.getFood_name());

        // Khi click vào item, hiển thị dialog chi tiết món ăn
        holder.itemView.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            // Lấy danh sách tất cả các món ăn từ DB
            List<Food> foodList = dbHelper.getAllFoods();
            Food food = null;
            // Lọc ra món ăn có id trùng với item.getFood_id()
            for (Food f : foodList) {
                if (f.getId() == item.getFood_id()) {
                    food = f;
                    break;
                }
            }
            if (food != null) {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_food_detail, null);
                TextView tvName = dialogView.findViewById(R.id.dialog_food_name);
                TextView tvDescription = dialogView.findViewById(R.id.dialog_food_description);
                TextView tvPrice = dialogView.findViewById(R.id.dialog_food_price);
                TextView tvStatus = dialogView.findViewById(R.id.dialog_food_status);
                ImageView ivFood = dialogView.findViewById(R.id.dialog_food_image);

                tvName.setText(food.getName());
                tvDescription.setText("Mô tả: " + food.getDescription());
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                String formattedPrice = nf.format(food.getPrice());
                tvPrice.setText("Đơn giá: " + formattedPrice + " VND");

                if (food.getStatus() == 0) {
                    tvStatus.setText("Còn hàng");
                    tvStatus.setTextColor(android.graphics.Color.GREEN);
                } else {
                    tvStatus.setText("Hết hàng");
                    tvStatus.setTextColor(android.graphics.Color.RED);
                }

                try {
                    if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
                        Uri imageUri = Uri.parse(food.getImageUrl());
                        ivFood.setImageURI(imageUri);
                    } else {
                        ivFood.setImageResource(R.drawable.dialog_create_category_icon);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ivFood.setImageResource(R.drawable.dialog_create_category_icon);
                }

                // Tạo dialog với hai nút: "Mua ngay" và "Đóng"
                Food finalFood = food;
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Chi tiết món ăn")
                        .setView(dialogView)
                        .setPositiveButton("Mua ngay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Mở form thêm vào giỏ hàng dựa trên dialog_choose_order_layout.xml
                                ChooseOrderDialogFragment dialogFragment = new ChooseOrderDialogFragment();
                                Bundle dialogBundle = new Bundle();
                                dialogBundle.putString("food_id", String.valueOf(finalFood.getId()));
                                dialogFragment.setArguments(dialogBundle);
                                dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "Choose Cart Dialog");
                            }
                        })
                        .setNegativeButton("Đóng", null)
                        .create();

                Food finalFood1 = food;
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (finalFood1.getStatus() != 0) { // Sản phẩm hết hàng
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
                        } else {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                        }
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.RED);
                    }
                });
                alertDialog.show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Lỗi")
                        .setMessage("Không tìm thấy thông tin chi tiết của món ăn.")
                        .setPositiveButton("Đóng", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularItems != null ? popularItems.size() : 0;
    }

    // Phương thức cập nhật danh sách mới cho adapter
    public void updateList(List<OrderItem> newList) {
        this.popularItems = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
        }
    }
}
