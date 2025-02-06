package com.application.application.activity.statistic;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

        statsTextView = findViewById(R.id.statsTextView);
        bestSellerTextView = findViewById(R.id.bestSellerTextView);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        statisticsButton = findViewById(R.id.statisticsButton);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        //Ban đầu ẩn biểu đồ và CardView thống kê
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        findViewById(R.id.statsCardView).setVisibility(View.GONE);

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
        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();

        //Kiểm tra xem hai ô chọn ngày đã được điền chưa
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            statsTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            statsTextView.setText("Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
            bestSellerTextView.setVisibility(View.GONE);
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            return;
        }

        //Chuyển chuỗi sang đối tượng Date để so sánh
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = null, endDate = null;
        try {
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Kiểm tra nếu ngày bắt đầu lớn hơn ngày kết thúc
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            statsTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            statsTextView.setText("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            bestSellerTextView.setVisibility(View.GONE);
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            return;
        }

        //Nếu không có lỗi, đặt lại màu chữ mặc định và hiển thị tiêu đề thống kê
        statsTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        bestSellerTextView.setVisibility(View.VISIBLE);

        //Lấy danh sách đơn hàng bán chạy trong khoảng thời gian đã chọn
        List<OrderItem> bestSellingItems = databaseHelper.getBestSellingItems(startDateStr, endDateStr);

        if (bestSellingItems.isEmpty()) {
            statsTextView.setText("Không có dữ liệu trong khoảng thời gian này.");
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
        } else {
            //Hiển thị top 5 (theo thứ hạng)
            StringBuilder stats = new StringBuilder();
            int rank = 1;
            for (OrderItem item : bestSellingItems) {
                if (rank > 5) break;
                stats.append("Top ").append(rank).append(": ");
                stats.append("Món ăn: ").append(item.getFood_name());
                stats.append(" | Số lượng: ").append(item.getQuantity());
                stats.append(" | Tổng tiền: ").append(item.getTotalPrice());
                stats.append("\n");
                rank++;
            }
            statsTextView.setText(stats.toString());

            //BarChart hiển thị số lượng bán được
            setBarChartData(bestSellingItems);
            //PieChart hiển thị tổng tiền bán được cho mỗi món
            setPieChartData(bestSellingItems);

            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
        }
    }

    //BarChart: hiển thị số lượng bán được cho mỗi món
    private void setBarChartData(List<OrderItem> items) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> foodNames = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            entries.add(new BarEntry(i, item.getQuantity()));
            foodNames.add(item.getFood_name());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng bán được");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        barChart.setFitBars(true);
        Description description = new Description();
        description.setText("Biểu đồ cột (Số lượng)");
        description.setYOffset(-10f);
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

    //PieChart: hiển thị tổng tiền bán được cho mỗi món (doanh thu)
    private void setPieChartData(List<OrderItem> items) {
        List<PieEntry> entries = new ArrayList<>();

        for (OrderItem item : items) {
            entries.add(new PieEntry((float) item.getTotalPrice(), item.getFood_name()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Tổng tiền bán được");
        dataSet.setColors(new int[]{R.color.colorPrimary, R.color.colorAccent}, this);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        Description description = new Description();
        description.setText("Biểu đồ tròn (Doanh thu)");
        pieChart.setDescription(description);
        pieChart.invalidate();
    }
}
