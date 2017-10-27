package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.adapters.RankingGraphPageAdapter;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.GlideImageLoader;

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
        FirebaseDatabaseManager.RetrieveVehiclesFromDB {

    private final static String TAG = UserDetailsRankingActivity.class.getSimpleName();

    // Resources
    private static User user;

    // Widgets
    private TextView username;
    private TextView points;
    private ImageView imageView;
    private ImageView availability;
    private TextView feedback;
    private ImageButton startChatButton;
    private PagerAdapter adapter;
    private ImageView currentImageVehicle;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_details_ranking);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
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
        this.currentImageVehicle = findViewById(R.id.currentVehicle);

        final ViewPager pager = findViewById(R.id.vpPager);
        this.adapter = new RankingGraphPageAdapter(
                this.getSupportFragmentManager(),
                UserDetailsRankingActivity.user.getUid()
        );
        pager.setAdapter(this.adapter);
        // Increase cache limit
        // TODO: 18/10/17 Per ora ci sono 5 tipi di grafici, quindi con un valore come 4 non viene distrutto nessun fragment. Nel caso in cui si qualche fragment venisse distrutto (aumento numero fragments o diminuzione valore di offset) gestire la ricostruzione del fragment (rendering legenda ecc...)
        // pager.setOffscreenPageLimit(4); // TODO: 26/10/17 BUG DATO DA QUESTA RIGA DI CODICE
        // pager.setPageTransformer(true, new AccordionTransformer());
        // pager.setPageTransformer(true, new DepthPageTransformer());
        // pager.setPageTransformer(true, new ZoomOutSlideTransformer());
        // pager.setPageTransformer(true, new CubeInTransformer());
        pager.setPageTransformer(true, new FlipHorizontalTransformer());
        // To set default page fragment
        // pager.setCurrentItem(int currentItem);

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
        GlideImageLoader.loadImageUri(
                this,
                this.imageView,
                UserDetailsRankingActivity.user.getPhotoUrl(),
                R.mipmap.user_icon,
                R.mipmap.user_icon);
        if (UserDetailsRankingActivity.user.getAvailability().equals(AVAILABLE)) {
            GlideImageLoader.loadImageUri(
                    this,
                    this.availability,
                    null,
                    R.drawable.available_shape,
                    R.drawable.available_shape
            );
            FirebaseDatabaseManager.getCurrentVehicleRanking(this, user.getUid());
        } else {
            GlideImageLoader.loadImageUri(
                    this,
                    this.availability,
                    null,
                    R.drawable.unavailable_shape,
                    R.drawable.unavailable_shape
            );
            this.currentImageVehicle.setVisibility(View.GONE);
        }
        if (UserDetailsRankingActivity.user.getFeedback() != 0) {
            this.feedback.setText(String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    UserDetailsRankingActivity.user.getFeedback()
            ));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
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
                        UserDetailsRankingActivity.user.getUsername(),
                        UserDetailsRankingActivity.user.getUid(),
                        UserDetailsRankingActivity.user.getToken()
                );
                break;
        }
    }

    @Override
    public void onUserVehiclesReceived(ArrayList<Vehicle> vehicles) {
        if (!vehicles.isEmpty()) {
            Uri uri;
            switch (vehicles.get(0).getType()) {
                case CAR:
                    uri = GlideImageLoader.fromResourceToUri(this, R.mipmap.car);
                    break;

                case VAN:
                    uri = GlideImageLoader.fromResourceToUri(this, R.mipmap.van);
                    break;

                case MOTO:
                    uri = GlideImageLoader.fromResourceToUri(this, R.mipmap.moto);
                    break;

                default:
                    uri = GlideImageLoader.fromResourceToUri(this, R.drawable.ic_answere_mark);
            }

            GlideImageLoader.loadImageUri(
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
}
