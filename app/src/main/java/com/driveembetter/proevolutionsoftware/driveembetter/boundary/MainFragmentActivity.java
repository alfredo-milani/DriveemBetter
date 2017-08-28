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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonGoogleProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.ImageLoadTask;

import java.util.ArrayList;

/**
 * Created by alfredo on 17/08/17.
 */

public class MainFragmentActivity
        extends AppCompatActivity
        implements Constants, TypeMessages,
        NavigationView.OnNavigationItemSelectedListener,
        TaskProgress {

    private final static String TAG = "MainFragmentActivity";

    // Resources
    private ArrayList<BaseProvider> baseProviderArrayList;
    private SingletonFirebaseProvider singletonFirebaseProvider;
    private User user;

    // Widgets
    private ProgressBar progressBar;
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView userPicture;
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
                    String currentUser;
                    if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                        currentUser = user.getUsername();
                    } else if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        currentUser = user.getEmail();
                    } else {
                        currentUser = getString(R.string.user_not_retrieved);
                    }
                    Toast.makeText(
                            MainFragmentActivity.this,
                            String.format(getString(R.string.sign_in_as), currentUser), Toast.LENGTH_SHORT
                    ).show();
                    break;

                case USER_LOGOUT:
                    Toast.makeText(MainFragmentActivity.this, getString(R.string.logout), Toast.LENGTH_SHORT).show();
                    startNewActivity(MainFragmentActivity.this, SignInActivity.class);
                    break;

                default:
                    Log.d(TAG, "MSG: " + msg.what);
            }
        }
    };

    private void startNewActivity(Context context, Class newClass) {
        Intent mainFragmentIntent = new Intent(context, newClass);
        this.startActivity(mainFragmentIntent);
        this.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.onRestoreInstanceState(savedInstanceState);
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.headerView =  navigationView.getHeaderView(0);

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
        this.emailTextView = this.headerView.findViewById(R.id.email_text_view);
        this.userPicture = this.headerView.findViewById(R.id.user_picture);
        if (this.user != null) {
            Log.d(TAG, "USER: " + this.user.getEmail() + " / " + this.user.getUsername() + " / " + this.user.getPhotoUrl());

            if (this.user.getEmail() != null) {
                this.emailTextView.setText(this.user.getEmail());
            }
            if (this.user.getUsername() != null) {
                this.usernameTextView.setText(this.user.getUsername());
            }
            if (this.user.getPhotoUrl() != null) {
                new ImageLoadTask(this.user.getPhotoUrl().toString(), this.userPicture).execute();
            }
        } else {
            Toast.makeText(MainFragmentActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            this.startNewActivity(MainFragmentActivity.this, SignInActivity.class);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logoutCurrentProviders() {
        for (BaseProvider baseProvider:
                this.baseProviderArrayList) {
            if (baseProvider.isSignIn()) {
                Log.d(TAG, "Logging out: " + baseProvider.getClass().toString());
                this.showProgress();
                baseProvider.signOut();
            }
        }

        if (this.singletonFirebaseProvider.getFirebaseUser() != null) {
            this.singletonFirebaseProvider.forceSignOut();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * Listen for option item selections so that we receive a notification
     * when the user requests a refresh by selecting the refresh action bar item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                // TODO vedi come gestire icona refresh menu...
                Log.i(TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                // mySwipeRefreshLayout.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                // stuff actions
                return true;

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;
        }

        // User didn't trigger a refresh, let the superclass handle this action
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
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER)).cancan();
                break;

            case R.id.nav_gallery:
                User user = ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER)).getGoogleUserInformations();
                if (user != null) {
                    Log.d(TAG, "GOOGLE USER: " + user.getUsername() + " jjj " + user.getEmail());
                } else {
                    Log.d(TAG, "USER GOOGLE NULL");
                }

                if (this.singletonFirebaseProvider.getFirebaseUser() != null) {
                    Log.d(TAG, "USER FIRE: " + this.singletonFirebaseProvider.getFirebaseUser().getEmail());
                } else {
                    Log.d(TAG, "USER FIRE NULL");
                }
                break;

            case R.id.nav_slideshow:
                Log.d(TAG, "DIO CONNected: " + ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER)).diodio());
                break;

            case R.id.nav_manage:
                this.startNewActivity(this, RankingFragment.class);
                break;

            case R.id.nav_share:
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                        .silentSignIn();
                break;

            case R.id.nav_send:
                break;

            case R.id.nav_logout:
                Log.d(TAG, "Logout pressed");

                this.logoutCurrentProviders();
                break;
            case R.id.nav_statistics:

            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SingletonGoogleProvider.RC_SIGN_IN:
                Log.d(TAG, "onActivityResult: GOOGLE" + requestCode);
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                        .activityResult(requestCode, resultCode, data);
                break;
            default:
                Log.w(TAG, "onActivityResult: Unknown requestCode: " + requestCode);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, ":start");
        this.singletonFirebaseProvider.setListenerOwner(this.hashCode());
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
        this.singletonFirebaseProvider.setHandler(this.handler);

        /*
        if (this.authenticationProvider == FactoryProviders.GOOGLE_PROVIDER) {
            SingletonGoogleProvider singletonGoogleProvider = ((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER));
            singletonGoogleProvider.connectAfterResume();
            singletonGoogleProvider.managePendingOperations();
        }
        */
    }

    // Called between onStart() and onPostCreate()
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, ":onRestoreInstanceState");
        this.restoreProviders(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, ":restart");
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.hideProgress();

        Log.d(TAG, ":resume");
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, ":pause");
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
    }

    // Called before onStop()
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, ":onSaveInstanceState");
        this.saveProviders(outState);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, ":stop");
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, ":destroy");
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
        //((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
        //.removeGoogleClient();
    }

    // TODO:
    // onSave / onRestore non sempre chiamate --> inserire i seguenti metodi in onStart o onResume o simili.
    // se non funziona vedi se è questione di cambio di contesto
    private void saveProviders(Bundle bundle) {
        if (this.baseProviderArrayList != null) {
            if (baseProviderArrayList
                    .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                    .isSignIn()) {
                bundle.putBoolean(FIREBASE_PROVIDER, true);
            } else if (baseProviderArrayList
                    .get(FactoryProviders.GOOGLE_PROVIDER)
                    .isSignIn()) {
                bundle.putBoolean(GOOGLE_PROVIDER, true);
            } else if (baseProviderArrayList
                    .get(FactoryProviders.FACEBOOK_PROVIDER)
                    .isSignIn()) {
                bundle.putBoolean(FACEBOOK_PROVIDER, true);
            } else if (baseProviderArrayList
                    .get(FactoryProviders.TWITTER_PROVIDER)
                    .isSignIn()) {
                bundle.putBoolean(TWITTER_PROVIDER, true);
            }
        }
    }

    private void restoreProviders(Bundle bundle) {
        if (this.baseProviderArrayList != null) {
            if (bundle.getBoolean(FIREBASE_PROVIDER, false) &&
                    !this.baseProviderArrayList
                            .get(FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER)
                            .isSignIn()) {
                Log.d(TAG, "restoring Firebase provider");
                this.singletonFirebaseProvider.forceSignOut();
                // this.startNewActivity(MainFragmentActivity.this, SignInActivity.class);
            } else if (bundle.getBoolean(GOOGLE_PROVIDER, false) &&
                    !this.baseProviderArrayList
                            .get(FactoryProviders.GOOGLE_PROVIDER)
                            .isSignIn()) {
                Log.d(TAG, "restoring Google provider");
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                        .silentSignIn();
            } else if (bundle.getBoolean(FACEBOOK_PROVIDER, false) &&
                    !this.baseProviderArrayList
                            .get(FactoryProviders.FACEBOOK_PROVIDER)
                            .isSignIn()) {
                Log.d(TAG, "restoring Facebook provider");
                this.baseProviderArrayList.get(FactoryProviders.FACEBOOK_PROVIDER).signIn(null, null);
            } else if (bundle.getBoolean(TWITTER_PROVIDER, false) &&
                    !this.baseProviderArrayList
                            .get(FactoryProviders.TWITTER_PROVIDER)
                            .isSignIn()) {
                Log.d(TAG, "restoring Twitter provider");
                this.baseProviderArrayList.get(FactoryProviders.TWITTER_PROVIDER).signIn(null, null);
            }
        }
    }
}
