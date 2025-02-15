package com.application.application.activity.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.food.FoodActivity;
import com.application.application.activity.order.activity.OrderActivity;
import com.application.application.activity.statistic.StatisticsActivity;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InformationActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);

        databaseHelper = new DatabaseHelper(this);
        userNameTextView = findViewById(R.id.userNameTextView);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("username", null);
        if (currentUsername == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(InformationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        loadUserInfo();

        View cardPersonalInfo = findViewById(R.id.cardPersonalInfo);
        View cardChangePassword = findViewById(R.id.cardChangePassword);
        View cardListCart = findViewById(R.id.cardListCart);
        View cardDelAcc = findViewById(R.id.cardDelAcc);
        View cardLogout = findViewById(R.id.cardLogout);

        cardPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this, ChangePersonalInfoActivity.class);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            }
        });

        cardChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            }
        });

        cardListCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        cardDelAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xóa tài khoản khỏi database
                int result = databaseHelper.deleteUser(currentUsername);
                if (result > 0) {
                    Toast.makeText(InformationActivity.this, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    // Xóa SharedPreferences (trạng thái đăng nhập và username)
                    SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(InformationActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(InformationActivity.this, "Xóa tài khoản thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InformationActivity.this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(InformationActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        setupBottomNavigation();
    }

    private void loadUserInfo() {
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT fullname FROM users WHERE username = ?", new String[]{currentUsername});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
                if (!fullname.isEmpty()) {
                    userNameTextView.setText(fullname);
                } else {
                    userNameTextView.setText(currentUsername);
                }
            }
            cursor.close();
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(InformationActivity.this, DashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.food) {
                startActivity(new Intent(InformationActivity.this, FoodActivity.class));
                finish();
                return true;
            } else if (id == R.id.order) {
                startActivity(new Intent(InformationActivity.this, OrderActivity.class));
                finish();
                return true;
            } else if (id == R.id.statistic) {
                startActivity(new Intent(InformationActivity.this, StatisticsActivity.class));
                finish();
                return true;
            } else if (id == R.id.account) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);
    }
}
