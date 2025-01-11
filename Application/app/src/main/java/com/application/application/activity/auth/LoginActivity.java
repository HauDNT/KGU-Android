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

import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.R;
import com.application.application.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);

        //Xử lý khi nhấn nút "Đăng nhập"
        loginButton.setOnClickListener(v -> loginUser());

        redirectToRegisterScreen();
    }

    private void loginUser() {
        String usernameOrEmail = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        //Kiểm tra nếu để trống 1 trong các trường
        if (TextUtils.isEmpty(usernameOrEmail) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Mã hóa mật khẩu nhập vào bằng hàm từ Utils
        String hashedPassword = Utils.hashPassword(password);

        //Kiểm tra xem nhập liệu có phải là email hay username
        boolean isLoginValid;

        if (isEmail(usernameOrEmail)) {
            //Kiểm tra Email có trong db không
            isLoginValid = databaseHelper.isEmailValid(usernameOrEmail, hashedPassword);
        } else {
            //Kiểm tra username có trong db không
            isLoginValid = databaseHelper.isUserValid(usernameOrEmail, hashedPassword);
        }

        if (isLoginValid) {
            //Thông tin đăng nhập đúng, chuyển sang màn hình Dashboard
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            //Thông tin đăng nhập sai
            Toast.makeText(this, "Thông tin đăng nhập không đúng. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
        }
    }

    //Kiểm tra xem đầu vào có phải là email không
    private boolean isEmail(String usernameOrEmail) {
        return usernameOrEmail.contains("@");
    }

    private void redirectToRegisterScreen() {
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