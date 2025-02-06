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

        //Tạo dữ liệu mẫu để kiểm thử (chỉ cần tạo 1 lần)
        databaseHelper.createTestData();

        //Thiết lập DatePicker cho trường ngày bắt đầu
        startDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày bắt đầu");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getSupportFragmentManager(), "START_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                //Định dạng lại timestamp sang chuỗi theo mẫu yyyy-MM-dd
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
            statsTextView.setText("Không có dữ liệu trong khoảng thời gian này.");
            //Ẩn biểu đồ nếu không có dữ liệu
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
        } else {
            StringBuilder stats = new StringBuilder("Đơn hàng bán chạy nhất:\n");
            for (OrderItem item : bestSellingItems) {
                stats.append("Món ăn ID: ").append(item.getFood_id())
                        .append(", Số lượng: ").append(item.getQuantity())
                        .append(", Tổng giá: ").append(item.getTotalPrice())
                        .append("\n");
            }
            statsTextView.setText(stats.toString());

            //Cập nhật dữ liệu cho biểu đồ
            setBarChartData(bestSellingItems);
            setPieChartData(bestSellingItems);

            //Hiển thị biểu đồ sau khi cập nhật dữ liệu
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
        }
    }

    private void setBarChartData(List<OrderItem> items) {
        List<BarEntry> entries = new ArrayList<>();

        //Sử dụng index của mảng làm x value
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            entries.add(new BarEntry(i, item.getQuantity()));
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
        barChart.invalidate(); //Cập nhật lại biểu đồ
    }

    private void setPieChartData(List<OrderItem> items) {
        List<PieEntry> entries = new ArrayList<>();

        //Tạo một entry cho mỗi món ăn theo food_id
        for (OrderItem item : items) {
            entries.add(new PieEntry(item.getQuantity(), "Food ID: " + item.getFood_id()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Số lượng bán được");
        //Sử dụng 2 màu mẫu, bạn có thể tùy chỉnh thêm
        dataSet.setColors(new int[]{R.color.colorPrimary, R.color.colorAccent}, this);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        Description description = new Description();
        description.setText("Biểu đồ tròn");
        pieChart.setDescription(description);
        pieChart.invalidate(); // Cập nhật lại biểu đồ
    }
}
