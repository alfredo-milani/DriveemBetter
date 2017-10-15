package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonEmailAndPasswordProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseStorageManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.GlideImageLoader;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.StringParser;

/**
 * Created by alfredo on 10/10/17.
 */

public class EditProfileDataActivity extends AppCompatActivity
        implements View.OnClickListener,
        SingletonEmailAndPasswordProvider.EditProfileCallback {

    private final static String TAG = EditProfileDataActivity.class.getSimpleName();

    private final static int RC_GALLERY = 9;

    // Widgets
    private ImageButton editUsernameButton;
    private LinearLayout editUsernameLayout;
    private EditText editTextUsername;
    private TextView usernameTextView;
    private Button backEditUsername;
    private Button confirmEditUsername;

    private ImageView userPicture;
    private ImageButton editProfilePictureButton;

    private ImageView editPasswordButton;

    private LinearLayout editPasswordLayout;
    private EditText editTextNewPsw;
    private EditText editTextNewPsw2;
    private Button backEditPassword;
    private Button confirmEditPassword;

    private TextView editEmailTextView;
    private Button editEmailButtonConfirm;
    private Button editEmailButtonCancel;
    private LinearLayout editEmailLayout;
    private ImageButton editEmailButton;
    private EditText editTextEmail;
    ////

    // Resources
    private SingletonUser user;
    private SingletonFirebaseProvider singletonFirebaseProvider;
    private SingletonEmailAndPasswordProvider emailAndPasswordProvider;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_data);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.user = SingletonUser.getInstance();
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();
        this.emailAndPasswordProvider = SingletonEmailAndPasswordProvider.getInstance();
    }

    private void initWidgets() {
        // Set action bar title
        this.setTitle(R.string.edit_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.editUsernameLayout = findViewById(R.id.change_username_data);
        this.editUsernameButton = findViewById(R.id.modifyUsername);
        this.editTextUsername = findViewById(R.id.editTextUsername);
        this.usernameTextView = findViewById(R.id.user);
        this.backEditUsername = findViewById(R.id.backModifyUsername);
        this.confirmEditUsername = findViewById(R.id.confirmModifyUsername);
        this.userPicture = findViewById(R.id.user_picture);
        this.editProfilePictureButton = findViewById(R.id.editProfilePictureButton);
        this.editPasswordButton = findViewById(R.id.imageView3);
        this.editPasswordLayout = findViewById(R.id.change_password_data);
        this.editTextNewPsw = findViewById(R.id.editTextPasswordNew);
        this.editTextNewPsw2 = findViewById(R.id.editTextPasswordNew2);
        this.confirmEditPassword = findViewById(R.id.confirmModifyPassword);
        this.backEditPassword = findViewById(R.id.backModifyPassword);
        this.editEmailTextView = findViewById(R.id.email);
        this.editEmailLayout = findViewById(R.id.change_email_data);
        this.editEmailButtonCancel = findViewById(R.id.backModifyEmail);
        this.editEmailButtonConfirm = findViewById(R.id.confirmModifyEmail);
        this.editTextEmail = findViewById(R.id.editTextEmail);
        this.editEmailButton = findViewById(R.id.modifyEmailLayout);

        this.editUsernameButton.setOnClickListener(this);
        this.confirmEditUsername.setOnClickListener(this);
        this.backEditUsername.setOnClickListener(this);
        this.editProfilePictureButton.setOnClickListener(this);
        this.editPasswordButton.setOnClickListener(this);
        this.backEditPassword.setOnClickListener(this);
        this.confirmEditPassword.setOnClickListener(this);
        this.editEmailButtonConfirm.setOnClickListener(this);
        this.editEmailButtonCancel.setOnClickListener(this);
        this.editEmailButton.setOnClickListener(this);

        if (this.user.getEmail() != null) {
            this.editEmailTextView.setText(this.user.getEmail());
        }
        if (this.user.getUsername() != null) {
            this.usernameTextView.setText(this.user.getUsername());
        }
        GlideImageLoader.loadImage(
                this,
                this.userPicture,
                this.user.getPhotoUrl(),
                R.mipmap.user_icon,
                R.mipmap.user_icon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GALLERY) {
            switch (resultCode) {
                case RESULT_OK:
                    FirebaseStorageManager.uploadProfileImage(this, data.getData());
                    break;

                case RESULT_CANCELED:
                    Toast.makeText(this, getString(R.string.canceled_action), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.d(TAG, "activityResult RC: " + requestCode + " / " + resultCode);
            }
        }
    }

    private void switchViewVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.modifyUsername:
                this.switchViewVisibility(this.editUsernameLayout);
                break;

            case R.id.backModifyUsername:
                this.switchViewVisibility(this.editUsernameLayout);
                break;

            case R.id.confirmModifyUsername:
                String tmp = this.editTextUsername.getText().toString();
                if (TextUtils.isEmpty(tmp)) {
                    this.editTextUsername.setError(getString(R.string.invalid_user));
                } else if (tmp.equals(user.getUsername())) {
                    this.editTextUsername.setError(getString(R.string.identical_username));
                } else {
                    this.emailAndPasswordProvider.editUsername(
                            this,
                            this.editTextUsername.getText().toString()
                    );
                }
                break;

            case R.id.editProfilePictureButton:
                Toast.makeText(this, getString(R.string.choose_new_profile_picture), Toast.LENGTH_LONG).show();
                this.startActivityForResult(new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                ), EditProfileDataActivity.RC_GALLERY);
                break;

            case R.id.imageView3:
                this.switchViewVisibility(this.editPasswordLayout);
                break;

            case R.id.backModifyPassword:
                this.switchViewVisibility(this.editPasswordLayout);
                break;

            case R.id.confirmModifyPassword:
                String newPsw = this.editTextNewPsw.getText().toString();
                String newPsw2 = this.editTextNewPsw2.getText().toString();

                if (TextUtils.isEmpty(newPsw)) {
                    this.editTextNewPsw.setError(getString(R.string.strEmptyField));
                    break;
                } else if (TextUtils.isEmpty(newPsw2)) {
                    this.editTextNewPsw2.setError(getString(R.string.strEmptyField));
                    break;
                } else if (!newPsw.equals(newPsw2)) {
                    this.editTextNewPsw.setError(getString(R.string.bad_new_psw));
                    this.editTextNewPsw2.setError(getString(R.string.bad_new_psw));
                    break;
                } else if (newPsw2.length() < 6) {
                    this.editTextNewPsw.setError(getString(R.string.password_invalid));
                    this.editTextNewPsw2.setError(getString(R.string.password_invalid));
                    break;
                }

                this.emailAndPasswordProvider.setPassword(this, newPsw2);
                break;

            case R.id.modifyEmailLayout:
                this.switchViewVisibility(this.editEmailLayout);
                break;

            case R.id.backModifyEmail:
                this.switchViewVisibility(this.editEmailLayout);
                break;

            case R.id.confirmModifyEmail:
                String tmpEmail = this.editTextEmail.getText().toString();
                String[] tmpEmailCheck = tmpEmail.split("@");
                if (TextUtils.isEmpty(tmpEmail)) {
                    this.editTextEmail.setError(getString(R.string.strEmptyField));
                    break;
                } else if (tmpEmailCheck.length != 2 || !tmpEmailCheck[1].contains(".")) {
                    this.editTextEmail.setError(getString(R.string.bad_formatted_email));
                    break;
                }

                this.emailAndPasswordProvider.setEmail(
                        this,
                        StringParser.trimString(tmpEmail)
                );
                break;
        }
    }

    private void syncUserData() {
        SingletonUser.resetSession();
        this.user = SingletonUser.getInstance(
                this.singletonFirebaseProvider.getFirebaseUser().getDisplayName(),
                this.singletonFirebaseProvider.getFirebaseUser().getEmail(),
                this.singletonFirebaseProvider.getFirebaseUser().getPhotoUrl(),
                this.singletonFirebaseProvider.getFirebaseUser().getUid(),
                this.singletonFirebaseProvider.getFirebaseUser().isEmailVerified(),
                null,
                null
        );
        this.singletonFirebaseProvider.sendMessageToUI(TypeMessages.USER_SYNC_REQUEST);
    }

    @Override
    public void onProfileModified(int response) {
        switch (response) {
            case EditProfileDataActivity.UP_USERNAME_SUCCESS:
                Toast.makeText(this, getString(R.string.username_updated), Toast.LENGTH_SHORT).show();
                this.switchViewVisibility(this.editUsernameLayout);
                this.syncUserData();
                this.usernameTextView.setText(this.user.getUsername());
                break;

            case EditProfileDataActivity.UP_USERNAME_FAILURE:
                Toast.makeText(this, getString(R.string.username_updated), Toast.LENGTH_LONG).show();
                break;

            case EditProfileDataActivity.UP_PICTURE_SUCCESS_STORAGE:
                this.emailAndPasswordProvider.setImageProfile(this, this.user.getPhotoUrl());
                break;

            case EditProfileDataActivity.UP_PICTURE_FAILURE_STORAGE:
                Toast.makeText(this, getString(R.string.profile_picture_not_updated), Toast.LENGTH_LONG).show();
                break;

            case EditProfileDataActivity.UP_PICTURE_SUCCESS_AUTH:
                this.syncUserData();
                GlideImageLoader.loadImage(
                        this,
                        this.userPicture,
                        this.user.getPhotoUrl(),
                        R.mipmap.user_icon,
                        R.mipmap.user_icon
                );
                Toast.makeText(this, getString(R.string.profile_picture_updated), Toast.LENGTH_SHORT).show();
                break;

            case EditProfileDataActivity.UP_PICTURE_FAILURE_AUTH:
                Toast.makeText(this, getString(R.string.profile_picture_not_updated), Toast.LENGTH_LONG).show();
                break;

            case EditProfileDataActivity.UP_PASSWORD_SUCCESS:
                Toast.makeText(this, getString(R.string.psw_updated), Toast.LENGTH_LONG).show();
                this.switchViewVisibility(this.editPasswordLayout);
                break;

            case EditProfileDataActivity.UP_PASSWORD_FAILURE:
                Toast.makeText(this, getString(R.string.psw_not_updated), Toast.LENGTH_LONG).show();
                break;

            case EditProfileDataActivity.UP_EMAIL_SUCCESS:
                this.syncUserData();
                Toast.makeText(this, getString(R.string.email_updated), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, getString(R.string.verification_email_success), Toast.LENGTH_SHORT).show();
                this.editEmailTextView.setText(this.user.getEmail());
                break;

            case EditProfileDataActivity.UP_EMAIL_FAILURE:
                Toast.makeText(this, getString(R.string.email_not_updated), Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
