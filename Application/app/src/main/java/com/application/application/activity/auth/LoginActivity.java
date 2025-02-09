package com.application.application.activity.auth;

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

import com.application.application.Utils;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private DatabaseHelper databaseHelper;
    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);
        usernameTextInputLayout = findViewById(R.id.textInputLayoutUsername);
        passwordTextInputLayout = findViewById(R.id.textInputLayoutPassword);


        loginButton.setOnClickListener(v -> loginUser());
        setupRegisterLink();
    }

    private void loginUser() {
        String usernameOrEmail = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isValid = true;

        //Kiểm tra tài khoản
        if (TextUtils.isEmpty(usernameOrEmail)) {
            usernameTextInputLayout.setError("Tài khoản không được để trống.");
            isValid = false;
        } else if (isEmail(usernameOrEmail) && !Utils.isValidEmail(usernameOrEmail)) {
            usernameTextInputLayout.setError("Email không hợp lệ.");
            isValid = false;
        } else {
            usernameTextInputLayout.setError(null);
        }

        //Kiểm tra mật khẩu
        if (TextUtils.isEmpty(password)) {
            passwordTextInputLayout.setError("Mật khẩu không được để trống.");
            isValid = false;
        } else if (!Utils.isValidPassword(password)) {
            passwordTextInputLayout.setError("Mật khẩu phải gồm chữ hoa, chữ thường và số và có độ dài tối thiểu 8 ký tự!");
            isValid = false;
        } else {
            passwordTextInputLayout.setError(null);
        }


        if (!isValid) return;

        String hashedPassword = Utils.hashPassword(password);

        boolean isLoginValid;
        if (isEmail(usernameOrEmail)) {
            isLoginValid = databaseHelper.isEmailValid(usernameOrEmail, hashedPassword);
        } else {
            isLoginValid = databaseHelper.isUserValid(usernameOrEmail, hashedPassword);
        }

        if (isLoginValid) {
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Thông tin đăng nhập không đúng. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmail(String usernameOrEmail) {
        return usernameOrEmail.contains("@");
    }

    private void setupRegisterLink() {
        String text = registerLink.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, 19, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new android.text.style.ForegroundColorSpan(0xFF0000FF), 19, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerLink.setText(spannableString);
        registerLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}