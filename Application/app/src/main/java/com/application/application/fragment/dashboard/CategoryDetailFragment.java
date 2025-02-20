package com.application.application.fragment.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;
import com.application.application.fragment.food.FoodFragmentAdapter;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.Food;

import java.util.List;

public class CategoryDetailFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodFragmentAdapter foodAdapter;
    private DatabaseHelper databaseHelper;
    private int categoryId;

    public CategoryDetailFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Sử dụng layout cho Fragment (ví dụ: fragment_category_detail.xml)
        return inflater.inflate(R.layout.fragment_category_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy categoryId từ Bundle arguments
        if (getArguments() != null) {
            categoryId = getArguments().getInt("categoryId", -1);
        } else {
            categoryId = -1;
        }

        databaseHelper = new DatabaseHelper(requireContext());

        recyclerView = view.findViewById(R.id.recyclerViewFoods);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadFoodsByCategory();
    }

    private void loadFoodsByCategory() {
        if (categoryId == -1) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Food> foodList = databaseHelper.getFoodsByCategory(categoryId);
        foodAdapter = new FoodFragmentAdapter(requireContext(), foodList, databaseHelper, null);
        recyclerView.setAdapter(foodAdapter);
    }
}
