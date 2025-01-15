package com.application.application.activity.food;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_products);

        // Nhận dữ liệu từ Intent
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        String productPrice = getIntent().getStringExtra("productPrice");
        int productImage = getIntent().getIntExtra("productImage", 0);

        // Hiển thị thông tin sản phẩm
        TextView nameTextView = findViewById(R.id.product_name);
        TextView descriptionTextView = findViewById(R.id.product_description);
        TextView priceTextView = findViewById(R.id.product_price);
        ImageView imageView = findViewById(R.id.product_image);

        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription);
        priceTextView.setText(productPrice);
        imageView.setImageResource(productImage);
    }

}