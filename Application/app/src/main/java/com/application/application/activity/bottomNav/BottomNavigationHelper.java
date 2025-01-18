package com.application.application.activity.bottomNav;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {

    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, OnBottomNavItemSelectedListener listener) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            listener.onBottomNavItemSelected(item.getItemId());
            return true;
        });

    }
}