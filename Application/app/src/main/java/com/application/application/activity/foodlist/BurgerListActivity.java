package com.application.application.activity.foodlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;
import com.application.application.activity.detail_food.DetailFoodActivity;
import com.application.application.model.Product;

import java.util.ArrayList;

public class BurgerListActivity extends AppCompatActivity {

    private ListView burgerListView;
    private ArrayList<Product> burgerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burger_list);

        burgerListView = findViewById(R.id.burgerListView);
        initializeBurgerList();
        setupListView();
    }

    private void initializeBurgerList() {
        burgerList = new ArrayList<>();
        burgerList.add(new Product("Cheeseburger", "Juicy cheeseburger", "Rs. 350", R.drawable.burger));
        // Thêm các loại burger khác vào danh sách
    }

    private void setupListView() {
        ArrayList<String> burgerNames = new ArrayList<>();
        for (Product burger : burgerList) {
            burgerNames.add(burger.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, burgerNames);
        burgerListView.setAdapter(adapter);

        burgerListView.setOnItemClickListener((parent, view, position, id) -> {
            openBurgerDetailActivity(position);
        });
    }

    private void openBurgerDetailActivity(int position) {
        Product selectedBurger = burgerList.get(position);
        Intent intent = new Intent(BurgerListActivity.this, DetailFoodActivity.class);
        intent.putExtra("productName", selectedBurger.getName());
        intent.putExtra("productDescription", selectedBurger.getDescription());
        intent.putExtra("productPrice", selectedBurger.getPrice());
        intent.putExtra("productImage", selectedBurger.getImageResource());
        startActivity(intent);
    }
}