package com.myproject.ui.activities;

import static com.myproject.ui.activities.HomeActivity.TAG;
import static com.myproject.ui.activities.HomeActivity.TEMP_VALUE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.myproject.R;
import com.myproject.room.Message;
import com.myproject.room.MessageViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class GraphActivity extends AppCompatActivity {

    private LineChart chart;
    //private SeekBar seekBarX, seekBarY;
    //private TextView tvX, tvY;

    private Typeface tfLight;
    ArrayList<Integer> tempValues;

    private MessageViewModel messageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        //seekBarX = findViewById(R.id.seekBar1);
        //seekBarX.setOnSeekBarChangeListener(this);

       // seekBarY = findViewById(R.id.seekBar2);
        //seekBarY.setOnSeekBarChangeListener(this);

       // tvX = findViewById(R.id.tvXMax);
       // tvY = findViewById(R.id.tvYMax);

        chart = findViewById(R.id.chart1);
        //chart.setOnChartValueSelectedListener(this);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.LTGRAY);

        // add data
       // seekBarX.setProgress(20);
       // seekBarY.setProgress(30);

        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTypeface(tfLight);
        l.setTextSize(16f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTypeface(tfLight);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(6);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(80f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.getMessagesFromKey(TEMP_VALUE).observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                tempValues = new ArrayList<>();
                ArrayList<String> timeLabels = new ArrayList<>();
                ArrayList<String> nonDuplicateTimes = new ArrayList<>();
                String time = "";
                for(int i = 0; i < messages.size(); i++) {
                    String t = String.valueOf(messages.get(i).getDate());
                    Log.i(TAG, "time: " + t);
                    time = t.substring(11, 16);
                    Log.i(TAG, "time: " + time);
                    timeLabels.add(time);
                    int temps = Integer.parseInt(messages.get(i).getValue().substring(0,2));
                    tempValues.add(temps);
                    Log.i(TAG, "tempValues: " + tempValues.get(i));
                }
                /*for(String t : timeLabels) {
                    if (!nonDuplicateTimes.contains(t)) {
                        nonDuplicateTimes.add(t);
                    }
                }*/
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(timeLabels));

                    //Log.i(TAG, "nonDuplicatedTimes list " + nonDuplicateTimes);

                setData(tempValues);

            }
        });

    }

    private void setData(ArrayList<Integer> values) {

        ArrayList<Entry> tempEntries = new ArrayList<>();

        for(int i = 0; i < values.size(); i++){
            float val = values.get(i);
            Log.i(TAG, "float value: " + val);
            tempEntries.add(new Entry(i, val));
        }

        LineDataSet set;

        if(chart.getData() != null && chart.getData().getDataSetCount() > 0){
            set = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set.setValues(tempEntries);
        } else {
            set = new LineDataSet(tempEntries, "Temperature Values");

            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(ColorTemplate.getHoloBlue());
            set.setCircleColor(Color.WHITE);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setDrawCircleHole(false);

            // create a data object with the data sets
            //LineData data = new LineData(set1, set2, set3);
            LineData data = new LineData(set);
            data.setDrawValues(false);
            //data.setValueTextColor(Color.WHITE);
            //data.setValueTextSize(9f);


            // set data
            chart.setData(data);
        }

        /*ArrayList<Entry> tempValues = new ArrayList<>();


        LineDataSet set;

        if(chart.getData() != null && chart.getData().getDataSetCount() > 0){
            set = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set.setValues(tempValues);
        } else {
            set = new LineDataSet(tempValues, "Temperature Values");

            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(ColorTemplate.getHoloBlue());
            set.setCircleColor(Color.WHITE);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setDrawCircleHole(false);
        }

        /*ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * (range / 2f)) + 50;
            values1.add(new Entry(i, val));
        }

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 450;
            values2.add(new Entry(i, val));
        }

        ArrayList<Entry> values3 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 500;
            values3.add(new Entry(i, val));
        }

        LineDataSet set1, set2, set3;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(values2, "DataSet 2");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(Color.RED);
            set2.setCircleColor(Color.WHITE);
            set2.setLineWidth(2f);
            set2.setCircleRadius(3f);
            set2.setFillAlpha(65);
            set2.setFillColor(Color.RED);
            set2.setDrawCircleHole(false);
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            set3 = new LineDataSet(values3, "DataSet 3");
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set3.setColor(Color.YELLOW);
            set3.setCircleColor(Color.WHITE);
            set3.setLineWidth(2f);
            set3.setCircleRadius(3f);
            set3.setFillAlpha(65);
            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
            set3.setDrawCircleHole(false);
            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the data sets
            //LineData data = new LineData(set1, set2, set3);
            LineData data = new LineData(set);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            chart.setData(data);*/

        }
    //}

}