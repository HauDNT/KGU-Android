package com.application.application.activity.order.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.activity.account.AccountActivity;
import com.application.application.activity.account.InformationActivity;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.food.FoodActivity;
import com.application.application.activity.order.activity.detail_order.DetailOrderDialogFragment;
import com.application.application.activity.sale.SaleActivity;
import com.application.application.activity.statistic.StatisticsActivity;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class OrderActivity extends AppCompatActivity implements DetailOrderDialogFragment.OnOrderUpdatedListener {
    private RecyclerView orderRecyclerView;
    private OrderActivityAdapter orderActivityAdapter;
    private List<Order> orderList;
    private DatabaseHelper dbHelper;
    private Button btnCreateNewOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        initElements();
        setupBottomNavigation();
    }

    private void initElements() {
        dbHelper = new DatabaseHelper(this);
        btnCreateNewOrder = findViewById(R.id.btn_create_new_order);
        initOrderRecycleView();

        btnCreateNewOrder.setOnClickListener(v -> showCreateDialog());
    }

    private void initOrderRecycleView() {
        orderRecyclerView = findViewById(R.id.order_list);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = dbHelper.getOrdersList();
        orderActivityAdapter = new OrderActivityAdapter(this, orderList);
        orderRecyclerView.setAdapter(orderActivityAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.cart); // Mark "Cart" as selected

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(OrderActivity.this, DashboardActivity.class));
                finish(); // Close OrderActivity
                return true;
            } else if (item.getItemId() == R.id.food) {
                startActivity(new Intent(OrderActivity.this, FoodActivity.class));
                finish(); // Close OrderActivity
                return true;
            } else if (item.getItemId() == R.id.cart) {
                return true; // Already in OrderActivity
            } else if (item.getItemId() == R.id.statistic) {
                startActivity(new Intent(OrderActivity.this, StatisticsActivity.class));
                finish(); // Close OrderActivity
                return true;
            } else if (item.getItemId() == R.id.account) {
                Intent intent = new Intent(OrderActivity.this, InformationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void showCreateDialog() {
        View createDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_order_layout, null);
        EditText orderName = createDialogView.findViewById(R.id.dialog_create_order_name);
        EditText orderCreatedAt = createDialogView.findViewById(R.id.dialog_create_order_created_at);
        orderCreatedAt.setText(Utils.getCurrentTime());

        EditText orderDescription = createDialogView.findViewById(R.id.dialog_create_order_description);
        Button orderCreateButton = createDialogView.findViewById(R.id.dialog_create_order_btn_create);
        Button closeButton = createDialogView.findViewById(R.id.dialog_create_order_btn_close);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(createDialogView);
        final AlertDialog dialog = builder.create();

        orderCreateButton.setOnClickListener(v -> createNewOrder(orderName, orderDescription, orderCreatedAt));
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void createNewOrder(EditText orderNameField, EditText orderDescriptionField, EditText orderCreatedAtField) {
        String orderName = orderNameField.getText().toString().trim();
        String orderDescription = orderDescriptionField.getText().toString().trim();
        String orderCreatedAt = orderCreatedAtField.getText().toString();
        String regex = getString(R.string.regex_cart_name);
        boolean isValid = Utils.regexVerify(orderName, regex);

        if (isValid) {
            boolean checkExistOrder = dbHelper.isExistsOrder("name", orderName);

            if (!checkExistOrder) {
                ContentValues values = new ContentValues();
                values.put("name", orderName);
                values.put("description", orderDescription);
                values.put("created_at", orderCreatedAt);
                values.put("updated_at", orderCreatedAt);
                values.put("status", String.valueOf(OrderStatus.PENDING.getStatusValue()));

                long result = dbHelper.createOrder(values);

                if (result > 0) {
                    // Reset form
                    orderNameField.setText("");
                    orderCreatedAtField.setText(Utils.getCurrentTime());
                    orderDescriptionField.setText("");

                    Toast.makeText(this, "Thêm giỏ hàng thành công!", Toast.LENGTH_LONG).show();
                    orderActivityAdapter.refreshOrdersList();
                }
            } else {
                Toast.makeText(this, "Giỏ hàng đã tồn tại", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Tên giỏ hàng không hợp lệ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.cart); // Set "Cart" as selected
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Logic is sufficient as is
    }

    @Override
    public void onOrderUpdated() {
        orderActivityAdapter.refreshOrdersList();
    }
}