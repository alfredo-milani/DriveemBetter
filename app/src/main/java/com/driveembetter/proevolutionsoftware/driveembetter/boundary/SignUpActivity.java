package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
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

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.USER;

/**
 * Created by alfredo on 17/08/17.
 */

public class SignUpActivity
        extends AppCompatActivity
        implements View.OnClickListener, TypeMessages, com.driveembetter.proevolutionsoftware.driveembetter.boundary.ProgressBar {

    private final static String TAG = "SignUpActivity";

    // Activity resources
    private SingletonEmailAndPasswordProvider singletonEmailAndPasswordProvider;

    // Activity widgets
    private Button signUpButton;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progressBar;
    private Button backButton;
    private Button resendVerificationEmail;



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

            hideProgress();
            switch (msg.what) {
                case USER_LOGIN_EMAIL_PSW:
                    User userEmailPsw = singletonEmailAndPasswordProvider.getUserInformations();
                    Log.d(TAG, "handleMessage:log_in EmailAndPsw user: " + userEmailPsw.getEmail());
                    Toast.makeText(SignUpActivity.this, String.format(getString(R.string.sign_in_as), userEmailPsw.getEmail()), Toast.LENGTH_SHORT).show();

                    startActivityWithDatas(USER, userEmailPsw);
                    break;

                case EMAIL_NOT_VERIFIED:
                    Log.d(TAG, "handleMessage:email_not_verified");
                    Toast.makeText(SignUpActivity.this, getString(R.string.email_not_verified), Toast.LENGTH_LONG).show();
                    break;

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

                case BAD_FORMATTED_EMAIL:
                    Log.d(TAG, "handleMessage:bad_formatted_email");
                    emailField.setError(getString(R.string.bad_formatted_email));
                    break;

                case PASSWORD_INVALID:
                    Log.d(TAG, "handleMessage:invalid_password");
                    passwordField.setError(getString(R.string.password_invalid));
                    break;

                case INVALID_CREDENTIALS:
                    Log.d(TAG, "handleMessage:invalid_credentials");
                    passwordField.setError(getString(R.string.invalid_credentials));
                    break;

                case INVALID_USER:
                    Log.d(TAG, "handleMessage:invalid user");
                    emailField.setError(getString(R.string.invalid_user));
                    break;

                case RESEND_VERIFICATION_EMAIL:
                    Log.d(TAG, "handleMessage:verification_email_resent");
                    Toast.makeText(SignUpActivity.this, getString(R.string.postponed_verification_email), Toast.LENGTH_LONG).show();

                    Intent signInIntent2 = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(signInIntent2);
                    finish();
                    break;

                case NETWORK_ERROR:
                    Log.d(TAG, "handleMessage:networ_error");
                    Toast.makeText(SignUpActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.w(TAG, "handleMessage:error: " + msg.what);
            }
        }
    };

    private void startActivityWithDatas(String key, Parcelable value) {
        Intent mainFragmentIntent = new Intent(SignUpActivity.this, MainFragmentActivity.class);
        mainFragmentIntent.putExtra(key, value);
        this.startActivity(mainFragmentIntent);
        this.finish();
    }

    private void initResources() {
        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);

        this.singletonEmailAndPasswordProvider =
                factoryProviders.createEmailAndPasswordProvider();
        this.singletonEmailAndPasswordProvider
                .changeHandler(this.handler);
    }

    private void initWidget() {
        this.signUpButton = (Button) findViewById(R.id.sign_up_button);
        this.usernameField = (EditText) findViewById(R.id.username_field);
        this.emailField = (EditText) findViewById(R.id.email_field);
        this.passwordField = (EditText) findViewById(R.id.password_field);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.backButton = (Button) findViewById(R.id.back_button);
        this.resendVerificationEmail = (Button) findViewById(R.id.resend_email);

        this.signUpButton.setOnClickListener(this);
        this.backButton.setOnClickListener(this);
        this.resendVerificationEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_button:
                Log.d(TAG, "onClick:signUp");

                this.showProgress();
                this.singletonEmailAndPasswordProvider.signUp(
                        this.emailField.getText().toString(),
                        this.passwordField.getText().toString()
                );
                break;

            case R.id.back_button:
                this.onBackPressed();
                break;

            case R.id.resend_email:
                this.showProgress();
                this.singletonEmailAndPasswordProvider.resendVerificationEmail(
                        this.emailField.getText().toString(),
                        this.passwordField.getText().toString()
                );
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
