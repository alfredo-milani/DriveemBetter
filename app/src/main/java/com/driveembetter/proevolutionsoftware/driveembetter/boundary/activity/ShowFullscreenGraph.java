package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Locale;

import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment.ARG_FRAGMENT_GRAPH;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment.ARG_FRAGMENT_GRAPH_SERIES;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment.GRAPH_ERROR;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.HOURS;

/**
 * Created by alfredo on 14/10/17.
 */

public class ShowFullscreenGraph extends AppCompatActivity {

    private final static String TAG = ShowFullscreenGraph.class.getSimpleName();

    private final static int MAX_LABEL_FEEDBACK_GRAPH = 3;

    // Resources
    private int typeGraph;
    private LineGraphSeries<DataPoint> graphSeries;

    // Widgets
    private GraphView graphView;
    private TextView unavailableData;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_graph_activity);

        Log.d(TAG, "ONCREATE");
        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.typeGraph = this.getIntent().getIntExtra(ARG_FRAGMENT_GRAPH, GRAPH_ERROR);
        this.graphSeries = this.getSeriesFromDouble(
                this.getIntent().getDoubleArrayExtra(ARG_FRAGMENT_GRAPH_SERIES)
        );
        Log.e(TAG, "T: "  + typeGraph);
    }

    private void initWidgets() {
        this.graphView = findViewById(R.id.graph);
        this.unavailableData = findViewById(R.id.unavailableData);

        // Set action bar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.initGraphView();
    }

    @Nullable
    private LineGraphSeries<DataPoint> getSeriesFromDouble(double[] values) {
        if (values.length == 0) {
            return null;
        }

        DataPoint[] dataPoints = new DataPoint[values.length];
        for (int i = 0; i < values.length; ++i) {
            dataPoints[i] = new DataPoint(i, values[i]);
        }

        return new LineGraphSeries<>(dataPoints);
    }

    private void initGraphView() {
        String string = getString(R.string.app_name);
        switch (this.typeGraph) {
            case PageFragment.VELOCITY_GRAPH_DAILY:
                string = getString(R.string.velocity_daily_graph);
                this.setGraphHorizontalScale(4, 0, HOURS - 1);
                this.initGraphVelocity();
                break;

            case PageFragment.VELOCITY_GRAPH_WEEKLY:
                string = getString(R.string.velocity_weekly_graph);
                this.setGraphHorizontalScale(Calendar.WEEK_OF_MONTH, 1, Calendar.WEEK_OF_MONTH);
                this.initGraphVelocity();
                break;

            case PageFragment.ACCELERATION_GRAPH_DAILY:
                string = getString(R.string.acceleration_daily_graph);
                this.setGraphHorizontalScale(4, 0, HOURS - 1);
                this.initGraphAcceleration();
                break;

            case PageFragment.ACCELERATION_GRAPH_WEEKLY:
                string = getString(R.string.acceleration_weekly_graph);
                this.setGraphHorizontalScale(Calendar.WEEK_OF_MONTH, 1, Calendar.WEEK_OF_MONTH);
                this.initGraphAcceleration();
                break;

            case PageFragment.FEEDBACK_GRAPH:
                string = getString(R.string.feedback_graph);
                this.setGraphHorizontalScale(MAX_LABEL_FEEDBACK_GRAPH, -1, -1);
                this.initGraphFeedback();
                break;

            case PageFragment.POINTS_GRAPH:
                string = getString(R.string.points_graph);
                this.initGraphPoints();
                break;
        }

        this.setTitle(string);
    }

    private void setGraphHorizontalScale(int numberOfLabels, long minVal, long maxVal) {
        this.graphView.getViewport().setXAxisBoundsManual(true);
        if (numberOfLabels >= 0) {
            this.graphView.getGridLabelRenderer().setNumHorizontalLabels(numberOfLabels);
        }
        if (minVal >= 0) {
            this.graphView.getViewport().setMinX(minVal);
        }
        if (maxVal >= 0) {
            this.graphView.getViewport().setMaxX(maxVal);
        }
    }

    private void initGraphSeries() {
        this.graphView.removeAllSeries();
        //
        this.graphSeries.setDrawAsPath(true);
        // Setting line width
        this.graphSeries.setThickness(3);

        switch (this.typeGraph) {
            case PageFragment.VELOCITY_GRAPH_DAILY:
            case PageFragment.VELOCITY_GRAPH_WEEKLY:
                this.graphSeries.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.velocity),
                        getString(R.string.vel_mu)
                ));
                // Setting color graphSeries
                this.graphSeries.setColor(ContextCompat.getColor(this, R.color.blue_700));
                break;

            case PageFragment.ACCELERATION_GRAPH_DAILY:
            case PageFragment.ACCELERATION_GRAPH_WEEKLY:
                this.graphSeries.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.acceleration),
                        getString(R.string.acc_mu)
                ));
                // Setting color graphSeries
                this.graphSeries.setColor(ContextCompat.getColor(this, R.color.red_700));
                break;

            case PageFragment.FEEDBACK_GRAPH:
                this.graphSeries.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.feedback),
                        getString(R.string.feedback_scale)
                ));
                // Setting color graphSeries
                this.graphSeries.setColor(ContextCompat.getColor(this, R.color.green_700));
                break;

            case PageFragment.POINTS_GRAPH:
                // TODO: 18/10/17
                break;
        }
    }

    private void initBaseGraph() {
        Log.d(TAG, "BASE GRAPH");
        // Legend
        this.graphView.getLegendRenderer().setVisible(true);
        this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
        this.graphView.getLegendRenderer().setFixedPosition(0, 0);
        // this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private void initGraphVelocity() {
        this.initBaseGraph();
        // Adding axis titles
        // this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.vel_mu));
        this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
    }

    private void initGraphAcceleration() {
        this.initBaseGraph();
        // Adding axis titles
        // this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.acc_mu));
        this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
    }

    private void initGraphFeedback() {
        this.initBaseGraph();
        // Adding axis titles
        // this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.acc_mu));
        this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.date));

        // Set date label formatter
        this.graphView.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(this)
        );

        // As we use dates as labels, the human rounding to nice readable numbers is not necessary
        this.graphView.getGridLabelRenderer().setHumanRounding(false);
    }

    private void initGraphPoints() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        this.initGraphSeries();
        this.updateUI();
    }


    public void updateUI() {
        if (this.graphSeries != null) {
            if (this.unavailableData.getVisibility() == View.VISIBLE) {
                this.unavailableData.setVisibility(View.GONE);
            }
            this.graphView.addSeries(this.graphSeries);
        } else {
            this.unavailableData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
