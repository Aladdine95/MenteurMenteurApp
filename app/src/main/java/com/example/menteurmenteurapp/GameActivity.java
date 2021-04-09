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

import java.util.ArrayList;
import java.util.List;

import static com.example.menteurmenteurapp.CalibrationActivity.c_pulsation_m;

public class GameActivity extends AppCompatActivity{
    private LineChart mpLineChart = null;
    private final static int ECART_PULSATION = 10;
    private final static int ECART_TEMPERATURE = 1;
    private final static int ECART_HYGROMETRIE = 2;
    private TextView true_or_falseView, ledView, tsView;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tsView = findViewById(R.id.temps1View);
        ledView = findViewById(R.id.ledView);
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
        Button mpRefreshButton = findViewById(R.id.menuprincipalButton);
        mpRefreshButton.setOnClickListener(v -> updateValuesGraph(mpLineChart));
        startRefresh();
    }

    public List<Entry> dataValuesPulsation(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        for(Float element: BluetoothActivity.c_Pulsation){
            dataValues.add(new Entry(index, element));
            index++;
        }
        return dataValues;
    }

    public List<Entry> dataValuesTemperatures(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        for(Float element: BluetoothActivity.c_Temperature){
            dataValues.add(new Entry(index, element));
            index++;
        }
        return dataValues;
    }

    public List<Entry> dataValuesHygrometrie(){
        List<Entry> dataValues = new ArrayList<>();
        int index = 0;
        for(Float element: BluetoothActivity.c_Hygrometrie){
            dataValues.add(new Entry(index, element));
            index++;
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
                updateValuesGraph(mpLineChart);
                runOnUiThread(this::mensongeTraitement);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @SuppressLint("SetTextI18n")
    private void mensongeTraitement() {
        tsView = findViewById(R.id.temps1View);
        ledView = findViewById(R.id.ledView);
        true_or_falseView = findViewById(R.id.trueorfalseView);
        if((derniereValeurCapteur(BluetoothActivity.c_Temperature) < CalibrationActivity.c_temperature_m - ECART_TEMPERATURE
                && derniereValeurCapteur(BluetoothActivity.c_Temperature) > CalibrationActivity.c_temperature_m + ECART_TEMPERATURE)
        && (derniereValeurCapteur(BluetoothActivity.c_Pulsation) < CalibrationActivity.c_pulsation_m - ECART_PULSATION
                && derniereValeurCapteur(BluetoothActivity.c_Pulsation) > CalibrationActivity.c_pulsation_m + ECART_PULSATION)
        && (derniereValeurCapteur(BluetoothActivity.c_Hygrometrie) < CalibrationActivity.c_hygrometrie_m - ECART_HYGROMETRIE
                && derniereValeurCapteur(BluetoothActivity.c_Hygrometrie) > CalibrationActivity.c_hygrometrie_m + ECART_HYGROMETRIE)){
            true_or_falseView.setText("MENTEUR !");
            true_or_falseView.setBackgroundColor(Color.RED);
            ledView.setBackgroundColor(Color.RED);
            tsView.setText("" + (MainActivity.timestamp_start - System.currentTimeMillis()));
            if(BluetoothActivity.c_Pulsation != null && BluetoothActivity.c_Temperature != null
                    && BluetoothActivity.c_Hygrometrie != null) {
                BluetoothActivity.c_Temperature.clear();
                BluetoothActivity.c_Hygrometrie.clear();
                BluetoothActivity.c_Pulsation.clear();
            }
        }
        else{

            true_or_falseView.setText("Stand by...");
            true_or_falseView.setBackgroundColor(Color.GREEN);
            ledView.setBackgroundColor(Color.GREEN);
            tsView.setText("" + (MainActivity.timestamp_start - System.currentTimeMillis()));
        }
    }

    private Float derniereValeurCapteur(List<Float> liste){
        return liste.get(liste.size() - 1);
    }
}