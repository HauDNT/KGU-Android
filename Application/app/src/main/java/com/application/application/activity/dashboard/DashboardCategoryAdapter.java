package com.application.application.activity.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.application.application.R;
import com.application.application.model.Category;
import java.util.ArrayList;
import java.util.List;

public class DashboardCategoryAdapter extends RecyclerView.Adapter<DashboardCategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public DashboardCategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        holder.viewMoreButton.setOnClickListener(v -> listener.onCategoryClick(category));
        // Mở `CategoryDetailActivity` khi nhấn vào danh mục
        holder.viewMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryDetailActivity.class);
            intent.putExtra("category_id", category.getId()); // Truyền ID danh mục
            intent.putExtra("category_name", category.getName()); // Truyền tên danh mục
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        Button viewMoreButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.item_name);
            viewMoreButton = itemView.findViewById(R.id.button_view_more);
        }
    }

    // ✅ Cập nhật danh sách danh mục mới
    public void updateList(List<Category> newCategories) {
        if (newCategories != null) {
            this.categoryList.clear();
            this.categoryList.addAll(newCategories);
            notifyDataSetChanged();
        }
    }
}
