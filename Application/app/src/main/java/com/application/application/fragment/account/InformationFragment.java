package com.application.application.fragment.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.application.application.MainActivity;
import com.application.application.R;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.database.DatabaseHelper;
import com.application.application.fragment.order.OrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InformationFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private TextView userNameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        userNameTextView = rootView.findViewById(R.id.userNameTextView);

        SharedPreferences prefs = getContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        currentUsername = prefs.getString("username", null);
        if (currentUsername == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return rootView;
        }

        loadUserInfo(rootView);

        // Set up click listeners for the buttons/cards
        setupCardListeners(rootView);

        return rootView;
    }

    private void loadUserInfo(View rootView) {
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT fullname FROM users WHERE username = ?", new String[]{currentUsername});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
                if (!fullname.isEmpty()) {
                    userNameTextView.setText(fullname);
                } else {
                    userNameTextView.setText(currentUsername);
                }
            }
            cursor.close();
        }
    }

    private void setupCardListeners(View rootView) {
        View cardPersonalInfo = rootView.findViewById(R.id.cardPersonalInfo);
        View cardChangePassword = rootView.findViewById(R.id.cardChangePassword);
        View cardListCart = rootView.findViewById(R.id.cardListCart);
        View cardDelAcc = rootView.findViewById(R.id.cardDelAcc);
        View cardLogout = rootView.findViewById(R.id.cardLogout);

        cardPersonalInfo.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                // Truyền args vào đây (Tương tự như dòng 138 - DashboardFragment) rồi mới
                // chuyển Fragment






                mainActivity.replaceFragment(new ChangePersonalInfoFragment());
            }
        });

        cardChangePassword.setOnClickListener(view -> {
            // Truyền args vào đây (Tương tự như dòng 138 - DashboardFragment) rồi mới
            // chuyển Fragment




            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.replaceFragment(new ChangePasswordFragment());
            }
        });

        cardListCart.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.replaceFragment(new OrderFragment());
            }
        });

        cardDelAcc.setOnClickListener(view -> {
            // Thêm thông báo YES / NO đi nghe











            // Xóa tài khoản khỏi database
            int result = databaseHelper.deleteUser(currentUsername);
            if (result > 0) {
                Toast.makeText(getActivity(), "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
                // Xóa SharedPreferences (trạng thái đăng nhập và username)
                SharedPreferences.Editor editor = getContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Xóa tài khoản thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });

        cardLogout.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "Đăng xuất", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
