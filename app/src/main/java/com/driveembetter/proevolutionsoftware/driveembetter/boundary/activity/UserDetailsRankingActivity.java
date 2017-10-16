package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.adapters.PageAdapter;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.GlideImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.VAN;

/**
 * Created by alfredo on 02/09/17.
 */

public class UserDetailsRankingActivity extends AppCompatActivity
        implements Constants,
        View.OnClickListener,
        PageFragment.UpdateUIGraph,
        ViewPager.OnPageChangeListener,
        FirebaseDatabaseManager.RetrieveVehiclesFromDB {

    private final static String TAG = UserDetailsRankingActivity.class.getSimpleName();

    private final static String formatData = "dd-MM-yyyy HH:mm:ss";

    // Resources
    private static User user;
    private SimpleDateFormat simpleDateFormat;

    // Widgets
    private TextView username;
    private TextView points;
    private ImageView imageView;
    private ImageView availability;
    private TextView feedback;
    private ImageButton startChatButton;
    private PagerAdapter adapter;
    private TextView titleGraph;
    private TextView subTitleGraph;
    private ImageButton fullscreenGraph;
    private ImageView currentImageVehicle;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_details_ranking);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        this.simpleDateFormat = new SimpleDateFormat(
                UserDetailsRankingActivity.formatData,
                Locale.getDefault()
        );
        UserDetailsRankingActivity.user = this.getIntent().getParcelableExtra(USER);
    }

    private void initWidgets() {
        // Set action bar title
        this.setTitle(R.string.detail_user);

        /* Display home as an "up" affordance:
         user that selecting home will return one level up rather than to the top level of the app */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.username = findViewById(R.id.user);
        this.points = findViewById(R.id.points);
        this.imageView = findViewById(R.id.user_picture);
        this.availability = findViewById(R.id.availability);
        this.feedback = findViewById(R.id.driverFeedbackContent);
        this.startChatButton = findViewById(R.id.startChatButton);
        this.titleGraph = findViewById(R.id.titleGraph);
        this.subTitleGraph = findViewById(R.id.subTitleGraph);
        this.fullscreenGraph = findViewById(R.id.fullscreenImageButton);
        this.currentImageVehicle = findViewById(R.id.currentVehicle);

        final ViewPager pager = findViewById(R.id.vpPager);
        this.adapter = new PageAdapter(this.getSupportFragmentManager());
        pager.setAdapter(this.adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "PAGET: " + position);
                PageFragment.pushDataToUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (UserDetailsRankingActivity.user.getUid().equals(SingletonUser.getInstance().getUid())) {
            this.startChatButton.setColorFilter(
                    ContextCompat.getColor(this, R.color.colorSchemasComplementary),
                    android.graphics.PorterDuff.Mode.MULTIPLY
            );
        } else {
            this.startChatButton.setOnClickListener(this);
        }

        if (UserDetailsRankingActivity.user.getUsername() != null && !user.getUsername().isEmpty()) {
            this.username.setText(UserDetailsRankingActivity.user.getUsername());
        } else {
            this.username.setText(UserDetailsRankingActivity.user.getUsernameFromUid());
        }
        this.points.setText(String.valueOf(UserDetailsRankingActivity.user.getPoints()));
        GlideImageLoader.loadImage(
                this,
                this.imageView,
                UserDetailsRankingActivity.user.getPhotoUrl(),
                R.mipmap.user_icon,
                R.mipmap.user_icon);
        if (UserDetailsRankingActivity.user.getAvailability().equals(AVAILABLE)) {
            this.availability.setImageResource(R.drawable.available_shape);
        } else {
            this.availability.setImageResource(R.drawable.unavailable_shape);
        }
        if (UserDetailsRankingActivity.user.getFeedback() != 0) {
            this.feedback.setText(String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    UserDetailsRankingActivity.user.getFeedback()
            ));
        }

        FirebaseDatabaseManager.getCurrentVehicleRanking(this, user.getUid());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fullscreenImageButton:
                // TODO: 15/10/17 fullscreen mode: vedi se fare con frgments
                Intent newIntent = new Intent(UserDetailsRankingActivity.this, ShowFullscreenGraph.class);
                newIntent.putExtra(USER, user);
                this.startActivity(newIntent);
                break;

            case R.id.startChatButton:
                if (UserDetailsRankingActivity.user.getEmail() == null ||
                        TextUtils.isEmpty(UserDetailsRankingActivity.user.getEmail()) ||
                        UserDetailsRankingActivity.user.getUid() == null ||
                        TextUtils.isEmpty(UserDetailsRankingActivity.user.getUid()) ||
                        UserDetailsRankingActivity.user.getToken() == null ||
                        TextUtils.isEmpty(UserDetailsRankingActivity.user.getToken())) {
                    Toast.makeText(this, getString(R.string.cannot_contact_user), Toast.LENGTH_SHORT).show();
                    break;
                }

                ChatActivity.startActivity(
                        this,
                        UserDetailsRankingActivity.user.getEmail(),
                        UserDetailsRankingActivity.user.getUid(),
                        UserDetailsRankingActivity.user.getToken()
                );
                break;

            case R.id.refreshGraph:

                break;
        }
    }

    @Override
    public void onUserVehiclesReceived(ArrayList<Vehicle> vehicles) {
        final String format = "android.resource://%s/%d";
        if (!vehicles.isEmpty()) {
            Uri uri;
            switch (vehicles.get(0).getType()) {
                case CAR:
                    uri = Uri.parse(String.format(
                            Locale.ENGLISH,
                            format,
                            this.getPackageName(),
                            R.mipmap.car
                    ));
                    break;

                case VAN:
                    uri = Uri.parse(String.format(
                            Locale.ENGLISH,
                            format,
                            this.getPackageName(),
                            R.mipmap.van
                    ));
                    break;

                case MOTO:
                    uri = Uri.parse(String.format(
                            Locale.ENGLISH,
                            format,
                            this.getPackageName(),
                            R.mipmap.moto
                    ));
                    break;

                default:
                    uri = Uri.parse(String.format(
                            Locale.ENGLISH,
                            format,
                            this.getPackageName(),
                            R.drawable.ic_answere_mark
                    ));
            }

            GlideImageLoader.loadImage(
                    this,
                    this.currentImageVehicle,
                    uri,
                    R.drawable.ic_answere_mark,
                    R.drawable.ic_answere_mark
            );
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void updateUI(String title, long subTitle, boolean clickable) {
        if (title != null) {
            this.titleGraph.setText(title);
        } else {
            this.titleGraph.setText(getString(R.string.error));
        }

        if (subTitle != 0) {
            this.subTitleGraph.setText(String.format(
                    Locale.ENGLISH,
                    "%s: %s",
                    getString(R.string.last_update),
                    this.simpleDateFormat.format(subTitle)
            ));
        } else {
            this.subTitleGraph.setText(getString(R.string.unknown));
        }

        if (clickable) {
            this.fullscreenGraph.setClickable(true);
        } else {
            this.fullscreenGraph.setClickable(false);
            this.fullscreenGraph.setColorFilter(
                    ContextCompat.getColor(this, R.color.colorSchemasComplementary),
                    android.graphics.PorterDuff.Mode.MULTIPLY
            );
        }
    }

    public static String getUserID() {
        return UserDetailsRankingActivity.user.getUid();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
