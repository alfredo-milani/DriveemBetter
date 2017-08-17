package com.driveembetter.proevolutionsoftware.driveembetter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.exeption.ProviderNotFoundExeption;

/**
 * Created by alfredo on 17/08/17.
 */

public class SignUpActivity
        extends AppCompatActivity
        implements TypeMessages {

    private final static String TAG = "SignUpActivity";

    // Activity resources
    private com.driveembetter.proevolutionsoftware.driveembetter.authentication.Provider Provider;
    private User user;

    // Activity widgets
    private Button signUpButton;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                case USER_LOGIN:
                    user = Provider.getUserInformation();
                    Log.d(TAG, "handleMessage:login");
                    if (user == null) {
                        Log.e(TAG, "handleMessage:error");
                        break;
                    }
                    Toast.makeText(SignUpActivity.this, "Signed in as: " + user.getEmail(), Toast.LENGTH_SHORT).show();
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
        FactoryProvider factoryProvider = new FactoryProvider(this, this.handler);

        try {
            this.Provider = factoryProvider.createProvider(1);
        } catch (ProviderNotFoundExeption e) {
            Log.e(TAG, "Invalid object type");
            e.printStackTrace();
        }
    }

    private void initWidget() {
        this.signUpButton = (Button) findViewById(R.id.sign_up_button);
        this.usernameField = (EditText) findViewById(R.id.username_field);
        this.emailField = (EditText) findViewById(R.id.email_field);
        this.passwordField = (EditText) findViewById(R.id.password_field);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.Provider != null)
            this.Provider.setStateListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.Provider != null)
            this.Provider.removeStateListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (this.Provider != null)
            this.Provider.setStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.Provider != null)
            this.Provider.setStateListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.Provider != null)
            this.Provider.removeStateListener();
    }
}
