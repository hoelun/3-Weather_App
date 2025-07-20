package com.example.weather_app;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText user_field;
    private TextView result_info;
    private Button main_btn;
    private static final String API_KEY = "...";
    private ImageView weatherImg;
    private RelativeLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

       ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);
        weatherImg = findViewById(R.id.weather_image);
        mainLayout = findViewById(R.id.main);

        main_btn.setOnClickListener(v -> {
            String city = user_field.getText().toString().trim();

            if (city.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.empty_field, Toast.LENGTH_LONG).show();
                return;
            }

            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=ru",
                    city,
                    API_KEY
            );

            new GetURLData().execute(url);
        });
    }

    private class GetURLData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText(R.string.loading);
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    return response.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null) {
                result_info.setText(R.string.error_name);
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
//
                int cod = jsonObject.getInt("cod");
//
                if (cod == 200) {
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    int humidity = main.getInt("humidity");

                    JSONObject wind = jsonObject.getJSONObject("wind");
                    double speed = wind.getDouble("speed");

                    String description = jsonObject.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description");

                    result_info.setText(String.format(
                            "Температура: %.1f°C\nПогода: %s\nВлажность: %d%%\nВетер: %.1f м/с",
                            temp,
                            description,
                            humidity,
                            speed
                    ));

                    String icon = jsonObject.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("icon");

//
                    Pair<Integer, Integer> weatherData = GetWeatherImgResource.getWeatherImg(icon);

                    weatherImg.setImageResource(weatherData.first);
                    mainLayout.setBackgroundColor(getResources().getColor(weatherData.second));

                } else {
                    String errorMessage = jsonObject.optString("message", "Неизвестная ошибка");
                    result_info.setText("Ошибка: " + errorMessage);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                result_info.setText(R.string.error);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                result_info.setText(R.string.error);
            }
        }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        result_info.setText(R.string.request_cancelled);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
