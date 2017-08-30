package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonGoogleProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonTwitterProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.chartBuild.ChartAsyncTask;
import com.driveembetter.proevolutionsoftware.driveembetter.chartBuild.RetainedFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.github.mikephil.charting.charts.ScatterChart;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;


/* Chart Activity Class */
public class ChartActivity extends AppCompatActivity {

    private String function;
    private double startIndex, endIndex;
    private ChartAsyncTask task;
    private ProgressDialog progress;

    @Override
    /* Called when the activity is starting */
    protected void onCreate(Bundle savedInstanceState) {
        /* Call through to the super class's implementation of this method */
        super.onCreate(savedInstanceState);
        /* Set the activity content from layout resource */
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
            //progress.setOnKeyListener(new KeyListener());
            chartFragment.setProgressDialog(progress);
            retainedFragment.setProgressDialog(progress);

            /* Get chart from chart fragment */
            ScatterChart chart = chartFragment.getChart();
            retainedFragment.setChart(chart);

            /* Create a new async task */
            task = new ChartAsyncTask(retainedFragment);
            task.execute(function, String.valueOf(startIndex), String.valueOf(endIndex));
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
                //progress.setOnKeyListener(new KeyListener());
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
                finish();
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
                    android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(ChartActivity.this);
                    ab.setMessage(getString(R.string.strAreYouSure)).setNegativeButton(android.R.string.no, listener).setPositiveButton(android.R.string.yes, listener).show();
                }
                return true;
            }
            return false;
        }
    }

}