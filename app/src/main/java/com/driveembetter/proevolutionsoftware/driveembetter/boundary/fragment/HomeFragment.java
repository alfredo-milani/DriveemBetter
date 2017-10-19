package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.AddFriendsActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.Speedometer;

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
    private FloatingActionButton fab;


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
        this.fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
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

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addFriendIntent = new Intent(getActivity(), AddFriendsActivity.class);
                getActivity().startActivity(addFriendIntent);
            }
        });

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
            positionText.setText(userDetails.getString("POSITION_TEXT", "N/A"));
            windText.setText(userDetails.getString("WIND_TEXT", "N/A"));
            temperatureText.setText(userDetails.getString("TEMP_TEXT", "N/A"));
            humidityText.setText(userDetails.getString("HUMIDITY_TEXT", "N/A"));
            visibilityText.setText(userDetails.getString("VISIBILITY_TEXT", "N/A"));
            speedLimitText.setText(userDetails.getString("SPEED_LIMIT_TEXT", "N/A"));
            windDirectionText.setText(userDetails.getString("WIND_DIRECTION_TEXT", "N/A"));
            weatherIcon.setImageResource(userDetails.getInt("WEATHER_ICON", R.mipmap.ic_weather_unknown));
            weatherIcon.setTag(userDetails.getInt("WEATHER_ICON", R.mipmap.ic_weather_unknown));

        }
    }

}
