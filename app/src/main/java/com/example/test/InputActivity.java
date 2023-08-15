package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InputActivity extends AppCompatActivity {

    private EditText namaEditText, umurEditText, alamatEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        namaEditText = findViewById(R.id.namaEditText);
        umurEditText = findViewById(R.id.umurEditText);
        alamatEditText = findViewById(R.id.alamatEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = namaEditText.getText().toString();
                String umur = umurEditText.getText().toString();
                String alamat = alamatEditText.getText().toString();

                try {
                    JSONObject postData = new JSONObject();
                    postData.put("nama", nama);
                    postData.put("umur", umur);
                    postData.put("alamat", alamat);

                    new SendDataTask().execute(postData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class SendDataTask extends AsyncTask<String, Void, String> {
        private final String API_URL = "https://jmp.surabayawebtech.com/api/mahasiswa";

        @Override
        protected String doInBackground(String... params) {
            String data = params[0];  // Data yang akan dikirim
            String response = "";

            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes("UTF-8"));
                os.close();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder responseBuilder = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        responseBuilder.append(inputLine);
                    }
                    in.close();

                    response = responseBuilder.toString();
                } else {
                    response = "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseJson = new JSONObject(result);
                String nama = responseJson.optString("nama");

                Toast.makeText(InputActivity.this, nama, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(InputActivity.this, MenuActivity.class);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
