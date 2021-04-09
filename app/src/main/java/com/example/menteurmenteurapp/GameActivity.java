package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity{
    private LineChart mpLineChart = null;
    private final static int ECART_PULSATION = 10;
    private final static int ECART_TEMPERATURE = 5;
    private final static int ECART_HYGROMETRIE = 2;
    private TextView true_or_falseView;
    private Timestamp ts;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ts = new Timestamp((int) System.currentTimeMillis());
        true_or_falseView = findViewById(R.id.trueorfalseView);

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
        startRefresh();
    }

    public List<Entry> dataValuesPulsation(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        if(BluetoothActivity.c_Pulsation!=null) {
            for (Float element : BluetoothActivity.c_Pulsation) {
                dataValues.add(new Entry(index, element));
                index++;
            }
        }
        return dataValues;
    }

    public List<Entry> dataValuesTemperatures(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        if(BluetoothActivity.c_Temperature!=null) {
            for (Float element : BluetoothActivity.c_Temperature) {
                dataValues.add(new Entry(index, element));
                index++;
            }
        }
        return dataValues;
    }

    public List<Entry> dataValuesHygrometrie(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        if(BluetoothActivity.c_Hygrometrie!=null) {
            for (Float element : BluetoothActivity.c_Hygrometrie) {
                dataValues.add(new Entry(index, element));
                index++;
            }
        }
        return dataValues;
    }

    public void updateValuesGraph(LineChart mpLineChart){
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
        data.notifyDataChanged();
        mpLineChart.setData(data);
        mpLineChart.notifyDataSetChanged();
        mpLineChart.setData(data);
        mpLineChart.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(thread != null){
            thread.interrupt();
        }
    }

    @SuppressLint("SetTextI18n")
    private void startRefresh(){
        if(thread != null){
            thread.interrupt();
        }

        thread = new Thread(() -> {

            while(true){
                runOnUiThread(this::mensongeTraitement);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateValuesGraph(mpLineChart);
            }
        });
        thread.start();
    }

    @SuppressLint("SetTextI18n")
    private void mensongeTraitement() {
        true_or_falseView = findViewById(R.id.trueorfalseView);
        if((derniereValeurCapteur(BluetoothActivity.c_Temperature) < CalibrationActivity.c_temperature_m - ECART_TEMPERATURE
                || derniereValeurCapteur(BluetoothActivity.c_Temperature) > CalibrationActivity.c_temperature_m + ECART_TEMPERATURE)
        || (derniereValeurCapteur(BluetoothActivity.c_Pulsation) < CalibrationActivity.c_pulsation_m - ECART_PULSATION
                ||  derniereValeurCapteur(BluetoothActivity.c_Pulsation) > CalibrationActivity.c_pulsation_m + ECART_PULSATION)
        || (derniereValeurCapteur(BluetoothActivity.c_Hygrometrie) < CalibrationActivity.c_hygrometrie_m - ECART_HYGROMETRIE
                ||  derniereValeurCapteur(BluetoothActivity.c_Hygrometrie) > CalibrationActivity.c_hygrometrie_m + ECART_HYGROMETRIE)){
            true_or_falseView.setText("MENTEUR !");
            true_or_falseView.setBackgroundColor(Color.RED);
        }
        else{
            true_or_falseView.setText("Vérité...");
            true_or_falseView.setBackgroundColor(Color.GREEN);
        }

        if(BluetoothActivity.c_Pulsation.size() == 50){
            BluetoothActivity.c_Pulsation.clear();
            BluetoothActivity.c_Pulsation.add(CalibrationActivity.c_pulsation_m);
        }
        if(BluetoothActivity.c_Temperature.size() == 50){
            BluetoothActivity.c_Temperature.clear();
            BluetoothActivity.c_Temperature.add(CalibrationActivity.c_temperature_m);
        }
        if(BluetoothActivity.c_Hygrometrie.size() == 50){
            BluetoothActivity.c_Hygrometrie.clear();
            BluetoothActivity.c_Hygrometrie.add(CalibrationActivity.c_hygrometrie_m);
        }
    }

    private Float derniereValeurCapteur(List<Float> liste){
        if(liste.size() < 2){
            return (float) 0;
        }
        else{
            return liste.get(liste.size() - 1);
        }
    }
}