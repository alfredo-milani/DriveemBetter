package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.driveembetter.proevolutionsoftware.driveembetter.R;

/**
 * Created by alfredo on 29/08/17.
 */

public class AboutUs extends AppCompatActivity {

    private final static String TAG = AboutUs.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }
}
