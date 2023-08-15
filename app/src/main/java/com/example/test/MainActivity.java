package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = findViewById(R.id.emailEditText);
                EditText passwordEditText = findViewById(R.id.passwordEditText);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                try {
                    JSONObject postData = new JSONObject();
                    postData.put("email", email);
                    postData.put("password", password);

                    new SendDataTask().execute(postData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class SendDataTask extends AsyncTask<String, Void, String> {
        private final String API_URL = "https://jmp.surabayawebtech.com/api/auth/login";

        @Override
        protected String doInBackground(String... params) {
            String data = params[0];  // Data login yang dikirim
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
            // Handle response from the API here
            Log.d("API Response", result);

            try {
                JSONObject responseJson = new JSONObject(result);
                String accessToken = responseJson.getString("access_token");

                // Save the access_token to SharedPreferences for later use
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("access_token", accessToken);
                editor.apply();

                // Assuming your API returns a JSON object with a field "success"
//                boolean isSuccess = responseJson.getBoolean("access_token");
//                Log.d("test Response", isSuccess);

//                if (result) {
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
//                } else {
                    // Handle the case where login is not successful
                    // For example, show an error message to the user
//                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error here
            }
        }
    }
}
