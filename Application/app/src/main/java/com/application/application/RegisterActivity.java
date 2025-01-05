package com.application.application;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.LoginActivity;
import com.application.application.R;
import com.application.application.database.DatabaseHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, fullnameEditText, emailEditText, phoneNumberEditText, addressEditText;
    private Button registerButton;
    private TextView loginLink;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        fullnameEditText = findViewById(R.id.fullname);
        emailEditText = findViewById(R.id.email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        addressEditText = findViewById(R.id.address);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        //Xử lý khi nhấn nút "Đăng ký"
        registerButton.setOnClickListener(v -> registerUser());

        //Chuyển sang màn hình đăng nhập
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        redirectToLoginScreen();
    }

    //Phương thức mã hóa mật khẩu bằng SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullname = fullnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        //Kiểm tra nếu để trống 1 trong các trường
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(fullname) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Kiểm tra trùng username
        if (databaseHelper.isUserExists(username)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Kiểm tra trùng mail
        if (databaseHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email này đã được đăng ký!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        String hashedPassword = hashPassword(password);

        //Thêm người dùng vào cơ sở dữ liệu
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", hashedPassword); //Lưu mật khẩu đã mã hóa
        values.put("fullname", fullname);
        values.put("email", email);
        values.put("phone_number", phoneNumber);
        values.put("address", address);
        values.put("is_admin", 0); //Mặc định người dùng không phải admin

        long result = databaseHelper.insertUser(values);
        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToLoginScreen() {
        String text = loginLink.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, 17, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new android.text.style.ForegroundColorSpan(0xFF0000FF), 17, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginLink.setText(spannableString);
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
