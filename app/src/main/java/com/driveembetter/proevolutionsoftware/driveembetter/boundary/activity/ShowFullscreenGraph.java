package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by alfredo on 14/10/17.
 */

public class ShowFullscreenGraph extends Fragment {

    private final static String TAG = ShowFullscreenGraph.class.getSimpleName();

    // Resources
    private MeanDay data;
    private LineGraphSeries<DataPoint> velocitySeries;
    private LineGraphSeries<DataPoint> accelerationSeries;

    // Widgets
    private GraphView graphView;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
    }

    private void initWidgets() {
    }
}
