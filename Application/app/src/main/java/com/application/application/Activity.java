package com.application.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;
import java.util.List;

public class Activity extends AppCompatActivity {

    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Khởi tạo danh sách sản phẩm
        productList = new ArrayList<>();
        productList.add(new Product("Margherita Pizza", "Cheesy pizza", "Rs. 1250", R.drawable.pizza));
        productList.add(new Product("Hamburger", "Double patty", "Rs. 350", R.drawable.burger));
        productList.add(new Product("Popcorn", "Buttery popcorn", "Rs. 150", R.drawable.popcorn));
        productList.add(new Product("Drink", "Refreshing drink", "Rs. 100", R.drawable.soda));
        // Lấy danh sách tên sản phẩm để hiển thị trong ListView
        List<String> productNames = new ArrayList<>();
        for (Product product : productList) {
            productNames.add(product.getName());
        }

        // Khởi tạo ListView
        ListView productListView = findViewById(R.id.productListView);

        // Sử dụng ArrayAdapter để gắn dữ liệu vào ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        productListView.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn vào một sản phẩm
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy sản phẩm được nhấn
                Product selectedProduct = productList.get(position);

                // Chuyển sang Activity khác
                Intent intent = new Intent(Activity.this, DetailActivity.class);
                intent.putExtra("productName", selectedProduct.getName());
                intent.putExtra("productDescription", selectedProduct.getDescription());
                intent.putExtra("productPrice", selectedProduct.getPrice());
                intent.putExtra("productImage", selectedProduct.getImageResource());
                startActivity(intent);
            }
        });

        // Xử lý sự kiện cho các nút "More"
        Button buttonPizza = findViewById(R.id.button_pizza);
        buttonPizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreDetails(0); // Chuyển tới chi tiết Pizza
            }
        });

        Button buttonBurger = findViewById(R.id.button_burger);
        buttonBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreDetails(1); // Chuyển tới chi tiết Hamburger
            }
        });

        Button buttonPopcorn = findViewById(R.id.button_popcorn);
        buttonPopcorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreDetails(2); // Chuyển tới chi tiết Popcorn
            }
        });
        Button buttonDrink = findViewById(R.id.button_drink);
        buttonDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreDetails(3); // Chuyển tới chi tiết Drink
            }
        });
    }

    private void openMoreDetails(int index) {
        Product selectedProduct = productList.get(index);
        Intent intent = new Intent(Activity.this, MoreDetailsActivity.class);
        intent.putExtra("productName", selectedProduct.getName());
        intent.putExtra("productDescription", selectedProduct.getDescription());
        intent.putExtra("productPrice", selectedProduct.getPrice());
        intent.putExtra("productImage", selectedProduct.getImageResource());
        startActivity(intent);
    }
}