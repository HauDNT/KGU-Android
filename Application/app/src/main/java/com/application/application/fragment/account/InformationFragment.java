package com.application.application.fragment.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.application.MainActivity;
import com.application.application.R;
import com.application.application.activity.auth.LoginActivity;
import com.application.application.database.DatabaseHelper;
import com.application.application.fragment.order.OrderFragment;

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

        String fullname = databaseHelper.loadUserFullnameByUsername(currentUsername);
        if (fullname == null || fullname.trim().isEmpty()){
            fullname = currentUsername; // Hoặc "Chưa cập nhật thông tin"
        }
        userNameTextView.setText(fullname);

        setupCardListeners(rootView);

        return rootView;
    }

    private void setupCardListeners(View rootView) {
        View cardPersonalInfo = rootView.findViewById(R.id.cardPersonalInfo);
        View cardChangePassword = rootView.findViewById(R.id.cardChangePassword);
        View cardListCart = rootView.findViewById(R.id.cardListCart);
        View cardDelAcc = rootView.findViewById(R.id.cardDelAcc);
        View cardLogout = rootView.findViewById(R.id.cardLogout);

        // Chuyển đến fragment đổi thông tin cá nhân
        cardPersonalInfo.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.replaceFragment(new ChangePersonalInfoFragment());
            }
        });

        // Chuyển đến fragment đổi mật khẩu
        cardChangePassword.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.replaceFragment(new ChangePasswordFragment());
            }
        });

        // Chuyển đến fragment danh sách đơn hàng (OrderFragment)
        cardListCart.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.replaceFragment(new OrderFragment());
            }
        });

        // Xử lý xóa tài khoản với AlertDialog
        cardDelAcc.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn xóa tài khoản không?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        int result = databaseHelper.deleteUser(currentUsername);
                        if (result > 0) {
                            Toast.makeText(getActivity(), "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
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
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        // Xử lý đăng xuất
        cardLogout.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "Đăng xuất", Toast.LENGTH_SHORT).show();
            SharedPreferences prefs = getContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false); // Chỉ đánh dấu đã đăng xuất
            // Không xóa key "username" để lần sau dùng cho đăng nhập vân tay
            editor.apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }
}
