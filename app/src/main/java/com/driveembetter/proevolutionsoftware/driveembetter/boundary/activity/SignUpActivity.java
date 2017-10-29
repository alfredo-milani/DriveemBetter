package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonEmailAndPasswordProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.StringParser;

/**
 * Created by alfredo on 17/08/17.
 */

public class SignUpActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        TypeMessages,
        TaskProgressInterface {

    private final static String TAG = SignUpActivity.class.getSimpleName();

    // Activity resources
    private SingletonFirebaseProvider singletonFirebaseProvider;
    private SingletonEmailAndPasswordProvider singletonEmailAndPasswordProvider;

    // Activity widgets
    private Button signUpButton;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText passwordField2;
    private ProgressBar progressBar;
    private ImageButton backButton;
    private Button resendVerificationEmail;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sign_up);

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
                    Log.d(TAG, "login received");
                    break;
                
                case USER_LOGOUT:
                    Log.d(TAG, "logout received");
                    break;
                
                case USER_LOGIN_EMAIL_PSW:
                    hideProgress();
                    Log.d(TAG, "handleMessage:login emailPsw");
                    startNewActivity(SignUpActivity.this, MainFragmentActivity.class);
                    break;

                case EMAIL_NOT_VERIFIED:
                    hideProgress();
                    Log.d(TAG, "handleMessage:email_not_verified");
                    Toast.makeText(SignUpActivity.this, getString(R.string.email_not_verified), Toast.LENGTH_LONG).show();
                    break;

                case BAD_EMAIL_OR_PSW:
                    hideProgress();
                    Log.d(TAG, "handleMessage:invalid email or password");
                    emailField.setError(getString(R.string.wrong_email_or_psw));
                    passwordField.setError(getString(R.string.wrong_email_or_psw));
                    break;

                case USER_ALREADY_EXIST:
                    hideProgress();
                    Log.d(TAG, "handleMessage:user_already_exist");
                    emailField.setError(getString(R.string.user_exist));
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

                case VERIFICATION_EMAIL_SENT:
                    hideProgress();
                    Log.d(TAG, "handleMessage:verification_email_sent");
                    Toast.makeText(SignUpActivity.this, String.format(getString(R.string.verification_email_success), getString(R.string.app_name)), Toast.LENGTH_LONG).show();

                    previousActivity();
                    break;

                case VERIFICATION_EMAIL_NOT_SENT:
                    hideProgress();
                    Log.d(TAG, "handleMessage:verification_email_not_sent");
                    Toast.makeText(SignUpActivity.this, getString(R.string.verification_email_failure), Toast.LENGTH_LONG).show();
                    break;

                case BAD_FORMATTED_EMAIL:
                    hideProgress();
                    Log.d(TAG, "handleMessage:bad_formatted_email");
                    emailField.setError(getString(R.string.bad_formatted_email));
                    break;

                case PASSWORD_INVALID:
                    hideProgress();
                    Log.d(TAG, "handleMessage:invalid_password");
                    passwordField.setError(getString(R.string.password_invalid));
                    break;

                case INVALID_CREDENTIALS:
                    hideProgress();
                    Log.d(TAG, "handleMessage:invalid_credentials");
                    passwordField.setError(getString(R.string.invalid_credentials));
                    break;

                case INVALID_USER:
                    hideProgress();
                    Log.d(TAG, "handleMessage:invalid user");
                    emailField.setError(getString(R.string.invalid_user));
                    break;

                case RESEND_VERIFICATION_EMAIL:
                    hideProgress();
                    Log.d(TAG, "handleMessage:verification_email_resent");
                    Toast.makeText(SignUpActivity.this, getString(R.string.postponed_verification_email), Toast.LENGTH_LONG).show();
                    previousActivity();
                    break;

                case NETWORK_ERROR:
                    hideProgress();
                    Log.d(TAG, "handleMessage:networ_error");
                    Toast.makeText(SignUpActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.w(TAG, "handleMessage:error: " + msg.what);
            }
        }
    };

    private void previousActivity() {
        this.finish();
    }

    private void startNewActivity(Context context, Class newClass) {
        Intent newIntent = new Intent(context, newClass);
        // newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();
        this.startActivity(newIntent);
    }

    private void initResources() {
        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);

        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();
        this.singletonEmailAndPasswordProvider =
                factoryProviders.getEmailAndPasswordProvider();
    }

    private void initWidget() {
        this.signUpButton = findViewById(R.id.terms_button);
        this.usernameField = findViewById(R.id.username_field);
        this.emailField = findViewById(R.id.email_field);
        this.passwordField = findViewById(R.id.password_field);
        this.passwordField2 = findViewById(R.id.password_field2);
        this.progressBar = findViewById(R.id.progress_bar);
        this.backButton = findViewById(R.id.back_button);
        this.resendVerificationEmail = findViewById(R.id.resend_email);

        this.signUpButton.setOnClickListener(this);
        this.backButton.setOnClickListener(this);
        this.resendVerificationEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.terms_button:
                Log.d(TAG, "onClick:signUp");

                String psw = this.passwordField.getText().toString();
                String psw2 = this.passwordField2.getText().toString();
                String email = this.emailField.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    this.emailField.setError(getString(R.string.field_required));
                    break;
                } else if (TextUtils.isEmpty(psw)) {
                    this.passwordField.setError(getString(R.string.strEmptyField));
                    break;
                } else if (TextUtils.isEmpty(psw2)) {
                    this.passwordField2.setError(getString(R.string.strEmptyField));
                    break;
                } else if (!psw.equals(psw2)) {
                    this.passwordField.setError(getString(R.string.bad_new_psw));
                    this.passwordField2.setError(getString(R.string.bad_new_psw));
                    break;
                }

                this.showProgress();
                this.singletonEmailAndPasswordProvider.signUp(
                        StringParser.trimString(email),
                        psw2,
                        this.usernameField.getText().toString()
                );
                break;

            case R.id.back_button:
                this.onBackPressed();
                break;

            case R.id.resend_email:
                String pswResend = this.passwordField.getText().toString();
                String emailResend = this.emailField.getText().toString();
                if (TextUtils.isEmpty(emailResend)) {
                    this.emailField.setError(getString(R.string.field_required));
                    break;
                } else if (TextUtils.isEmpty(pswResend)) {
                    this.passwordField.setError(getString(R.string.strEmptyField));
                    break;
                }

                this.showProgress();
                this.singletonEmailAndPasswordProvider.resendVerificationEmail(
                        StringParser.trimString(
                                this.emailField.getText().toString()
                        ),
                        this.passwordField.getText().toString()
                );
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

        this.singletonFirebaseProvider.setListenerOwner(this.hashCode());
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
        this.singletonFirebaseProvider.setHandler(this.handler);
        this.singletonFirebaseProvider.setContext(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
    }

    @Override
    public void onStop() {
        super.onStop();

        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
        //((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
        //.removeGoogleClient();
    }
}
