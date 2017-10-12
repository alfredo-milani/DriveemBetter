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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanWeek;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.GlideImageLoader;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by alfredo on 02/09/17.
 */

public class UserDetailsRankingActivity
        extends AppCompatActivity
        implements Constants, FirebaseDatabaseManager.RetrieveDataDB,
            View.OnClickListener {

    private final static String TAG = UserDetailsRankingActivity.class.getSimpleName();

    private final static String formatData = "dd-MM-yyyy HH:mm:ss";
    private final static int minValYAcceleration = 0;
    private final static int maxValYAcceleration = 3;

    // Resources
    private User user;
    private LineGraphSeries<DataPoint> velocitySeries;
    private LineGraphSeries<DataPoint> accelerationSeries;

    // Widgets
    private TextView username;
    private TextView points;
    private ImageView imageView;
    private ImageView availability;
    private GraphView graphView;
    private TextView unavailableData;
    private ProgressBar progressBar;
    private TextView feedback;
    private ImageButton fullscreen;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_details_ranking);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.user = this.getIntent().getParcelableExtra(USER);
    }

    private void initWidgets() {
        // Set action bar title
        this.setTitle(R.string.detail_user);

        /* Display home as an "up" affordance:
         user that selecting home will return one level up rather than to the top level of the app */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.username = findViewById(R.id.user);
        this.points = findViewById(R.id.points);
        this.imageView = findViewById(R.id.user_picture);
        this.availability = findViewById(R.id.availability);
        this.graphView = findViewById(R.id.graph_points);
        this.unavailableData = findViewById(R.id.unavailable_data);
        this.progressBar = findViewById(R.id.progress_bar);
        this.feedback = findViewById(R.id.driverFeedbackContent);
        this.fullscreen = findViewById(R.id.fullscreeImageButton);
        this.fullscreen.setOnClickListener(this);

        if (this.user.getUsername() != null && !user.getUsername().isEmpty()) {
            this.username.setText(this.user.getUsername());
        } else {
            this.username.setText(this.user.getUsernameFromUid());
        }
        this.points.setText(String.valueOf(this.user.getPoints()));
        GlideImageLoader.loadImage(
                this,
                this.imageView,
                this.user.getPhotoUrl(),
                R.mipmap.user_icon,
                R.mipmap.user_icon);
        if (this.user.getAvailability().equals(AVAILABLE)) {
            this.availability.setImageResource(R.drawable.available_shape);
        } else {
            this.availability.setImageResource(R.drawable.unavailable_shape);
        }
        if (this.user.getFeedback() != 0) {
            this.feedback.setText(String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    this.user.getFeedback()
            ));
        }

        FirebaseDatabaseManager.retrieveDailyData(this, user.getUid());
        // Graph properties
        this.velocitySeries = new LineGraphSeries<>();
        this.accelerationSeries = new LineGraphSeries<>();
        this.velocitySeries.setTitle(getString(R.string.velocity) + " (km/h)");
        this.accelerationSeries.setTitle(getString(R.string.acceleration) + " (m/s^2)");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fullscreeImageButton:

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onWeeklyDataReceived(MeanWeek meanWeek) {

    }

    @Override
    public void onDailyDataReceived(MeanDay data) {
        Log.d(TAG, "Data received");
        this.progressBar.setVisibility(View.GONE);
        if (data == null || data.getMap().size() < 1) {
            Log.d(TAG, "Data null");
            this.unavailableData.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Data consistent");
            for (int i = 0; i < Constants.HOURS; ++i) {
                if (data.getMap().get(i) != null) {
                    float sampleSumAcceleration = data.getMap().get(i).getSampleSumAcceleration();
                    float sampleSumVelocity = data.getMap().get(i).getSampleSumVelocity();
                    int sampleSizeAcceleration = data.getMap().get(i).getSampleSizeAcceleration();
                    int sampleSizeVelocity = data.getMap().get(i).getSampleSizeVelocity();

                    this.accelerationSeries.appendData(
                            new DataPoint(i, sampleSumAcceleration / sampleSizeAcceleration),
                            false,
                            Constants.HOURS
                    );
                    this.velocitySeries.appendData(
                            new DataPoint(i, sampleSumVelocity / sampleSizeVelocity),
                            false,
                            Constants.HOURS
                    );
                } else {
                    this.accelerationSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            Constants.HOURS
                    );
                    this.velocitySeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            Constants.HOURS
                    );
                }
            }

            // titles
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    UserDetailsRankingActivity.formatData,
                    Locale.ITALIAN
            );
            this.graphView.setTitle(String.format(
                    Locale.ENGLISH,
                    "%s: %s",
                    getString(R.string.last_update),
                    simpleDateFormat.format(data.getLocalDate())
            ));

            // Adding axis titles
            // this.graphView.getGridLabelRenderer().setVerticalAxisTitle("m/s");
            // this.graphView.getGridLabelRenderer().setHorizontalAxisTitle("h");
            // Adding series to graph
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
        }
    }
}
