package com.example.motoshop.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.motoshop.R;
import com.example.motoshop.utils.CurrencyFormatter;
import com.example.motoshop.utils.DateUtils;
import com.example.motoshop.viewmodel.SalesViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

// Fragment hiển thị một phần giao diện và xử lý dữ liệu cho màn hình này.
public class RevenueChartFragment extends Fragment {

    private SalesViewModel salesViewModel;
    private BarChart barChart;
    private TabLayout tabLayout;
    private Spinner spinnerYear;
    private TextView tvMax, tvMin, tvAvg;

    // Tạo giao diện cho Fragment.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_revenue_chart, container, false);
    }

    // Ánh xạ view và chuẩn bị dữ liệu sau khi giao diện được tạo.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        salesViewModel = new ViewModelProvider(this).get(SalesViewModel.class);

        initViews(view);
        setupSpinner();
        setupTabLayout();

        // Quan sát dữ liệu từ ViewModel để cập nhật biểu đồ khi có thay đổi.
        salesViewModel.allOrders.observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                updateChart();
            }
        });
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void initViews(View v) {
        barChart = v.findViewById(R.id.barChart);
        tabLayout = v.findViewById(R.id.tabLayout);
        spinnerYear = v.findViewById(R.id.spinnerYear);
        tvMax = v.findViewById(R.id.tvMax);
        tvMin = v.findViewById(R.id.tvMin);
        tvAvg = v.findViewById(R.id.tvAvg);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupSpinner() {
        String[] years = {"2022", "2023", "2024", "2025"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);
        spinnerYear.setSelection(2); // 2024

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Cập nhật biểu đồ khi người dùng chọn năm.
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateChart();
            }
            // Không cần xử lý khi chưa chọn gì trong spinner.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // Cập nhật biểu đồ khi người dùng đổi tab doanh thu.
            @Override
            public void onTabSelected(TabLayout.Tab tab) { updateChart(); }
            // Không cần xử lý khi tab bị bỏ chọn.
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            // Không cần xử lý thêm khi bấm lại tab đang chọn.
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // Định dạng dữ liệu để hiển thị dễ đọc hơn.
    private void updateChart() {
        if (spinnerYear.getSelectedItem() == null) return;

        int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        int selectedTab = tabLayout.getSelectedTabPosition();

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        if (selectedTab == 0) {
            addMonthlyRevenue(selectedYear, entries, labels, values);
        } else if (selectedTab == 1) {
            addQuarterlyRevenue(selectedYear, entries, labels, values);
        } else {
            addYearlyRevenue(selectedYear, entries, labels, values);
        }

        drawChart(entries, labels, values);
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private void addMonthlyRevenue(int selectedYear, List<BarEntry> entries, List<String> labels, List<Double> values) {
        for (int i = 0; i < 12; i++) {
            long start = DateUtils.startOfMonth(selectedYear, i);
            long end = DateUtils.endOfMonth(selectedYear, i);
            Double revenue = salesViewModel.getRevenueInRange(start, end);
            float value = (float) ((revenue != null ? revenue : 0) / 1000000.0);
            entries.add(new BarEntry(i, value));
            labels.add("T" + (i + 1));
            values.add((double) value);
        }
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private void addQuarterlyRevenue(int selectedYear, List<BarEntry> entries, List<String> labels, List<Double> values) {
        for (int q = 0; q < 4; q++) {
            double quarterTotal = 0;
            for (int m = q * 3; m < (q + 1) * 3; m++) {
                long start = DateUtils.startOfMonth(selectedYear, m);
                long end = DateUtils.endOfMonth(selectedYear, m);
                Double revenue = salesViewModel.getRevenueInRange(start, end);
                quarterTotal += revenue != null ? revenue : 0;
            }
            float value = (float) (quarterTotal / 1000000.0);
            entries.add(new BarEntry(q, value));
            labels.add("Quý " + (q + 1));
            values.add((double) value);
        }
    }

    // Thêm mới hoặc lưu dữ liệu người dùng nhập.
    private void addYearlyRevenue(int selectedYear, List<BarEntry> entries, List<String> labels, List<Double> values) {
        for (int i = 0; i < 3; i++) {
            int year = selectedYear - 2 + i;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, 0, 1, 0, 0, 0);
            long start = calendar.getTimeInMillis();
            calendar.set(year, 11, 31, 23, 59, 59);
            long end = calendar.getTimeInMillis();
            Double revenue = salesViewModel.getRevenueInRange(start, end);
            float value = (float) ((revenue != null ? revenue : 0) / 1000000.0);
            entries.add(new BarEntry(i, value));
            labels.add(String.valueOf(year));
            values.add((double) value);
        }
    }

    // Vẽ biểu đồ cột doanh thu từ dữ liệu đã tính.
    private void drawChart(List<BarEntry> entries, List<String> labels, List<Double> values) {
        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu (triệu VNĐ)");
        dataSet.setColor(Color.parseColor("#E65100"));
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.animateY(1000);
        barChart.invalidate();

        // Cập nhật số liệu tóm tắt
        if (!values.isEmpty()) {
            double max = Collections.max(values);
            double min = Collections.min(values);
            double sum = 0;
            for (double v : values) sum += v;
            double avg = sum / values.size();

            tvMax.setText("Cao nhất: " + CurrencyFormatter.format(max * 1000000));
            tvMin.setText("Thấp nhất: " + CurrencyFormatter.format(min * 1000000));
            tvAvg.setText("Trung bình: " + CurrencyFormatter.format(avg * 1000000));
        }
    }
}
