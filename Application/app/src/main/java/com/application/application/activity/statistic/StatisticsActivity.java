package com.application.application.activity.statistic;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.application.application.R;
import com.application.application.database.DatabaseHelper;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.OrderItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView statsTextView, bestSellerTextView;
    private TextInputEditText startDateEditText, endDateEditText;
    private Button statisticsButton;
    private BarChart barChart;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Ánh xạ view từ layout
        statsTextView = findViewById(R.id.statsTextView);
        bestSellerTextView = findViewById(R.id.bestSellerTextView);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        statisticsButton = findViewById(R.id.statisticsButton);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        // Ban đầu ẩn biểu đồ và CardView thống kê
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        findViewById(R.id.statsCardView).setVisibility(View.GONE);

        databaseHelper = new DatabaseHelper(this);

        // Thiết lập DatePicker cho trường ngày bắt đầu (định dạng dd/MM/yyyy)
        startDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày bắt đầu");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getSupportFragmentManager(), "START_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Định dạng ngày theo dd/MM/yyyy
                String formattedDate = DateFormat.format("dd/MM/yyyy", selection).toString();
                startDateEditText.setText(formattedDate);
            });
        });

        // Thiết lập DatePicker cho trường ngày kết thúc (định dạng dd/MM/yyyy)
        endDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày kết thúc");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getSupportFragmentManager(), "END_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                String formattedDate = DateFormat.format("dd/MM/yyyy", selection).toString();
                endDateEditText.setText(formattedDate);
            });
        });

        // Sự kiện khi nhấn nút "Thống kê"
        statisticsButton.setOnClickListener(v -> displayStatistics());
    }

    private void displayStatistics() {
        // Reset nội dung thông báo và ẩn các view dữ liệu cũ
        statsTextView.setText("");
        bestSellerTextView.setText("");
        bestSellerTextView.setVisibility(View.GONE);

        // Xóa dữ liệu cũ trong bảng thống kê
        TableLayout tableLayout = findViewById(R.id.statsTableLayout);
        tableLayout.removeAllViews();

        // Ẩn biểu đồ
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);

        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();

        // Kiểm tra xem người dùng đã chọn đầy đủ ngày chưa
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            statsTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            statsTextView.setText("Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
            // Hiển thị CardView để thông báo lỗi được thấy
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            return;
        }

        // Chuyển chuỗi ngày sang đối tượng Date để so sánh
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // Kiểm tra: ngày bắt đầu phải trước ngày kết thúc
            if (startDate.after(endDate)) {
                statsTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                statsTextView.setText("Ngày bắt đầu phải sớm hơn ngày kết thúc.");
                // Hiển thị CardView để thông báo lỗi được thấy
                findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            statsTextView.setText("Định dạng ngày không hợp lệ.");
            // Hiển thị CardView để thông báo lỗi được thấy
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            return;
        }

        // Lấy dữ liệu thống kê
        List<OrderItem> bestSellingItems = databaseHelper.getBestSellingItems(startDateStr, endDateStr);

        if (bestSellingItems == null || bestSellingItems.isEmpty()) {
            statsTextView.setText("Không có dữ liệu doanh thu trong khoảng thời gian này.");
            bestSellerTextView.setVisibility(View.GONE);
            // Hiển thị CardView để thông báo lỗi được thấy
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
        } else {
            // Có dữ liệu: hiển thị tiêu đề và bảng thống kê
            bestSellerTextView.setVisibility(View.VISIBLE);
            bestSellerTextView.setText("Sản phẩm bán chạy nhất:");
            displayTable(bestSellingItems);

            // Hiển thị CardView và các biểu đồ thống kê
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            setBarChartData(bestSellingItems);
            setPieChartData(bestSellingItems);
        }
    }

    private void displayTable(List<OrderItem> items) {
        // Lấy TableLayout từ layout
        TableLayout tableLayout = findViewById(R.id.statsTableLayout);
        // Xóa toàn bộ các hàng cũ (nếu có)
        tableLayout.removeAllViews();

        // Tạo header cho bảng
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        headerRow.setPadding(8, 8, 8, 8);

        // Danh sách tiêu đề cột
        String[] headers = {"Top", "Sản phẩm", "SL", "Tổng tiền"};
        for (String header : headers) {
            TextView tv = new TextView(this);
            tv.setText(header);
            tv.setTextColor(ContextCompat.getColor(this, R.color.white));
            tv.setPadding(16, 16, 16, 16);
            tv.setTextSize(16);
            tv.setTypeface(null, Typeface.BOLD);
            headerRow.addView(tv);
        }
        tableLayout.addView(headerRow);

        // Hiển thị top 5 món bán chạy nhất (hoặc toàn bộ nếu số lượng ít hơn 5)
        int limit = Math.min(items.size(), 5);
        for (int i = 0; i < limit; i++) {
            OrderItem item = items.get(i);
            TableRow row = new TableRow(this);
            row.setPadding(8, 8, 8, 8);

            // Cột Top (Rank)
            TextView tvRank = new TextView(this);
            tvRank.setText(String.valueOf(i + 1));
            tvRank.setPadding(16, 16, 16, 16);
            tvRank.setTextSize(16);

            // Cột tên sản phẩm
            TextView tvFoodName = new TextView(this);
            tvFoodName.setText(item.getFood_name());
            tvFoodName.setPadding(16, 16, 16, 16);
            tvFoodName.setTextSize(16);

            // Cột số lượng bán được
            TextView tvQuantity = new TextView(this);
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            tvQuantity.setPadding(16, 16, 16, 16);
            tvQuantity.setTextSize(16);

            // Cột tổng tiền bán được
            TextView tvTotalPrice = new TextView(this);
            tvTotalPrice.setText(String.valueOf(item.getTotalPrice()) + " VND");
            tvTotalPrice.setPadding(16, 16, 16, 16);
            tvTotalPrice.setTextSize(16);

            // Thêm các TextView vào hàng
            row.addView(tvRank);
            row.addView(tvFoodName);
            row.addView(tvQuantity);
            row.addView(tvTotalPrice);

            // (Tùy chọn) Thêm viền cho hàng bằng drawable
            row.setBackgroundResource(R.drawable.table_row_border);

            // Thêm hàng vào TableLayout
            tableLayout.addView(row);
        }
    }

    private void setBarChartData(List<OrderItem> items) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> foodNames = new ArrayList<>();

        int limit = Math.min(items.size(), 5);
        for (int i = 0; i < limit; i++) {
            OrderItem item = items.get(i);
            entries.add(new BarEntry(i, item.getQuantity()));
            foodNames.add(item.getFood_name());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng bán được");
        int[] barColors = new int[] {
                ContextCompat.getColor(this, R.color.barColor1),
                ContextCompat.getColor(this, R.color.barColor2),
                ContextCompat.getColor(this, R.color.barColor3),
                ContextCompat.getColor(this, R.color.barColor4),
                ContextCompat.getColor(this, R.color.barColor5)
        };
        dataSet.setColors(barColors);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        barChart.setFitBars(true);
        Description description = new Description();
        description.setText("Biểu đồ cột (Số lượng bán được)");
        description.setYOffset(-10f);
        barChart.setDescription(description);

        // Đặt granularity để trục X hiển thị giá trị nguyên
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        // Sử dụng IndexAxisValueFormatter để gán tên món ăn cho trục X mà không bị lặp
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(foodNames));
        barChart.invalidate();
    }

    private void setPieChartData(List<OrderItem> items) {
        List<PieEntry> entries = new ArrayList<>();
        int limit = Math.min(items.size(), 5);
        for (int i = 0; i < limit; i++) {
            OrderItem item = items.get(i);
            entries.add(new PieEntry((float) item.getTotalPrice(), item.getFood_name()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Tổng tiền bán được");
        int[] pieColors = new int[] {
                ContextCompat.getColor(this, R.color.pieColor1),
                ContextCompat.getColor(this, R.color.pieColor2),
                ContextCompat.getColor(this, R.color.pieColor3),
                ContextCompat.getColor(this, R.color.pieColor4),
                ContextCompat.getColor(this, R.color.pieColor5)
        };
        dataSet.setColors(pieColors);
        // Đặt màu cho giá trị (tổng tiền) thành trắng
        dataSet.setValueTextColor(ContextCompat.getColor(this, android.R.color.white));
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        Description description = new Description();
        description.setText("Biểu đồ tròn (Tổng tiền bán được)");
        pieChart.setDescription(description);
        // Đặt màu cho nhãn (tên món ăn) trên PieChart thành đen
        pieChart.setEntryLabelColor(ContextCompat.getColor(this, android.R.color.black));
        pieChart.invalidate();
    }
}
