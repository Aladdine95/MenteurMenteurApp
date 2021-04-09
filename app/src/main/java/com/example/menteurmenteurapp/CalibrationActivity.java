package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CalibrationActivity extends AppCompatActivity {
    private ArrayList<String> questionList;
    private Iterator<String> iteratorQuestions;
    private Button suivantButton;
    private TextView questionsView;
    private int indexQuestion;
    private boolean startQuestion, calibrationStart, calibrationDone, cleared;
    private Thread thread;
    public static TextView temperatureVariantes;
    public static TextView hygrometrieVariantes;
    public static TextView pulsationVariantes;

    public static float c_pulsation_m, c_temperature_m, c_hygrometrie_m;
    public static final int GAME_ACTIVITY_REQUEST_CODE = 35;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        System.out.println(getResources().openRawResource(R.raw.questions).toString());
        InputStream inputStream = getResources().openRawResource(R.raw.questions);
        questionList = getQuestionsFromFile(inputStream);

        temperatureVariantes = findViewById(R.id.degresView);
        hygrometrieVariantes = findViewById(R.id.gm3View);
        pulsationVariantes = findViewById(R.id.bpmView);
        suivantButton = findViewById(R.id.suivantButton);
        Button retourMenuButton = findViewById(R.id.retourMenuButton);

        questionsView = findViewById(R.id.questionsView);

        iteratorQuestions = questionList.iterator();
        indexQuestion = 1;
        startQuestion = true;
        calibrationStart = false;
        calibrationDone = false;
        cleared = false;

        suivantButton.setOnClickListener(v -> {
            if(startQuestion){
                suivantButton.setBackgroundColor(Color.GREEN);
                suivantButton.setText("Suivant (" + indexQuestion + "/" + questionList.size() +")");
                if(!calibrationStart) {
                    calibrationStart = true;
                    calibration();
                }
                if(indexQuestion <= questionList.size()) {
                    for (int index = 0; index <= indexQuestion; index++) {
                        if (index == indexQuestion) {
                            questionsView.setText(iteratorQuestions.next());
                        }
                    }
                    indexQuestion++;
                }
                else{
                    suivantButton.setText("Menu suivant");
                    suivantButton.setTextColor(Color.WHITE);
                    suivantButton.setBackgroundColor(Color.GRAY);
                    questionsView.setText("Fin des questions !");
                    questionsView.setBackgroundColor(Color.GRAY);
                    startQuestion = false;
                    calibrationDone = true;
                }
            }
            else{
                Intent gameActivity = new Intent(CalibrationActivity.this, GameActivity.class);
                startActivityForResult(gameActivity, GAME_ACTIVITY_REQUEST_CODE);
            }
        });

        retourMenuButton.setOnClickListener(v -> finish());
    }

    public ArrayList<String> getQuestionsFromFile(InputStream inputStream){
        ArrayList<String> questionsList = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while(line != null){
                questionsList.add(line);
                line = reader.readLine();
            }
            reader.close();

            return questionsList;
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return questionsList;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(thread != null){
            thread.interrupt();
        }
    }

    @SuppressLint("SetTextI18n")
    private void calibration(){
        if(thread != null){
            thread.interrupt();
        }

        thread = new Thread(() -> {
            while(calibrationStart){

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(calibrationStart && !cleared
                        && BluetoothActivity.c_Pulsation != null && BluetoothActivity.c_Temperature != null
                && BluetoothActivity.c_Hygrometrie != null){
                    cleared = true;
                    BluetoothActivity.c_Pulsation.clear();
                    BluetoothActivity.c_Temperature.clear();
                    BluetoothActivity.c_Hygrometrie.clear();
                }

                runOnUiThread(() -> {
                    temperatureVariantes.setText(BluetoothActivity.c_Temperature.get(BluetoothActivity.c_Temperature.size() - 1).toString() + " °C");
                    hygrometrieVariantes.setText(BluetoothActivity.c_Hygrometrie.get(BluetoothActivity.c_Hygrometrie.size() - 1).toString() + " g/m3");
                    pulsationVariantes.setText(BluetoothActivity.c_Pulsation.get(BluetoothActivity.c_Pulsation.size() - 1).toString() + "BPM");
                });

                if(calibrationDone){
                    c_pulsation_m = calculMoyenne(BluetoothActivity.c_Pulsation);
                    c_temperature_m = calculMoyenne(BluetoothActivity.c_Temperature);
                    c_hygrometrie_m = calculMoyenne(BluetoothActivity.c_Hygrometrie);

                    System.err.println("c_pulsation_m : " + c_pulsation_m);
                    System.err.println("c_temperature_m : " + c_temperature_m);
                    System.err.println("c_hygrometrie_m : " + c_hygrometrie_m);

                    BluetoothActivity.c_Pulsation.clear();
                    BluetoothActivity.c_Temperature.clear();
                    BluetoothActivity.c_Hygrometrie.clear();
                    calibrationStart = false;
                }
            }
        });
        thread.start();
    }

    private float calculMoyenne(List<Float> arrayList){
        int index = 0;
        float resultat = 0;
        for(Float elt: arrayList){
            resultat += elt;
            index++;
        }
        resultat = resultat / index;
        return resultat;
    }
}