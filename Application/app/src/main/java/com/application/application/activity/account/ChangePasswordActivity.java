package com.application.application.activity.account;

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

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private TextInputLayout oldPasswordTextInputLayout, newPasswordTextInputLayout, confirmPasswordTextInputLayout;
    private DatabaseHelper databaseHelper;
    private String currentUsername; // Lấy từ Intent hoặc SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        databaseHelper = new DatabaseHelper(this);
        // Giả sử username được truyền qua Intent
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gán xử lý cho nút quay về
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ChangePasswordActivity.this, InformationActivity.class);
            startActivity(intent);
            finish();
        });

        oldPasswordEditText = findViewById(R.id.old_password);
        newPasswordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);

        oldPasswordTextInputLayout = findViewById(R.id.textInputLayoutOldPassword);
        newPasswordTextInputLayout = findViewById(R.id.textInputLayoutNewPassword);
        confirmPasswordTextInputLayout = findViewById(R.id.textInputLayoutConfirmPassword);

        findViewById(R.id.change_password_button).setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(oldPassword)) {
            oldPasswordTextInputLayout.setError("Mật khẩu cũ không được để trống.");
            isValid = false;
        } else {
            oldPasswordTextInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordTextInputLayout.setError("Mật khẩu mới không được để trống.");
            isValid = false;
        } else if (!Utils.isValidPassword(newPassword)) {
            newPasswordTextInputLayout.setError("Mật khẩu phải gồm chữ hoa, chữ thường, số và tối thiểu 8 ký tự!");
            isValid = false;
        } else {
            newPasswordTextInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordTextInputLayout.setError("Xác nhận mật khẩu không được để trống.");
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            confirmPasswordTextInputLayout.setError("Xác nhận mật khẩu không khớp!");
            isValid = false;
        } else {
            confirmPasswordTextInputLayout.setError(null);
        }

        if (!isValid) return;

        String hashedOldPassword = Utils.hashPassword(oldPassword);
        // Kiểm tra mật khẩu cũ có đúng không
        if (!databaseHelper.isUserValid(currentUsername, hashedOldPassword)) {
            oldPasswordTextInputLayout.setError("Mật khẩu cũ không đúng.");
            return;
        }

        String hashedNewPassword = Utils.hashPassword(newPassword);
        int result = databaseHelper.updateUserPassword(currentUsername, hashedNewPassword);
        if (result > 0) {
            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Đổi mật khẩu thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}
