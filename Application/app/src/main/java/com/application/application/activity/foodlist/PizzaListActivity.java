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

public class PizzaListActivity extends AppCompatActivity {

    private ListView pizzaListView;
    private ArrayList<Product> pizzaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_list);

        pizzaListView = findViewById(R.id.pizzaListView);
        initializePizzaList();
        setupListView();
    }

    private void initializePizzaList() {
        pizzaList = new ArrayList<>();
        pizzaList.add(new Product("Margherita Pizza", "Classic cheesy pizza", "Rs. 1250", R.drawable.pizza));
        // Thêm các loại pizza khác vào danh sách
    }

    private void setupListView() {
        ArrayList<String> pizzaNames = new ArrayList<>();
        for (Product pizza : pizzaList) {
            pizzaNames.add(pizza.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pizzaNames);
        pizzaListView.setAdapter(adapter);

        pizzaListView.setOnItemClickListener((parent, view, position, id) -> {
            openPizzaDetailActivity(position);
        });
    }

    private void openPizzaDetailActivity(int position) {
        Product selectedPizza = pizzaList.get(position);
        Intent intent = new Intent(PizzaListActivity.this, DetailFoodActivity.class);
        intent.putExtra("productName", selectedPizza.getName());
        intent.putExtra("productDescription", selectedPizza.getDescription());
        intent.putExtra("productPrice", selectedPizza.getPrice());
        intent.putExtra("productImage", selectedPizza.getImageResource());
        startActivity(intent);
    }
}