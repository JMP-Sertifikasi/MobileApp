package com.example.test;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        listView = findViewById(R.id.listView);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        registerForContextMenu(listView); // Register the ListView for context menu
        new FetchDataFromApiTask().execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        if (item.getItemId() == R.id.menuUpdate) {
            String data = dataList.get(position); // Ambil data pada posisi yang di-klik
            String id = data.substring(data.indexOf("ID: ") + 4, data.indexOf("\n")); // Ambil ID dari data

            Intent intentUpdate = new Intent(ShowActivity.this, UpdateActivity.class);
            intentUpdate.putExtra("id_mahasiswa", id); // Kirim ID ke UpdateActivity
            startActivity(intentUpdate);
            return true;
        } else if (item.getItemId() == R.id.menuDelete) {
            String data = dataList.get(position); // Ambil data pada posisi yang di-klik
            String id = data.substring(data.indexOf("ID: ") + 4, data.indexOf("\n")); // Ambil ID dari data

            new DeleteDataFromApiTask().execute(id); // Hapus data dengan ID yang dipilih
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private class DeleteDataFromApiTask extends AsyncTask<String, Void, String> {
        private final String API_URL = "https://jmp.surabayawebtech.com/api/mahasiswa";

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];

            try {
                URL url = new URL(API_URL + "/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE"); // Menggunakan metode DELETE untuk operasi delete
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Success";
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
            if (result.equals("Success")) {
                Intent intent = new Intent(ShowActivity.this, MenuActivity.class);
                startActivity(intent);
            } else {
                // Gagal dihapus, tampilkan pesan atau lakukan tindakan lain
            }
        }
    }



    private class FetchDataFromApiTask extends AsyncTask<Void, Void, String> {
        private final String API_URL = "https://jmp.surabayawebtech.com/api/mahasiswa"; // Ganti dengan URL API Anda

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
            Log.d("API Response", result);
            try {
                JSONObject responseJson = new JSONObject(result);
                int status = responseJson.getInt("status");
                String message = responseJson.getString("message");

                if (status == 200 && message.equals("success")) {
                    JSONArray dataArray = responseJson.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        String id = dataObject.getString("id");
                        String nama = dataObject.getString("nama");
                        String umur = dataObject.getString("umur");
                        String alamat = dataObject.getString("alamat");

                        // Tampilkan data dalam format yang Anda inginkan
                        String dataString = "ID: " + id + "\nNama: " + nama + "\nUmur: " + umur + "\nAlamat: " + alamat;
                        dataList.add(dataString);
                    }

                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
