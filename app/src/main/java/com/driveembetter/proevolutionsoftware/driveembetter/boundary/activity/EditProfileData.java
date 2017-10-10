package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by alfredo on 10/10/17.
 */

public class EditProfileData extends AppCompatActivity
        implements View.OnClickListener {

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
        this.editUsernameLayout = findViewById(R.id.change_username_data);
        this.editUsernemButton = findViewById(R.id.modifyUsername);
        this.editTextUsername = findViewById(R.id.editTextUsername);
        this.usernameTextView = findViewById(R.id.user);
        this.backEditUsername = findViewById(R.id.backModifyUsername);
        this.confirmEditUsername = findViewById(R.id.confirmModifyUsername);
        this.userPicture = findViewById(R.id.user_picture);
        this.emailTextView = findViewById(R.id.email);

        this.editUsernemButton.setOnClickListener(this);
        this.confirmEditUsername.setOnClickListener(this);
        this.backEditUsername.setOnClickListener(this);

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
                    Log.d(TAG, "DATA: " + data.getData());

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference riversRef = storageRef.child("images/"+data.getData().getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(data.getData());

// Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.d(TAG, "Unsuccess upload");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d(TAG, "Success upload");
                        }
                    });
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
                if (!this.user.getUsername().equals(tmp)) {
                    this.emailAndPasswordProvider.editUsername(
                            this.editTextUsername.getText().toString()
                    );
                } else if (tmp.isEmpty()) {
                    this.editTextUsername.setError(getString(R.string.invalid_user));
                } else {
                    this.editTextUsername.setError(getString(R.string.identical_username));
                }
                break;

            case R.id.modifyEmail:
                // TODO: 10/10/17
                break;
        }

        /*
        this.startActivityForResult(new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ), EditProfileData.RC_GALLERY);
        */
    }
}
