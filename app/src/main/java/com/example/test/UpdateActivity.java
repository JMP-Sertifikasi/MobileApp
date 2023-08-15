package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateActivity extends AppCompatActivity {

    private EditText namaEditText, umurEditText, alamatEditText;
    private String idMahasiswa;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        namaEditText = findViewById(R.id.namaEditText);
        umurEditText = findViewById(R.id.umurEditText);
        alamatEditText = findViewById(R.id.alamatEditText);
        submitButton = findViewById(R.id.submitButton);

        idMahasiswa = getIntent().getStringExtra("id_mahasiswa");

        new FetchDataFromApiTask().execute();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = namaEditText.getText().toString();
                String umur = umurEditText.getText().toString();
                String alamat = alamatEditText.getText().toString();

                new UpdateDataToApiTask().execute(nama, umur, alamat);
            }
        });
    }

    private class FetchDataFromApiTask extends AsyncTask<Void, Void, String> {
        private final String API_URL = "https://jmp.surabayawebtech.com/api/mahasiswa";

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseJson = new JSONObject(result);
                int status = responseJson.getInt("status");
                String message = responseJson.getString("message");

                if (status == 200 && message.equals("success")) {
                    JSONArray dataArray = responseJson.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        String id = dataObject.getString("id");

                        if (id.equals(idMahasiswa)) {
                            String nama = dataObject.getString("nama");
                            String umur = dataObject.getString("umur");
                            String alamat = dataObject.getString("alamat");

                            namaEditText.setText(nama);
                            umurEditText.setText(umur);
                            alamatEditText.setText(alamat);

                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateDataToApiTask extends AsyncTask<String, Void, String> {
        private final String API_URL = "https://jmp.surabayawebtech.com/api/mahasiswa";

        @Override
        protected String doInBackground(String... params) {
            String nama = params[0];
            String umur = params[1];
            String alamat = params[2];

            try {
                Log.d("id mahasiswa: ", idMahasiswa);
                URL url = new URL(API_URL+ "/" + idMahasiswa);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject data = new JSONObject();
                data.put("id", idMahasiswa);
                data.put("nama", nama);
                data.put("umur", umur);
                data.put("alamat", alamat);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.toString().getBytes());
                outputStream.flush();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    return response.toString();
                } else {
                    return "Error";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseJson = new JSONObject(result);
                int status = responseJson.getInt("status");
                String message = responseJson.getString("message");

                if (status == 200) {
                    Intent intent = new Intent(UpdateActivity.this, MenuActivity.class);
                    startActivity(intent);
                } else {
                    // Gagal disimpan, tampilkan pesan atau lakukan tindakan lain
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
