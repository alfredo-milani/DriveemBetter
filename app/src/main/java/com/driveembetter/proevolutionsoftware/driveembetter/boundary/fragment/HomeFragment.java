package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.Speedometer;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Mattia on 10/10/2017.
 */

public class HomeFragment extends Fragment {

    private Speedometer speedometer;
    private ImageView speedLimitSign;
    private double latitude;
    private double longitude;
    private ImageView weatherIcon;
    private TextView positionText, windText, temperatureText, humidityText, visibilityText, speedLimitText, windDirectionText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return init_view(inflater, container);
    }

    private void initResources() {

    }

    private View init_view(LayoutInflater inflater, ViewGroup container) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        speedometer = (Speedometer) view.findViewById(R.id.Speedometer);
        speedLimitSign = (ImageView) view.findViewById(R.id.speed_limit);
        speedLimitText = (TextView) view.findViewById(R.id.speed_limit_text);
        weatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
        windText = (TextView) view.findViewById(R.id.wind_text);
        windDirectionText = (TextView) view.findViewById(R.id.wind_direction_text);
        positionText = (TextView) view.findViewById(R.id.position_text);
        temperatureText = (TextView) view.findViewById(R.id.temperature);
        humidityText = (TextView) view.findViewById(R.id.humidity_text);
        visibilityText = (TextView) view.findViewById(R.id.visibility_text);
        PositionManager.getInstance(getContext()).createTools(view);
        // Set action bar title
        this.getActivity().setTitle(R.string.general);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentState.setFragmentState(FragmentState.HOME_FRAGMENT, true);
        PositionManager.getInstance(getContext()).resetCity();
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences userDetails = getActivity().getApplicationContext().getSharedPreferences("HOME_DETAILS", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.clear();
        edit.putString("POSITION_TEXT", positionText.getText().toString().trim());
        edit.putString("WIND_TEXT", windText.getText().toString().trim());
        edit.putString("TEMP_TEXT", temperatureText.getText().toString().trim());
        edit.putString("HUMIDITY_TEXT", humidityText.getText().toString().trim());
        edit.putString("VISIBILITY_TEXT", visibilityText.getText().toString().trim());
        edit.putString("SPEED_LIMIT_TEXT", speedLimitText.getText().toString().trim());
        edit.putString("WIND_DIRECTION_TEXT", windDirectionText.getText().toString().trim());
        if (weatherIcon != null)
            edit.putInt("WEATHER_ICON", (Integer) weatherIcon.getTag());
        edit.apply();

        FragmentState.setFragmentState(FragmentState.HOME_FRAGMENT, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        FragmentState.setFragmentState(FragmentState.HOME_FRAGMENT, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences userDetails = getActivity().getApplicationContext().getSharedPreferences("HOME_DETAILS", MODE_PRIVATE);

        if (userDetails != null) {
            positionText.setText(userDetails.getString("POSITION_TEXT", ""));
            windText.setText(userDetails.getString("WIND_TEXT", ""));
            temperatureText.setText(userDetails.getString("TEMP_TEXT", ""));
            humidityText.setText(userDetails.getString("HUMIDITY_TEXT", ""));
            visibilityText.setText(userDetails.getString("VISIBILITY_TEXT", ""));
            speedLimitText.setText(userDetails.getString("SPEED_LIMIT_TEXT", ""));
            windDirectionText.setText(userDetails.getString("WIND_DIRECTION_TEXT", ""));
            weatherIcon.setImageResource(userDetails.getInt("WEATHER_ICON", 0));
            weatherIcon.setTag(userDetails.getInt("WEATHER_ICON", 0));

        }
    }

}
