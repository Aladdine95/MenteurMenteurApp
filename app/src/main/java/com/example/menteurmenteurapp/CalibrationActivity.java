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

/**
 * CalibrationActivity est l'activité de calibration.
 * On y définit les méthodes et actions pour la calibration de notre simulation.
 * @author Équipe détecteur de mensonge
 * @version 0.9
 */
public class CalibrationActivity extends AppCompatActivity {
    /**
     * Collection contenant les questions qui seront posées au sujet (Pas optimal, utiliser une liste (c.f classe List) pour éviter l'itérator).
     */
    private ArrayList<String> questionList;
    /**
     * Permet d'itérer à travers notre ArrayList de questions.
     */
    private Iterator<String> iteratorQuestions;
    private Button suivantButton;
    private TextView questionsView;
    /**
     * indexQuestion est le numéro de la question actuelle posée à l'utilisateur.
     */
    private int indexQuestion;
    private boolean startQuestion, calibrationStart, calibrationDone, cleared;
    /**
     * Thread permettant l'acquisition des données reçues par notre capteur bluetooth, ainsi que l'affichage en temps-réel sur l'application.
     */
    private Thread thread;
    @SuppressLint("StaticFieldLeak")
    public static TextView temperatureVariantes, hygrometrieVariantes, pulsationVariantes;
    /**
     * Ces flottants contiendront les valeurs moyennes du capteur de pulsation, température et hygrométrie.
     */
    public static float c_pulsation_m, c_temperature_m, c_hygrometrie_m;
    public static final int GAME_ACTIVITY_REQUEST_CODE = 35;

    /**
     * Méthode appelée dés le lancement de l'activité définissant le layout et les actions possibles
     * pour la création de l'activité.
     * @param savedInstanceState
     */
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

    /**
     * On récupère les questions depuis une liste de questions définies dans un fichier qui sera lu ligne par ligne.
     * Format de la question dans le fichier (exemple) :
     * Quel est ton prénom?
     * Quel age a-tu?
     * Quel est la couleur de tes cheveux?
     * Quel est ton plat préféré?
     * Combien mesures-tu?
     * Quelle est la couleur de ton pentalon?
     * Es-tu à l'aise?
     *
     * @param inputStream
     * @return
     */
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

    /**
     * Cette méthode est appelée lorsque l'application est en arrière plan.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(thread != null){
            //Interruption du thread lorsque le thread est en cours de fonctionnement
            thread.interrupt();
        }
    }

    /**
     * Méthode définissant le comportement de la calibration.
     */
    @SuppressLint("SetTextI18n")
    private void calibration(){
        if(thread != null){
            thread.interrupt();
        }
        //Ce Thread nous permet de récupérer les données envoyées par le module bluetooth de façon continuelle (tant que le thread est started).
        thread = new Thread(() -> {
            while(calibrationStart){

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //On clear une première fois les listes contenant les données précédentes des capteurs de pulsation, température et hygrométrie
                //afin d'effectuer nos traitements sur le une base nouvelle.
                if(calibrationStart && !cleared
                        && BluetoothActivity.c_Pulsation != null && BluetoothActivity.c_Temperature != null
                && BluetoothActivity.c_Hygrometrie != null){
                    cleared = true;
                    BluetoothActivity.c_Pulsation.clear();
                    BluetoothActivity.c_Temperature.clear();
                    BluetoothActivity.c_Hygrometrie.clear();
                }

                //On affiche à l'utilisateur les valeurs des capteurs pour lui montrer quelles sont les constantes vitales du sujet.
                runOnUiThread(() -> {
                   if(BluetoothActivity.c_Temperature.size()>1 )
                        temperatureVariantes.setText(BluetoothActivity.c_Temperature.get(BluetoothActivity.c_Temperature.size() - 1).toString() + " °C");
                   if(BluetoothActivity.c_Hygrometrie.size()>1)
                        hygrometrieVariantes.setText(BluetoothActivity.c_Hygrometrie.get(BluetoothActivity.c_Hygrometrie.size() - 1).toString() + " g/m3");
                   if(BluetoothActivity.c_Pulsation.size()>1)
                        pulsationVariantes.setText(BluetoothActivity.c_Pulsation.get(BluetoothActivity.c_Pulsation.size() - 1).toString() + "BPM");
                });

                //Une fois la calibration effectuée, on calcul la moyenne grâce aux données reçues puis on les sauvegarde dans des variables
                //qu'on utilisera plus tard
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

    /**
     * Effectue le calcul de la moyenne lors de la calibration (à partir des données stockée dans une liste donnée).
     * @param arrayList
     * @return float
     */
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