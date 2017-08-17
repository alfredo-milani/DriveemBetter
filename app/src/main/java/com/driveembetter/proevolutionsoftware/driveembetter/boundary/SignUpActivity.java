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
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.EmailAndPasswordProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFactoryProvider;
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
    private EmailAndPasswordProvider emailAndPasswordProvider;
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
                case USER_LOGIN_EMAIL_PSW:
                    user = emailAndPasswordProvider.getUserInformation();
                    Log.d(TAG, "handleMessage:login");
                    if (user == null) {
                        Log.e(TAG, "handleMessage:error");
                        break;
                    }
                    Toast.makeText(SignUpActivity.this, "Signed in as: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, MainFragmentActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case USER_LOGOUT:
                    Log.d(TAG, "handleMessage:logout");
                    Toast.makeText(SignUpActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
                    break;

                case USER_ALREADY_EXIST:
                    Log.d(TAG, "handleMessage:user_already_exist");
                    Toast.makeText(SignUpActivity.this, "User already exist. Try with another email address", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.e(TAG, "handleMessage:error");
            }
        }
    };

    private void initResources() {
        SingletonFactoryProvider singletonFactoryProvider = new SingletonFactoryProvider(this, this.handler);

        this.emailAndPasswordProvider = singletonFactoryProvider.getSingletonSingletonEmailAndPasswordProvider();
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
        /*
        if (this.Provider != null)
            this.Provider.setStateListener();
            */
    }

    @Override
    public void onStop() {
        super.onStop();
        /*
        if (this.Provider != null)
            this.Provider.removeStateListener();
            */
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /*
        if (this.Provider != null)
            this.Provider.setStateListener();
            */
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if (this.Provider != null)
            this.Provider.setStateListener();
            */
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        if (this.Provider != null)
            this.Provider.removeStateListener();
            */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
