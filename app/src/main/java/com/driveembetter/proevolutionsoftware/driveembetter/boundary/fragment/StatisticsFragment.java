package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.driveembetter.proevolutionsoftware.driveembetter.threads.ChartAsyncTask;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.ScatterData;

/**
 * Created by alfredo on 28/08/17.
 */

/* Retained Fragment Class*/
public class StatisticsFragment extends Fragment
        implements TaskProgressInterface {

    private final static String TAG = StatisticsFragment.class.getSimpleName();

    // Widgets
    private View rootView;
    private ScatterChart chart;
    private ProgressBar progressBar;

    // Resources
    private ScatterData data;



    @Override
    /* Called when the activity is starting */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Fragment instance must be retained across Activity re-creation */
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return this.rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initWidgets();
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Create a new async task */
        new ChartAsyncTask(StatisticsFragment.this).execute("", "0", "0");
    }

    private void initWidgets() {
        this.chart = this.rootView.findViewById(R.id.graph_view);
        this.progressBar = this.getActivity().findViewById(R.id.progress_bar);
    }

    @Override
    public void hideProgress() {
        if (this.progressBar.getVisibility() == View.VISIBLE) {
            this.progressBar.setIndeterminate(true);
            this.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showProgress() {
        if (this.progressBar.getVisibility() == View.GONE) {
            this.progressBar.setIndeterminate(true);
            this.progressBar.setVisibility(View.VISIBLE);
        }
    }

    public ScatterChart getChart() {
        return chart;
    }

    public void setChart(ScatterChart chart) {
        this.chart = chart;
    }

    public void setData(ScatterData data) {
        this.data = data;
    }

    public ScatterData getData() {
        return data;
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentState.setFragmentState(FragmentState.STATISTICS_FRAGMENT, false);
    }

    @Override
    public void onPause() {
        super.onPause();

        FragmentState.setFragmentState(FragmentState.STATISTICS_FRAGMENT, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        FragmentState.setFragmentState(FragmentState.STATISTICS_FRAGMENT, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FragmentState.setFragmentState(FragmentState.STATISTICS_FRAGMENT, false);
    }
}
