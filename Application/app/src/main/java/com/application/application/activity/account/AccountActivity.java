package com.application.application.activity.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.order.activity.OrderActivity;
import com.application.application.activity.sale.SaleActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

//        setupBottomNavigation();
    }

//    private void setupBottomNavigation() {
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.account); // Đánh dấu mục "Tài khoản" là mục được chọn
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.home) {
//                startActivity(new Intent(AccountActivity.this, DashboardActivity.class));
//                finish(); // Kết thúc AccountActivity để không quay lại khi nhấn nút quay lại
//                return true;
//            } else if (item.getItemId() == R.id.sale) {
//                startActivity(new Intent(AccountActivity.this, SaleActivity.class));
//                finish(); // Kết thúc AccountActivity
//                return true;
//            } else if (item.getItemId() == R.id.category) {
//                startActivity(new Intent(AccountActivity.this, OrderActivity.class));
//                finish(); // Kết thúc AccountActivity
//                return true;
//            } else if (item.getItemId() == R.id.account) {
//                // Đang ở AccountActivity, không cần chuyển
//                return true;
//            }
//            return false;
//        });
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.account); // Đặt item "Tài khoản" là đã chọn
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        // Không cần thêm logic ở đây, chỉ cần quay lại là đủ
//    }
}