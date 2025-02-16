package com.application.application.fragment.statistic;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.application.application.R;
import com.application.application.activity.account.InformationActivity;
import com.application.application.activity.dashboard.DashboardActivity;
import com.application.application.activity.food.FoodActivity;
import com.application.application.activity.order.activity.OrderActivity;
import com.application.application.database.DatabaseHelper;
import com.application.application.model.OrderItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private TextView statsTextView, bestSellerTextView;
    private TextInputEditText startDateEditText, endDateEditText, searchFoodEditText;
    private Button statisticsButton;

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statsTextView = view.findViewById(R.id.statsTextView);
        bestSellerTextView = view.findViewById(R.id.bestSellerTextView);
        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        searchFoodEditText = view.findViewById(R.id.searchFoodEditText);
        statisticsButton = view.findViewById(R.id.statisticsButton);

        // Ẩn CardView thống kê ban đầu
        view.findViewById(R.id.statsCardView).setVisibility(View.GONE);

        databaseHelper = new DatabaseHelper(getContext());

        // Thiết lập DatePicker cho ngày bắt đầu
        startDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày bắt đầu");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getChildFragmentManager(), "START_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                String formattedDate = DateFormat.format("dd/MM/yyyy", selection).toString();
                startDateEditText.setText(formattedDate);
            });
        });

        // Thiết lập DatePicker cho ngày kết thúc
        endDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày kết thúc");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getChildFragmentManager(), "END_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                String formattedDate = DateFormat.format("dd/MM/yyyy", selection).toString();
                endDateEditText.setText(formattedDate);
            });
        });

        // Sự kiện khi nhấn nút "Thống kê"
        statisticsButton.setOnClickListener(v -> displayStatistics(view));

    }

    private void displayStatistics(View view) {
        // Reset nội dung thông báo và ẩn dữ liệu cũ
        statsTextView.setText("");
        bestSellerTextView.setText("");
        bestSellerTextView.setVisibility(View.GONE);

        // Xóa dữ liệu cũ trong bảng thống kê
        TableLayout tableLayout = view.findViewById(R.id.statsTableLayout);
        tableLayout.removeAllViews();

        // Lấy dữ liệu từ các trường nhập
        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();
        String searchQuery = searchFoodEditText.getText().toString().trim();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            statsTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
            statsTextView.setText("Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
            view.findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // Ngày bắt đầu phải trước ngày kết thúc
            if (startDate.after(endDate)) {
                statsTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
                statsTextView.setText("Ngày bắt đầu phải sớm hơn ngày kết thúc.");
                view.findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            statsTextView.setText("Định dạng ngày không hợp lệ.");
            view.findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            return;
        }

        // Lấy dữ liệu thống kê từ database dựa theo ngày và từ khóa tìm kiếm (nếu có)
        List<OrderItem> bestSellingItems = databaseHelper.getBestSellingItems(startDateStr, endDateStr, searchQuery);

        if (bestSellingItems == null || bestSellingItems.isEmpty()) {
            statsTextView.setText("Không có dữ liệu doanh thu trong khoảng thời gian này.");
            bestSellerTextView.setVisibility(View.GONE);
            view.findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
        } else {
            bestSellerTextView.setVisibility(View.VISIBLE);
            bestSellerTextView.setText("Sản phẩm bán chạy nhất:");
            displayTable(view, bestSellingItems);
            view.findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            // Lưu ý: 2 biểu đồ đã bị loại bỏ.
        }
    }

    private void displayTable(View view, List<OrderItem> items) {
        TableLayout tableLayout = view.findViewById(R.id.statsTableLayout);
        tableLayout.removeAllViews();

        // Tạo header cho bảng thống kê
        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        headerRow.setPadding(8, 8, 8, 8);

        String[] headers = {"Top", "Sản phẩm", "SL", "Tổng tiền"};
        for (String header : headers) {
            TextView tv = new TextView(getContext());
            tv.setText(header);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tv.setPadding(16, 16, 16, 16);
            tv.setTextSize(16);
            tv.setTypeface(null, Typeface.BOLD);
            headerRow.addView(tv);
        }
        tableLayout.addView(headerRow);

        // Hiển thị từng dòng dữ liệu thống kê
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            TableRow row = new TableRow(getContext());
            row.setPadding(8, 8, 8, 8);

            TextView tvRank = new TextView(getContext());
            tvRank.setText(String.valueOf(i + 1));
            tvRank.setPadding(16, 16, 16, 16);
            tvRank.setTextSize(16);

            TextView tvFoodName = new TextView(getContext());
            tvFoodName.setText(item.getFood_name());
            tvFoodName.setPadding(16, 16, 16, 16);
            tvFoodName.setTextSize(16);

            TextView tvQuantity = new TextView(getContext());
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            tvQuantity.setPadding(16, 16, 16, 16);
            tvQuantity.setTextSize(16);

            TextView tvTotalPrice = new TextView(getContext());
            tvTotalPrice.setText(String.valueOf(item.getTotalPrice()) + " VND");
            tvTotalPrice.setPadding(16, 16, 16, 16);
            tvTotalPrice.setTextSize(16);

            row.addView(tvRank);
            row.addView(tvFoodName);
            row.addView(tvQuantity);
            row.addView(tvTotalPrice);

            row.setBackgroundResource(R.drawable.table_row_border);
            tableLayout.addView(row);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
