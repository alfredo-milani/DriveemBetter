package com.driveembetter.proevolutionsoftware.driveembetter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.Provider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.exeption.ProviderNotFoundExeption;


public class SignInActivity
        extends AppCompatActivity
        implements View.OnClickListener, TypeMessages {

    private final static String TAG = "SignInActivity";

    // Activity resources
    private Provider Provider;
    private User user;

    // Activity widgets
    private Button signInButton;
    private com.google.android.gms.common.SignInButton signInGoogleButton;
    private Button signUpButton;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);

        /*
         DA INSERIRE NELL'ACTIVITY CONTENENTE I FRAGMENT
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */

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
                    Toast.makeText(SignInActivity.this, "Signed in as: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignInActivity.this, MainFragmentActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case USER_LOGOUT:
                    Log.d(TAG, "handleMessage:logout");
                    Toast.makeText(SignInActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
                    break;

                case USER_ALREADY_EXIST:
                    Log.d(TAG, "handleMessage:user_already_exist");
                    Toast.makeText(SignInActivity.this, "User already exist. Try with another email address", Toast.LENGTH_SHORT).show();
                    break;

                case EMAIL_REQUIRED:
                    Log.d(TAG, "handleMessage:email_required");
                    emailField.setError(getString(R.string.field_required));
                    break;

                case PASSWORD_REQUIRED:
                    Log.d(TAG, "handleMessage:password_required");
                    passwordField.setError(getString(R.string.field_required));
                    break;

                default:
                    Log.w(TAG, "handleMessage:error");
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
        this.signInButton = (Button) findViewById(R.id.sign_in_button);
        this.signInGoogleButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.sign_in_google_button);
        this.signUpButton = (Button) findViewById(R.id.sign_up_button);
        this.emailField = (EditText) findViewById(R.id.email_field);
        this.passwordField = (EditText) findViewById(R.id.password_field);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        this.signInButton.setOnClickListener(this);
        this.signInGoogleButton.setOnClickListener(this);
        this.signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                this.Provider.signIn(
                        this.emailField.getText().toString(),
                        this.passwordField.getText().toString()
                );
                break;

            case R.id.sign_in_google_button:
                this.Provider.signIn(null, null);
                break;

            case R.id.sign_up_button:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                Log.w(TAG, "Unknown error in onClick");
        }
    }

    @Override
    public void onBackPressed() {

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
