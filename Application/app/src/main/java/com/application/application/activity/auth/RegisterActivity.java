package com.application.application.activity.auth;

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

import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, fullnameEditText, emailEditText, phoneNumberEditText, addressEditText;
    private Button registerButton;
    private TextView loginLink;
    private DatabaseHelper databaseHelper;
    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout, fullnameTextInputLayout, emailTextInputLayout, phoneNumberTextInputLayout, addressTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        fullnameEditText = findViewById(R.id.fullname);
        emailEditText = findViewById(R.id.email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        addressEditText = findViewById(R.id.address);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        usernameTextInputLayout = findViewById(R.id.textInputLayoutUsername);
        passwordTextInputLayout = findViewById(R.id.textInputLayoutPassword);
        fullnameTextInputLayout = findViewById(R.id.textInputLayoutFullname);
        emailTextInputLayout = findViewById(R.id.textInputLayoutEmail);
        phoneNumberTextInputLayout = findViewById(R.id.textInputLayoutPhoneNumber);
        addressTextInputLayout = findViewById(R.id.textInputLayoutAddress);

        registerButton.setOnClickListener(v -> registerUser());
        redirectToLoginScreen();
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullname = fullnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        boolean isValid = true;

        //Kiểm tra các trường nhập liệu
        if (TextUtils.isEmpty(username)) {
            usernameTextInputLayout.setError("Tên đăng nhập không được để trống.");
            isValid = false;
        } else {
            usernameTextInputLayout.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            passwordTextInputLayout.setError("Mật khẩu không được để trống.");
            isValid = false;
        } else if (!Utils.isValidPassword(password)) {
            passwordTextInputLayout.setError("Mật khẩu phải gồm chữ hoa, chữ thường và số và có độ dài tối thiểu 8 ký tự!");
            isValid = false;
        } else {
            passwordTextInputLayout.setError(null);
        }
        if (TextUtils.isEmpty(fullname)) {
            fullnameTextInputLayout.setError("Họ và tên không được để trống.");
            isValid = false;
        } else {
            fullnameTextInputLayout.setError(null);
        }
        if (TextUtils.isEmpty(email)) {
            emailTextInputLayout.setError("Email không được để trống.");
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

        //Kiểm tra trùng username và email
        if (databaseHelper.isUserExists(username)) {
            usernameTextInputLayout.setError("Tên đăng nhập đã tồn tại!");
            return;
        }
        if (databaseHelper.isEmailExists(email)) {
            emailTextInputLayout.setError("Email này đã được đăng ký!");
            return;
        }

        String hashedPassword = Utils.hashPassword(password);

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", hashedPassword);
        values.put("fullname", fullname);
        values.put("email", email);
        values.put("phone_number", phoneNumber);
        values.put("address", address);
        values.put("is_admin", 0);

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