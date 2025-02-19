package com.application.application.fragment.category;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.application.application.fragment.dashboard.DashboardFragment;
import com.application.application.model.Category;

import java.util.ArrayList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category categoryItem = categoryList.get(position);
        holder.category_title.setText(categoryItem.getName());

        holder.btn_delete_category.setOnClickListener(v -> removeCategoryDB(position, categoryItem.getId()));
        holder.category_bound.setOnClickListener(v -> showInfoCategoryDialog(position, categoryItem.getId(), categoryItem.getName()));
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public ImageView category_image;
        public TextView category_title;
        public ImageView btn_delete_category;
        public LinearLayout category_bound;

        CategoryViewHolder(View itemView) {
            super(itemView);
            category_image = itemView.findViewById(R.id.category_image);
            category_title = itemView.findViewById(R.id.category_title);
            btn_delete_category = itemView.findViewById(R.id.category_btn_delete);
            category_bound = itemView.findViewById(R.id.category_bound);
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

    public void showInfoCategoryDialog(int position, int id, String name) {
        View infoDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_info_category_layout, null);
        EditText categoryName = infoDialogView.findViewById(R.id.dialog_info_category_name);
        Button categoryUpdateInfoBtn = infoDialogView.findViewById(R.id.dialog_info_category_btn_update);
        Button closeButton = infoDialogView.findViewById(R.id.dialog_info_category_btn_close);
        TextView alertText = infoDialogView.findViewById(R.id.dialog_info_category_alert);

        categoryName.setText(name);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(infoDialogView);
        final AlertDialog dialog = builder.create();

        categoryUpdateInfoBtn.setOnClickListener(v -> {
            Category newCategory = new Category(id, categoryName.getText().toString());
            boolean result = updateCategory(newCategory, alertText);

            if (result) {
                categoryList.set(position, newCategory);
                notifyItemChanged(position, newCategory);
                categoryName.setText("");
            }
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public boolean updateCategory(Category newCategory, TextView alertTextArea) {
        boolean isValid = Utils.regexVerify(newCategory.getName(), context.getString(R.string.regex_category_name));

        if (isValid) {
            boolean checkExistCategory = dbHelper.isExistsCategory("name", newCategory.getName());

            if (!checkExistCategory) {
                ContentValues values = new ContentValues();
                values.put("id", newCategory.getId());
                values.put("name", newCategory.getName());

                long result = dbHelper.updateCategoryName(values);

                if (result != 0) {
                    Toast.makeText(context, "Cập nhật loại sản phẩm thành công!", Toast.LENGTH_LONG).show();
                    alertTextArea.setVisibility(View.GONE);
                    return true;
                } else {
                    alertTextArea.setVisibility(View.VISIBLE);
                    alertTextArea.setText("Cập nhật loại sản phẩm thất bại.");
                }
            } else {
                alertTextArea.setVisibility(View.VISIBLE);
                alertTextArea.setText("Loại sản phẩm đã tồn tại.");
            }
        } else {
            alertTextArea.setVisibility(View.VISIBLE);
            alertTextArea.setText("Tên loại phải là tiếng Việt, gồm 1 - 10 từ.");
        }
        return false;
    }

    public void removeCategoryDB(int position, int id) {
        long result = dbHelper.deleteCategory(id);
        if (result != 0) {
            Log.d("Category size", String.valueOf(categoryList.size()));
            removeCategoryOnUI(position);
            removeCategoryOnUI(position);
            Toast.makeText(context, "Xoá danh mục thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Xoá danh mục thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeCategoryOnUI(int position) {
        if (position >= 0 && position < categoryList.size()) {
            categoryList.remove(position);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, categoryList.size());
            }, 1000);
        }
    }
}

