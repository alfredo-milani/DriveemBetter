package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingGraphFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Locale;

import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingGraphFragment.ARG_FRAGMENT_GRAPH;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingGraphFragment.ARG_FRAGMENT_GRAPH_LAST_UPDATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingGraphFragment.ARG_FRAGMENT_GRAPH_SERIES;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingGraphFragment.GRAPH_ERROR;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.HOURS;

/**
 * Created by alfredo on 14/10/17.
 */

public class ShowFullscreenGraph extends AppCompatActivity
        implements View.OnClickListener {

    private final static String TAG = ShowFullscreenGraph.class.getSimpleName();

    private final static int MAX_LABEL_FEEDBACK_GRAPH = 3;

    // Resources
    private int typeGraph;
    private LineGraphSeries<DataPoint> graphSeries;
    private String lastUpdate;

    // Widgets
    private GraphView graphView;
    private TextView unavailableData;
    private TextView title;
    private TextView subTitle;
    private ImageButton backArrow;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_graph);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.typeGraph = this.getIntent().getIntExtra(ARG_FRAGMENT_GRAPH, GRAPH_ERROR);

        this.graphSeries = this.getSeriesFromDouble(
                this.getIntent().getDoubleArrayExtra(ARG_FRAGMENT_GRAPH_SERIES)
        );

        this.lastUpdate = this.getIntent().getStringExtra(ARG_FRAGMENT_GRAPH_LAST_UPDATE);
    }

    private void initWidgets() {
        this.graphView = findViewById(R.id.graph);
        this.unavailableData = findViewById(R.id.unavailableData);
        this.title = findViewById(R.id.titleGraph);
        this.subTitle = findViewById(R.id.subTitleGraph);
        if (this.lastUpdate != null) {
            this.subTitle.setText(this.lastUpdate);
        }
        this.backArrow = findViewById(R.id.backArrow);

        this.backArrow.setOnClickListener(this);

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
            case RankingGraphFragment.VELOCITY_GRAPH_DAILY:
                string = getString(R.string.velocity_daily_graph);
                this.setGraphHorizontalScale(4, 0, HOURS - 1);
                this.initGraphVelocity();
                break;

            case RankingGraphFragment.VELOCITY_GRAPH_WEEKLY:
                string = getString(R.string.velocity_weekly_graph);
                this.setGraphHorizontalScale(Calendar.DAY_OF_WEEK, 1, Calendar.DAY_OF_WEEK);
                this.initGraphVelocity();
                break;

            case RankingGraphFragment.ACCELERATION_GRAPH_DAILY:
                string = getString(R.string.acceleration_daily_graph);
                this.setGraphHorizontalScale(4, 0, HOURS - 1);
                this.initGraphAcceleration();
                break;

            case RankingGraphFragment.ACCELERATION_GRAPH_WEEKLY:
                string = getString(R.string.acceleration_weekly_graph);
                this.setGraphHorizontalScale(Calendar.DAY_OF_WEEK, 1, Calendar.DAY_OF_WEEK);
                this.initGraphAcceleration();
                break;

            case RankingGraphFragment.FEEDBACK_GRAPH:
                string = getString(R.string.feedback_graph);
                this.setGraphHorizontalScale(MAX_LABEL_FEEDBACK_GRAPH, -1, -1);
                this.initGraphFeedback();
                break;

            case RankingGraphFragment.POINTS_GRAPH:
                string = getString(R.string.points_graph);
                this.initGraphPoints();
                break;
        }

        this.title.setText(string);
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
        // Set graph dot
        this.graphSeries.setDrawDataPoints(true);
        this.graphSeries.setDataPointsRadius(5);
        //
        this.graphSeries.setDrawAsPath(true);
        // Setting line width
        this.graphSeries.setThickness(3);

        switch (this.typeGraph) {
            case RankingGraphFragment.VELOCITY_GRAPH_DAILY:
            case RankingGraphFragment.VELOCITY_GRAPH_WEEKLY:
                this.graphSeries.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.velocity),
                        getString(R.string.vel_mu)
                ));
                // Setting color graphSeries
                this.graphSeries.setColor(ContextCompat.getColor(this, R.color.blue_700));
                break;

            case RankingGraphFragment.ACCELERATION_GRAPH_DAILY:
            case RankingGraphFragment.ACCELERATION_GRAPH_WEEKLY:
                this.graphSeries.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.acceleration),
                        getString(R.string.acc_mu)
                ));
                // Setting color graphSeries
                this.graphSeries.setColor(ContextCompat.getColor(this, R.color.red_700));
                break;

            case RankingGraphFragment.FEEDBACK_GRAPH:
                this.graphSeries.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.feedback),
                        getString(R.string.feedback_scale)
                ));
                // Setting color graphSeries
                this.graphSeries.setColor(ContextCompat.getColor(this, R.color.green_700));
                break;

            case RankingGraphFragment.POINTS_GRAPH:
                // TODO: 18/10/17
                break;
        }
    }

    private void initBaseGraph() {
        // Legend
        this.graphView.getLegendRenderer().setVisible(true);
        this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
        this.graphView.getLegendRenderer().setFixedPosition(0, 0);
        // this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        // Enable scaling
        this.graphView.getViewport().setScalable(true);
        this.graphView.getViewport().setScalableY(true);
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.backArrow:
                this.onBackPressed();
                break;
        }
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
}
