package reddeveloper.ru.braintrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textViewTimer;
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewLV;
    private TextView textViewRV;
    private TextView textViewLN;
    private TextView textViewRN;
    private ArrayList<TextView> options = new ArrayList<>();

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private boolean isPositive;
    private int min = 5;
    private int max = 30;
    private int countOfQuestions = 0;
    private int countOfRightAnswers = 0;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTimer = findViewById(R.id.textViewTimer);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewScore = findViewById(R.id.textViewScore);
        textViewLV = findViewById(R.id.textViewLV);
        textViewLN = findViewById(R.id.textViewLN);
        textViewRV = findViewById(R.id.textViewRV);
        textViewRN = findViewById(R.id.textViewRN);
        options.add(textViewLN);
        options.add(textViewLV);
        options.add(textViewRN);
        options.add(textViewRV);
        playNext();
        CountDownTimer timer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long l) {
                textViewTimer.setText(getTime(l));
                if (l <= 10000){
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if(countOfRightAnswers > max){
                    preferences.edit().putInt("max", countOfRightAnswers).apply();
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightAnswers);
                startActivity(intent);
            }
        };
        timer.start();
    }
    private void playNext(){
        generateQuestion();

        for (int i = 0; i < options.size(); i++){
            if (i == rightAnswerPosition){
                options.get(i).setText(Integer.toString(rightAnswer));
            }
            else {
                options.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
        }
        String score = String.format("%s / %s", countOfRightAnswers, countOfQuestions);
        textViewScore.setText(score);
    }

    private void generateQuestion(){
        int a = (int) (Math.random() * (max - min + 1) + min);
        int b = (int) (Math.random() * (max - min + 1) + min);
        int mark = (int)(Math.random() * 2);
        isPositive = mark == 1;
        if (isPositive){
            rightAnswer = a + b;
            question = String.format("%s + %s", a, b);
        }
        else {
            rightAnswer = a - b;
            question = String.format("%s - %s", a, b);
        }
        textViewQuestion.setText(question);
        rightAnswerPosition = (int)(Math.random()*4);
    }

    private int generateWrongAnswer(){
        int result;
        do {
            result = (int)(Math.random() * max * 2 + 1) - (max - min);
        } while (result == rightAnswer);
            return result;

    }

    private String getTime(long milis){
        int seconds = (int) (milis/1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public void onClickAnswer(View view) {
        if (!gameOver){
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int choosenAnswer = Integer.parseInt(answer);
            if (choosenAnswer == rightAnswer){
                Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();
                countOfRightAnswers++;
            }
            else {
                Toast.makeText(this, "Ошибка " + choosenAnswer + " " + rightAnswer, Toast.LENGTH_SHORT).show();
            }
            countOfQuestions++;

            playNext();
        }

    }
}
