package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonGoogleProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonTwitterProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;

import java.util.ArrayList;

/**
 * Created by alfredo on 17/08/17.
 */

public class MainFragmentActivity
        extends AppCompatActivity
        implements Constants,
        NavigationView.OnNavigationItemSelectedListener,
        com.driveembetter.proevolutionsoftware.driveembetter.boundary.ProgressBar,
        TypeMessages {

    private final static String TAG = "MainFragmentActivity";

    // Resources
    private ArrayList<FirebaseProvider> firebaseProviderArrayList;
    private User user;
    private int authenticationProvider;

    // Widgets
    private android.widget.ProgressBar progressBar;
    private TextView usernameTextView;
    private View headerView;



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
                    Log.d(TAG, "handleMessage:log_in EmailAndPsw login");
                    if (user != null) {
                        Toast.makeText(
                                MainFragmentActivity.this,
                                String.format(getString(R.string.sign_in_as), user.getEmail()),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;

                case USER_LOGIN_GOOGLE:
                    Log.d(TAG, "handleMessage:log_in Google login");
                    if (user != null) {
                        Toast.makeText(
                                MainFragmentActivity.this,
                                String.format(getString(R.string.sign_in_as), user.getEmail()),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;

                case USER_LOGIN_FACEBOOK:
                    Log.d(TAG, "handleMessage:log_in Facebook login");
                    if (user != null) {
                        Toast.makeText(
                                MainFragmentActivity.this,
                                String.format(getString(R.string.sign_in_as), user.getEmail()),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;

                case USER_LOGIN_TWITTER:
                    Log.d(TAG, "handleMessage:log_in Twitter login");
                    if (user != null) {
                        Toast.makeText(
                                MainFragmentActivity.this,
                                String.format(getString(R.string.sign_in_as), user.getEmail()),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;

                case USER_LOGOUT_EMAIL_PSW:
                case USER_LOGOUT_GOOGLE:
                case USER_LOGOUT_FACEBOOK:
                case USER_LOGOUT_TWITTER:
                    Toast.makeText(MainFragmentActivity.this, getString(R.string.logout), Toast.LENGTH_SHORT).show();
                    returnToSignIn();
                    break;

                default:
                    Log.d(TAG, "MSG: " + msg.what);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.headerView =  navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        this.initResources();
        this.initWidgets();
    }

    private void initResources() {
        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);
        this.firebaseProviderArrayList = factoryProviders.createAllProviders();

        this.changeAllProvidersHandlers();
        this.user = this.getUser();

    }

    private User getUser() {
        Intent i = getIntent();
        this.authenticationProvider = i.getIntExtra(PROVIDER_TYPE, 0);

        switch (this.authenticationProvider) {
            case FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER:
                Log.d(TAG, "MainFragmentActivity:EmailPswProv: " + this.authenticationProvider);
                return this.firebaseProviderArrayList.get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                        .getUserInformations();

            case FactoryProviders.GOOGLE_PROVIDER:
                //if (this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER) == null)
                Log.d(TAG, "MainFragmentActivity:GoogleProv: " + this.authenticationProvider);
                return this.user = ((SingletonGoogleProvider) this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                        .getGoogleUserInformations();

            case FactoryProviders.TWITTER_PROVIDER:
                Log.d(TAG, "MainFragmentActivity:TwitterProv: " + this.authenticationProvider);
                return  ((SingletonTwitterProvider) this.firebaseProviderArrayList.get(FactoryProviders.TWITTER_PROVIDER))
                        .getTwitterUserInformations();

            case FactoryProviders.FACEBOOK_PROVIDER:
                Log.d(TAG, "MainFragmentActivity:FacebookProv: " + this.authenticationProvider);
                return this.firebaseProviderArrayList.get(FactoryProviders.FACEBOOK_PROVIDER)
                        .getUserInformations();

            default:
                Log.d(TAG, "MainFragmentActivity:Error while getting user: " + this.authenticationProvider);
                return null;
        }
    }

    private void changeAllProvidersHandlers() {
        this.firebaseProviderArrayList
                .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                .changeHandler(this.handler);
        this.firebaseProviderArrayList
                .get(FactoryProviders.GOOGLE_PROVIDER)
                .changeHandler(this.handler);
        this.firebaseProviderArrayList
                .get(FactoryProviders.FACEBOOK_PROVIDER)
                .changeHandler(this.handler);
        this.firebaseProviderArrayList
                .get(FactoryProviders.TWITTER_PROVIDER)
                .changeHandler(this.handler);
    }

    private void initWidgets() {
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.usernameTextView = this.headerView.findViewById(R.id.username_text_view);
        if (this.user != null) {
            this.usernameTextView.setText(this.user.getUsername());
        } else {
            Toast.makeText(MainFragmentActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            this.returnToSignIn();
        }
    }

    private void returnToSignIn() {
        Intent mainFragmentIntent = new Intent(MainFragmentActivity.this, SignInActivity.class);
        this.startActivity(mainFragmentIntent);
        this.finish();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                // Handle the camera action
                Log.d(TAG, "Camera pressed");
                break;

            case R.id.nav_gallery:
                break;

            case R.id.nav_slideshow:
                break;

            case R.id.nav_manage:
                break;

            case R.id.nav_share:
                break;

            case R.id.nav_send:
                break;

            case R.id.nav_logout:
                Log.d(TAG, "Logout pressed");

                super.onBackPressed();
                break;

            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.firebaseProviderArrayList
                .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                .changeHandler(this.handler);

        SingletonGoogleProvider singletonGoogleProvider = ((SingletonGoogleProvider) this.firebaseProviderArrayList
                .get(FactoryProviders.GOOGLE_PROVIDER));
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ((SingletonGoogleProvider) this.firebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                .removeGoogleClient();
    }
}
