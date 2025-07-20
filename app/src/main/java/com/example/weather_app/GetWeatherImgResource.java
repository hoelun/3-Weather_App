package com.example.weather_app;

import android.util.Pair;

public class GetWeatherImgResource {


    public static Pair <Integer, Integer> getWeatherImg (String icon) {


        switch (icon) {
            case "11d":
                return new Pair<>(R.drawable.storm, R.color.storm);

            case "09d":
            case "10d":
                return new Pair<>(R.drawable.rain, R.color.rain);

            case "13d":
                return new Pair<>(R.drawable.snow, R.color.snow);

            case "50d": // fog
                return new Pair<>(R.drawable.fog, R.color.fog);

            case "01d":
            case "01n":
                return new Pair<>(R.drawable.sun, R.color.sun);

            case "02d":
            case "02n":
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                return new Pair<>(R.drawable.clouds, R.color.clouds);

            default:
                return new Pair<>(R.drawable.clouds, R.color.clouds);
        }
    }
}
