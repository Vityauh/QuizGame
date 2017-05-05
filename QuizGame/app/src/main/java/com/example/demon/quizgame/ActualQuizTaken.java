package com.example.demon.quizgame;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActualQuizTaken extends AppCompatActivity {

    private String urlSite;
    private ProgressDialog progressDialog;
    String quizId;
    private ArrayList<String> arrayListQuestions = new ArrayList<>();
    private ArrayList<String> arrayListAnswers = new ArrayList<>();
    private ArrayList<Integer> arrayListOrder = new ArrayList<>();
    int whichQuestion;
    int whickAnswer = 0;


    @BindView(R.id.beginButton)
    Button beginButton;
    @BindView(R.id.questionTextView)
    TextView questionTextView;
    @BindView(R.id.radioButton1)
    RadioButton radioButton1;
    @BindView(R.id.radioButton2)
    RadioButton radioButton2;
    @BindView(R.id.radioButton3)
    RadioButton radioButton3;
    @BindView(R.id.radioButton4)
    RadioButton radioButton4;
    @BindView(R.id.textViewTest)
    TextView textViewTest;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView3)
    TextView textView3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_quiz_taken);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                quizId = null;
            } else {
                quizId = extras.getString("Quiz Title");
            }
        } else {
            quizId = (String) savedInstanceState.getSerializable("Quiz Title");
        }

        new GetDataTask().execute();




    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ActualQuizTaken.this);
            progressDialog.setMessage("downloading api");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return getData();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                progressDialog.hide();
                ActualQuizTaken.this.getQuestionsJSON(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private String getData() throws IOException {
        urlSite = "http://quiz.o2.pl/api/v1/quiz/"+ quizId + "/0";
        URL url = new URL(urlSite);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(inputStream);
        } finally {
            urlConnection.disconnect();
        }
    }
    private String readStream(InputStream stream) throws IOException {
        return IOUtils.toString(stream, StandardCharsets.UTF_8.name());
    }

    private void getQuestionsJSON(String data)throws JSONException {

        whichQuestion = -1;
        if (TextUtils.isEmpty(data)) {
            return;
        }

        JSONObject jsonObject = new JSONObject(data);
        JSONArray quizQuestionNameArray = jsonObject.getJSONArray("questions");
        JSONArray answersArray = jsonObject.getJSONArray("questions");
        for (int i = 0 ; i <answersArray.length();i++){
            JSONArray moreSpecificAnswers = new JSONArray(answersArray.getJSONObject(i).getString("answers"));
            for (int j = 0 ; j <moreSpecificAnswers.length(); j++){
                arrayListAnswers.add(moreSpecificAnswers.getJSONObject(j).getString("text"));
            }
        }


        for (int i = 0; i<quizQuestionNameArray.length(); i++){
            arrayListQuestions.add(i,quizQuestionNameArray.getJSONObject(i).getString("text"));
        }
        textViewTest.setText(arrayListAnswers.size()+ " ");

        for (int i = 0 ; i <answersArray.length();i++){
            JSONArray moreSpecificAnswers = new JSONArray(answersArray.getJSONObject(i).getString("answers"));
            for (int j = 0 ; j <moreSpecificAnswers.length(); j++){
                arrayListOrder.add(moreSpecificAnswers.getJSONObject(j).getInt("order"));
            }
        }






        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginButton.setText("Next question!");

                if (whichQuestion<arrayListQuestions.size()-1) {
                    whichQuestion++;
                    questionTextView.setText(arrayListQuestions.get(whichQuestion));
                    what :
                    if (whickAnswer<=arrayListOrder.size()) {
                        radioButton1.setText(arrayListAnswers.get(whickAnswer));
                        whickAnswer++;
                        radioButton2.setText(arrayListAnswers.get(whickAnswer));
                        if (whickAnswer == arrayListOrder.size()-1){
                            break what;
                        }
                        whickAnswer++;
                        if (arrayListOrder.get(whickAnswer) == 3 ) {
                            radioButton3.setVisibility(View.VISIBLE);
                            radioButton4.setVisibility(View.VISIBLE);
                            radioButton3.setText(arrayListAnswers.get(whickAnswer));
                            whickAnswer++;
                            radioButton4.setText(arrayListAnswers.get(whickAnswer));
                            whickAnswer++;
                        } else if (arrayListOrder.get(whickAnswer) ==1){
                            radioButton3.setVisibility(View.INVISIBLE);
                            radioButton4.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    questionTextView.setText("To wszystko");
                    radioButton1.setVisibility(View.INVISIBLE);
                    radioButton2.setVisibility(View.INVISIBLE);
                    radioButton3.setVisibility(View.INVISIBLE);
                    radioButton4.setVisibility(View.INVISIBLE);

                }


            }
        });
    }

}
