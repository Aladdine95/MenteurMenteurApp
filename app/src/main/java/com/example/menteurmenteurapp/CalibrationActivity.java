package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class CalibrationActivity extends AppCompatActivity {
    private ArrayList<String> questionList;
    private Iterator<String> iteratorQuestions;
    private Button suivantButton, retourMenuButton;
    private TextView questionsView;
    private int indexQuestion;
    private boolean startQuestion;

    public static final int GAME_ACTIVITY_REQUEST_CODE = 35;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        System.out.println(getResources().openRawResource(R.raw.questions).toString());
        InputStream inputStream = getResources().openRawResource(R.raw.questions);
        questionList = getQuestionsFromFile(inputStream);

        suivantButton = findViewById(R.id.suivantButton);
        retourMenuButton = findViewById(R.id.retourMenuButton);

        questionsView = findViewById(R.id.questionsView);

        iteratorQuestions = questionList.iterator();
        indexQuestion = 1;
        startQuestion = true;

        suivantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startQuestion == true){
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
            }
        });

        retourMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity(MainActivity.CALIBRATION_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    public ArrayList<String> getQuestionsFromFile(InputStream inputStream){
        ArrayList<String> questionsList = new ArrayList<String>();
        FileReader fileReader = null;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while(line != null){
                System.out.println(line); // A supprimer
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