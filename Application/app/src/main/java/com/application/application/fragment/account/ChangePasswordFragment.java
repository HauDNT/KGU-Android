package com.application.application.fragment.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private TextInputLayout oldPasswordTextInputLayout, newPasswordTextInputLayout, confirmPasswordTextInputLayout;
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        currentUsername = getArguments() != null ? getArguments().getString("username") : null;

        if (currentUsername == null) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return view;
        }

        ImageView backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());

        oldPasswordEditText = view.findViewById(R.id.old_password);
        newPasswordEditText = view.findViewById(R.id.new_password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);

        oldPasswordTextInputLayout = view.findViewById(R.id.textInputLayoutOldPassword);
        newPasswordTextInputLayout = view.findViewById(R.id.textInputLayoutNewPassword);
        confirmPasswordTextInputLayout = view.findViewById(R.id.textInputLayoutConfirmPassword);

        view.findViewById(R.id.change_password_button).setOnClickListener(v -> changePassword());

        return view;
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
        if (!databaseHelper.isUserValid(currentUsername, hashedOldPassword)) {
            oldPasswordTextInputLayout.setError("Mật khẩu cũ không đúng.");
            return;
        }

        String hashedNewPassword = Utils.hashPassword(newPassword);
        int result = databaseHelper.updateUserPassword(currentUsername, hashedNewPassword);
        if (result > 0) {
            Toast.makeText(requireContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(requireContext(), "Đổi mật khẩu thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}
