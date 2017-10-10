package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.SensorHandler;

/**
 * Created by alfredo on 11/09/17.
 */

public class SettingsActivity
        extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener,
                    View.OnClickListener {

    private final static String TAG = SettingsActivity.class.getSimpleName();

    // Widgets
    private Switch aSwitch;
    private TextView changeProfileData;

    // Resources
    private SensorHandler sensorHandler;
    private PositionManager positionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.sensorHandler = new SensorHandler(this);
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

        this.changeProfileData = findViewById(R.id.changeDataProfile);
        this.changeProfileData.setOnClickListener(this);
    }

    private void startNewActivity(Context context, Class newClass) {
        Intent newIntent = new Intent(context, newClass);
        this.startActivity(newIntent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.changeDataProfile:
                this.startNewActivity(SettingsActivity.this, EditProfileData.class);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            if (!this.positionManager.isListenerSetted()) {
                this.sensorHandler.startSensorHandler();
                this.positionManager.updatePosition();
            }
        } else {
            if (this.positionManager.isListenerSetted()) {
                this.sensorHandler.removeSensorHandler();
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
