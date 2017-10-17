package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.content.Context;
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
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.DAYS;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.DAY_MS;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.HOURS;

/**
 * Created by alfredo on 15/10/17.
 */

public class PageFragment extends Fragment
        implements TaskProgressInterface,
        FirebaseDatabaseManager.RetrieveDataDB,
        View.OnClickListener {

    private final static String TAG = PageFragment.class.getSimpleName();

    public final static String ARG_FRAGMENT_GRAPH = "graphType";
    public final static String ARG_FRAGMENT_GRAPH_SERIES = "graphSeries";
    public final static int GRAPH_ERROR = -1;
    public final static int VELOCITY_GRAPH_DAILY = 1;
    public final static int VELOCITY_GRAPH_WEEKLY = 2;
    public final static int ACCELERATION_GRAPH_DAILY = 3;
    public final static int ACCELERATION_GRAPH_WEEKLY = 4;
    public final static int FEEDBACK_GRAPH = 5;
    public final static int POINTS_GRAPH = 6;

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
    private BarGraphSeries<DataPoint> barGraphSeries;

    public static PageFragment newInstance(int type, String userID) {
        PageFragment.userID = userID;
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(PageFragment.ARG_FRAGMENT_GRAPH, type);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
    }

    private void initResources() {
        this.simpleDateFormat = new SimpleDateFormat(
                PageFragment.formatData,
                Locale.getDefault()
        );
        this.typeGraph = this.getArguments().getInt(
                PageFragment.ARG_FRAGMENT_GRAPH,
                PageFragment.VELOCITY_GRAPH_DAILY
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

        this.rootView = view;
        this.initWidgets();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.getGraphData();
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
        this.getGraphData();
        Toast.makeText(
                this.getActivity(),
                getString(R.string.updated_graph),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void startFullscreenGraph() {
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
        this.startActivity(fullscreenIntent);
    }

    private void getGraphData() {
        this.showProgress();

        switch (this.typeGraph) {
            case PageFragment.VELOCITY_GRAPH_DAILY:
                this.initGraphVelocity();
                FirebaseDatabaseManager.retrieveDailyData(
                        this,
                        VELOCITY_GRAPH_DAILY,
                        PageFragment.userID
                );
                break;

            case PageFragment.VELOCITY_GRAPH_WEEKLY:
                this.initGraphVelocity();
                FirebaseDatabaseManager.retrieveWeeklyData(
                        this,
                        VELOCITY_GRAPH_WEEKLY,
                        PageFragment.userID
                );
                break;

            case PageFragment.ACCELERATION_GRAPH_DAILY:
                this.initGraphAcceleration();
                FirebaseDatabaseManager.retrieveDailyData(
                        this,
                        ACCELERATION_GRAPH_DAILY,
                        PageFragment.userID
                );
                break;

            case PageFragment.ACCELERATION_GRAPH_WEEKLY:
                this.initGraphAcceleration();
                FirebaseDatabaseManager.retrieveWeeklyData(
                        this,
                        ACCELERATION_GRAPH_WEEKLY,
                        PageFragment.userID
                );
                break;

            case PageFragment.FEEDBACK_GRAPH:
                this.initGraphFeedback();
                FirebaseDatabaseManager.retrieveFeedbackHistory(
                        this,
                        PageFragment.userID
                );
                break;

            case PageFragment.POINTS_GRAPH:
                // TODO: 17/10/17
                this.initGraphPoints();
                break;
        }
    }

    private void setGraphHorizontalScale(int hLabels, long maxVal) {
        this.graphView.getViewport().setXAxisBoundsManual(true);
        this.graphView.getGridLabelRenderer().setNumHorizontalLabels(hLabels);
        // this.graphView.getViewport().setMinX(0);
        this.graphView.getViewport().setMaxX(maxVal);
    }

    private void initBaseGraph() {
        if (this.graphSeries != null) { // Delete old data
            this.graphView.removeSeries(this.graphSeries);
        } else if (this.barGraphSeries != null) {
            this.graphView.removeSeries(this.barGraphSeries);
        } else { // First init
            // Legend
            this.graphView.getLegendRenderer().setVisible(true);
            this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
            this.graphView.getLegendRenderer().setFixedPosition(0, 0);
            // this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }
        this.graphSeries = new LineGraphSeries<>();
        this.barGraphSeries = new BarGraphSeries<>();
        //
        this.graphSeries.setDrawAsPath(true);
        this.barGraphSeries.setDrawValuesOnTop(true);
        // Setting line width
        this.graphSeries.setThickness(3);
    }

    private void initGraphVelocity() {
        this.initBaseGraph();
        this.graphSeries.setTitle(String.format(
                Locale.ENGLISH,
                "%s (%s)",
                getString(R.string.velocity),
                getString(R.string.vel_mu)
        ));
        // Adding axis titles
        // this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.vel_mu));
        this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
        // Setting color graphSeries
        this.graphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.blue_700));
    }

    private void initGraphAcceleration() {
        this.initBaseGraph();
        this.graphSeries.setTitle(String.format(
                Locale.ENGLISH,
                "%s (%s)",
                getString(R.string.acceleration),
                getString(R.string.acc_mu)
        ));
        // Adding axis titles
        // this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.acc_mu));
        this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
        // Setting color graphSeries
        this.graphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.red_700));
    }

    private void initGraphFeedback() {
        this.initBaseGraph();
        this.barGraphSeries.setTitle(String.format(
                Locale.ENGLISH,
                "%s (%s)",
                getString(R.string.feedback),
                getString(R.string.feedback_scale)
        ));

        // Adding axis titles
        // this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.acc_mu));
        // this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.date));
        // Setting color graphSeries
        // this.barGraphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.green_700));

        // Spacing
        // this.barGraphSeries.setSpacing(50);

        // this.graphView.getViewport().setXAxisBoundsManual(true);

        // Draw values on top
        // this.barGraphSeries.setDrawValuesOnTop(true);
        // this.barGraphSeries.setValuesOnTopColor(Color.RED);

        // Set date label formatter
        /*
        this.graphView.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(this.getActivity())
        );
        */
        // this.graphView.getGridLabelRenderer().setNumHorizontalLabels(3);

        // As we use dates as labels, the human rounding to nice readable numbers is not necessary
        // this.graphView.getGridLabelRenderer().setHumanRounding(false);
    }

    private void initGraphPoints() {

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

    @Override
    public void onDailyVelocityReceived(MeanDay meanDay) {
        this.setGraphHorizontalScale(4, HOURS);
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanDay == null || meanDay.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.unavailableData.setVisibility(View.VISIBLE);
            this.updateUI(
                    getString(R.string.velocity_daily_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            for (int i = 0; i < HOURS; ++i) {
                if (meanDay.getMap().get(i) != null) {
                    float sampleSumVelocity = meanDay.getMap().get(i).getSampleSumVelocity();
                    int sampleSizeVelocity = meanDay.getMap().get(i).getSampleSizeVelocity();

                    this.graphSeries.appendData(
                            new DataPoint(i, sampleSumVelocity / sampleSizeVelocity),
                            false,
                            HOURS
                    );
                } else {
                    this.graphSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            HOURS
                    );
                }
            }

            this.updateUI(
                    getString(R.string.velocity_daily_graph),
                    meanDay.getTimestamp(),
                    true
            );
            this.graphView.addSeries(this.graphSeries);
        }
    }

    @Override
    public void onWeeklyVelocityReceived(MeanWeek meanWeek) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanWeek == null || meanWeek.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.unavailableData.setVisibility(View.VISIBLE);
            this.updateUI(
                    getString(R.string.velocity_weekly_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            for (int i = 0; i < DAYS; ++i) {
                if (meanWeek.getMap().get(i) != null) {
                    float sampleSumVelocity = meanWeek.getMap().get(i).getSampleSumVelocity();
                    int sampleSizeVelocity = meanWeek.getMap().get(i).getSampleSizeVelocity();

                    this.graphSeries.appendData(
                            new DataPoint(i, sampleSumVelocity / sampleSizeVelocity),
                            false,
                            DAYS
                    );
                } else {
                    this.graphSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            DAYS
                    );
                }
            }

            this.updateUI(
                    getString(R.string.velocity_weekly_graph),
                    meanWeek.getTimestamp(),
                    true
            );
            this.graphView.addSeries(this.graphSeries);
        }
    }

    @Override
    public void onDailyAccelerationReceived(MeanDay meanDay) {
        this.setGraphHorizontalScale(4, HOURS);
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanDay == null || meanDay.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.unavailableData.setVisibility(View.VISIBLE);
            this.updateUI(
                    getString(R.string.acceleration_daily_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            for (int i = 0; i < HOURS; ++i) {
                if (meanDay.getMap().get(i) != null) {
                    float sampleSumAcceleration = meanDay.getMap().get(i).getSampleSumAcceleration();
                    int sampleSizeAcceleration = meanDay.getMap().get(i).getSampleSizeAcceleration();

                    this.graphSeries.appendData(
                            new DataPoint(i, sampleSumAcceleration / sampleSizeAcceleration),
                            false,
                            HOURS
                    );

                } else {
                    this.graphSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            HOURS
                    );
                }
            }

            this.updateUI(
                    getString(R.string.acceleration_daily_graph),
                    meanDay.getTimestamp(),
                    true
            );
            this.graphView.addSeries(this.graphSeries);
        }
    }

    @Override
    public void onWeeklyAccelerationReceived(MeanWeek meanWeek) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanWeek == null || meanWeek.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.unavailableData.setVisibility(View.VISIBLE);
            this.updateUI(
                    getString(R.string.acceleration_weekly_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            for (int i = 0; i < DAYS; ++i) {
                if (meanWeek.getMap().get(i) != null) {
                    float sampleSumAcceleration = meanWeek.getMap().get(i).getSampleSumAcceleration();
                    int sampleSizeAcceleration = meanWeek.getMap().get(i).getSampleSizeAcceleration();

                    this.graphSeries.appendData(
                            new DataPoint(i, sampleSumAcceleration / sampleSizeAcceleration),
                            false,
                            DAYS
                    );

                } else {
                    this.graphSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            DAYS
                    );
                }
            }

            this.updateUI(
                    getString(R.string.acceleration_weekly_graph),
                    meanWeek.getTimestamp(),
                    true
            );
            this.graphView.addSeries(this.graphSeries);
        }
    }

    @Override
    public void onFeedbackReceived(Map<Date, Double> map) {
        this.hideProgress();

        this.barGraphSeries = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(3, 8),
                new DataPoint(7, 2)
        });

        // Set manual x bounds to have nice steps
        this.graphView.getViewport().setMinX(System.currentTimeMillis() - DAY_MS);
        this.graphView.getViewport().setMaxX(System.currentTimeMillis() + DAY_MS);

        this.graphView.addSeries(this.barGraphSeries);

        if (true) return;

        Log.d(TAG, "Data received");
        if (map == null || map.size() < 1) {
            Log.d(TAG, "Data null");
            this.unavailableData.setVisibility(View.VISIBLE);
            this.updateUI(
                    getString(R.string.feedback_graph),
                    0,
                    false
            );
        } else {
            Log.d(TAG, "Data consistent");
            int i = 0; int mapSize = map.size();
            Date firstUpdate = null;
            Date lastUpdate = null;
            int occurence = 0;
            double meanFeedback = 0.0;
            Calendar lastOcc = Calendar.getInstance();
            Calendar currentOcc = Calendar.getInstance();
            for (Map.Entry entry : map.entrySet()) {
                if (i == 0) {
                    firstUpdate = (Date) entry.getKey();
                    currentOcc.setTime((Date) entry.getKey());
                    meanFeedback += (Double) entry.getValue();
                    ++occurence; ++i;
                    continue;
                } else if (i == mapSize - 1) {
                    lastUpdate = (Date) entry.getKey();
                }

                lastOcc.setTime((Date) entry.getKey());
                if (lastOcc.get(Calendar.YEAR) == currentOcc.get(Calendar.YEAR) &&
                        lastOcc.get(Calendar.DAY_OF_YEAR) == currentOcc.get(Calendar.DAY_OF_YEAR)) {
                    meanFeedback += (Double) entry.getValue();
                    ++occurence;
                    continue;
                }

                this.barGraphSeries.appendData(
                        new DataPoint((Date) entry.getKey(), meanFeedback / occurence),
                        false,
                        3
                );

                ++i; meanFeedback = 0.0; occurence = 0;
            }

            this.updateUI(
                    getString(R.string.feedback_graph),
                    lastUpdate == null ? null : lastUpdate.getTime(),
                    true
            );
            this.barGraphSeries = new BarGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(3, 8),
                    new DataPoint(7, 2)
            });

            // Set manual x bounds to have nice steps
            this.graphView.getViewport().setMinX(firstUpdate.getTime() - DAY_MS);
            this.graphView.getViewport().setMaxX(lastUpdate.getTime() + DAY_MS);

            this.graphView.addSeries(this.barGraphSeries);
        }
    }

    @Override
    public void onPointsDataReceived() {
        this.hideProgress();

        this.unavailableData.setVisibility(View.VISIBLE);
        this.updateUI(
                getString(R.string.points_graph),
                0,
                false
        );

        // TODO: 17/10/17
    }

    public void updateUI(String title, long subTitle, boolean clickable) {
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
            this.fullscreenGraph.setClickable(false);
            this.fullscreenGraph.setColorFilter(
                    ContextCompat.getColor(this.getContext(), R.color.colorSchemasComplementary),
                    android.graphics.PorterDuff.Mode.MULTIPLY
            );
        }
    }
}