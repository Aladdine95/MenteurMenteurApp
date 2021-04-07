package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private LineData data = null;
    private LineChart mpLineChart = null;
    private Button mpRefreshButton = null;
    private ArrayList<Entry> entries;
    private int index = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mpLineChart = findViewById(R.id.line_chart);
        entries = dataValues();
        LineDataSet lineDataSet = new LineDataSet(dataValues(), "BPM");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.GRAY);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();
        mpLineChart.setBackgroundColor(Color.WHITE);
        mpRefreshButton = findViewById(R.id.menuprincipalButton);
        mpRefreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LineDataSet lineDataSet = new LineDataSet(dataValues2(), "BPM");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet);
                data = new LineData(dataSets);
                mpLineChart.setData(data);
                mpLineChart.invalidate();
            }
        });
    }

    private ArrayList<Entry> dataValues(){
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.add(new Entry(0, 20));
        dataValues.add(new Entry(1, 70));
        dataValues.add(new Entry(2, 90));
        dataValues.add(new Entry(3, 120));
        dataValues.add(new Entry(4, 140));
        dataValues.add(new Entry(5, 160));
        return dataValues;
    }

    private ArrayList<Entry> dataValues2(){
        ArrayList<Entry> dataValues = entries;
        Random random = new Random();
        dataValues.add(new Entry(index, random.nextInt(200)));
        this.index++;
        return dataValues;
    }
}