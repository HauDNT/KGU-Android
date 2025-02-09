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

        // Ban đầu ẩn biểu đồ và CardView thống kê (nếu có)
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        findViewById(R.id.statsCardView).setVisibility(View.GONE);

        databaseHelper = new DatabaseHelper(this);

        // Thiết lập DatePicker cho trường ngày bắt đầu với định dạng dd/MM/yyyy
        startDateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày bắt đầu");
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.show(getSupportFragmentManager(), "START_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Định dạng thành dd/MM/yyyy
                String formattedDate = DateFormat.format("dd/MM/yyyy", selection).toString();
                startDateEditText.setText(formattedDate);
            });
        });

        // Thiết lập DatePicker cho trường ngày kết thúc với định dạng dd/MM/yyyy
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

        statisticsButton.setOnClickListener(v -> displayStatistics());
    }

    private void displayStatistics() {
        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            statsTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            statsTextView.setText("Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            return;
        }

        // Vì DatePicker hiển thị theo dd/MM/yyyy, ta truyền trực tiếp các chuỗi này cho truy vấn
        // (trong truy vấn SQL, ta sử dụng biểu thức CASE để tách phần ngày từ created_at)
        List<OrderItem> bestSellingItems = databaseHelper.getBestSellingItems(startDateStr, endDateStr);

        if (bestSellingItems == null || bestSellingItems.isEmpty()) {
            statsTextView.setText("Không có dữ liệu doanh thu trong khoảng thời gian này.");
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
        } else {
            // Hiển thị top 5 món bán chạy nhất
            StringBuilder stats = new StringBuilder();
            int rank = 1;
            for (OrderItem item : bestSellingItems) {
                if (rank > 5) break;
                stats.append("Top ").append(rank).append(": ").append(item.getFood_name());
                stats.append(" | SL: ").append(item.getQuantity());
                stats.append(" | Tổng tiền: ").append(item.getTotalPrice()).append(" VND\n");
                rank++;
            }
            statsTextView.setText(stats.toString());

            // Nếu muốn hiển thị biểu đồ, gọi hàm setBarChartData và setPieChartData
            findViewById(R.id.statsCardView).setVisibility(View.VISIBLE);
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            setBarChartData(bestSellingItems);
            setPieChartData(bestSellingItems);
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
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        Description description = new Description();
        description.setText("Biểu đồ tròn (Tổng tiền bán được)");
        pieChart.setDescription(description);
        pieChart.invalidate();
    }
}
