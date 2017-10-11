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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonEmailAndPasswordProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;

/**
 * Created by alfredo on 10/10/17.
 */

public class EditProfileData extends AppCompatActivity
        implements View.OnClickListener,
        SingletonEmailAndPasswordProvider.EditProfileCallback {

    private final static String TAG = EditProfileData.class.getSimpleName();

    private final static int RC_GALLERY = 9;

    // Widgets
    private ImageButton editUsernemButton;
    private LinearLayout editUsernameLayout;
    private EditText editTextUsername;
    private TextView usernameTextView;
    private Button backEditUsername;
    private Button confirmEditUsername;
    private ImageView userPicture;
    private TextView emailTextView;
    private ImageButton editProfilePictureButton;

    // Resources
    private SingletonUser user;
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
        this.editUsernemButton = findViewById(R.id.modifyUsername);
        this.editTextUsername = findViewById(R.id.editTextUsername);
        this.usernameTextView = findViewById(R.id.user);
        this.backEditUsername = findViewById(R.id.backModifyUsername);
        this.confirmEditUsername = findViewById(R.id.confirmModifyUsername);
        this.userPicture = findViewById(R.id.user_picture);
        this.emailTextView = findViewById(R.id.email);
        this.editProfilePictureButton = findViewById(R.id.editProfilePictureButton);

        this.editUsernemButton.setOnClickListener(this);
        this.confirmEditUsername.setOnClickListener(this);
        this.backEditUsername.setOnClickListener(this);
        this.editProfilePictureButton.setOnClickListener(this);

        if (this.user.getEmail() != null) {
            this.emailTextView.setText(this.user.getEmail());
        }
        if (this.user.getUsername() != null) {
            this.usernameTextView.setText(this.user.getUsername());
        }
        if (this.user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(this.user.getPhotoUrl().toString())
                    .dontTransform()
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(this.userPicture);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GALLERY) {
            switch (resultCode) {
                case RESULT_OK:
                    this.emailAndPasswordProvider.setImageProfile(this, data.getData());
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
                ), EditProfileData.RC_GALLERY);
                break;

            case R.id.modifyEmail:
                // TODO: 10/10/17
                break;
        }
    }

    @Override
    public void onProfileModified(int response) {
        switch (response) {
            case EditProfileData.UP_USERNAME_SUCCESS:
                Toast.makeText(this, getString(R.string.username_updated), Toast.LENGTH_SHORT).show();
                this.switchViewVisibility(this.editUsernameLayout);
                this.user.setUsername(this.emailAndPasswordProvider.getUserInformations().getUsername());
                this.usernameTextView.setText(this.user.getUsername());
                break;

            case EditProfileData.UP_USERNAME_FAILURE:
                Toast.makeText(this, getString(R.string.username_updated), Toast.LENGTH_LONG).show();
                break;

            case EditProfileData.UP_PICTURE_SUCCESS:
                Toast.makeText(this, getString(R.string.profile_picture_updated), Toast.LENGTH_SHORT).show();
                break;

            case EditProfileData.UP_PICTURE_FAILURE:
                Toast.makeText(this, getString(R.string.profile_picture_not_updated), Toast.LENGTH_LONG).show();
                break;
        }

        finish();
        startActivity(getIntent());
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
