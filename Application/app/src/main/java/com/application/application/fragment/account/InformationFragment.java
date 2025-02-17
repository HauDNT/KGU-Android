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

import com.application.application.R;
import com.application.application.activity.account.ChangePersonalInfoFragment;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.food.FoodActivity;
import com.application.application.activity.order.activity.OrderActivity;
import com.application.application.activity.statistic.StatisticsActivity;
import com.application.application.database.DatabaseHelper;
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

        setupBottomNavigation(rootView);

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
            Intent intent = new Intent(getActivity(), ChangePersonalInfoFragment.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });

        cardChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ChangePasswordFragment.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });

        cardListCart.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), OrderActivity.class);
            startActivity(intent);
        });

        cardDelAcc.setOnClickListener(view -> {
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

    private void setupBottomNavigation(View rootView) {
        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(getActivity(), DashboardActivity.class));
                getActivity().finish();
                return true;
            } else if (id == R.id.food) {
                startActivity(new Intent(getActivity(), FoodActivity.class));
                getActivity().finish();
                return true;
            } else if (id == R.id.order) {
                startActivity(new Intent(getActivity(), OrderActivity.class));
                getActivity().finish();
                return true;
            } else if (id == R.id.statistic) {
                startActivity(new Intent(getActivity(), StatisticsActivity.class));
                getActivity().finish();
                return true;
            } else if (id == R.id.account) {
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);
    }
}
