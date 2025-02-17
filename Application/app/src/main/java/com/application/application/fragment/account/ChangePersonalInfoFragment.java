package com.application.application.fragment.account;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.application.application.R;
import com.application.application.Utils;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePersonalInfoFragment extends Fragment {

    private TextInputEditText fullnameEditText, emailEditText, phoneNumberEditText, addressEditText;
    private TextInputLayout fullnameTextInputLayout, emailTextInputLayout, phoneNumberTextInputLayout, addressTextInputLayout;
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy thông tin username từ arguments hoặc SharedPreferences
        if (getArguments() != null) {
            currentUsername = getArguments().getString("username");
        }

        if (currentUsername == null) {
            Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_change_personal_info, container, false);

        databaseHelper = new DatabaseHelper(getActivity());

        fullnameEditText = view.findViewById(R.id.fullname);
        emailEditText = view.findViewById(R.id.email);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        addressEditText = view.findViewById(R.id.address);

        fullnameTextInputLayout = view.findViewById(R.id.textInputLayoutFullname);
        emailTextInputLayout = view.findViewById(R.id.textInputLayoutEmail);
        phoneNumberTextInputLayout = view.findViewById(R.id.textInputLayoutPhoneNumber);
        addressTextInputLayout = view.findViewById(R.id.textInputLayoutAddress);

        // Gán xử lý cho nút quay về
        ImageView backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> getActivity().onBackPressed());

        view.findViewById(R.id.update_button).setOnClickListener(v -> updatePersonalInfo());

        return view;
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
            Toast.makeText(getActivity(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Cập nhật thông tin thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}
