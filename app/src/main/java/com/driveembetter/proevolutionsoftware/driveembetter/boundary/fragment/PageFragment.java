package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.jjoe64.graphview.GraphView;

/**
 * Created by alfredo on 15/10/17.
 */

public class PageFragment extends Fragment
        implements View.OnClickListener {

    private final static String TAG = PageFragment.class.getSimpleName();

    public final static String ARG_FRAGMENT_GRAPH = "graph";
    public final static int VELOCITY_GRAPH = 1;
    public final static int ACCELERATION_GRAPH = 2;
    public final static int FEEDBACK_GRAPH = 3;

    // Widgets
    private View rootView;
    private GraphView graphView;
    private TextView unavailableData;
    private ProgressBar progressBar;
    private ImageButton fullscreen;
    private TextView titleGraph;
    private TextView subTitleGraph;
    private ImageButton refreshData;

    // Resources
    private int typeGraph;

    public static PageFragment newInstance(int type) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(PageFragment.ARG_FRAGMENT_GRAPH, type);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_pageview, container, false);

        return this.rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initWidgets();
    }

    private void initResources() {
        this.typeGraph = this.getArguments().getInt(
                PageFragment.ARG_FRAGMENT_GRAPH,
                PageFragment.VELOCITY_GRAPH
        );
    }

    private void initWidgets() {
        this.graphView = this.rootView.findViewById(R.id.graph);
        this.unavailableData = this.rootView.findViewById(R.id.unavailable_data);
        this.progressBar = this.rootView.findViewById(R.id.progress_bar);
        this.fullscreen = this.rootView.findViewById(R.id.fullscreenImageButton);
        this.titleGraph = this.rootView.findViewById(R.id.titleGraph);
        this.subTitleGraph = this.rootView.findViewById(R.id.subTitleGraph);
        this.refreshData = this.rootView.findViewById(R.id.refreshGraph);

        this.refreshData.setOnClickListener(this);
        this.fullscreen.setOnClickListener(this);



        // Adding axis titles
        // this.graphView.getGridLabelRenderer().setVerticalAxisTitle("m/s");
        // this.graphView.getGridLabelRenderer().setHorizontalAxisTitle("h");
        // Adding series to graph
        /*
        this.graphView.addSeries(this.velocitySeries);
        this.graphView.getSecondScale().addSeries(this.accelerationSeries);
        this.graphView.getSecondScale().setMinY(UserDetailsRankingActivity.minValYAcceleration);
        this.graphView.getSecondScale().setMaxY(UserDetailsRankingActivity.maxValYAcceleration);
        // this.graphView.addSeries(this.accelerationSeries);
        // Setting color series
        this.velocitySeries.setColor(ContextCompat.getColor(this, R.color.blue_700));
        this.accelerationSeries.setColor(ContextCompat.getColor(this, R.color.tw__composer_red));
        //
        this.velocitySeries.setDrawAsPath(true);
        this.accelerationSeries.setDrawAsPath(true);
        // Setting line width
        this.velocitySeries.setThickness(3);
        this.accelerationSeries.setThickness(3);
        // Legend
        this.graphView.getLegendRenderer().setVisible(true);
        this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
        this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        */
    }

    private void initGraphVelocity() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

        }
    }
}