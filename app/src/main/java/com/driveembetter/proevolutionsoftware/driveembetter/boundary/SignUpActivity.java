package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonEmailAndPasswordProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;

/**
 * Created by alfredo on 17/08/17.
 */

public class SignUpActivity
        extends AppCompatActivity
        implements View.OnClickListener, TypeMessages {

    private final static String TAG = "SignUpActivity";

    // Activity resources
    private SingletonEmailAndPasswordProvider singletonEmailAndPasswordProvider;
    private User user;

    // Activity widgets
    private Button signUpButton;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progressBar;
    private Button backButton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        this.initWidget();
        this.initResources();
    }

    // Defines a Handler object that's attached to the UI thread
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        /*
         * handleMessage() defines the operations to perform when
         * the Handler receives a new Message to process.
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case USER_ALREADY_EXIST:
                    Log.d(TAG, "handleMessage:user_already_exist");
                    Toast.makeText(SignUpActivity.this, "User already exist. Try with another email address", Toast.LENGTH_LONG).show();
                    break;

                case EMAIL_REQUIRED:
                    Log.d(TAG, "handleMessage:email_required");
                    emailField.setError(getString(R.string.field_required));
                    break;

                case PASSWORD_REQUIRED:
                    Log.d(TAG, "handleMessage:password_required");
                    passwordField.setError(getString(R.string.field_required));
                    break;

                case VERIFICATION_EMAIL_SENT:
                    Log.d(TAG, "handleMessage:verification_email_sent");
                    Toast.makeText(SignUpActivity.this, String.format(getString(R.string.verification_email_success), getString(R.string.app_name)), Toast.LENGTH_LONG).show();

                    Intent signInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(signInIntent);
                    finish();
                    break;

                case VERIFICATION_EMAIL_NOT_SENT:
                    Log.d(TAG, "handleMessage:verification_email_not_sent");
                    Toast.makeText(SignUpActivity.this, getString(R.string.verification_email_failure), Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.w(TAG, "handleMessage:error");
            }
        }
    };

    private void initResources() {
        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);

        this.singletonEmailAndPasswordProvider =
                factoryProviders.createEmailAndPasswordProvider();
    }

    private void initWidget() {
        this.signUpButton = (Button) findViewById(R.id.sign_up_button);
        this.usernameField = (EditText) findViewById(R.id.username_field);
        this.emailField = (EditText) findViewById(R.id.email_field);
        this.passwordField = (EditText) findViewById(R.id.password_field);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.backButton = (Button) findViewById(R.id.back_button);

        this.signUpButton.setOnClickListener(this);
        this.backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_button:
                this.progressBar.setVisibility(View.VISIBLE);
                this.singletonEmailAndPasswordProvider.signUp(
                        this.emailField.getText().toString(),
                        this.passwordField.getText().toString()
                );
                this.progressBar.setVisibility(View.GONE);
                break;

            case R.id.back_button:
                this.onBackPressed();
                break;

            default:
                Log.w(TAG, "Unknown error in onClick");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
