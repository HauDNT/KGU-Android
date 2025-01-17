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

public class PopcornListActivity extends AppCompatActivity {

    private ListView popcornListView;
    private ArrayList<Product> popcornList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popcorn_list);

        popcornListView = findViewById(R.id.popcornListView);
        initializePopcornList();
        setupListView();
    }

    private void initializePopcornList() {
        popcornList = new ArrayList<>();
        popcornList.add(new Product("Cheeseburger", "Juicy cheeseburger", "Rs. 350", R.drawable.popcorn));
        // Thêm các loại burger khác vào danh sách
    }

    private void setupListView() {
        ArrayList<String> popcornNames = new ArrayList<>();
        for (Product popcorn : popcornList) {
            popcornNames.add(popcorn.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, popcornNames);
        popcornListView.setAdapter(adapter);

        popcornListView.setOnItemClickListener((parent, view, position, id) -> {
            openPopcornDetailActivity(position);
        });
    }

    private void openPopcornDetailActivity(int position) {
        Product selectedPopcorn = popcornList.get(position);
        Intent intent = new Intent(PopcornListActivity.this, DetailFoodActivity.class);
        intent.putExtra("productName", selectedPopcorn.getName());
        intent.putExtra("productDescription", selectedPopcorn.getDescription());
        intent.putExtra("productPrice", selectedPopcorn.getPrice());
        intent.putExtra("productImage", selectedPopcorn.getImageResource());
        startActivity(intent);
    }
}
