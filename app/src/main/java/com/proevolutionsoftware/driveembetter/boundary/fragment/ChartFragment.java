package com.proevolutionsoftware.driveembetter.boundary.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.ScatterChart;
import com.proevolutionsoftware.driveembetter.R;

/**
 * Created by alfredo on 28/08/17.
 */

/* Chart Fragment Class*/
public class ChartFragment extends Fragment {
    private View view;
    private ProgressDialog progressDialog;

    @Override
    /* Called when the activity is starting */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    /* Called after onCreate(Bundle) */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph, container, false);
        return view;
    }

    @Override
    /* Called when a fragment is first attached to its context */
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    /* Called when the fragment is no longer attached to its activity */
    public void onDetach() {
        super.onDetach();
    }

    @Override
    /* Called when the fragment is no longer in use */
    public void onDestroy() {
        super.onDestroy();
    }

    /* Find scatter chart */
    public ScatterChart getChart() {
        return (ScatterChart) view.findViewById(R.id.view);
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}