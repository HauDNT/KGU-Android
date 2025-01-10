package com.application.application.activity.food;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_products);

        ListView listView = findViewById(R.id.moreProductsListView);
        String category = getIntent().getStringExtra("category");

        List<String> products = new ArrayList<>();
        if ("pizza".equals(category)) {
            products.add("Margherita Pizza");
            products.add("Pepperoni Pizza");
            products.add("Veggie Pizza");
            // Thêm các loại pizza khác nếu có
        } else if ("burger".equals(category)) {
            products.add("Cheeseburger");
            products.add("Bacon Burger");
            products.add("Veggie Burger");
            // Thêm các loại burger khác nếu có
        } else if ("popcorn".equals(category)) {
            products.add("Butter Popcorn");
            products.add("Cheese Popcorn");
            // Thêm các loại popcorn khác nếu có
        } else if ("drink".equals(category)) {
            products.add("Cola");
            products.add("Lemonade");
            // Thêm các loại drink khác nếu có
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(adapter);
    }
}