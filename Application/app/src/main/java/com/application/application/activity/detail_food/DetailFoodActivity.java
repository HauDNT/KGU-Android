package com.application.application.activity.detail_food;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;

public class DetailFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_popular); // Tạo layout cho DetailFoodActivity

        // Nhận dữ liệu từ Intent
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        String productPrice = getIntent().getStringExtra("productPrice");
        int productImage = getIntent().getIntExtra("productImage", -1);

        // Hiển thị dữ liệu
        TextView nameTextView = findViewById(R.id.itemName);
        TextView descriptionTextView = findViewById(R.id.itemDescription);
        TextView priceTextView = findViewById(R.id.itemPrice);
        ImageView imageView = findViewById(R.id.itemImage);

        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription);
        priceTextView.setText(productPrice);
        if (productImage != -1) {
            imageView.setImageResource(productImage);
        }
    }
}