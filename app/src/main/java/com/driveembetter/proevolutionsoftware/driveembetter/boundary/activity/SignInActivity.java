package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonGoogleProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonTwitterProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.StringParser;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;


public class SignInActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        TypeMessages,
        TaskProgressInterface {

    private final static String TAG = SignInActivity.class.getSimpleName();

    // Activity resources
    private ArrayList<BaseProvider> baseProviderArrayList;
    private SingletonFirebaseProvider singletonFirebaseProvider;

    // Activity widgets
    private Button signInButton;
    private SignInButton signInGoogleButton;
    private TwitterLoginButton twitterLoginButton;
    private Button signUpButton;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progressBar;

    // If we are authenticated with Firebase we check if email is verified before log in
    private boolean checkEmailBeforeLogIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
        this.setContentView(R.layout.activity_sign_in);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
        }

        this.initWidget();
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

            int id = msg.what;
            switch (id) {
                case USER_LOGIN:
                    // TODO a volte a seguito del logout, il silent-signin di google manda un segnale di login (causato dalla onStart)
                    hideProgress();
                    Log.d(TAG, "handleMessage:Login");
                    // Check if email has been verified
                    if (checkEmailBeforeLogIn && (singletonFirebaseProvider
                            .getFirebaseUser() == null ||
                            !singletonFirebaseProvider
                                    .getFirebaseUser()
                                    .isEmailVerified())) {
                        Log.d(TAG, "handleMessage:login:email_not_verified");
                        break;
                    }

                    Log.d(TAG, "Login: email verified");
                    startNewActivity(SignInActivity.this, MainFragmentActivity.class);
                    break;

                case EMAIL_REQUIRED:
                    hideProgress();
                    Log.d(TAG, "handleMessage:email_required");
                    emailField.setError(getString(R.string.field_required));
                    break;

                case PASSWORD_REQUIRED:
                    hideProgress();
                    Log.d(TAG, "handleMessage:password_required");
                    passwordField.setError(getString(R.string.field_required));
                    break;

                case EMAIL_NOT_VERIFIED:
                    hideProgress();
                    Log.d(TAG, "handleMessage:email_not_verified");
                    emailField.setError(getString(R.string.email_not_verified));
                    break;

                case BAD_EMAIL_OR_PSW:
                    hideProgress();
                    Log.d(TAG, "handleMessage:invalid email or password");
                    emailField.setError(getString(R.string.wrong_email_or_psw));
                    passwordField.setError(getString(R.string.wrong_email_or_psw));
                    break;

                case INVALID_USER:
                    hideProgress();
                    Log.d(TAG, "handleMessage:invalid user");
                    emailField.setError(getString(R.string.invalid_user));
                    break;

                case BAD_FORMATTED_EMAIL:
                    hideProgress();
                    Log.d(TAG, "handleMessage:bad_formatted_email");
                    emailField.setError(getString(R.string.bad_formatted_email));
                    break;

                case NETWORK_ERROR:
                    hideProgress();
                    Log.d(TAG, "handleMessage:network_error");
                    Toast.makeText(SignInActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    break;

                case INTERNAL_FIREBASE_ERROR_LOGIN:
                    hideProgress();
                    Log.d(TAG, "handleMessage:internal firebase signin error");
                    Toast.makeText(SignInActivity.this, getString(R.string.internal_firebase_login_error), Toast.LENGTH_LONG).show();
                    break;

                case GOOGLE_SIGNIN_ERROR:
                    hideProgress();
                    Log.d(TAG, "handleMessage:google_signin_error");
                    // Toast.makeText(SignInActivity.this, getString(R.string.google_signin_error), Toast.LENGTH_LONG).show();
                    break;

                case CANCELED_ACTION:
                    hideProgress();
                    Log.d(TAG, "handleMessage:signin_error:action_canceled");
                    Toast.makeText(SignInActivity.this, getString(R.string.canceled_action), Toast.LENGTH_SHORT).show();
                    break;

                case UNKNOWN_ERROR:
                    hideProgress();
                    Log.d(TAG, "handleMessage:unknwown error");
                    Toast.makeText(SignInActivity.this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.w(TAG, "handleMessage:error: " + id);
            }
        }
    };

    private void startNewActivity(Context context, Class newClass) {
        Intent newIntent = new Intent(context, newClass);
        this.finish();
        this.startActivity(newIntent);
    }

    private void initWidget() {
        this.signInButton = findViewById(R.id.sign_in_button);
        this.signInGoogleButton = findViewById(R.id.sign_in_google_button);
        this.twitterLoginButton = findViewById(R.id.twitter_login_button);
        this.signUpButton = findViewById(R.id.sign_up_button);
        this.emailField = findViewById(R.id.email_field);
        this.passwordField = findViewById(R.id.password_field);
        this.progressBar = findViewById(R.id.progress_bar);

        this.signInButton.setOnClickListener(this);
        this.signInGoogleButton.setOnClickListener(this);
        this.signUpButton.setOnClickListener(this);
        this.twitterLoginButton.setOnClickListener(this);
        ((SingletonTwitterProvider) this.baseProviderArrayList
                .get(FactoryProviders.TWITTER_PROVIDER))
                .setCallback(this.twitterLoginButton);
    }

    private void initResources() {
        Log.d(TAG, "init resources");

        this.checkEmailBeforeLogIn = true;
        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance(this, this.handler);
        this.baseProviderArrayList = factoryProviders.getAllProviders();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SingletonGoogleProvider.RC_SIGN_IN:
                Log.d(TAG, "GOOGLE onActivityResult: " + requestCode);
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                        .activityResult(requestCode, resultCode, data);
                break;

            case SingletonTwitterProvider.RC_SIGN_IN:
                Log.d(TAG, "TWITTER onActivityResult: " + requestCode);
                // Pass the activity result to the login button.
                this.twitterLoginButton.onActivityResult(requestCode, resultCode, data);
                break;

            default:
                Log.w(TAG, "UNKNOWN requestCode: " + requestCode);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Sign in with email and password
            case R.id.sign_in_button:
                // Code strength
                this.checkEmailBeforeLogIn = true;
                ////
                if (TextUtils.isEmpty(this.emailField.getText().toString())) {
                    this.emailField.setError(getString(R.string.field_required));
                    break;
                } else if (TextUtils.isEmpty(this.passwordField.getText().toString())) {
                    this.passwordField.setError(getString(R.string.field_required));
                    break;
                }

                this.showProgress();
                this.baseProviderArrayList
                        .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                        .signIn(
                                StringParser.trimString(
                                        this.emailField.getText().toString()
                                ),
                                this.passwordField.getText().toString()
                        );
                break;

            // Sign in with Google
            case R.id.sign_in_google_button:
                this.checkEmailBeforeLogIn = false;
                this.showProgress();
                this.baseProviderArrayList
                        .get(FactoryProviders.GOOGLE_PROVIDER)
                        .signIn(null, null);
                break;

            // Sign in with Twitter
            case R.id.twitter_login_button:
                this.checkEmailBeforeLogIn = false;
                this.showProgress();
                this.baseProviderArrayList
                        .get(FactoryProviders.TWITTER_PROVIDER)
                        .signIn(null, null);
                break;

            // Sign up
            case R.id.sign_up_button:
                // Code strength
                this.checkEmailBeforeLogIn = true;
                ////
                Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                this.startActivity(signUpIntent);
                break;

            default:
                Log.w(TAG, "Unknown error in onClick");
        }
    }

    @Override
    public void hideProgress() {
        if (this.progressBar.getVisibility() == View.VISIBLE) {
            this.progressBar.setIndeterminate(true);
            this.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showProgress() {
        if (this.progressBar.getVisibility() == View.GONE) {
            this.progressBar.setIndeterminate(true);
            this.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, ":start");
        this.singletonFirebaseProvider.setListenerOwner(this.hashCode());
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
        this.singletonFirebaseProvider.setHandler(this.handler);
        this.singletonFirebaseProvider.setContext(this);

        ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                .silentSignIn();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, ":restart");
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, ":resume");
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, ":pause");
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, ":stop");
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, ":destroy");
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
        //((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
        //.removeGoogleClient();
    }
}
