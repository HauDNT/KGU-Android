package com.application.application.activity.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.application.application.MainActivity;
import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private DatabaseHelper databaseHelper;
    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nếu đã đăng nhập rồi, chuyển luôn sang MainActivity
        if (isUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        databaseHelper = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);
        usernameTextInputLayout = findViewById(R.id.textInputLayoutUsername);
        passwordTextInputLayout = findViewById(R.id.textInputLayoutPassword);

        // Đăng nhập bằng tài khoản/mật khẩu
        loginButton.setOnClickListener(v -> loginUser());
        setupRegisterLink();

        // Đăng nhập bằng vân tay
        ImageView fingerprintImageView = findViewById(R.id.imageView2);
        fingerprintImageView.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(LoginActivity.this);
            if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
                Toast.makeText(LoginActivity.this, "Thiết bị không hỗ trợ xác thực sinh trắc học", Toast.LENGTH_SHORT).show();
                return;
            }

            Executor executor = ContextCompat.getMainExecutor(LoginActivity.this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(LoginActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(LoginActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    String username = prefs.getString("username", "");
                    if (username.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Chưa có thông tin tài khoản. Vui lòng đăng nhập bằng tài khoản.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Đánh dấu trạng thái đăng nhập mới
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // Chuyển sang MainActivity; InformationFragment sẽ load dữ liệu từ database dựa trên username đã lưu
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(LoginActivity.this, "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Đăng nhập bằng vân tay")
                    .setSubtitle("Xác thực bằng vân tay để đăng nhập")
                    .setNegativeButtonText("Hủy")
                    .build();

            biometricPrompt.authenticate(promptInfo);
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    private void loginUser() {
        String usernameOrEmail = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isValid = true;

        // Kiểm tra tài khoản
        if (TextUtils.isEmpty(usernameOrEmail)) {
            usernameTextInputLayout.setError("Tài khoản không được để trống.");
            isValid = false;
        } else if (isEmail(usernameOrEmail) && !Utils.isValidEmail(usernameOrEmail)) {
            usernameTextInputLayout.setError("Email không hợp lệ.");
            isValid = false;
        } else {
            usernameTextInputLayout.setError(null);
        }

        // Kiểm tra mật khẩu
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
            // Lưu trạng thái đăng nhập và thông tin tài khoản
            SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("username", usernameOrEmail);
            editor.apply();

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

        // Giả sử phần "Đăng ký" nằm ở vị trí 19 đến 26 (điều chỉnh nếu cần)
        spannableString.setSpan(clickableSpan, 19, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new android.text.style.ForegroundColorSpan(0xFF0000FF), 19, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerLink.setText(spannableString);
        registerLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
