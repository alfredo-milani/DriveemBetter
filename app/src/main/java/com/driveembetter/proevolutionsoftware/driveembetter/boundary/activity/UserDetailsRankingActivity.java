package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;

/**
 * Created by alfredo on 02/09/17.
 */

public class UserDetailsRankingActivity
        extends AppCompatActivity
        implements Constants,
        View.OnClickListener {

    private final static String TAG = UserDetailsRankingActivity.class.getSimpleName();

    // Resources
    private User user;

    // Widgets
    private TextView username;
    private TextView points;
    private ImageView imageView;
    private ImageView availability;
    private ImageButton backButton;

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
        this.backButton = findViewById(R.id.back_button);
        this.backButton.setOnClickListener(this);

        if (this.user.getUsername() != null) {
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
        if (this.user.getAvailability() != null &&
            this.user.getAvailability().equals(AVAILABLE)) {
            this.availability.setImageResource(R.drawable.available_shape);
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.back_button:
                super.onBackPressed();
                break;

            default:
                Log.e(TAG, "onClick: error touch: " + id);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
