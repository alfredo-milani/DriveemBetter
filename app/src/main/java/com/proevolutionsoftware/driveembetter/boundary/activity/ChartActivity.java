package com.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.github.mikephil.charting.charts.ScatterChart;
import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.boundary.fragment.ChartFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.RetainedFragment;
import com.proevolutionsoftware.driveembetter.constants.Constants;
import com.proevolutionsoftware.driveembetter.threads.ChartAsyncTask;

/**
 * Created by Fabiana Rossi.
 */

/* Chart Activity Class */
public class ChartActivity extends AppCompatActivity {

    private String function;
    private double startIndex, endIndex;
    private ChartAsyncTask task;
    private ProgressDialog progress;
    private RadioButton rbVelocity, rbTime;
    String value = Constants.VELOCITY;
    String valueTime = Constants.STR_WEEK;



    @Override
    /* Called when the activity is starting */
    protected void onCreate(Bundle savedInstanceState) {
        /* Call through to the super class's implementation of this method */
        super.onCreate(savedInstanceState);


        /* Set the activity content from layout resource */
        this.setContentView(R.layout.activity_graph);

        /* Display home as an "up" affordance:
         user that selecting home will return one level up rather than to the top level of the app */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /* Find retained fragment by tag: return null if fragment is not found */
        FragmentManager fragmentManager = getFragmentManager();
        RetainedFragment retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(getString(R.string.fragment_tag));

        if (retainedFragment == null) {
            /* First launch */

            /* Initialize data */
            initData();

            /* Create a new retained fragment */
            retainedFragment = new RetainedFragment();
            /* Set fragment tag */
            fragmentManager.beginTransaction().add(retainedFragment, getString(R.string.fragment_tag)).commit();

            /* Get chart fragment */
            ChartFragment chartFragment = (ChartFragment) getFragmentManager().findFragmentById(R.id.chartFragment);

            /* Create and set a new progress dialog */
            progress = new ProgressDialog(this);
            progress.setMax(100);
            progress.setMessage(getString(R.string.strProgressDialogMessage));
            progress.setTitle(getString(R.string.strProgressDialogTitle));
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setCancelable(true);
            progress.setCanceledOnTouchOutside(false);
            progress.setOnKeyListener(new KeyListener());
            chartFragment.setProgressDialog(progress);
            retainedFragment.setProgressDialog(progress);

            /* Get chart from chart fragment */
            ScatterChart chart = chartFragment.getChart();
            retainedFragment.setChart(chart);

            /* Create a new async task */
            task = new ChartAsyncTask(retainedFragment);

            rbVelocity =  findViewById(R.id.radio_velocity);
            rbTime = findViewById(R.id.radio_week);

            if(rbVelocity.isChecked()) {
                value = Constants.VELOCITY;
            } else{
                value = Constants.ACCELERATION;
            }
            if (rbTime.isChecked()) {
                valueTime = Constants.STR_WEEK;
            }else {
                valueTime = Constants.STR_DAY;
            }


            task.execute(value, valueTime, String.valueOf(endIndex));
            retainedFragment.setTask(task);
        } else {
            /* Retained fragment already exists; activity has been recreated */
            initData();

            if (retainedFragment.getTask() != null) {
                /* Task is running */

                /* Get chart fragment */
                ChartFragment chartFragment = (ChartFragment) getFragmentManager().findFragmentById(R.id.chartFragment);

                /* Create and set a new progress dialog */
                progress = new ProgressDialog(this);
                progress.setMax(100);
                progress.setMessage(getString(R.string.strProgressDialogMessage));
                progress.setTitle(getString(R.string.strProgressDialogTitle));
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setCancelable(true);
                progress.setCanceledOnTouchOutside(false);
                progress.setOnKeyListener(new KeyListener());
                progress.show();
                chartFragment.setProgressDialog(progress);
                retainedFragment.setProgressDialog(progress);

                /* Get chart from chart fragment */
                ScatterChart chart = chartFragment.getChart();
                retainedFragment.setChart(chart);
            } else {
                /* Task is not running */

                /* Get chart fragment */
                ChartFragment chartFragment = (ChartFragment) getFragmentManager().findFragmentById(R.id.chartFragment);

                if (retainedFragment.getData() != null) {
                    /* Get chart */
                    ScatterChart chart = chartFragment.getChart();
                    chart.setData(retainedFragment.getData());

                    /*Redraw chart */
                    chart.invalidate();
                }
            }
        }
    }



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_velocity:
                if (checked) {
                    createGraph();
                }
                break;
            case R.id.radio_acceleration:
                if (checked)    {
                    createGraph();
                }
                break;


        }
    }

    public void onRadioButtonClickedTime(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_week:
                if (checked) {
                    createGraph();
                }
                break;
            case R.id.radio_day:
                if (checked) {
                    createGraph();
                }
                break;


        }
    }






    /* Initialize function, startIndex and endIndex variables from intent extras */
    private void initData() {
        Intent intent = getIntent();
       /* function = intent.getStringExtra(getResources().getResourceName(R.integer.FUNCTION_EXPRESSION));
        startIndex = intent.getDoubleExtra(getResources().getResourceName(R.integer.START_INDEX), 0);
        endIndex = intent.getDoubleExtra(getResources().getResourceName(R.integer.END_INDEX), 0);*/
    }

    @Override
    /* This hook is called whenever an item in options menu is selected */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /* Home key pressed: close activity */
                this.finish();
                return true;

            default:
                /* Do nothing */
                return super.onOptionsItemSelected(item);
        }
    }

    /* This method will be invoked when a button in the dialog is clicked */
    private class DialogClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    /* "Yes" button clicked: stop task  */
                    task.cancel(true);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    /* "No" button clicked: do nothing */
                    break;
            }
        }
    }

    /* This method will be invoked when a hardware key event is dispatched to this dialog */
    private class KeyListener implements DialogInterface.OnKeyListener {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
                /* "Back" button pressed */
                if (progress.isShowing()) {
                    /* If a progress dialog is showing, create a new dialog with "Yes" and "No" buttons */
                    DialogClickListener listener = new DialogClickListener();
                    AlertDialog.Builder ab = new AlertDialog.Builder(ChartActivity.this);
                    ab.setMessage(getString(R.string.strAreYouSure)).setNegativeButton(android.R.string.no, listener).setPositiveButton(android.R.string.yes, listener).show();
                }
                return true;
            }
            return false;
        }
    }

    private void createGraph() {
        FragmentManager fragmentManager = getFragmentManager();
        RetainedFragment retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(getString(R.string.fragment_tag));

         /* Get chart fragment */
        ChartFragment chartFragment = (ChartFragment) getFragmentManager().findFragmentById(R.id.chartFragment);

        /* Get chart */
        ScatterChart chart = chartFragment.getChart();
        if (retainedFragment.getData() != null &&
                chart != null) {
            chart.clearValues();
            chart.clear();
        }

        /* Create a new retained fragment */
        retainedFragment = new RetainedFragment();
        /* Set fragment tag */
        fragmentManager.beginTransaction().add(retainedFragment, getString(R.string.fragment_tag)).commit();


        /* Create and set a new progress dialog */
        progress = new ProgressDialog(this);
        progress.setMax(100);
        progress.setMessage(getString(R.string.strProgressDialogMessage));
        progress.setTitle(getString(R.string.strProgressDialogTitle));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(false);
        progress.setOnKeyListener(new KeyListener());
        chartFragment.setProgressDialog(progress);
        retainedFragment.setProgressDialog(progress);

        /* Get chart from chart fragment */
        if (chart != null) {
            chart = chartFragment.getChart();
            retainedFragment.setChart(chart);
        }

        /* Create a new async task */
        task = new ChartAsyncTask(retainedFragment);

        rbVelocity =  findViewById(R.id.radio_velocity);
        rbTime = findViewById(R.id.radio_week);

        if(rbVelocity.isChecked()) {
            value = Constants.VELOCITY;
        } else{
            value = Constants.ACCELERATION;
        }

        if (rbTime.isChecked()) {
            valueTime = Constants.STR_WEEK;
        }else {
            valueTime = Constants.STR_DAY;
        }

        task.execute(value, valueTime, String.valueOf(endIndex));
        retainedFragment.setTask(task);
    }


}