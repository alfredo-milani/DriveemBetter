package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.UserDetailsRankingActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanWeek;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by alfredo on 15/10/17.
 */

public class PageFragment extends Fragment
        implements TaskProgressInterface,
        FirebaseDatabaseManager.RetrieveDataDB {

    private final static String TAG = PageFragment.class.getSimpleName();

    public final static String ARG_FRAGMENT_GRAPH = "graphType";
    public final static int VELOCITY_GRAPH_DAILY = 1;
    public final static int VELOCITY_GRAPH_WEEKLY = 2;
    public final static int ACCELERATION_GRAPH_DAILY = 3;
    public final static int ACCELERATION_GRAPH_WEEKLY = 4;
    public final static int FEEDBACK_GRAPH = 5;
    public final static int POINTS_GRAPH = 6;
    private static int CURRENT_FRAGMENT;

    // Widgets
    private Context context;
    private View rootView;
    private GraphView graphView;
    private ProgressBar progressBar;
    private TextView unavailableData;

    // Resources
    private static String userID;
    private static UpdateUIGraph updateUI;
    private int typeGraph;
    private LineGraphSeries<DataPoint> graphSeries;
    private static boolean userVisibleHint;

    private static String title;
    private static long timestamp;
    private static boolean dataReceived;

    public interface UpdateUIGraph {
        void updateUI(String title, long subTitle, boolean dataReceived);
    }

    public static PageFragment newInstance(int type) {
        PageFragment.userID = UserDetailsRankingActivity.getUserID();
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(PageFragment.ARG_FRAGMENT_GRAPH, type);
        fragment.setArguments(args);
        return fragment;
    }

    public static void refreshData() {
        //getGraphData();

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        try {
            this.updateUI = (UpdateUIGraph) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement OnHeadlineSelectedListener"
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
    }

    private void initResources() {
        this.typeGraph = this.getArguments().getInt(
                PageFragment.ARG_FRAGMENT_GRAPH,
                PageFragment.VELOCITY_GRAPH_DAILY
        );
    }

    private void initWidgets() {
        this.graphView = this.rootView.findViewById(R.id.graph);
        this.progressBar = this.rootView.findViewById(R.id.progress_bar);
        this.unavailableData = this.rootView.findViewById(R.id.unavailable_data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pageview, container, false);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.e(TAG, "DIO: " + this.typeGraph + " / " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            this.userVisibleHint = true;
        }
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
                // FirebaseDatabaseManager.retrieveWeeklyData();
                break;

            case PageFragment.POINTS_GRAPH: break;
        }
    }

    private void initGraphVelocity() {
        this.graphSeries = new LineGraphSeries<>();
        this.graphSeries.setTitle(getString(R.string.velocity) /* + " " + getString(R.string.vel_mu) */);
        // Adding axis titles
        this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.vel_mu));
        this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
        // Setting color graphSeries
        this.graphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.blue_700));
        //
        this.graphSeries.setDrawAsPath(true);
        // Setting line width
        this.graphSeries.setThickness(3);
        // Legend
        this.graphView.getLegendRenderer().setVisible(true);
        this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
        this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private void initGraphAcceleration() {
        this.graphSeries = new LineGraphSeries<>();
        this.graphSeries.setTitle(getString(R.string.acceleration) /* + " " + getString(R.string.acc_mu) */);
        // Adding axis titles
        this.graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.acc_mu));
        this.graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.hour_mu));
        // Setting color graphSeries
        this.graphSeries.setColor(ContextCompat.getColor(this.getActivity(), R.color.red_700));
        //
        this.graphSeries.setDrawAsPath(true);
        // Setting line width
        this.graphSeries.setThickness(3);
        // Legend
        this.graphView.getLegendRenderer().setVisible(true);
        this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
        this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
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
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanDay == null || meanDay.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.title = getString(R.string.velocity_graph);
            this.timestamp = 0;
            this.dataReceived = false;
        } else {
            Log.d(TAG, "Data consistent");
            this.unavailableData.setVisibility(View.GONE);
            for (int i = 0; i < Constants.HOURS; ++i) {
                if (meanDay.getMap().get(i) != null) {
                    float sampleSumVelocity = meanDay.getMap().get(i).getSampleSumVelocity();
                    int sampleSizeVelocity = meanDay.getMap().get(i).getSampleSizeVelocity();

                    this.graphSeries.appendData(
                            new DataPoint(i, sampleSumVelocity / sampleSizeVelocity),
                            false,
                            Constants.HOURS
                    );
                } else {
                    this.graphSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            Constants.HOURS
                    );
                }
            }

            this.title = getString(R.string.velocity_graph);
            this.timestamp = meanDay.getTimestamp();
            this.dataReceived = true;
            this.graphView.addSeries(this.graphSeries);
        }

        this.pushDataToUI();
    }

    @Override
    public void onWeeklyVelocityReceived(MeanWeek meanWeek) {
        this.hideProgress();
    }

    @Override
    public void onDailyAccelerationReceived(MeanDay meanDay) {
        this.hideProgress();

        Log.d(TAG, "Data received");
        if (meanDay == null || meanDay.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.title = getString(R.string.acceleration_graph);
            this.timestamp = 0;
            this.dataReceived = false;
        } else {
            Log.d(TAG, "Data consistent");
            this.unavailableData.setVisibility(View.GONE);
            for (int i = 0; i < Constants.HOURS; ++i) {
                if (meanDay.getMap().get(i) != null) {
                    float sampleSumAcceleration = meanDay.getMap().get(i).getSampleSumAcceleration();
                    int sampleSizeAcceleration = meanDay.getMap().get(i).getSampleSizeAcceleration();

                    this.graphSeries.appendData(
                            new DataPoint(i, sampleSumAcceleration / sampleSizeAcceleration),
                            false,
                            Constants.HOURS
                    );

                } else {
                    this.graphSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            Constants.HOURS
                    );
                }
            }

            this.title = getString(R.string.acceleration_graph);
            this.timestamp = meanDay.getTimestamp();
            this.dataReceived = true;
            this.graphView.addSeries(this.graphSeries);
        }

        this.pushDataToUI();
    }

    @Override
    public void onWeeklyAccelerationReceived(MeanWeek meanWeek) {
        this.hideProgress();
    }

    @Override
    public void onPointsDataReceived() {
        this.hideProgress();
    }

    @Override
    public void onFeedbackReceived() {
        this.hideProgress();
    }

    public static void pushDataToUI() {
        if (userVisibleHint) {
            updateUI.updateUI(
                    title,
                    timestamp,
                    dataReceived
            );
        }
    }
}