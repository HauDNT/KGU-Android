package com.application.application;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.application.databinding.ActivityMainBinding;
import com.application.application.fragment.account.InformationFragment;
import com.application.application.fragment.category.CategoryFragment;
import com.application.application.fragment.dashboard.DashboardFragment;
import com.application.application.fragment.food.FoodFragment;
import com.application.application.fragment.order.OrderFragment;
import com.application.application.fragment.order.detail_order.DetailOrderDialogFragment;
import com.application.application.fragment.statistic.StatisticFragment;

public class MainActivity extends AppCompatActivity implements DetailOrderDialogFragment.OnOrderUpdatedListener {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        replaceFragment(new DashboardFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new DashboardFragment());
                    break;
                case R.id.food:
                    replaceFragment(new FoodFragment());
                    break;
                case R.id.category:
                    replaceFragment(new CategoryFragment());
                    break;
                case R.id.statistic:
                    replaceFragment(new StatisticFragment());
                    break;
                case R.id.account:
                    replaceFragment(new InformationFragment());
                    break;
            }

            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onOrderUpdated() {
        /*  Thay thế Fragment Order chứa dữ liệu cũ thành Fragment
            Order mới để load lại dữ liệu mới.
         */

        replaceFragment(new OrderFragment());
    }
}