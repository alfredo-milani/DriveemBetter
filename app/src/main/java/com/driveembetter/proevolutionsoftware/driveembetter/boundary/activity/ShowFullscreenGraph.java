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
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Locale;

import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment.ARG_FRAGMENT_GRAPH;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment.ARG_FRAGMENT_GRAPH_SERIES;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment.GRAPH_ERROR;

/**
 * Created by alfredo on 14/10/17.
 */

public class ShowFullscreenGraph extends AppCompatActivity {

    private final static String TAG = ShowFullscreenGraph.class.getSimpleName();

    // Resources
    private int typeGraph;
    private LineGraphSeries<DataPoint> series;

    // Widgets
    private GraphView graphView;
    private TextView unavailableData;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_graph_activity);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.typeGraph = this.getIntent().getIntExtra(ARG_FRAGMENT_GRAPH, GRAPH_ERROR);
        this.series = this.getSeriesFromDouble(
                this.getIntent().getDoubleArrayExtra(ARG_FRAGMENT_GRAPH_SERIES)
        );
        Log.e(TAG, "T: "  + typeGraph);
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

    private void initWidgets() {
        this.graphView = findViewById(R.id.graph);
        this.unavailableData = findViewById(R.id.unavailable_data);

        // Set action bar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.setTitleSerie();
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.initGraph();
    }

    private void initGraph() {
        this.graphView.getLegendRenderer().setVisible(true);
        this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
        this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        if (this.typeGraph != GRAPH_ERROR && this.series != null) {
            this.graphView.addSeries(this.series);
        } else {
            this.unavailableData.setVisibility(View.VISIBLE);
        }
    }

    private void setTitleSerie() {
        switch (this.typeGraph) {
            case PageFragment.VELOCITY_GRAPH_DAILY:
                this.setTitle(R.string.velocity_daily_graph);
                this.series.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.velocity),
                        getString(R.string.vel_mu)
                ));

                // Adding axis titles
                this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
                // Setting color graphSeries
                this.series.setColor(ContextCompat.getColor(this, R.color.blue_700));
                break;

            case PageFragment.VELOCITY_GRAPH_WEEKLY:
                this.setTitle(R.string.velocity_weekly_graph);
                this.series.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.velocity),
                        getString(R.string.vel_mu)
                ));

                // Adding axis titles
                this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
                // Setting color graphSeries
                this.series.setColor(ContextCompat.getColor(this, R.color.blue_700));
                break;

            case PageFragment.ACCELERATION_GRAPH_DAILY:
                this.setTitle(R.string.acceleration_daily_graph);
                this.series.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.acceleration),
                        getString(R.string.acc_mu)
                ));

                // Adding axis titles
                this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
                // Setting color graphSeries
                this.series.setColor(ContextCompat.getColor(this, R.color.red_700));
                break;

            case PageFragment.ACCELERATION_GRAPH_WEEKLY:
                this.setTitle(R.string.acceleration_weekly_graph);
                this.series.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.acceleration),
                        getString(R.string.acc_mu)
                ));

                // Adding axis titles
                this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
                // Setting color graphSeries
                this.series.setColor(ContextCompat.getColor(this, R.color.red_700));
                break;

            case PageFragment.FEEDBACK_GRAPH:
                this.setTitle(R.string.feedback_graph);
                this.series.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.feedback),
                        getString(R.string.feedback_scale)
                ));

                // Adding axis titles
                this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.date));
                // Setting color graphSeries
                this.series.setColor(ContextCompat.getColor(this, R.color.green_700));
                break;

            case PageFragment.POINTS_GRAPH:
                this.setTitle(R.string.points_graph);
                this.series.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s",
                        getString(R.string.points)
                ));
                break;
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
