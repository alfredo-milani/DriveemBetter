package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.Converter;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.NumberManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mattia on 11/10/2017.
 */

public class YahooWeatherParser extends AsyncTask<String, String, String[]> {

    private ImageView weatherIcon;
    private TextView windText, windDirectionText, temperatureText, humidityText, visibilityText;

    public YahooWeatherParser(ImageView weatherIcon, TextView windText, TextView windDirectionText,
                              TextView temperatureText, TextView humidityText, TextView visibilityText) {
        this.weatherIcon = weatherIcon;
        this.windText = windText;
        this.windDirectionText = windDirectionText;
        this.temperatureText = temperatureText;
        this.humidityText = humidityText;
        this.visibilityText = visibilityText;
    }

    @Override
    protected String[] doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + strings[0] + "%2C%20" + strings[1] + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
                .build();

        Response response = null;
        String[] weatherData = new String[6];
        try {
            response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject jsonResult = new JSONObject(jsonData);
            JSONObject channel = jsonResult.getJSONObject("query")
                    .getJSONObject("results")
                    .getJSONObject("channel");
            JSONObject wind = channel.getJSONObject("wind");
            JSONObject atmosphere = channel.getJSONObject("atmosphere");
            JSONObject astronomy = channel.getJSONObject("astronomy");
            JSONObject condition = channel.getJSONObject("item")
                    .getJSONObject("condition");

            Double windSpeedMph = wind.getDouble("speed");
            int windDirectionInDegrees = wind.getInt("direction");
            Double temperatureFahrenheit = condition.getDouble("temp");
            Double visibilityInMiles = atmosphere.getDouble("visibility"); //m


            //WIND DATA
            Double windSpeedKmh = Converter.convertMphToKmh(windSpeedMph);
            String windDirectionCardinal = Converter.convertDegreeToCardinalDirection(windDirectionInDegrees);

            //ATMOSPHERE DATA
            int humidity = atmosphere.getInt("humidity");
            Double visibilityInKm = NumberManager.round(Converter.convertMilesToKm(visibilityInMiles), 3);

            //CONDITION DATA
            Double temperatureCelsius = Converter.convertFahrenheitToCelsius(temperatureFahrenheit);
            int weather = condition.getInt("code");

            weatherData[0] = String.valueOf(windSpeedKmh);
            weatherData[1] = windDirectionCardinal;
            weatherData[2] = String.valueOf(humidity);
            weatherData[3] = String.valueOf(visibilityInKm);
            weatherData[4] = String.valueOf(temperatureCelsius);
            weatherData[5] = String.valueOf(weather);

            Log.e("YAHOO WEATHER", "wind speed and direction: " + windSpeedKmh + "  " + windDirectionCardinal + "  " +
            "humidity: " + humidity + " visibility: " + visibilityInKm + " temperature: " + temperatureCelsius + "  weather: " + weather);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return weatherData;
    }

    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
        if (s != null) {
            setWeatherIcon(s[5]);
            windText.setText((int) Double.parseDouble(s[0]) + " km/h");
            windDirectionText.setText(s[1]);
            Double temperature = NumberManager.round(Double.parseDouble(s[4]), 1);
            temperatureText.setText(temperature.toString() + " °C");
            humidityText.setText(s[2] + " %");
            visibilityText.setText(s[3] + " km");
        }
    }

    private void setWeatherIcon(String weather) {
        weatherIcon.setImageResource(0);
        weatherIcon.setTag(0);
        Integer code = Integer.parseInt(weather);
        switch (code) {
            case 0:
                weatherIcon.setImageResource(R.mipmap.ic_weather_tornado);
                weatherIcon.setTag(R.mipmap.ic_weather_tornado);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 37:
            case 38:
            case 39:
            case 45:
            case 47:
                weatherIcon.setImageResource(R.mipmap.ic_weather_thunderstorms);
                weatherIcon.setTag(R.mipmap.ic_weather_thunderstorms);
                break;
            case 5:
            case 6:
            case 7:
            case 18:
                weatherIcon.setImageResource(R.mipmap.ic_weather_sleet);
                weatherIcon.setTag(R.mipmap.ic_weather_sleet);
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
                weatherIcon.setImageResource(R.mipmap.ic_weather_rain);
                weatherIcon.setTag(R.mipmap.ic_weather_rain);
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 40:
            case 43:
            case 46:
                weatherIcon.setImageResource(R.mipmap.ic_weather_snow);
                weatherIcon.setTag(R.mipmap.ic_weather_snow);
                break;
            case 19:
            case 20:
            case 21:
            case 22:
                weatherIcon.setImageResource(R.mipmap.ic_weather_fog);
                weatherIcon.setTag(R.mipmap.ic_weather_fog);
                break;
            case 27:
            case 29:
            case 44:
                weatherIcon.setImageResource(R.mipmap.ic_weather_cloudy_night);
                weatherIcon.setTag(R.mipmap.ic_weather_cloudy_night);
                break;
            case 26:
                weatherIcon.setImageResource(R.mipmap.ic_weather_cloudy);
                weatherIcon.setTag(R.mipmap.ic_weather_cloudy);
                break;
            case 28:
                weatherIcon.setImageResource(R.mipmap.ic_weather_scattered_clouds);
                weatherIcon.setTag(R.mipmap.ic_weather_scattered_clouds);
                break;
            case 30:
                weatherIcon.setImageResource(R.mipmap.ic_weather_partly_sunny);
                weatherIcon.setTag(R.mipmap.ic_weather_partly_sunny);
                break;
            case 31:
            case 33:
                weatherIcon.setImageResource(R.mipmap.ic_weather_clear_night);
                weatherIcon.setTag(R.mipmap.ic_weather_clear_night);
                break;
            case 32:
            case 34:
                weatherIcon.setImageResource(R.mipmap.ic_weather_clear);
                weatherIcon.setTag(R.mipmap.ic_weather_clear);
                break;
            default:
                weatherIcon.setImageResource(R.mipmap.ic_weather_unknown);
                weatherIcon.setTag(R.mipmap.ic_weather_unknown);

        }
    }

}