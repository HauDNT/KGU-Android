package com.application.application.fragment.account;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.application.MainActivity;
import com.application.application.R;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePersonalInfoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private String currentUsername;

    private TextInputEditText etFullname, etEmail, etPhone, etAddress;
    private MaterialButton btnUpdate;
    private ImageView ivBackArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_personal_info, container, false);

        etFullname = view.findViewById(R.id.fullname);
        etEmail = view.findViewById(R.id.email);
        etPhone = view.findViewById(R.id.phone_number);
        etAddress = view.findViewById(R.id.address);
        btnUpdate = view.findViewById(R.id.update_button);
        ivBackArrow = view.findViewById(R.id.back_arrow);

        databaseHelper = new DatabaseHelper(getContext());

        SharedPreferences prefs = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        currentUsername = prefs.getString("username", null);
        if (currentUsername == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            loadUserData();
        }

        ivBackArrow.setOnClickListener(v -> {
            if(getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new InformationFragment());
            }
        });

        btnUpdate.setOnClickListener(v -> {
            String fullname = etFullname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Cập nhật dữ liệu vào database
            boolean isUpdated = databaseHelper.updateUser(currentUsername, fullname, email, phone, address);
            if(isUpdated) {
                Toast.makeText(getContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                if(getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(new InformationFragment());
                }
            } else {
                Toast.makeText(getContext(), "Cập nhật thông tin thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @SuppressLint("Range")
    private void loadUserData() {
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT fullname, email, phone_number, address FROM users WHERE username = ?",
                new String[]{currentUsername});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                String phone = cursor.getString(cursor.getColumnIndex("phone_number"));
                String address = cursor.getString(cursor.getColumnIndex("address"));

                etFullname.setText(fullname);
                etEmail.setText(email);
                etPhone.setText(phone);
                etAddress.setText(address);
            }
            cursor.close();
        }
    }
}
