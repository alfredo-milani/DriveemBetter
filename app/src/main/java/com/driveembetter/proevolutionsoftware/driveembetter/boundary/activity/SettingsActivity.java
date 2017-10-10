package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    private final static int RC_GALLERY = 9;

    // Widgets
    private Switch aSwitch;
    private TextView changeProfilePicture;

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

        this.changeProfilePicture = findViewById(R.id.changeProfilePicture);
        this.changeProfilePicture.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GALLERY) {
            switch (resultCode) {
                case RESULT_OK:
                    Log.d(TAG, "DATA: " + data.getData());
                    break;

                case RESULT_CANCELED:
                    Toast.makeText(this, getString(R.string.canceled_action), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.d(TAG, "activityResult RC: " + requestCode + " / " + resultCode);
            }
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
    public void onClick(View view) {
        this.startActivityForResult(new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                ), SettingsActivity.RC_GALLERY);
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
