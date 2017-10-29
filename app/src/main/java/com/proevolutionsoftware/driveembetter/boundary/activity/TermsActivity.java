package com.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.utils.SharedPrefUtil;

/**
 * Created by alfredo on 28/10/17.
 */

public class TermsActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final static String TAG = TermsActivity.class.getSimpleName();

    public final static int TERMS_ACTIVITY = 54;
    public final static String TERMS_RESULT = "termsResult";
    public final static boolean TERMS_RESULT_OK = true;
    public final static boolean TERMS_RESULT_CANCEL = false;

    // Resources
    private SharedPrefUtil sharedPrefUtil;

    // Widgets
    private Button accept;
    private Button decline;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_terms);

        this.initResources();

        this.initWidgets();
    }

    private void initResources() {
        this.sharedPrefUtil = new SharedPrefUtil(this);
    }

    private void initWidgets() {
        this.accept = findViewById(R.id.acceptButton);
        this.decline = findViewById(R.id.declineButton);
        if (this.sharedPrefUtil.getBoolean(TERMS_RESULT, false)) {
            this.accept.setBackgroundResource(R.drawable.button_boarder_active);
        } else {
            this.decline.setBackgroundResource(R.drawable.button_boarder_active);
        }

        this.accept.setOnClickListener(this);
        this.decline.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        int id = view.getId();
        switch (id) {
            case R.id.acceptButton:
                resultIntent.putExtra(TERMS_RESULT, TERMS_RESULT_OK);
                this.setResult(Activity.RESULT_OK, resultIntent);
                break;

            case R.id.declineButton:
                resultIntent.putExtra(TERMS_RESULT, TERMS_RESULT_CANCEL);
                this.setResult(Activity.RESULT_OK, resultIntent);
                break;
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
