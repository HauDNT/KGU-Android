package com.application.application.fragment.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.application.application.R;
import com.application.application.model.Category;
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
        holder.itemImage.setImageResource(R.drawable.ic_category_item);
        holder.viewMoreButton.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView categoryName;
        Button viewMoreButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            categoryName = itemView.findViewById(R.id.item_name);
            viewMoreButton = itemView.findViewById(R.id.button_view_more);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // ✅ Cập nhật danh sách danh mục mới
    public void updateList(List<Category> newCategories) {
        this.categoryList.clear();
        this.categoryList.addAll(newCategories);
        notifyDataSetChanged();
    }
}