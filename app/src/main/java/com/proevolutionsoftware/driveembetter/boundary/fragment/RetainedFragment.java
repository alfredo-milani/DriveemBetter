package com.proevolutionsoftware.driveembetter.boundary.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.ScatterData;
import com.proevolutionsoftware.driveembetter.threads.ChartAsyncTask;

/**
 * Created by alfredo on 28/08/17.
 */

/* Retained Fragment Class*/
public class RetainedFragment extends Fragment {
    private ProgressDialog progressDialog;
    private ChartAsyncTask task;
    private ScatterChart chart;
    private ScatterData data;

    @Override
    /* Called when the activity is starting */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Fragment instance must be retained across Activity re-creation */
        setRetainInstance(true);
    }

    /* Setters */
    public void setData(ScatterData data) {
        this.data = data;
    }

    public void setChart(ScatterChart chart) {
        this.chart = chart;
    }

    public void setTask(ChartAsyncTask task) {
        this.task = task;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    /* Getters */
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public ChartAsyncTask getTask() {
        return task;
    }

    public ScatterChart getChart() {
        return chart;
    }

    public ScatterData getData() {
        return data;
    }
}
