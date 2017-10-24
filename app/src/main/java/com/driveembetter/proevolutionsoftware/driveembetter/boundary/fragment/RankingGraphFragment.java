package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.ShowFullscreenGraph;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanWeek;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.DAY_MS;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.HOURS;

/**
 * Created by alfredo on 15/10/17.
 */

public class RankingGraphFragment extends Fragment
        implements TaskProgressInterface,
        FirebaseDatabaseManager.RetrieveDataDB,
        View.OnClickListener {

    private final static String TAG = RankingGraphFragment.class.getSimpleName();

    public final static String ARG_FRAGMENT_GRAPH = "fragmentGraphType";
    public final static String ARG_FRAGMENT_GRAPH_SERIES = "fragmentGraphSeries";
    public final static String ARG_FRAGMENT_GRAPH_LAST_UPDATE = "fragmentGraphLastUpdate";
    public final static int GRAPH_ERROR = -1;
    public final static int VELOCITY_GRAPH_DAILY = 1;
    public final static int ACCELERATION_GRAPH_DAILY = 2;
    public final static int VELOCITY_GRAPH_WEEKLY = 3;
    public final static int ACCELERATION_GRAPH_WEEKLY = 4;
    public final static int FEEDBACK_GRAPH = 5;
    public final static int POINTS_GRAPH = 6;

    private final static int MAX_LABEL_FEEDBACK_GRAPH = 3;

    // Widgets
    private View rootView;
    private GraphView graphView;
    private ProgressBar progressBar;
    private TextView unavailableData;
    private TextView titleGraph;
    private TextView subTitleGraph;
    private ImageButton fullscreenGraph;
    private ImageButton refreshGraph;

    // Resources
    private final static String formatData = "dd-MM-yyyy HH:mm:ss";
    private SimpleDateFormat simpleDateFormat;
    private static String userID;
    private int typeGraph;
    private LineGraphSeries<DataPoint> graphSeries;

    public static void setUserID(String userID) {
        RankingGraphFragment.userID = userID;
    }

    public static RankingGraphFragment newInstance(int type) {
        RankingGraphFragment fragment = new RankingGraphFragment();
        Bundle args = new Bundle();
        args.putInt(RankingGraphFragment.ARG_FRAGMENT_GRAPH, type);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.initResources();
        }
    }

    private void initResources() {
        this.simpleDateFormat = new SimpleDateFormat(
                RankingGraphFragment.formatData,
                Locale.getDefault()
        );
        this.typeGraph = this.getArguments().getInt(
                RankingGraphFragment.ARG_FRAGMENT_GRAPH,
                RankingGraphFragment.GRAPH_ERROR
        );
    }

    private void initWidgets() {
        this.graphView = this.rootView.findViewById(R.id.graph);
        this.progressBar = this.rootView.findViewById(R.id.progress_bar);
        this.unavailableData = this.rootView.findViewById(R.id.unavailable_data);
        this.titleGraph = this.rootView.findViewById(R.id.titleGraph);
        this.subTitleGraph = this.rootView.findViewById(R.id.subTitleGraph);
        this.fullscreenGraph = this.rootView.findViewById(R.id.fullscreenImageButton);
        this.refreshGraph = this.rootView.findViewById(R.id.refreshGraph);

        this.fullscreenGraph.setOnClickListener(this);
        this.refreshGraph.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pageview_graph_ranking, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            this.rootView = view;
            this.initWidgets();
            this.initGraphView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        this.fillGraph();
    }

    private void fillGraph() {
        this.graphView.removeAllSeries();
        this.retrieveGraphData();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fullscreenImageButton:
                this.startFullscreenGraph();
                break;

            case R.id.refreshGraph:
                this.refreshGraphData();
                break;
        }
    }

    private void refreshGraphData() {
        Toast.makeText(
                this.getActivity(),
                getString(R.string.updating_graph_data),
                Toast.LENGTH_SHORT
        ).show();

        this.fillGraph();
    }

    private void startFullscreenGraph() {
        Log.e(TAG, "GGG: " + typeGraph);
        Intent fullscreenIntent = new Intent(this.getActivity(), ShowFullscreenGraph.class);
        fullscreenIntent.putExtra(ARG_FRAGMENT_GRAPH, this.typeGraph);

        if (this.graphSeries != null) {
            Iterator<DataPoint> dataPoint = this.graphSeries.getValues(
                    this.graphSeries.getLowestValueX(),
                    this.graphSeries.getHighestValueX()
            );

            int i = 0; double[] values = new double[(int) this.graphSeries.getHighestValueX() + 1];
            while (dataPoint.hasNext()) {
                DataPoint point = dataPoint.next();
                values[i] = point.getY();
                ++i;
            }

            fullscreenIntent.putExtra(ARG_FRAGMENT_GRAPH_SERIES, values);
        }

        String lastUpdate = this.subTitleGraph.getText().toString();
        fullscreenIntent.putExtra(ARG_FRAGMENT_GRAPH_LAST_UPDATE, lastUpdate);

        this.startActivity(fullscreenIntent);
    }

    private void initGraphView() {
        Log.d(TAG, "EEE: " + typeGraph);
        switch (this.typeGraph) {
            case RankingGraphFragment.VELOCITY_GRAPH_DAILY:
                this.setGraphHorizontalScale(-1, 0, HOURS - 1);
                this.initGraphVelocity();
                break;

            case RankingGraphFragment.VELOCITY_GRAPH_WEEKLY:
                this.setGraphHorizontalScale(Calendar.WEEK_OF_MONTH + 1, 1, Calendar.WEEK_OF_MONTH + 1);
                this.initGraphVelocity();
                break;

            case RankingGraphFragment.ACCELERATION_GRAPH_DAILY:
                this.setGraphHorizontalScale(-1, 0, HOURS - 1);
                this.initGraphAcceleration();
                break;

            case RankingGraphFragment.ACCELERATION_GRAPH_WEEKLY:
                this.setGraphHorizontalScale(Calendar.WEEK_OF_MONTH + 1, 1, Calendar.WEEK_OF_MONTH + 1);
                this.initGraphAcceleration();
                break;

            case RankingGraphFragment.FEEDBACK_GRAPH:
                this.setGraphHorizontalScale(MAX_LABEL_FEEDBACK_GRAPH, -1, -1);
                this.initGraphFeedback();
                break;

            case RankingGraphFragment.POINTS_GRAPH:
                this.initGraphPoints();
                break;
        }
    }

    private void retrieveGraphData() {
        Log.d(TAG, "TEST: " + this.typeGraph);
        this.showProgress();

        switch (this.typeGraph) {
            case RankingGraphFragment.VELOCITY_GRAPH_DAILY:
                FirebaseDatabaseManager.retrieveDailyData(
                        this,
                        VELOCITY_GRAPH_DAILY,
                        RankingGraphFragment.userID
                );
                break;

            case RankingGraphFragment.VELOCITY_GRAPH_WEEKLY:
                FirebaseDatabaseManager.retrieveWeeklyData(
                        this,
                        VELOCITY_GRAPH_WEEKLY,
                        RankingGraphFragment.userID
                );
                break;

            case RankingGraphFragment.ACCELERATION_GRAPH_DAILY:
                FirebaseDatabaseManager.retrieveDailyData(
                        this,
                        ACCELERATION_GRAPH_DAILY,
                        RankingGraphFragment.userID
                );
                break;

            case RankingGraphFragment.ACCELERATION_GRAPH_WEEKLY:
                FirebaseDatabaseManager.retrieveWeeklyData(
                        this,
                        ACCELERATION_GRAPH_WEEKLY,
                        RankingGraphFragment.userID
                );
                break;

            case RankingGraphFragment.FEEDBACK_GRAPH:
                FirebaseDatabaseManager.retrieveFeedbackHistory(
                        this,
                        RankingGraphFragment.userID
                );
                break;

            case RankingGraphFragment.POINTS_GRAPH:
                // TODO: 17/10/17
                break;
        }
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
                this.graphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.blue_700));
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
                this.graphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.red_700));
                break;

            case RankingGraphFragment.FEEDBACK_GRAPH:
                this.graphSeries.setTitle(String.format(
                        Locale.ENGLISH,
                        "%s (%s)",
                        getString(R.string.feedback),
                        getString(R.string.feedback_scale)
                ));
                // Setting color graphSeries
                this.graphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.green_700));
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
                new DateAsXAxisLabelFormatter(this.getContext())
        );

        // As we use dates as labels, the human rounding to nice readable numbers is not necessary
        this.graphView.getGridLabelRenderer().setHumanRounding(false);
    }

    private void initGraphPoints() {

    }

    @Override
    public void hideProgress() {
        if (this.progressBar.getVisibility() == View.VISIBLE) {
            this.progressBar.setIndeterminate(true);
            this.progressBar.setVisibility(View.INVISIBLE);
            this.fullscreenGraph.setClickable(true);
        }
    }

    @Override
    public void showProgress() {
        if (this.progressBar.getVisibility() == View.GONE ||
                this.progressBar.getVisibility() == View.INVISIBLE) {
            this.progressBar.setIndeterminate(true);
            this.progressBar.setVisibility(View.VISIBLE);
            this.fullscreenGraph.setClickable(false);
        }
    }

    @Override
    public void onDailyVelocityReceived(MeanDay meanDay) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanDay == null || meanDay.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.updateUI(
                    null,
                    getString(R.string.velocity_daily_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            DataPoint[] dataPoints = new DataPoint[HOURS];
            for (int i = 0; i < HOURS; ++i) {
                if (meanDay.getMap().get(i) != null) {
                    float sampleSumVelocity = meanDay.getMap().get(i).getSampleSumVelocity();
                    int sampleSizeVelocity = meanDay.getMap().get(i).getSampleSizeVelocity();

                    dataPoints[i] = new DataPoint(i, sampleSumVelocity / sampleSizeVelocity);
                } else {
                    dataPoints[i] = new DataPoint(i, 0);
                }
            }

            if (this.isAdded()) {
                this.updateUI(
                        dataPoints,
                        this.getString(R.string.velocity_daily_graph),
                        meanDay.getTimestamp(),
                        true
                );
            }
        }
    }

    @Override
    public void onWeeklyVelocityReceived(MeanWeek meanWeek) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanWeek == null || meanWeek.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.updateUI(
                    null,
                    getString(R.string.velocity_weekly_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            DataPoint[] dataPoints = new DataPoint[Calendar.WEEK_OF_MONTH];
            for (int i = 1; i <= Calendar.WEEK_OF_MONTH; ++i) {
                if (meanWeek.getMap().get(i) != null) {
                    float sampleSumVelocity = meanWeek.getMap().get(i).getSampleSumVelocity();
                    int sampleSizeVelocity = meanWeek.getMap().get(i).getSampleSizeVelocity();

                    dataPoints[i - 1] = new DataPoint(i, sampleSumVelocity / sampleSizeVelocity);
                } else {
                    dataPoints[i - 1] = new DataPoint(i, 0);
                }
            }


            if (this.isAdded()) {
                this.updateUI(
                        dataPoints,
                        this.getString(R.string.velocity_weekly_graph),
                        meanWeek.getTimestamp(),
                        true
                );
            }
        }
    }

    @Override
    public void onDailyAccelerationReceived(MeanDay meanDay) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanDay == null || meanDay.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.updateUI(
                    null,
                    getString(R.string.acceleration_daily_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            DataPoint[] dataPoints = new DataPoint[HOURS];
            for (int i = 0; i < HOURS; ++i) {
                if (meanDay.getMap().get(i) != null) {
                    float sampleSumAcceleration = meanDay.getMap().get(i).getSampleSumAcceleration();
                    int sampleSizeAcceleration = meanDay.getMap().get(i).getSampleSizeAcceleration();

                    dataPoints[i] = new DataPoint(i, sampleSumAcceleration / sampleSizeAcceleration);
                } else {
                    dataPoints[i] = new DataPoint(i, 0);
                }
            }

            if (this.isAdded()) {
                this.updateUI(
                        dataPoints,
                        this.getString(R.string.acceleration_daily_graph),
                        meanDay.getTimestamp(),
                        true
                );
            }
        }
    }

    @Override
    public void onWeeklyAccelerationReceived(MeanWeek meanWeek) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanWeek == null || meanWeek.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.updateUI(
                    null,
                    getString(R.string.acceleration_weekly_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            DataPoint[] dataPoints = new DataPoint[Calendar.WEEK_OF_MONTH];
            for (int i = 1; i <= Calendar.WEEK_OF_MONTH; ++i) {
                if (meanWeek.getMap().get(i) != null) {
                    float sampleSumAcceleration = meanWeek.getMap().get(i).getSampleSumAcceleration();
                    int sampleSizeAcceleration = meanWeek.getMap().get(i).getSampleSizeAcceleration();

                    dataPoints[i - 1] = new DataPoint(i, sampleSumAcceleration / sampleSizeAcceleration);
                } else {
                    dataPoints[i - 1] = new DataPoint(i, 0);
                }
            }

            if (this.isAdded()) {
                this.updateUI(
                        dataPoints,
                        this.getString(R.string.acceleration_weekly_graph),
                        meanWeek.getTimestamp(),
                        true
                );
            }
        }
    }

    @Override
    public void onFeedbackReceived(Map<Date, Double> map) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (map == null || map.size() < 1) {
            Log.d(TAG, "Data null");
            this.updateUI(
                    null,
                    getString(R.string.feedback_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            int i = 0; int mapSize = map.size();
            DataPoint[] dataPoints = new DataPoint[mapSize];
            Date firstUpdate;
            Date lastUpdate = null;
            for (Map.Entry entry : map.entrySet()) {
                if (i == 0) {
                    firstUpdate = (Date) entry.getKey();
                    this.graphView.getViewport().setMinX(firstUpdate.getTime() - DAY_MS);
                } else if (i == mapSize - 1) {
                    lastUpdate = (Date) entry.getKey();
                    this.graphView.getViewport().setMaxX(lastUpdate.getTime() + DAY_MS);
                }

                dataPoints[i++] = new DataPoint((Date) entry.getKey(), (Double) entry.getValue());
            }

            if (this.isAdded()) {
                this.updateUI(
                        dataPoints,
                        this.getString(R.string.feedback_graph),
                        lastUpdate == null ? 0 : lastUpdate.getTime(),
                        true
                );
            }
        }
    }

    @Override
    public void onPointsDataReceived() {
        this.hideProgress();

        this.unavailableData.setVisibility(View.VISIBLE);

        if (this.isAdded()) {
            this.updateUI(
                    null,
                    this.getString(R.string.points_graph),
                    0,
                    false
            );
        }

        // TODO: 17/10/17
    }

    public void updateUI(DataPoint[] dataPoint, String title, long subTitle, boolean clickable) {
        if (this.isAdded()) {
            if (title != null) {
                this.titleGraph.setText(title);
            } else {
                this.titleGraph.setText(getString(R.string.error));
            }

            if (subTitle != 0) {
                this.subTitleGraph.setText(String.format(
                        Locale.ENGLISH,
                        "%s: %s",
                        getString(R.string.last_update),
                        this.simpleDateFormat.format(subTitle)
                ));
            } else {
                this.subTitleGraph.setText(getString(R.string.unknown));
            }

            if (clickable) {
                this.fullscreenGraph.setClickable(true);
            } else {
                this.unavailableData.setVisibility(View.VISIBLE);
                this.fullscreenGraph.setClickable(false);
                this.fullscreenGraph.setColorFilter(
                        ContextCompat.getColor(this.getContext(), R.color.colorSchemasComplementary),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                );
            }

            if (dataPoint != null) {
                this.graphSeries = new LineGraphSeries<DataPoint>(dataPoint);
                this.initGraphSeries();
                this.graphView.addSeries(this.graphSeries);
            }
        }
    }
}