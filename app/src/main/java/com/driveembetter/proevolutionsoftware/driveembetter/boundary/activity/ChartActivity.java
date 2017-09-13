package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.threads.ChartAsyncTask;

/**
 * Created by alfredo on 28/08/17.
 */

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
        // this.setContentView(R.layout.activity_graph);

        /* Display home as an "up" affordance:
         user that selecting home will return one level up rather than to the top level of the app */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
                    AlertDialog.Builder ab = new AlertDialog.Builder(ChartActivity.this);
                    ab.setMessage(getString(R.string.strAreYouSure)).setNegativeButton(android.R.string.no, listener).setPositiveButton(android.R.string.yes, listener).show();
                }
                return true;
            }
            return false;
        }
    }

}