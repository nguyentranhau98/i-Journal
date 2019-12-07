package com.i_journal;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;


public class StatsFragment extends Fragment implements OnChartValueSelectedListener {

    private CombinedChart mChart;
    private TextView tvTime;
    FirebaseUser currentFirebaseUser;
    FirebaseHelper firebaseHelper;

    public StatsFragment(FirebaseUser currentFirebaseUser,FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
        this.currentFirebaseUser = currentFirebaseUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats, container, false);

        tvTime = (TextView) root.findViewById(R.id.tv_time);
        String currentDate = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
        tvTime.setText(currentDate.toString());

        mChart = (CombinedChart) root.findViewById(R.id.combinedChart);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.setOnChartValueSelectedListener(this);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0.0f);
        rightAxis.setAxisMaximum(6.0f);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0.0f);
        leftAxis.setAxisMaximum(6.0f);

        final List<String> xLabel = new ArrayList<>();
        List<Post> listPost = firebaseHelper.getStatList(currentFirebaseUser.getUid());

        for (int i = 0 ; i <listPost.size(); i++){
            String dateString = new SimpleDateFormat("dd/MM").format(new Date(listPost.get(i).time));
            xLabel.add(dateString);
        }

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int) value % xLabel.size());
            }
        });

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart(listPost));
        data.setData(lineDatas);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();

        return root;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        tvTime.setText(e+"");
    }

    @Override
    public void onNothingSelected() {
        String currentDate = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
        tvTime.setText(currentDate.toString());
    }

    private static DataSet dataChart(List<Post> listPost) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < listPost.size(); index++) {
            entries.add(new Entry(index, listPost.get(index).getRating()));
        }

        LineDataSet set = new LineDataSet(entries, "Moods of User");
        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.GREEN);
        set.setCircleRadius(5f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }
}
