package com.application.application.activity.statistic;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.application.R;
import com.application.application.database.DatabaseHelper;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView statsTextView;
    private TextInputEditText startDateEditText, endDateEditText;
    private Button statisticsButton;
    private BarChart barChart;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statsTextView = findViewById(R.id.statsTextView);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        statisticsButton = findViewById(R.id.statisticsButton);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        //Ban đầu ẩn biểu đồ
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);

        databaseHelper = new DatabaseHelper(this);

        //Tạo dữ liệu mẫu để kiểm thử
        databaseHelper.createTestData();

        //Thiết lập DatePicker cho trường ngày bắt đầu
        startDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày bắt đầu");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getSupportFragmentManager(), "START_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                String formattedDate = DateFormat.format("yyyy-MM-dd", selection).toString();
                startDateEditText.setText(formattedDate);
            });
        });

        //Thiết lập DatePicker cho trường ngày kết thúc
        endDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày kết thúc");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getSupportFragmentManager(), "END_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                String formattedDate = DateFormat.format("yyyy-MM-dd", selection).toString();
                endDateEditText.setText(formattedDate);
            });
        });

        statisticsButton.setOnClickListener(v -> displayStatistics());
    }

    private void displayStatistics() {
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        //Lấy danh sách đơn hàng bán chạy trong khoảng thời gian đã chọn
        List<OrderItem> bestSellingItems = databaseHelper.getBestSellingItems(startDate, endDate);

        if (bestSellingItems.isEmpty()) {
            //Không có dữ liệu => hiển thị thông báo và ẩn CardView kết quả thống kê cùng biểu đồ
            statsTextView.setText("Không có dữ liệu trong khoảng thời gian này.");
            findViewById(R.id.statsCardView).setVisibility(View.GONE);
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
        } else {
            //Có dữ liệu => cập nhật nội dung và hiển thị CardView kết quả thống kê cùng biểu đồ
            StringBuilder stats = new StringBuilder();
            for (OrderItem item : bestSellingItems) {
                stats.append("Món ăn: ").append(item.getFood_name())
                        .append(", Số lượng: ").append(item.getQuantity())
                        .append(", Tổng giá: ").append(item.getTotalPrice())
                        .append("\n");
            }
            statsTextView.setText(stats.toString());

            setBarChartData(bestSellingItems);
            setPieChartData(bestSellingItems);

            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
        }
    }


    private void setBarChartData(List<OrderItem> items) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> foodNames = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            entries.add(new BarEntry(i, item.getQuantity()));
            foodNames.add(item.getFood_name());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng bán được");
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        barChart.setFitBars(true);
        Description description = new Description();
        description.setText("Biểu đồ cột");
        barChart.setDescription(description);

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);
                if (index >= 0 && index < foodNames.size()) {
                    return foodNames.get(index);
                }
                return "";
            }
        });
        barChart.invalidate();
    }

    private void setPieChartData(List<OrderItem> items) {
        List<PieEntry> entries = new ArrayList<>();

        for (OrderItem item : items) {
            entries.add(new PieEntry(item.getQuantity(), item.getFood_name()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Số lượng bán được");
        dataSet.setColors(new int[]{R.color.colorPrimary, R.color.colorAccent}, this);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        Description description = new Description();
        description.setText("Biểu đồ tròn");
        pieChart.setDescription(description);
        pieChart.invalidate();
    }
}
