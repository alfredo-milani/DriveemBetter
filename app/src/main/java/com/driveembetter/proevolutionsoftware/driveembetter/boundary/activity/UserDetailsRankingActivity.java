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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.StringParser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by alfredo on 02/09/17.
 */

public class UserDetailsRankingActivity
        extends AppCompatActivity
        implements Constants, FirebaseDatabaseManager.RetrieveDataDB {

    private final static String TAG = UserDetailsRankingActivity.class.getSimpleName();

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

        if (this.user.getUsername() != null && !user.getUsername().isEmpty()) {
            this.username.setText(this.user.getUsername());
        } else {
            this.username.setText(this.user.getUsernameFromUid());
        }
        this.points.setText(String.valueOf(this.user.getPoints()));
        if (this.user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(this.user.getPhotoUrl())
                    .dontTransform()
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(this.imageView);
        } else {
            Glide.with(this)
                    .load(R.mipmap.user_black_icon)
                    .dontTransform()
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(this.imageView);
        }
        if (this.user.getAvailability().equals(AVAILABLE)) {
            this.availability.setImageResource(R.drawable.available_shape);
        } else {
            this.availability.setImageResource(R.drawable.unavailable_shape);
        }


        FirebaseDatabaseManager.retrieveUserData(this, user.getUid());
        // Graph properties
        this.velocitySeries = new LineGraphSeries<>();
        this.accelerationSeries = new LineGraphSeries<>();
        this.velocitySeries.setTitle("velocity");
        this.accelerationSeries.setTitle("acceleration");

        /*
        // Plot points graph
        // this.graphView.setTitle(getString(R.string.user_points_title));

        // first series
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setTitle("first line");
        this.graphView.addSeries(series);

        // second series
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 3),
                new DataPoint(1, 3),
                new DataPoint(2, 6),
                new DataPoint(3, 2),
                new DataPoint(4, 5)
        });
        series2.setTitle("speed");
        series2.setDrawBackground(true);
        series2.setColor(Color.argb(255, 255, 60, 60));
        series2.setBackgroundColor(Color.argb(100, 204, 119, 119));
        series2.setDrawDataPoints(true);
        this.graphView.addSeries(series2);

        // legend
        this.graphView.getLegendRenderer().setVisible(true);
        this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        */
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
    public void onDataReceived(String data) {
        Log.d(TAG, "Data received");
        this.progressBar.setVisibility(View.GONE);
        if (data == null || data.split(StringParser.itemSeparator).length == 0) {
            Log.d(TAG, "Data null");
            this.unavailableData.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Data consistent");
            String[] items = data.split(StringParser.itemSeparator);
            for (int i = 0; i < Constants.HOURS; ++i) {
                String[] values = items[i].split(StringParser.subItemSeparator);
                if (values.length < 5) {
                    this.velocitySeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            Constants.HOURS
                    );
                    this.accelerationSeries.appendData(
                            new DataPoint(i, 0),
                            false,
                            Constants.HOURS
                    );
                } else {
                    this.velocitySeries.appendData(
                            new DataPoint(i, Double.valueOf(values[2])),
                            false,
                            Constants.HOURS
                    );
                    this.accelerationSeries.appendData(
                            new DataPoint(i, Double.valueOf(values[4])),
                            false,
                            Constants.HOURS
                    );
                }
            }

            // Adding series to graph
            this.graphView.addSeries(this.velocitySeries);
            this.graphView.addSeries(this.accelerationSeries);
            // Setting color series
            this.velocitySeries.setColor(ContextCompat.getColor(this, R.color.blue_700));
            this.accelerationSeries.setColor(ContextCompat.getColor(this, R.color.tw__composer_red));
            //
            this.velocitySeries.setDrawAsPath(true);
            this.accelerationSeries.setDrawAsPath(true);
            // Setting line spessor
            this.velocitySeries.setThickness(3);
            this.accelerationSeries.setThickness(3);
            // Legend
            this.graphView.getLegendRenderer().setVisible(true);
            this.graphView.getLegendRenderer().setBackgroundColor(Color.alpha(255));
            this.graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }
    }
}
