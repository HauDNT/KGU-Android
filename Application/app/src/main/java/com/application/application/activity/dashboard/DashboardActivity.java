package com.application.application.activity.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.application.application.activity.account.InformationActivity;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.activity.bottomNav.BottomNavigationHelper;
import com.application.application.activity.bottomNav.OnBottomNavItemSelectedListener;
import com.application.application.R;
import com.application.application.activity.account.AccountActivity;
import com.application.application.activity.bottomNav.BottomNavigationHelper;
import com.application.application.activity.bottomNav.OnBottomNavItemSelectedListener;
import com.application.application.activity.sale.SaleActivity;
import com.application.application.common.PosterAdapter;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Category;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements OnBottomNavItemSelectedListener {

    private RecyclerView recyclerViewPopular;
    private DashboardCategoryAdapter dashboardCategoryAdapter;
    private List<Category> categoryList;
    private ViewPager2 viewPager;
    private Handler handler = new Handler();
    private int currentPage = 0;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        databaseHelper = new DatabaseHelper(this);

        // Khởi tạo RecyclerView
        setupRecyclerView();

        // Khởi tạo ViewPager2 cho banner quảng cáo
        setupViewPager();

        // Khởi tạo Bottom Navigation
        setupBottomNavigation();

        categoryList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
    }
    private void initializeCategoryList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        categoryList = dbHelper.getAllCategories(); // Lấy danh sách danh mục từ DB
    }
    @Override
    protected void onResume() {
        super.onResume();
        initializeCategoryList();
        if (dashboardCategoryAdapter != null) {
            dashboardCategoryAdapter.updateList(categoryList);
        }
    }

    private void setupRecyclerView() {
        recyclerViewPopular = findViewById(R.id.recyclerViewPopular);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        categoryList = databaseHelper.getAllCategories(); // Lấy danh mục từ DB
        dashboardCategoryAdapter = new DashboardCategoryAdapter(this, categoryList, this::openCategoryDetailActivity);
        recyclerViewPopular.setAdapter(dashboardCategoryAdapter);
    }

    private void openCategoryDetailActivity(Category category) {
        Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
        intent.putExtra("categoryId", category.getId());
        intent.putExtra("categoryName", category.getName());
        startActivity(intent);
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        int[] posterImages = {R.drawable.poster1, R.drawable.poster2, R.drawable.poster3};

        PosterAdapter adapterViewPager = new PosterAdapter(this, posterImages);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(2);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPage == posterImages.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, this);
    }

    @Override
    public void onBottomNavItemSelected(int itemId) {
        if (itemId == R.id.home) {
            // Đang ở DashboardActivity, không cần chuyển
        } else if (itemId == R.id.food) {
            startActivity(new Intent(DashboardActivity.this, SaleActivity.class));
        } else if (itemId == R.id.home) {
            startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
        } else if (itemId == R.id.statistic) {
            startActivity(new Intent(DashboardActivity.this, StatisticsActivity.class));
        } else if (itemId == R.id.account) {
            startActivity(new Intent(DashboardActivity.this, InformationActivity.class));
        }
    }
}