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

public class DrinkListActivity extends AppCompatActivity {

    private ListView drinkListView;
    private ArrayList<Product> drinkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_list);

        drinkListView = findViewById(R.id.drinkListView);
        initializeDrinkList();
        setupListView();
    }

    private void initializeDrinkList() {
        drinkList = new ArrayList<>();
        drinkList.add(new Product("Cheeseburger", "Juicy cheeseburger", "Rs. 350", R.drawable.soda));
        // Thêm các loại burger khác vào danh sách
    }

    private void setupListView() {
        ArrayList<String> drinkNames = new ArrayList<>();
        for (Product drink : drinkList) {
            drinkNames.add(drink.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drinkNames);
        drinkListView.setAdapter(adapter);

        drinkListView.setOnItemClickListener((parent, view, position, id) -> {
            openDrinkDetailActivity(position);
        });
    }

    private void openDrinkDetailActivity(int position) {
        Product selectedDrink = drinkList.get(position);
        Intent intent = new Intent(DrinkListActivity.this, DetailFoodActivity.class);
        intent.putExtra("productName", selectedDrink.getName());
        intent.putExtra("productDescription", selectedDrink.getDescription());
        intent.putExtra("productPrice", selectedDrink.getPrice());
        intent.putExtra("productImage", selectedDrink.getImageResource());
        startActivity(intent);
    }
}