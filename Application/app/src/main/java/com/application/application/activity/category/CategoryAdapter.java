package com.application.application.activity.category;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private DatabaseHelper dbHelper;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category categoryItem = categoryList.get(position);
        holder.category_title.setText(categoryItem.getName());

        holder.btn_delete_category.setOnClickListener(v -> {
            removeCategoryDB(
                position,
                categoryItem.getId()
            );
        });
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public ImageView category_image;
        public TextView category_title;
        public ImageView btn_delete_category;

        CategoryViewHolder(View itemView) {
            super(itemView);
            category_image = itemView.findViewById(R.id.category_image);
            category_title = itemView.findViewById(R.id.category_title);
            btn_delete_category = itemView.findViewById(R.id.category_btn_delete);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateCategoryOnUI(List<Category> newCategoriesList) {
        this.categoryList = newCategoriesList;
        notifyDataSetChanged();
    }

    public void removeCategoryOnUI(int position) {
        if (position >= 0 && position < categoryList.size()) {
            categoryList.remove(position);
            notifyItemChanged(position);
        }
    }

    public void removeCategoryDB(int position, int id) {
        long result = dbHelper.deleteCategory(id);
        if (result != 0) {
            removeCategoryOnUI(position);
            Toast.makeText(context, "Xoá danh mục thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Xoá danh mục thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
