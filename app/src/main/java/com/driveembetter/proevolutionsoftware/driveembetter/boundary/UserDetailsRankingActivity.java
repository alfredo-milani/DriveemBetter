package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
    private TextView textView;
    private TextView textView2;
    private ImageView imageView;

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
        this.textView = (TextView) findViewById(R.id.textView10);
        this.textView2 = (TextView) findViewById(R.id.textView11);
        this.imageView = (ImageView) findViewById(R.id.user_picture);

        if (this.user.getUsername() != null) {
            this.textView.setText(this.user.getUsername());
        } else if (this.user.getEmail() != null) {
            this.textView.setText(this.user.getEmail());
        }
        this.textView2.setText(String.valueOf(this.user.getPoints()));
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
