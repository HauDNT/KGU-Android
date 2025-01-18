package com.application.application.activity.cart;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.application.application.R;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.sale.SaleActivity;
import com.application.application.activity.account.AccountActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.cart); // Đánh dấu mục "Giỏ hàng" là mục được chọn

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(CartActivity.this, DashboardActivity.class));
                finish(); // Kết thúc CartActivity
                return true;
            } else if (item.getItemId() == R.id.sale) {
                startActivity(new Intent(CartActivity.this, SaleActivity.class));
                finish(); // Kết thúc CartActivity
                return true;
            } else if (item.getItemId() == R.id.cart) {
                // Đang ở CartActivity, không cần chuyển
                return true;
            } else if (item.getItemId() == R.id.account) {
                startActivity(new Intent(CartActivity.this, AccountActivity.class));
                finish(); // Kết thúc CartActivity
                return true;
            }
            return false;
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Không cần thêm logic ở đây, chỉ cần quay lại là đủ
    }
}