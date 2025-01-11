package com.application.application.activity.food;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Food;

import java.io.IOException;
import java.util.List;

public class FoodActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private DatabaseHelper dbHelper;
    private Button btnCreateFood;
    private Uri imageUri;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food);

        recyclerView = findViewById(R.id.food_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        loadFoodList();

        btnCreateFood = findViewById(R.id.btn_create_food);
        btnCreateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateFoodDialog();
            }
        });

        //Kiểm tra quyền truy cập
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void loadFoodList() {
        List<Food> foodList = dbHelper.getAllFoods();
        foodAdapter = new FoodAdapter(foodList, dbHelper);
        recyclerView.setAdapter(foodAdapter);
    }

    private void showCreateFoodDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_create_food_layout, null);
        dialogBuilder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.dialog_create_food_name);
        EditText editDescription = dialogView.findViewById(R.id.dialog_create_food_description);
        EditText editPrice = dialogView.findViewById(R.id.dialog_create_food_price);
        Spinner spinnerCategory = dialogView.findViewById(R.id.dialog_create_food_category);
        Spinner spinnerStatus = dialogView.findViewById(R.id.dialog_create_food_status);
        Button btnCreate = dialogView.findViewById(R.id.dialog_create_food_btn_create);
        Button btnClose = dialogView.findViewById(R.id.dialog_create_food_btn_close);
        Button btnUpload = dialogView.findViewById(R.id.dialog_create_food_btn_upload);
        ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
        TextView alertText = dialogView.findViewById(R.id.dialog_create_food_alert);

        //Thiết lập giá trị cho Spinner trạng thái
        setupSpinner(spinnerStatus);
        //Thiết lập giá trị cho Spinner loại sản phẩm
        setupCategorySpinner(spinnerCategory);

        AlertDialog alertDialog = dialogBuilder.create();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String priceString = editPrice.getText().toString().trim();

                if (name.isEmpty() || description.isEmpty() || priceString.isEmpty() || imageUri == null) {
                    alertText.setText("Vui lòng điền đầy đủ thông tin và tải lên hình ảnh.");
                    alertText.setVisibility(View.VISIBLE);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceString);
                } catch (NumberFormatException e) {
                    alertText.setText("Đơn giá không hợp lệ.");
                    alertText.setVisibility(View.VISIBLE);
                    return;
                }

                int status = spinnerStatus.getSelectedItemPosition();

                String category = null;
                if (spinnerCategory.getSelectedItem() != null) {
                    category = spinnerCategory.getSelectedItem().toString();
                }

                Log.d("FoodActivity", "Creating food: Name=" + name + ", Description=" + description + ", Price=" + price + ", Status=" + status + ", Category=" + (category != null ? category : "Không chọn") + ", Image URI=" + imageUri);

                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("description", description);
                values.put("price", price);
                values.put("status", status);
                values.put("image_url", imageUri.toString());

                if (category != null) {
                    values.put("category", category);
                }

                long newRowId = dbHelper.insertFood(values);
                if (newRowId != -1) {
                    Toast.makeText(FoodActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    loadFoodList();
                } else {
                    Toast.makeText(FoodActivity.this, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void setupSpinner(Spinner spinnerStatus) {
        String[] statuses = {"Còn hàng", "Hết hàng"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    private void setupCategorySpinner(Spinner spinnerCategory) {
        List<String> categories = dbHelper.getAllCategories(); // Lấy danh sách loại sản phẩm từ database
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}