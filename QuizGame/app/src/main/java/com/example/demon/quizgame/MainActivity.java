package com.example.demon.quizgame;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {



    @BindView(R.id.quizLists)
    ListView quizLists;
    private String urlSite;
    private ArrayList <String> arrayListId = new ArrayList<>();
    private String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new GetDataTask().execute();
        quizLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ActualQuizTaken.class);
                i.putExtra("Quiz Title", arrayListId.get(position));
                startActivity(i);
            }
        });

    }


    private class GetDataTask extends AsyncTask<Void, Void, String> {
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
                MainActivity.this.parseAndShowJsonData(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private String getData() throws IOException {
        urlSite = "http://quiz.o2.pl/api/v1/quizzes/0/100";
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

    private void parseAndShowJsonData (String data)throws JSONException {
        if (TextUtils.isEmpty(data)) {
            return;
        }

        ArrayList<String> arrayListTitle = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(data);
        JSONArray quizNameArray = jsonObject.getJSONArray("items");
        for (int i = 0; i<quizNameArray.length(); i++){
            arrayListTitle.add(0,quizNameArray.getJSONObject(i).getString("title"));
            arrayListId.add(0,quizNameArray.getJSONObject(i).getString("id"));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListTitle);
        quizLists.setAdapter(arrayAdapter);
    }
}