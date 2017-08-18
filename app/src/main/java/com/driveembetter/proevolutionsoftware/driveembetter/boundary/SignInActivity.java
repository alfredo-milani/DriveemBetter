package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

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

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonGoogleProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonTwitterProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;


public class SignInActivity
        extends AppCompatActivity
        implements View.OnClickListener, TypeMessages {

    private final static String TAG = "SignInActivity";

    // Activity resources
    private ArrayList<FirebaseProvider> firebaseProviderArrayList;
    private User user;

    // Activity widgets
    private Button signInButton;
    private SignInButton signInGoogleButton;
    private TwitterLoginButton twitterLoginButton;
    private Button signUpButton;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_in_layout);
        this.initResources();
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
            switch (msg.what) {
                case USER_LOGIN:
                    Toast.makeText(SignInActivity.this, String.format(getString(R.string.sign_in_as), user.getEmail()), Toast.LENGTH_SHORT).show();

                    Intent mainFragmentIntent = new Intent(SignInActivity.this, MainFragmentActivity.class);
                    startActivity(mainFragmentIntent);
                    finish();
                    break;

                case USER_LOGOUT:
                    Log.d(TAG, "handleMessage:logout");
                    Toast.makeText(SignInActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
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

                case INVALID_CREDENTIALS:
                    Log.d(TAG, "handleMessage:invalid_credentials");
                    Toast.makeText(SignInActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    break;

                case INVALID_USER:
                    Log.d(TAG, "handleMessage:invalid user");
                    Toast.makeText(SignInActivity.this, getString(R.string.invalid_user), Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.w(TAG, "handleMessage:error");
            }
        }
    };

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
        this.twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                ((SingletonTwitterProvider) firebaseProviderArrayList.get(FactoryProviders.TWITTER_PROVIDER))
                        .handleTwitterSession(result.data);

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                Log.d(TAG, "USERNAME: " + session.getUserName());
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
            }
        });
    }

    private void initResources() {
        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);
        this.firebaseProviderArrayList = factoryProviders.createAllProviders();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SingletonGoogleProvider.RC_SIGN_IN:
                ((SingletonGoogleProvider) this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
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
            // Sign in with email and password
            case R.id.sign_in_button:
                this.progressBar.setVisibility(View.VISIBLE);
                this.firebaseProviderArrayList
                        .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                        .signIn(
                                this.emailField.getText().toString(),
                                this.passwordField.getText().toString()
                        );
                this.progressBar.setVisibility(View.GONE);
                break;

            // Sign in with Google
            case R.id.sign_in_google_button:
                FirebaseProvider firebaseGoogleProvider = this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER);
                firebaseGoogleProvider.initStateListener();
                firebaseGoogleProvider.setStateListener();
                this.progressBar.setVisibility(View.VISIBLE);
                firebaseGoogleProvider.signIn(null, null);
                this.progressBar.setVisibility(View.GONE);
                break;

            // Sign in with Twitter
            case R.id.twitter_login_button:
                this.progressBar.setVisibility(View.VISIBLE);
                this.firebaseProviderArrayList
                        .get(FactoryProviders.TWITTER_PROVIDER)
                        .signIn(null, null);
                this.progressBar.setVisibility(View.GONE);
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

        /*
        TODO rimuovere risorse GoogleApi
        mGoogleClient.stopAutoManage(getActivity());
    mGoogleClient.disconnect();
         */
    }
}
