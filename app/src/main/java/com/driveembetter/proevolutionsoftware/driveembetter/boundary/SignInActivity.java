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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.EmailAndPasswordProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FacebookProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.GoogleProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFactoryProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TwitterProvider;
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


public class SignInActivity
        extends AppCompatActivity
        implements View.OnClickListener, TypeMessages {

    private final static String TAG = "SignInActivity";

    // Activity resources
    private EmailAndPasswordProvider emailAndPasswordProvider;
    private GoogleProvider googleProvider;
    private FacebookProvider facebookProvider;
    private TwitterProvider twitterProvider;
    private SingletonFactoryProvider singletonFactoryProvider;
    private User user;

    // Activity widgets
    private Button signInButton;
    private SignInButton signInGoogleButton;
    private TwitterLoginButton twitterLoginButton;
    private Button signUpButton;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progressBar;

    // DEBUG
    private TextView signout;
    private ImageView revoke;



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
                    user = googleProvider.getUserInformation();
                    Log.d(TAG, "handleMessage:login");
                    if (user == null) {
                        Log.e(TAG, "handleMessage:error");
                        break;
                    }
                    Toast.makeText(SignInActivity.this, "Signed in as: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    /*
                    Intent intent = new Intent(SignInActivity.this, MainFragmentActivity.class);
                    startActivity(intent);
                    finish();
                    */
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

                case INVALID_CREDENTIALS:
                    Log.d(TAG, "handleMessage:invalid_credentials");
                    Toast.makeText(SignInActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    break;

                case INVALID_USER:
                    Log.d(TAG, "handleMessage:invalid user");
                    Toast.makeText(SignInActivity.this, getString(R.string.invalid_user), Toast.LENGTH_SHORT).show();
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
                twitterProvider.handleTwitterSession(result.data);

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

        // DEBUG
        this.signout = (TextView) findViewById(R.id.textView4);
        this.signout.setOnClickListener(this);
        this.revoke = (ImageView) findViewById(R.id.imageView7);
        this.revoke.setOnClickListener(this);
    }

    private void initResources() {
        this.singletonFactoryProvider = new SingletonFactoryProvider(this, this.handler);

        this.twitterProvider = this.singletonFactoryProvider.getSingletonTwitterProvider();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (this.googleProvider != null && this.googleProvider.getClass() == GoogleProvider.class) {
            this.googleProvider.activityResult(requestCode, resultCode, data);
        } else if (this.twitterProvider != null && this.twitterProvider.getClass() == TwitterProvider.class) {
            // Pass the activity result to the login button.
            this.twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //DEBUG
            case R.id.textView4:
                this.googleProvider = this.singletonFactoryProvider.getSingletonGoogleProvider();
                this.googleProvider.signOut();
                break;
            case R.id.imageView7:
                /*
                this.googleProvider = this.singletonFactoryProvider.getSingletonGoogleProvider();
                this.googleProvider.revokeAccess();
                */
                this.googleProvider.clearAccount();
                break;

            // sign in with email and password
            case R.id.sign_in_button:
                this.emailAndPasswordProvider = this.singletonFactoryProvider.getSingletonSingletonEmailAndPasswordProvider();

                this.progressBar.setVisibility(View.VISIBLE);
                this.emailAndPasswordProvider.signIn(
                        this.emailField.getText().toString(),
                        this.passwordField.getText().toString()
                );
                this.progressBar.setVisibility(View.GONE);
                break;

            // sign in with Google
            case R.id.sign_in_google_button:
                this.googleProvider = this.singletonFactoryProvider.getSingletonGoogleProvider();

                this.googleProvider.initStateListener();
                this.googleProvider.setStateListener();
                this.progressBar.setVisibility(View.VISIBLE);
                this.googleProvider.signIn(null, null);
                this.progressBar.setVisibility(View.GONE);
                break;

            // sign in with Twitter
            case R.id.twitter_login_button:
                this.twitterProvider = this.singletonFactoryProvider.getSingletonTwitterProvider();
                this.progressBar.setVisibility(View.VISIBLE);
                this.twitterProvider.signIn(null, null);
                this.progressBar.setVisibility(View.GONE);
                break;

            // sign up
            case R.id.sign_up_button:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
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
        if (this.googleProvider != null)
            this.googleProvider.setStateListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.googleProvider != null)
            this.googleProvider.removeStateListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (this.googleProvider != null)
            this.googleProvider.setStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.googleProvider != null)
            this.googleProvider.setStateListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.googleProvider != null)
            this.googleProvider.removeStateListener();
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
