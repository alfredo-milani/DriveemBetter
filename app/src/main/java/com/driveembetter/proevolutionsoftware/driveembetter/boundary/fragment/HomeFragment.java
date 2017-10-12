package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

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

        if (savedInstanceState != null) {
            //RESTORE TEXT VIEW STATE
            positionText.setText(savedInstanceState.getString("POSITION_TEXT"));
            windText.setText(savedInstanceState.getString("WIND_TEXT"));
            temperatureText.setText(savedInstanceState.getString("TEMP_TEXT"));
            humidityText.setText(savedInstanceState.getString("HUMIDITY_TEXT"));
            visibilityText.setText(savedInstanceState.getString("VISIBILITY_TEXT"));
            speedLimitText.setText(savedInstanceState.getString("SPEED_LIMIT_TEXT"));
            windDirectionText.setText(savedInstanceState.getString("WIND_DIRECTION_TEXT"));
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //SAVE TEXT VIEW STATE

        //    private TextView positionText, windText, temperatureText, humidityText, visibilityText, speedLimitText, windDirectionText;

        outState.putString("POSITION_TEXT", positionText.getText().toString());
        outState.putString("WIND_TEXT", windText.getText().toString());
        outState.putString("TEMP_TEXT", temperatureText.getText().toString());
        outState.putString("HUMIDITY_TEXT", humidityText.getText().toString());
        outState.putString("VISIBILITY_TEXT", visibilityText.getText().toString());
        outState.putString("SPEED_LIMIT_TEXT", speedLimitText.getText().toString());
        outState.putString("WIND_DIRECTION_TEXT", windDirectionText.getText().toString());


    }

}
