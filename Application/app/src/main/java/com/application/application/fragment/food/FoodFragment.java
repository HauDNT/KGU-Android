package com.application.application.fragment.food;

import android.Manifest;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.activity.account.InformationActivity;
import com.application.application.activity.category.CategoryActivity;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.order.activity.OrderActivity;
import com.application.application.activity.statistic.StatisticsActivity;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Category;
import com.application.application.model.Food;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private RecyclerView recyclerView;
    private FoodFragmentAdapter foodFragmentAdapter;
    private DatabaseHelper dbHelper;
    private Button btnCreateFood;
    private Uri imageUri;
    private View dialogView;
    private List<String> selectedCategories;

    public FoodFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.food_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());
        loadFoodList();

        btnCreateFood = view.findViewById(R.id.btn_create_food);
        btnCreateFood.setOnClickListener(v -> showCreateFoodDialog());

        // Kiểm tra quyền truy cập
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void loadFoodList() {
        List<Food> foodList = dbHelper.getAllFoods();
        if (!foodList.isEmpty()) {
            foodFragmentAdapter = new FoodFragmentAdapter(getContext(), foodList, dbHelper, FoodFragment.this);
            recyclerView.setAdapter(foodFragmentAdapter);
        } else {
            Toast.makeText(getContext(), "Không có dữ liệu món ăn", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCreateFoodDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_create_food_layout, null);
        dialogBuilder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.dialog_create_food_name);
        EditText editDescription = dialogView.findViewById(R.id.dialog_create_food_description);
        EditText editPrice = dialogView.findViewById(R.id.dialog_create_food_price);
        ChipGroup chipGroupCategories = dialogView.findViewById(R.id.dialog_create_food_category_group);
        Spinner spinnerStatus = dialogView.findViewById(R.id.dialog_create_food_status);
        Button btnCreate = dialogView.findViewById(R.id.dialog_create_food_btn_create);
        Button btnClose = dialogView.findViewById(R.id.dialog_create_food_btn_close);
        Button btnUpload = dialogView.findViewById(R.id.dialog_create_food_btn_upload);
        ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
        TextView alertText = dialogView.findViewById(R.id.dialog_create_food_alert);
        TextView categoryInfo = dialogView.findViewById(R.id.dialog_create_food_category_info);

        selectedCategories = new ArrayList<>();

        // Thiết lập giá trị cho Spinner trạng thái
        setupSpinner(spinnerStatus);

        // Thiết lập sự kiện cho loại sản phẩm
        categoryInfo.setOnClickListener(v -> showCategorySelectionDialog(categoryInfo, chipGroupCategories));

        AlertDialog alertDialog = dialogBuilder.create();

        btnUpload.setOnClickListener(v -> openFileChooser());

        btnCreate.setOnClickListener(v -> {
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

            // Kiểm tra trùng tên
            if (dbHelper.isFoodExists(name)) {
                alertText.setText("Sản phẩm với tên '" + name + "' đã tồn tại.");
                alertText.setVisibility(View.VISIBLE);
                return;
            }

            String categories = String.join(", ", selectedCategories);

            Log.d("FoodFragment", "Creating food: Name=" + name + ", Description=" + description +
                    ", Price=" + price + ", Status=" + status + ", Categories=" + categories + ", Image URI=" + imageUri);

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);
            values.put("price", price);
            values.put("status", status);
            values.put("image_url", imageUri.toString());

            long newRowId = dbHelper.insertFood(values);
            if (newRowId != -1) {
                for (String category : selectedCategories) {
                    long categoryId = dbHelper.getCategoryIdByName(category);
                    if (categoryId != -1) {
                        dbHelper.insertFoodCategory(newRowId, categoryId);
                    }
                }
                Toast.makeText(getContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loadFoodList();
            } else {
                Toast.makeText(getContext(), "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void setupSpinner(Spinner spinnerStatus) {
        String[] statuses = {"Còn hàng", "Hết hàng"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    private void showCategorySelectionDialog(TextView categoryInfo, ChipGroup chipGroup) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_category, null);
        dialogBuilder.setView(dialogView);

        ChipGroup chipGroupCategories = dialogView.findViewById(R.id.chip_group_categories);
        Button buttonConfirm = dialogView.findViewById(R.id.button_confirm_selection);
        Button btnAddCategory = dialogView.findViewById(R.id.button_add_category);

        // Lấy danh sách loại sản phẩm từ db
        String[] columns = {"id", "name", "created_at", "updated_at"};
        List<Category> categories = dbHelper.getCategoriesList(columns);

        for (Category category : categories) {
            Chip chip = new Chip(getContext());
            chip.setText(category.getName());
            chip.setCheckable(true);

            chip.setChipBackgroundColorResource(R.color.chip_background);
            chip.setTextColor(getResources().getColor(R.color.chip_text_color));

            chip.setChipStrokeWidth(5);
            chip.setChipStrokeColorResource(R.color.chip_stroke_color);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    chip.setChipBackgroundColorResource(R.color.chip_selected_background);
                    chip.setChipStrokeColorResource(R.color.chip_stroke_selected_color);
                } else {
                    chip.setChipBackgroundColorResource(R.color.chip_background);
                    chip.setChipStrokeColorResource(R.color.chip_stroke_color);
                }
            });

            chipGroupCategories.addView(chip);
        }

        AlertDialog alertDialog = dialogBuilder.create();

        buttonConfirm.setOnClickListener(v -> {
            selectedCategories.clear();
            for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupCategories.getChildAt(i);
                if (chip.isChecked()) {
                    selectedCategories.add(chip.getText().toString());
                }
            }
            updateSelectedCategoriesDisplay(categoryInfo, chipGroup);
            alertDialog.dismiss();
        });

        // Xử lý sự kiện click cho nút "Thêm loại"
        btnAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CategoryActivity.class);
            startActivity(intent);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void updateSelectedCategoriesDisplay(TextView categoryInfo, ChipGroup chipGroup) {
        chipGroup.removeAllViews();

        if (selectedCategories.isEmpty()) {
            categoryInfo.setVisibility(View.VISIBLE);
            categoryInfo.setText("Chọn loại sản phẩm (Nhấn vào để chọn)");
        } else {
            categoryInfo.setVisibility(View.GONE);
            for (String category : selectedCategories) {
                Chip chip = new Chip(getContext());
                chip.setText(category);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> {
                    selectedCategories.remove(category);
                    updateSelectedCategoriesDisplay(categoryInfo, chipGroup);
                });
                chipGroup.addView(chip);
            }
            categoryInfo.setText("Loại sản phẩm: " + String.join(", ", selectedCategories));
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
                imagePreview.setImageBitmap(bitmap);
                saveImageToInternalStorage(bitmap, "food_image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmap, String name) {
        File directory = new File(requireActivity().getFilesDir(), "images");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String uniqueName = name + "_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, uniqueName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            imageUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEditFoodDialog(Food food) {
        // Reset imageUri để tránh dùng ảnh của sản phẩm trước đó
        imageUri = null;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_edit_food_layout, null);
        dialogBuilder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.dialog_edit_food_name);
        EditText editDescription = dialogView.findViewById(R.id.dialog_edit_food_description);
        EditText editPrice = dialogView.findViewById(R.id.dialog_edit_food_price);
        ChipGroup chipGroupCategories = dialogView.findViewById(R.id.dialog_edit_food_category_group);
        Spinner spinnerStatus = dialogView.findViewById(R.id.dialog_edit_food_status);
        Button btnSave = dialogView.findViewById(R.id.dialog_edit_food_btn_save);
        Button btnClose = dialogView.findViewById(R.id.dialog_edit_food_btn_close);
        ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
        TextView alertText = dialogView.findViewById(R.id.dialog_edit_food_alert);
        TextView categoryInfo = dialogView.findViewById(R.id.dialog_edit_food_category_info);
        Button btnUpload = dialogView.findViewById(R.id.dialog_edit_food_btn_upload);

        // Điền thông tin sản phẩm vào các trường
        editName.setText(food.getName());
        editDescription.setText(food.getDescription());
        editPrice.setText(String.valueOf((int) food.getPrice()));

        // Tải ảnh ban đầu: sử dụng biến cục bộ currentImageUri để hiển thị ảnh gốc
        Uri currentImageUri = Uri.parse(food.getImageUrl());
        imagePreview.setImageURI(currentImageUri);

        // Cập nhật danh sách loại sản phẩm đã chọn
        selectedCategories = dbHelper.getCategoriesForFood(food.getId());
        updateSelectedCategoriesDisplay(categoryInfo, chipGroupCategories);

        // Thiết lập giá trị cho Spinner trạng thái
        setupSpinner(spinnerStatus);

        // Thêm sự kiện click cho danh mục
        categoryInfo.setOnClickListener(v -> showCategorySelectionDialog(categoryInfo, chipGroupCategories));

        AlertDialog alertDialog = dialogBuilder.create();

        btnUpload.setOnClickListener(v -> openFileChooser());

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String priceString = editPrice.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || priceString.isEmpty()) {
                alertText.setText("Vui lòng điền đầy đủ thông tin.");
                alertText.setVisibility(View.VISIBLE);
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceString);
            } catch (NumberFormatException e) {
                alertText.setText("Đơn giá không hợp lệ.");
                alertText.setVisibility(View.VISIBLE);
                return;
            }

            // Kiểm tra trùng tên
            if (dbHelper.isFoodExists(name) && !name.equals(food.getName())) {
                alertText.setText("Sản phẩm với tên '" + name + "' đã tồn tại.");
                alertText.setVisibility(View.VISIBLE);
                return;
            }

            int status = spinnerStatus.getSelectedItemPosition();

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);
            values.put("price", price);
            values.put("status", status);

            // Nếu người dùng chọn ảnh mới, biến toàn cục imageUri đã được cập nhật qua onActivityResult()
            if (this.imageUri != null) {
                values.put("image_url", this.imageUri.toString());
            } else {
                values.put("image_url", food.getImageUrl());
            }

            // Cập nhật sản phẩm trong cơ sở dữ liệu
            int rowsUpdated = dbHelper.updateFood(food.getId(), values);
            if (rowsUpdated > 0) {
                dbHelper.updateFoodCategories(food.getId(), selectedCategories);
                Toast.makeText(getContext(), "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loadFoodList();
            } else {
                Toast.makeText(getContext(), "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
        updateSelectedCategoriesDisplay(categoryInfo, chipGroupCategories);
    }

    @Override
    public void onResume() {
        super.onResume();
        foodFragmentAdapter.refreshFoodlist();
    }
}
