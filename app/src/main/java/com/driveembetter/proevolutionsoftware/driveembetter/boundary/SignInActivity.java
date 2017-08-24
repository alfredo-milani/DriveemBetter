package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.content.Context;
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
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;


public class SignInActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        TypeMessages,
        com.driveembetter.proevolutionsoftware.driveembetter.boundary.ProgressBar {

    private final static String TAG = "SignInActivity";

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

    // If we are authenticated with Firebase we check if email is verified before login
    private boolean checkEmailBeforeLogIn;

    //DEBUG
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initResources();
        setContentView(R.layout.activity_sign_in);

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

            hideProgress();
            emailField.setError(null);
            passwordField.setError(null);
            switch (id) {
                case USER_LOGIN:
                    Log.d(TAG, "handleMessage:Login");
                    if (checkEmailBeforeLogIn && !singletonFirebaseProvider.isFirebaseSignIn()) {
                        checkEmailBeforeLogIn = false;
                        break;
                    }
                    startNewActivity(SignInActivity.this, MainFragmentActivity.class);
                    break;

                case EMAIL_REQUIRED:
                    Log.d(TAG, "handleMessage:email_required");
                    emailField.setError(getString(R.string.field_required));
                    break;

                case PASSWORD_REQUIRED:
                    Log.d(TAG, "handleMessage:password_required");
                    passwordField.setError(getString(R.string.field_required));
                    break;

                case EMAIL_NOT_VERIFIED:
                    Log.d(TAG, "handleMessage:email_not_verified");
                    Toast.makeText(SignInActivity.this, getString(R.string.email_not_verified), Toast.LENGTH_LONG).show();
                    break;

                case BAD_EMAIL_OR_PSW:
                    Log.d(TAG, "handleMessage:invalid email or password");
                    emailField.setError(getString(R.string.wrong_email_or_psw));
                    passwordField.setError(getString(R.string.wrong_email_or_psw));
                    break;

                case INVALID_USER:
                    Log.d(TAG, "handleMessage:invalid user");
                    emailField.setError(getString(R.string.invalid_user));
                    break;

                case BAD_FORMATTED_EMAIL:
                    Log.d(TAG, "handleMessage:bad_formatted_email");
                    emailField.setError(getString(R.string.bad_formatted_email));
                    break;

                case NETWORK_ERROR:
                    Log.d(TAG, "handleMessage:networ_error");
                    Toast.makeText(SignInActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    break;

                case GOOGLE_SIGNIN_ERROR:
                    Log.d(TAG, "handleMessage:google_signin_error");

                case UNKNOWN_ERROR:
                    Log.d(TAG, "handleMessage:google(firebase)_signin_error");
                    Toast.makeText(SignInActivity.this, getString(R.string.google_signin_error), Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.w(TAG, "handleMessage:error: " + id);
            }
        }
    };

    private void startNewActivity(Context context, Class newClass) {
        Intent mainFragmentIntent = new Intent(context, newClass);
        this.startActivity(mainFragmentIntent);
        this.finish();
    }

    private void initWidget() {
        this.signInButton = (Button) findViewById(R.id.sign_in_button);
        this.signInGoogleButton = (SignInButton) findViewById(R.id.sign_in_google_button);
        this.twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        this.signUpButton = (Button) findViewById(R.id.sign_up_button);
        this.emailField = (EditText) findViewById(R.id.email_field);
        this.passwordField = (EditText) findViewById(R.id.password_field);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        this.signInButton.setOnClickListener(this);
        this.signInGoogleButton.setOnClickListener(this);
        this.signUpButton.setOnClickListener(this);
        this.twitterLoginButton.setOnClickListener(this);
        ((SingletonTwitterProvider) this.baseProviderArrayList.get(FactoryProviders.TWITTER_PROVIDER))
                .setCallback(this.twitterLoginButton);

        //DEBUG
        this.imageView = (ImageView) findViewById(R.id.imageView7);
        this.imageView.setOnClickListener(this);
    }

    private void initResources() {
        this.checkEmailBeforeLogIn = false;
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
                // Pass the activity result to the login button.
                this.twitterLoginButton.onActivityResult(requestCode, resultCode, data);
                break;

            default:
                Log.w(TAG, "Unknown requestCode: " + requestCode);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // DEBUG
            case R.id.imageView7:
                User user = ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER)).getGoogleUserInformations();
                if (user != null) {
                    Log.d(TAG, "GOOGLE USER: " + user.getUsername() + " jjj " + user.getEmail());
                } else {
                    Log.d(TAG, "USER GOOGLE NULL");
                }

                if (this.singletonFirebaseProvider.getFirebaseUser() != null) {
                    Log.d(TAG, "USER FIRE: " + this.singletonFirebaseProvider.getFirebaseUser().getEmail());
                } else {
                    Log.d(TAG, "USER FIRE NULL");
                }

                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER)).cancan();
                break;


            // Sign in with email and password
            case R.id.sign_in_button:
                this.checkEmailBeforeLogIn = true;
                this.showProgress();
                this.baseProviderArrayList
                        .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                        .signIn(
                                this.emailField.getText().toString(),
                                this.passwordField.getText().toString()
                        );
                break;

            // Sign in with Google
            case R.id.sign_in_google_button:
                this.showProgress();
                this.baseProviderArrayList
                        .get(FactoryProviders.GOOGLE_PROVIDER)
                        .signIn(null, null);
                break;

            // Sign in with Twitter
            case R.id.twitter_login_button:
                this.baseProviderArrayList
                        .get(FactoryProviders.TWITTER_PROVIDER)
                        .signIn(null, null);
                break;

            // Sign up
            case R.id.sign_up_button:
                Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
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

        /*
        if (this.authenticationProvider == FactoryProviders.GOOGLE_PROVIDER) {
            SingletonGoogleProvider singletonGoogleProvider = ((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER));
            singletonGoogleProvider.connectAfterResume();
            singletonGoogleProvider.managePendingOperations();
        }
        */
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

        // this.hideProgress();

        Log.d(TAG, ":resume");
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.checkEmailBeforeLogIn = false;
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
