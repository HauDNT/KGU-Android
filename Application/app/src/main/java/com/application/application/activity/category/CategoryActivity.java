package com.application.application.activity.category;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.application.R;
import com.application.application.activity.auth.Utils;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private Button btn_create_category;
    private TextView alertText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        dbHelper = new DatabaseHelper(this);

        initElements();
        btn_create_category.setOnClickListener(v -> {
            showCreateDialog();
        });
    }

    // Hàm khởi tạo các element (buttons, text views,...) của activity
    private void initElements() {
        btn_create_category = findViewById(R.id.btn_create_category);
        initCategoryRecycleView();
    }

    // Hàm xây dựng recycle view (danh sách loại sản phẩm)
    @SuppressLint("WrongViewCast")
    private void initCategoryRecycleView() {
        categoryRecyclerView = findViewById(R.id.category_list);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();

        getAllCatagoriesAndApplyToRecycleView(categoryList);

        categoryAdapter = new CategoryAdapter(categoryList);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void getAllCatagoriesAndApplyToRecycleView(List<Category> list) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = { "id", "name" };

        try (
                Cursor cursor = db.query(
                        "categories",
                        columns,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        ) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {   // Lặp qua từng dòng dữ liệu để lấy id và name của category và đưa vào hàm constructor Category(id, name) { }
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                    // Mỗi lần lặp sẽ thêm 1 category vào list
                    list.add(new Category(id, name));
                }
            }
            else {
                list.add(new Category(0, "Không có dữ liệu"));
            }
        } catch (Exception e) {
            Log.e("CategoryActivity", "Lỗi khi tải danh sách loại sản phẩm", e);
        } finally {
            db.close();
        }
    }

    // Hàm hiển thị Dialog tạo loại sản phẩm và xử lý sự kiện ấn nút tạo
    private void showCreateDialog() {
        View createDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_category_layout, null);
        EditText categoryName = createDialogView.findViewById(R.id.dialog_create_category_name);
        Button categoryCreateButton = createDialogView.findViewById(R.id.dialog_create_category_btn_create);
        Button closeButton = createDialogView.findViewById(R.id.dialog_create_category_btn_close);
        alertText = createDialogView.findViewById(R.id.dialog_create_category_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(createDialogView);
        final AlertDialog dialog = builder.create();

        categoryCreateButton.setOnClickListener(v -> {
            createNewCategory(categoryName);
        });

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    // Hàm logic chính xử lý logic tạo loại sản phẩm mới
    private void createNewCategory(EditText categoryNameField) {
        String name = categoryNameField.getText().toString().trim();
        String regex = getString(R.string.regex_category_name);

        boolean isValid = Utils.regexVerify(name, regex);

        if (isValid) {
            boolean checkExistsCategory = isExistsCategory(name);
            if (!checkExistsCategory) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put("name", name);
                db.insert("categories", null, values);
                db.close();

                categoryNameField.setText("");
                Toast.makeText(this, "Thêm loại sản phẩm thành công!", Toast.LENGTH_LONG).show();

                // Gọi lại hàm load dữ liệu loại sản phẩm vào recycle view
                initCategoryRecycleView();
            } else {
                alertText.setVisibility(View.VISIBLE);
                alertText.setText("Loại sản phẩm đã tồn tại");
            }
        } else
        {
            alertText.setVisibility(View.VISIBLE);
            alertText.setText("Tên loại phải là tiếng Việt, gồm 1 - 10 từ.");
        }
    }

    // Hàm kiểm tra xem trong DB đã tồn tại tên loại sản phẩm này chưa?
    private Boolean isExistsCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"name"};

        // Điều kiện Where đưa vào Cursor có thể viết như sau:
        // String selection = "name = ?";
        // String[] selectionArgs = new String[] { categoryName };

        try (
                Cursor cursor = db.query(
                        "categories",
                        columns,
                        "name = ?",
                        new String[] { categoryName },
                        null,
                        null,
                        null
                );
        ) {
            if (cursor.getCount() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("CategoryActivity","Xảy ra lỗi khi truy vấn tên loại trong bảng Categories", e);
            return false;
        } finally {
            db.close();
        }
    }
}