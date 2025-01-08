package com.application.application;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class Activity extends AppCompatActivity {

    private List<Product> productList;
    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Khởi tạo danh sách sản phẩm
        initializeProductList();

        // Khởi tạo ListView
        setupListView();

        // Khởi tạo ViewPager2 cho các poster
        setupViewPager();
    }

    private void initializeProductList() {
        productList = new ArrayList<>();
        productList.add(new Product("Margherita Pizza", "Cheesy pizza", "Rs. 1250", R.drawable.pizza));
        productList.add(new Product("Hamburger", "Double patty", "Rs. 350", R.drawable.burger));
        productList.add(new Product("Popcorn", "Buttery popcorn", "Rs. 150", R.drawable.popcorn));
        productList.add(new Product("Drink", "Refreshing drink", "Rs. 100", R.drawable.soda));
    }

    private void setupListView() {
        List<String> productNames = new ArrayList<>();
        for (Product product : productList) {
            productNames.add(product.getName());
        }

        ListView productListView = findViewById(R.id.productListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        productListView.setAdapter(adapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDetailActivity(position);
            }
        });

        // Xử lý sự kiện cho các nút "More"
        setupMoreButtons();
    }

    private void setupMoreButtons() {
        Button buttonPizza = findViewById(R.id.button_pizza);
        buttonPizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreProductsActivity("pizza"); // Chuyển tới danh sách Pizza
            }
        });

        Button buttonBurger = findViewById(R.id.button_burger);
        buttonBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreProductsActivity("burger"); // Chuyển tới danh sách Burger
            }
        });

        Button buttonPopcorn = findViewById(R.id.button_popcorn);
        buttonPopcorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreProductsActivity("popcorn"); // Chuyển tới danh sách Popcorn
            }
        });

        Button buttonDrink = findViewById(R.id.button_drink);
        buttonDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreProductsActivity("drink"); // Chuyển tới danh sách Drink
            }
        });
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);

        // Mảng các poster
        int[] posterImages = {
                R.drawable.poster1,
                R.drawable.poster2,
                R.drawable.poster3,
                // Thêm các poster khác nếu có
        };

        PosterAdapter adapterViewPager = new PosterAdapter(this, posterImages);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(2); // Giữ 2 trang bên cạnh trong bộ nhớ

        // Thiết lập Handler và Runnable để tự động chuyển động
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == posterImages.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // Thay đổi sau mỗi 3 giây
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(runnable, 3000); // Bắt đầu tự động chuyển động
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable); // Dừng tự động chuyển động khi không còn hiển thị
    }

    private void openDetailActivity(int position) {
        Product selectedProduct = productList.get(position);
        Intent intent = new Intent(Activity.this, DetailActivity.class);
        intent.putExtra("productName", selectedProduct.getName());
        intent.putExtra("productDescription", selectedProduct.getDescription());
        intent.putExtra("productPrice", selectedProduct.getPrice());
        intent.putExtra("productImage", selectedProduct.getImageResource());
        startActivity(intent);
    }

    private void openMoreProductsActivity(String category) {
        Intent intent = new Intent(Activity.this, MoreProductsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}