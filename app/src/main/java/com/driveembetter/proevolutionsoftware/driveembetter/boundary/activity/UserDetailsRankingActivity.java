package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        implements Constants {

    private final static String TAG = UserDetailsRankingActivity.class.getSimpleName();

    // Resources
    private User user;

    // Widgets
    private TextView username;
    private TextView points;
    private ImageView imageView;
    private ImageView availability;

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
        this.username = (TextView) findViewById(R.id.user);
        this.points = (TextView) findViewById(R.id.points);
        this.imageView = (ImageView) findViewById(R.id.user_picture);
        this.availability = (ImageView) findViewById(R.id.availability);

        if (this.user.getUsername() != null) {
            this.username.setText(this.user.getUsername());
        } else {
            this.username.setText(this.user.getUsernameFromUid());
        }
        this.points.setText(String.valueOf(this.user.getPoints()));
        Log.d(TAG, "CANE: " + user.getUsername() + " / " + String.valueOf(user.getPoints()) + " / " + user.getPhotoUrl() + " / " + user.getAvailability());
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
    protected void onPause() {
        super.onPause();
    }
}
