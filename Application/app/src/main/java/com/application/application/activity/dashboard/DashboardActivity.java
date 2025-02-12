package com.application.application.activity.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.application.application.activity.auth.LoginActivity;
import com.application.application.activity.bottomNav.BottomNavigationHelper;
import com.application.application.activity.bottomNav.OnBottomNavItemSelectedListener;
import com.application.application.R;
import com.application.application.activity.account.AccountActivity;
import com.application.application.activity.detail_food.DetailFoodActivity;
import com.application.application.activity.food.FoodActivity;
import com.application.application.activity.food.FoodListActivity;
import com.application.application.activity.foodlist.BurgerListActivity;
import com.application.application.activity.foodlist.DrinkListActivity;
import com.application.application.activity.foodlist.PizzaListActivity;
import com.application.application.activity.foodlist.PopcornListActivity;
import com.application.application.activity.order.activity.OrderActivity;
import com.application.application.activity.sale.SaleActivity;
import com.application.application.activity.statistic.StatisticsActivity;
import com.application.application.common.PosterAdapter;
import com.application.application.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements OnBottomNavItemSelectedListener {

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

        // Khởi tạo Bottom Navigation
        setupBottomNavigation();
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
        buttonPizza.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this,  PizzaListActivity.class);
            startActivity(intent);
        });

        Button buttonBurger = findViewById(R.id.button_burger);
        buttonBurger.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, BurgerListActivity.class);
            startActivity(intent);
        });

        Button buttonPopcorn = findViewById(R.id.button_popcorn);
        buttonPopcorn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PopcornListActivity.class);
            startActivity(intent);
        });

        Button buttonDrink = findViewById(R.id.button_drink);
        buttonDrink.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DrinkListActivity.class);
            startActivity(intent);
        });
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        int[] posterImages = {R.drawable.poster1, R.drawable.poster2, R.drawable.poster3};

        PosterAdapter adapterViewPager = new PosterAdapter(this, posterImages);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(2);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == posterImages.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    private void openDetailActivity(int position) {
        Product selectedProduct = productList.get(position);
        Intent intent = new Intent(DashboardActivity.this, DetailFoodActivity.class);
        intent.putExtra("productName", selectedProduct.getName());
        intent.putExtra("productDescription", selectedProduct.getDescription());
        intent.putExtra("productPrice", selectedProduct.getPrice());
        intent.putExtra("productImage", selectedProduct.getImageResource());
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, this);
    }

    @Override
    public void onBottomNavItemSelected(int itemId) {
        if (itemId == R.id.home) {
            // Đang ở DashboardActivity, không cần chuyển
            return; // Thêm return để không thực hiện gì cả
        } else if (itemId == R.id.food) {
            startActivity(new Intent(DashboardActivity.this, FoodActivity.class));
        } else if (itemId == R.id.cart) {
            startActivity(new Intent(DashboardActivity.this, OrderActivity.class));
        } else if (itemId == R.id.statistic) {
            startActivity(new Intent(DashboardActivity.this, StatisticsActivity.class));
        } else if (itemId == R.id.logout) {
            logoutUser();
        }
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
        editor.clear(); 
        editor.apply();

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        // Xóa stack để không thể quay lại Dashboard bằng nút Back
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home); // Đặt item "Home" là đã chọn
    }
}