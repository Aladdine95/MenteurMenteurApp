package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class GameActivity extends AppCompatActivity{
    private LineChart mpLineChart = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mpLineChart = findViewById(R.id.line_chart);
        LineDataSet lineDataSetP = new LineDataSet(dataValuesPulsation(), "BPM");
        lineDataSetP.setColor(Color.BLUE);
        lineDataSetP.setCircleColor(Color.BLUE);
        LineDataSet lineDataSetT = new LineDataSet(dataValuesTemperatures(), "°C");
        lineDataSetT.setColor(Color.RED);
        lineDataSetT.setCircleColor(Color.RED);
        LineDataSet lineDataSetH = new LineDataSet(dataValuesHygrometrie(), "g/m3");
        lineDataSetH.setColor(Color.GREEN);
        lineDataSetH.setCircleColor(Color.GREEN);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSetP);
        dataSets.add(lineDataSetT);
        dataSets.add(lineDataSetH);
        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();
        mpLineChart.setBackgroundColor(Color.WHITE);

        RefreshCharts rCharts = new GameActivity.RefreshCharts(mpLineChart);
        rCharts.run();
    }

    private static List<Entry> dataValuesPulsation(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        for(Float element: BluetoothActivity.c_Pulsation){
            dataValues.add(new Entry(index, element));
            index++;
        }
        return dataValues;
    }

    private static List<Entry> dataValuesTemperatures(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        for(Float element: BluetoothActivity.c_Temperature){
            dataValues.add(new Entry(index, element));
            index++;
        }
        return dataValues;
    }

    private static List<Entry> dataValuesHygrometrie(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        for(Float element: BluetoothActivity.c_Hygrometrie){
            dataValues.add(new Entry(index, element));
            index++;
        }
        return dataValues;
    }

    public static void updateValuesGraph(LineChart mpLineChart){
        LineDataSet lineDataSetP = new LineDataSet(dataValuesPulsation(), "BPM");
        lineDataSetP.setColor(Color.BLUE);
        lineDataSetP.setCircleColor(Color.BLUE);
        LineDataSet lineDataSetT = new LineDataSet(dataValuesTemperatures(), "°C");
        lineDataSetT.setColor(Color.RED);
        lineDataSetT.setCircleColor(Color.RED);
        LineDataSet lineDataSetH = new LineDataSet(dataValuesHygrometrie(), "g/m3");
        lineDataSetH.setColor(Color.GREEN);
        lineDataSetH.setCircleColor(Color.GREEN);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSetP);
        dataSets.add(lineDataSetT);
        dataSets.add(lineDataSetH);
        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();
    }

    private static class RefreshCharts extends Thread{
        private LineChart mpLineChart = null;

        public RefreshCharts(LineChart mpLineChart){
            this.mpLineChart = mpLineChart;
        }

        public void run(){
            updateValuesGraph(mpLineChart);
        }
    }
}