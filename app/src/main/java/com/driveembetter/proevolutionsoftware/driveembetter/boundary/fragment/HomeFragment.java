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
    private TextView speedLimitText;
    private double latitude;
    private double longitude;
    private ImageView weatherIcon;

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

}
