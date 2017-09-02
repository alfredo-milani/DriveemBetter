package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.driveembetter.proevolutionsoftware.driveembetter.R;

/**
 * Created by alfredo on 02/09/17.
 */

public class UserDetailsRanking
        extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_details_ranking);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
