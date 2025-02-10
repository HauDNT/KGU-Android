package com.application.application.activity.sale;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.account.AccountActivity;
import com.application.application.activity.order.activity.OrderActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.sale); // Đánh dấu mục "Khuyến mãi" là mục được chọn

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(SaleActivity.this, DashboardActivity.class));
                finish(); // Kết thúc SaleActivity để không quay lại khi nhấn nút quay lại
                return true;
            } else if (item.getItemId() == R.id.sale) {
                // Đang ở SaleActivity, không cần chuyển
                return true;
            } else if (item.getItemId() == R.id.cart) {
                startActivity(new Intent(SaleActivity.this, OrderActivity.class));
                finish(); // Kết thúc SaleActivity
                return true;
            } else if (item.getItemId() == R.id.account) {
                startActivity(new Intent(SaleActivity.this, AccountActivity.class));
                finish(); // Kết thúc SaleActivity
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.sale); // Đặt item "Khuyến mãi" là đã chọn
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Không cần thêm logic ở đây, chỉ cần quay lại là đủ
    }
}