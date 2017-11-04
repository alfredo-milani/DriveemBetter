package com.proevolutionsoftware.driveembetter.boundary.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.utils.FragmentsState;

/**
 * Created by alfredo on 26/08/17.
 */
public class AboutUsFragment extends Fragment {

    private final static String TAG = AboutUsFragment.class.getSimpleName();

    // Widgets
    private View rootView;
    private Context context;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return this.rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initWidgets();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initResources() {
        this.setRetainInstance(true);
    }

    private void initWidgets() {
        // Set action bar title
        ((Activity) this.context).setTitle(R.string.about_us);
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentsState.setFragmentState(FragmentsState.ABOUT_US, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        FragmentsState.setFragmentState(FragmentsState.ABOUT_US, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        FragmentsState.setFragmentState(FragmentsState.ABOUT_US, false);
    }
}
