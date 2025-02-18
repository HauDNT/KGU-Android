package com.application.application.fragment.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.application.application.MainActivity;
import com.application.application.R;
import com.application.application.Utils;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword;
    private ImageView ivBackArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        ivBackArrow = view.findViewById(R.id.back_arrow);
        etOldPassword = view.findViewById(R.id.old_password);
        etNewPassword = view.findViewById(R.id.new_password);
        etConfirmPassword = view.findViewById(R.id.confirm_password);
        btnChangePassword = view.findViewById(R.id.change_password_button);

        databaseHelper = new DatabaseHelper(getContext());

        // Lấy username từ SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        currentUsername = prefs.getString("username", null);
        if (currentUsername == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        // Xử lý nút quay lại
        ivBackArrow.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new InformationFragment());
            }
        });

        // Xử lý nút "Đổi mật khẩu"
        btnChangePassword.setOnClickListener(v -> {
            String oldPass = etOldPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            // Kiểm tra các trường không được rỗng
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu mới và xác nhận có khớp nhau không
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mã hóa mật khẩu cũ do người dùng nhập
            String hashedOldInput = Utils.hashPassword(oldPass);

            // Truy vấn mật khẩu đã được lưu (đã mã hóa) trong database
            Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                    "SELECT password FROM users WHERE username = ?", new String[]{currentUsername});
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("password");
                    if (columnIndex == -1) {
                        Toast.makeText(getContext(), "Lỗi: Không tìm thấy cột password trong database", Toast.LENGTH_SHORT).show();
                        cursor.close();
                        return;
                    }
                    String savedHashedPassword = cursor.getString(columnIndex);
                    // So sánh mật khẩu cũ đã mã hóa
                    if (!savedHashedPassword.equals(hashedOldInput)) {
                        Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                        cursor.close();
                        return;
                    }
                }
                cursor.close();
            }

            // Mã hóa mật khẩu mới trước khi lưu vào database
            String hashedNewPassword = Utils.hashPassword(newPass);
            boolean isUpdated = databaseHelper.updatePassword(currentUsername, hashedNewPassword);
            if (isUpdated) {
                Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(new InformationFragment());
                }
            } else {
                Toast.makeText(getContext(), "Đổi mật khẩu thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
