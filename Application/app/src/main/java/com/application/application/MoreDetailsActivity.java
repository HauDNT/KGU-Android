// MoreDetailsActivity.java
package com.application.application;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MoreDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addeditdelete); // Tạo layout cho Activity này

        // Nhận dữ liệu từ Intent
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        String productPrice = getIntent().getStringExtra("productPrice");
        int productImage = getIntent().getIntExtra("productImage", -1);

        // Hiển thị dữ liệu
        TextView nameTextView = findViewById(R.id.moreProductName);
        TextView descriptionTextView = findViewById(R.id.moreProductDescription);
        TextView priceTextView = findViewById(R.id.moreProductPrice);
        ImageView imageView = findViewById(R.id.moreProductImage);

        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription);
        priceTextView.setText(productPrice);
        if (productImage != -1) {
            imageView.setImageResource(productImage);
        }
    }
}