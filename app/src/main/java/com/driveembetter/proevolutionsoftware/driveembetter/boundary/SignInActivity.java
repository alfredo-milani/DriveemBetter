package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
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
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PROVIDER_TYPE;


public class SignInActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        TypeMessages,
        com.driveembetter.proevolutionsoftware.driveembetter.boundary.ProgressBar {

    private final static String TAG = "SignInActivity";

    // Activity resources
    private ArrayList<FirebaseProvider> firebaseProviderArrayList;

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
        this.initResources();
        setContentView(R.layout.sign_in_layout);

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

            hideProgress();
            switch (msg.what) {
                case USER_LOGIN_EMAIL_PSW:
                    User userEmailPsw = firebaseProviderArrayList
                            .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                            .getUserInformations();
                    Log.d(TAG, "handleMessage:log_in EmailAndPsw user: " + userEmailPsw.getEmail());
                    Toast.makeText(SignInActivity.this, String.format(getString(R.string.sign_in_as), userEmailPsw.getEmail()), Toast.LENGTH_SHORT).show();

                    startActivityWithDatas(PROVIDER_TYPE, FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER);
                    break;

                case USER_LOGIN_GOOGLE:
                    User userGoogle = ((SingletonGoogleProvider) firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                            .getGoogleUserInformations();
                    Log.d(TAG, "handleMessage:log_in Google user: " + userGoogle.getUsername());
                    Toast.makeText(SignInActivity.this, String.format(getString(R.string.sign_in_as), userGoogle.getUsername()), Toast.LENGTH_SHORT).show();

                    startActivityWithDatas(PROVIDER_TYPE, FactoryProviders.GOOGLE_PROVIDER);
                    break;

                case USER_LOGIN_FACEBOOK:
                    Log.d(TAG, "handleMessage:log_in Facebook user: ");
                    break;

                case USER_LOGIN_TWITTER:
                    User userTwitter = ((SingletonTwitterProvider) firebaseProviderArrayList.get(FactoryProviders.TWITTER_PROVIDER))
                            .getTwitterUserInformations();
                    Log.d(TAG, "handleMessage:log_in Twitter user: ");
                    Toast.makeText(SignInActivity.this, String.format(getString(R.string.sign_in_as), userTwitter.getUsername()), Toast.LENGTH_SHORT).show();

                    startActivityWithDatas(PROVIDER_TYPE, FactoryProviders.TWITTER_PROVIDER);
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
                    passwordField.setError(getString(R.string.invalid_credentials));
                    break;

                case INVALID_USER:
                    Log.d(TAG, "handleMessage:invalid user");
                    emailField.setError(getString(R.string.invalid_user));
                    break;

                default:
                    Log.w(TAG, "handleMessage:error");
            }
        }
    };

    private void startActivityWithDatas(String key, Parcelable value) {
        Intent mainFragmentIntent = new Intent(SignInActivity.this, MainFragmentActivity.class);
        mainFragmentIntent.putExtra(key, value);
        this.startActivity(mainFragmentIntent);
        this.finish();
    }

    private void startActivityWithDatas(String key, int value) {
        Intent mainFragmentIntent = new Intent(SignInActivity.this, MainFragmentActivity.class);
        mainFragmentIntent.putExtra(key, value);
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
        ((SingletonTwitterProvider) this.firebaseProviderArrayList.get(FactoryProviders.TWITTER_PROVIDER))
                .setCallback(this.twitterLoginButton);
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
                this.showProgress();
                this.firebaseProviderArrayList
                        .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                        .signIn(
                                this.emailField.getText().toString(),
                                this.passwordField.getText().toString()
                        );
                break;

            // Sign in with Google
            case R.id.sign_in_google_button:
                this.showProgress();
                SingletonGoogleProvider singletonGoogleProvider = ((SingletonGoogleProvider) this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER));
                if (singletonGoogleProvider.getUserInformations() == null) {
                    singletonGoogleProvider.signIn(null, null);
                } else {
                    // singletonGoogleProvider.managePendingOperations();
                    // singletonGoogleProvider.asyncReSign();
                    Message message = this.handler.obtainMessage(USER_LOGIN_GOOGLE);
                    this.handler.sendMessage(message);
                }
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
        this.progressBar.setIndeterminate(true);
        this.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        this.progressBar.setIndeterminate(true);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    // TODO imposta/togli listeners per autenticazione una volta ripresa dalla sospensione
    @Override
    public void onStart() {
        super.onStart();

        this.firebaseProviderArrayList
                .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                .changeHandler(this.handler);

        SingletonGoogleProvider singletonGoogleProvider = ((SingletonGoogleProvider) this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER));
        singletonGoogleProvider.connectAfterResume();
        singletonGoogleProvider.managePendingOperations();

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

        this.hideProgress();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //((SingletonGoogleProvider) this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                //.removeGoogleClient();
    }
}
