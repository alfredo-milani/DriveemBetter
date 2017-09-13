package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;

/**
 * Created by alfredo on 11/09/17.
 */

public class SettingsActivity
        extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {

    private final static String TAG = SettingsActivity.class.getSimpleName();

    // Widgets
    private Switch aSwitch;

    // Resources
    private PositionManager positionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.positionManager = PositionManager.getInstance(this);
    }

    private void initWidgets() {
        /* Display home as an "up" affordance:
         user that selecting home will return one level up rather than to the top level of the app */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.setTitle(R.string.action_settings);
        this.aSwitch = findViewById(R.id.switch1);
        this.aSwitch.setChecked(this.positionManager.isListenerSetted());
        this.aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            if (!this.positionManager.isListenerSetted()) {
                this.positionManager.updatePosition();
            }
        } else {
            if (this.positionManager.isListenerSetted()) {
                this.positionManager.removeLocationUpdates();
            }
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
