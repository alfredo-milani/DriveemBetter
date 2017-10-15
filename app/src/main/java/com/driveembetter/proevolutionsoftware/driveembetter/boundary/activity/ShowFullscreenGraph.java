package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by alfredo on 14/10/17.
 */

public class ShowFullscreenGraph extends AppCompatActivity {

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
        setContentView(R.layout.fullscreen_graph_activity);

    }

    private void initResources() {

    }

    private void initWidgets() {

    }
}
