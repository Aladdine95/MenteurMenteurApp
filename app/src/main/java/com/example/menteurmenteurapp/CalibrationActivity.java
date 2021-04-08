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

public class CalibrationActivity extends AppCompatActivity {
    private ArrayList<String> questionList;
    private Iterator<String> iteratorQuestions;
    private Button suivantButton;
    private TextView questionsView;
    private int indexQuestion;
    private boolean startQuestion;

    public TextView temperatureVariantes;
    public TextView hygrometrieVariantes;
    public TextView pulsationVariantes;
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

        suivantButton.setOnClickListener(v -> {
            if(startQuestion){
                suivantButton.setBackgroundColor(Color.GREEN);
                suivantButton.setText("Suivant (" + indexQuestion + "/" + questionList.size() +")");
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
                    //suivantButton.setEnabled(false);
                    suivantButton.setBackgroundColor(Color.GRAY);
                    questionsView.setText("Fin des questions !");
                    questionsView.setBackgroundColor(Color.GRAY);
                    startQuestion = false;
                }
            }
            else{
                Intent gameActivity = new Intent(CalibrationActivity.this, GameActivity.class);
                startActivityForResult(gameActivity, GAME_ACTIVITY_REQUEST_CODE);
            }
        });

        retourMenuButton.setOnClickListener(v -> {
            //finishActivity(MainActivity.CALIBRATION_ACTIVITY_REQUEST_CODE);
            finish();
        });
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
}