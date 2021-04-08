package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private static LineChart mpLineChart = null;

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

    public static void updateValuesGraph(){
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
}