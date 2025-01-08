package com.application.application;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_popular);

        TextView nameTextView = findViewById(R.id.itemName);
        TextView descriptionTextView = findViewById(R.id.itemDescription);
        TextView priceTextView = findViewById(R.id.itemPrice);
        ImageView imageView = findViewById(R.id.itemImage);

        // Nhận dữ liệu từ Intent
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String price = getIntent().getStringExtra("price");
        int image = getIntent().getIntExtra("image", 0);


        nameTextView.setText(name);
        descriptionTextView.setText(description);
        priceTextView.setText(price);
        imageView.setImageResource(image);
    }
}
