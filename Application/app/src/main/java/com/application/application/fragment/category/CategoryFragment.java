package com.application.application.fragment.category;

import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.fragment.category.CategoryAdapter;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private Button btnCreateCategory;
    private TextView alertText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        btnCreateCategory = view.findViewById(R.id.btn_create_category);
        categoryRecyclerView = view.findViewById(R.id.category_list);

        initCategoryRecyclerView();

        btnCreateCategory.setOnClickListener(v -> showCreateDialog());

        return view;
    }

    private void initCategoryRecyclerView() {
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryList = new ArrayList<>();
        categoryList = dbHelper.getCategoriesList(new String[]{"id", "name"});
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void showCreateDialog() {
        View createDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_category_layout, null);
        EditText categoryName = createDialogView.findViewById(R.id.dialog_create_category_name);
        Button categoryCreateButton = createDialogView.findViewById(R.id.dialog_create_category_btn_create);
        Button closeButton = createDialogView.findViewById(R.id.dialog_create_category_btn_close);
        alertText = createDialogView.findViewById(R.id.dialog_create_category_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(createDialogView);
        final AlertDialog dialog = builder.create();

        categoryCreateButton.setOnClickListener(v -> createNewCategory(categoryName));
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void createNewCategory(EditText categoryNameField) {
        String name = categoryNameField.getText().toString().trim();
        String regex = getString(R.string.regex_category_name);
        boolean isValid = Utils.regexVerify(name, regex);

        if (isValid) {
            boolean checkExistsCategory = dbHelper.isExistsCategory("name", name);
            if (!checkExistsCategory) {
                ContentValues values = new ContentValues();
                values.put("name", name);
                dbHelper.insertCategory(values);

                categoryNameField.setText("");
                Toast.makeText(requireContext(), "Thêm loại sản phẩm thành công!", Toast.LENGTH_LONG).show();
                alertText.setVisibility(View.GONE);

                categoryAdapter.updateCategoryOnUI(dbHelper.getCategoriesList(new String[]{"id", "name"}));
            } else {
                alertText.setVisibility(View.VISIBLE);
                alertText.setText("Loại sản phẩm đã tồn tại");
            }
        } else {
            alertText.setVisibility(View.VISIBLE);
            alertText.setText("Tên loại phải là tiếng Việt, gồm 1 - 10 từ.");
        }
    }
}
