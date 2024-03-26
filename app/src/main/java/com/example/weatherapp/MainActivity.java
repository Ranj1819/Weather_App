package com.example.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.R;
import com.example.weatherapp.models.WeatherResponse;
import com.example.weatherapp.models.WeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = ""; // Base url of an api site
    private static final String API_KEY = ""; // API KEY
    TextView textView, showPlace;
    Button getButton;
    EditText getPlaceName;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.showWeather);
        getButton=findViewById(R.id.getWeather);
        showPlace=findViewById(R.id.showPlace);
        getPlaceName=findViewById(R.id.placeName);
        progressBar=findViewById(R.id.progress_bar);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // passing base url
                .addConverterFactory(GsonConverterFactory.create()) // Converts json responses into java objects
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);


        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                // getCurrentWeather with the desired city name, API key, and units.
                Call<WeatherResponse> call= weatherService
                        .getCurrentWeather(getPlaceName.getText().toString(), API_KEY, "metric");
                // enqueue for handling request asynchronously
                call.enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful()) {
                            WeatherResponse weatherResponse = response.body();
                            String weatherDescription = weatherResponse.getWeather().get(0).getDescription();
                            double temperature = weatherResponse.getMain().getTemp();

                            String text=weatherDescription +temperature + "°C";
                            showPlace.setText(getPlaceName.getText().toString());
                            textView.setText(text);

                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            Log.e("RetrofitWeather", "Error fetching weather data: " + response.message());
                            textView.setText(response.message());

                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        Log.e("RetrofitWeather", "Network error: " + t.getMessage());
                        textView.setText(t.getMessage());

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }
}
