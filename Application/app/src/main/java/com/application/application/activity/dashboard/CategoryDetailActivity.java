package com.application.application.activity.dashboard;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.application.application.R;
import com.application.application.activity.food.FoodAdapter;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Food;
import java.util.List;

public class CategoryDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private DatabaseHelper databaseHelper;
    private int categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        categoryId = getIntent().getIntExtra("category_id", -1);
        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewFoods);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFoodsByCategory();
    }

    private void loadFoodsByCategory() {
        if (categoryId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Food> foodList = databaseHelper.getFoodsByCategory(categoryId);
        foodAdapter = new FoodAdapter(this, foodList, databaseHelper, null);
        recyclerView.setAdapter(foodAdapter);
    }
}
