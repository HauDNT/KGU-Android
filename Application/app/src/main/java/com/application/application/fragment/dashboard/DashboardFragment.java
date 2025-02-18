package com.application.application.fragment.dashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.application.application.MainActivity;
import com.application.application.R;
import com.application.application.common.PosterAdapter;
import com.application.application.database.DatabaseHelper;
import com.application.application.fragment.category.CategoryFragment;
import com.application.application.model.Category;
import com.application.application.model.OrderItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class DashboardFragment extends Fragment {
    private RecyclerView recyclerViewPopular;
    private DashboardCategoryAdapter dashboardCategoryAdapter;
    private List<Category> categoryList;
    private ViewPager2 viewPager;
    private Handler handler = new Handler();
    private int currentPage = 0;
    private DatabaseHelper databaseHelper;

    // Các biến cho phần hiển thị món ăn phổ biến
    private RecyclerView recyclerViewPopularFood;
    private PopularFoodAdapter popularFoodAdapter;
    private List<OrderItem> popularItems;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        databaseHelper = new DatabaseHelper(requireContext());

        // Khởi tạo RecyclerView
        setupRecyclerView(view);

        // Khởi tạo ViewPager2 cho banner quảng cáo
        setupViewPager(view);

        // Thêm phần hiển thị món ăn phổ biến
        setupPopularFoodRecyclerView(view);

        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerViewPopular = view.findViewById(R.id.recyclerViewPopular);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        categoryList = databaseHelper.getAllCategories(); // Lấy danh mục từ DB
        dashboardCategoryAdapter = new DashboardCategoryAdapter(requireContext(), categoryList, this::openCategoryDetailFragment);
        recyclerViewPopular.setAdapter(dashboardCategoryAdapter);
    }

    // Phương thức mới: thiết lập RecyclerView hiển thị món ăn phổ biến
    private void setupPopularFoodRecyclerView(View view) {
        recyclerViewPopularFood = view.findViewById(R.id.recyclerViewPopularFood);
        // Sử dụng hướng dọc (VERTICAL)
        recyclerViewPopularFood.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        // Lấy danh sách món ăn phổ biến (6 món bán chạy nhất)
        popularItems = databaseHelper.getPopularItems("");
        popularFoodAdapter = new PopularFoodAdapter(requireContext(), popularItems);
        recyclerViewPopularFood.setAdapter(popularFoodAdapter);
    }


    private void initializeCategoryList() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        categoryList = dbHelper.getAllCategories(); // Lấy danh sách danh mục từ DB
    }

    public void onResume() {
        super.onResume();
        initializeCategoryList();
        if (dashboardCategoryAdapter != null) {
            dashboardCategoryAdapter.updateList(categoryList);
        }
        // Cập nhật lại danh sách món ăn phổ biến
        popularItems = databaseHelper.getPopularItems("");
        if (popularFoodAdapter != null) {
            popularFoodAdapter.updateList(popularItems);
        }
    }

    private void setupViewPager(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        int[] posterImages = {R.drawable.poster1, R.drawable.poster2, R.drawable.poster3};

        PosterAdapter adapterViewPager = new PosterAdapter(requireContext(), posterImages);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(2);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPage == posterImages.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void openCategoryDetailFragment(Category category) {
        // Ta sử dụng FragmentTransaction để truyền tải dữ liệu
        CategoryDetailFragment categoryDetailFragment = new CategoryDetailFragment();
        Bundle args = new Bundle();
        args.putInt("categoryId", category.getId());
        args.putString("categoryName", category.getName());
        categoryDetailFragment.setArguments(args);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrameLayout, categoryDetailFragment);
        transaction.addToBackStack(null); // Cho phép quay lại Fragment trước đó
        transaction.commit();
    }
}