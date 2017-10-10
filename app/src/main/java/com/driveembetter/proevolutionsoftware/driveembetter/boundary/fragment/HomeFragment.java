package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.Speedometer;

import java.util.ArrayList;

/**
 * Created by Mattia on 10/10/2017.
 */

public class HomeFragment extends Fragment {

    private Speedometer speedometer;

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
        PositionManager.getInstance(getContext()).createSpeedometer(view);

        // Set action bar title
        this.getActivity().setTitle(R.string.general);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentState.setFragmentState(FragmentState.HOME_FRAGMENT, true);
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
