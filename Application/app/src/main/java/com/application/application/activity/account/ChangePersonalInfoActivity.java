package com.application.application.activity.account;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePersonalInfoActivity extends AppCompatActivity {

    private TextInputEditText fullnameEditText, emailEditText, phoneNumberEditText, addressEditText;
    private TextInputLayout fullnameTextInputLayout, emailTextInputLayout, phoneNumberTextInputLayout, addressTextInputLayout;
    private DatabaseHelper databaseHelper;
    private String currentUsername; // Lấy từ Intent hoặc SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_personal_info);

        databaseHelper = new DatabaseHelper(this);
        // Giả sử username của người dùng hiện tại được truyền qua Intent
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gán xử lý cho nút quay về
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ChangePersonalInfoActivity.this, InformationActivity.class);
            startActivity(intent);
            finish();
        });

        fullnameEditText = findViewById(R.id.fullname);
        emailEditText = findViewById(R.id.email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        addressEditText = findViewById(R.id.address);

        fullnameTextInputLayout = findViewById(R.id.textInputLayoutFullname);
        emailTextInputLayout = findViewById(R.id.textInputLayoutEmail);
        phoneNumberTextInputLayout = findViewById(R.id.textInputLayoutPhoneNumber);
        addressTextInputLayout = findViewById(R.id.textInputLayoutAddress);

        // (Tùy chọn) Tải dữ liệu hiện tại của người dùng từ database để hiển thị

        findViewById(R.id.update_button).setOnClickListener(v -> updatePersonalInfo());
    }

    private void updatePersonalInfo() {
        String fullname = fullnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(fullname)) {
            fullnameTextInputLayout.setError("Họ và tên không được để trống.");
            isValid = false;
        } else {
            fullnameTextInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            emailTextInputLayout.setError("Email không được để trống.");
            isValid = false;
        } else if (!Utils.isValidEmail(email)) {
            emailTextInputLayout.setError("Email không hợp lệ.");
            isValid = false;
        } else {
            emailTextInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberTextInputLayout.setError("Số điện thoại không được để trống.");
            isValid = false;
        } else {
            phoneNumberTextInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(address)) {
            addressTextInputLayout.setError("Địa chỉ không được để trống.");
            isValid = false;
        } else {
            addressTextInputLayout.setError(null);
        }

        if (!isValid) return;

        ContentValues values = new ContentValues();
        values.put("fullname", fullname);
        values.put("email", email);
        values.put("phone_number", phoneNumber);
        values.put("address", address);

        int result = databaseHelper.updateUserPersonalInfo(currentUsername, values);
        if (result > 0) {
            Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thông tin thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}
