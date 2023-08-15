package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private TextView namaTextView, umurTextView, alamatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        namaTextView = findViewById(R.id.namaTextView);
        umurTextView = findViewById(R.id.umurTextView);
        alamatTextView = findViewById(R.id.alamatTextView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String idMahasiswa = extras.getString("id_mahasiswa");
            new FetchDataFromApiTask().execute(idMahasiswa);
        }
    }

    private class FetchDataFromApiTask extends AsyncTask<String, Void, String> {
        private final String API_URL = "https://jmp.surabayawebtech.com/api/mahasiswa/";

        @Override
        protected String doInBackground(String... params) {
            String idMahasiswa = params[0];
            String response = "";

            try {
                Log.d("Id Test: ", idMahasiswa);
                URL url = new URL(API_URL + idMahasiswa);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    response += line;
                }

                reader.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseJson = new JSONObject(result);
                int status = responseJson.getInt("status");
                String message = responseJson.getString("message");

                if (status == 200 && message.equals("success")) {
                    JSONObject dataObject = responseJson.getJSONObject("data");
                    String nama = dataObject.getString("nama");
                    String umur = dataObject.getString("umur");
                    String alamat = dataObject.getString("alamat");

                    namaTextView.setText("Nama: " + nama);
                    umurTextView.setText("Umur: " + umur);
                    alamatTextView.setText("Alamat: " + alamat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
