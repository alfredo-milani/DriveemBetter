package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.content.Context;
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
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;

import java.util.ArrayList;

/**
 * Created by alfredo on 17/08/17.
 */

public class MainFragmentActivity
        extends AppCompatActivity
        implements Constants, TypeMessages,
        NavigationView.OnNavigationItemSelectedListener,
        com.driveembetter.proevolutionsoftware.driveembetter.boundary.ProgressBar {

    private final static String TAG = "MainFragmentActivity";

    // Resources
    private ArrayList<BaseProvider> baseProviderArrayList;
    private SingletonFirebaseProvider singletonFirebaseProvider;
    private User user;

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
                case USER_LOGIN:
                    Toast.makeText(
                            MainFragmentActivity.this,
                            String.format(getString(R.string.sign_in_as),
                                    user.getEmail()), Toast.LENGTH_SHORT
                    ).show();
                    break;

                case USER_LOGOUT:
                    Toast.makeText(MainFragmentActivity.this, getString(R.string.logout), Toast.LENGTH_SHORT).show();
                    startActivity(MainFragmentActivity.this, SignInActivity.class);
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
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();
        this.baseProviderArrayList = factoryProviders.getAllProviders();
        this.user = this.singletonFirebaseProvider.getUserInformations();
    }

    private void initWidgets() {
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.usernameTextView = this.headerView.findViewById(R.id.username_text_view);
        if (this.user != null) {
            this.usernameTextView.setText(this.user.getEmail());
        } else {
            Toast.makeText(MainFragmentActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            this.returnToSignIn();
        }
    }

    private void returnToSignIn() {
        this.finish();
    }

    private void startActivity(Context context, Class newClass) {
        Intent mainFragmentIntent = new Intent(context, newClass);
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
            // TODO elimina riga se torna alla activity di login
            super.onBackPressed();
        }
    }

    private void logoutCurrentProviders() {
        for (BaseProvider baseProvider:
                this.baseProviderArrayList) {
            if (baseProvider.isSignIn()) {
                Log.d(TAG, "Logging out: " + baseProvider.getClass().toString());
                baseProvider.signOut();
            }
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
                this.singletonFirebaseProvider.ddddd();
                break;

            case R.id.nav_send:
                // TODO controlla se sono settati i listeners
                this.singletonFirebaseProvider.setStateListener();
                break;

            case R.id.nav_logout:
                Log.d(TAG, "Logout pressed");

                this.logoutCurrentProviders();
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

        this.singletonFirebaseProvider.setStateListener();
        this.singletonFirebaseProvider.setHandler(this.handler);

        /*
        if (this.authenticationProvider == FactoryProviders.GOOGLE_PROVIDER) {
            SingletonGoogleProvider singletonGoogleProvider = ((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER));
            singletonGoogleProvider.connectAfterResume();
            singletonGoogleProvider.managePendingOperations();
        }
        */
    }

    @Override
    public void onStop() {
        super.onStop();

        this.singletonFirebaseProvider.removeStateListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        this.singletonFirebaseProvider.setStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.hideProgress();

        this.singletonFirebaseProvider.setStateListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.singletonFirebaseProvider.removeStateListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.singletonFirebaseProvider.removeStateListener();
        //((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
        //.removeGoogleClient();
    }
}
