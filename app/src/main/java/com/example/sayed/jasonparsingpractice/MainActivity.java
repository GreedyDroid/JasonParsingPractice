package com.example.sayed.jasonparsingpractice;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView flowerTV;

    private static final String FLR_URL = "http://services.hanselandpetal.com/feeds/flowers.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo !=null){
            if (networkInfo.isAvailable()&& networkInfo.isConnected()){
                new MyJsonDownloadTask().execute(FLR_URL);
            }else {
                Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "No network Found", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyJsonDownloadTask extends AsyncTask<String,Void, String>{
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url= new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                flowerTV = (TextView) findViewById(R.id.flowerName);

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";

                while ((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line);
                }
                //Return full json in String>>
                return stringBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String  s) {
            super.onPostExecute(s);

            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject flowerObj = jsonArray.getJSONObject(1);
                String flowerName = flowerObj.getString("name");

                Toast.makeText(MainActivity.this, flowerName, Toast.LENGTH_SHORT).show();
                flowerTV.setText(flowerName);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
