package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.FactoryProviders;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonGoogleProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.driveembetter.proevolutionsoftware.driveembetter.fcm.FirebaseUtility;
import com.driveembetter.proevolutionsoftware.driveembetter.services.SwipeClosureHandler;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.DatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.ProtectedAppsManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.SharedPrefUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredo on 17/08/17.
 */

public class MainFragmentActivity
        extends AppCompatActivity
        implements Constants, TypeMessages,
        NavigationView.OnNavigationItemSelectedListener,
        TaskProgressInterface {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    // Resources
    private ArrayList<BaseProvider> baseProviderArrayList;
    private SingletonFirebaseProvider singletonFirebaseProvider;
    private SingletonUser singletonUser;
    private PositionManager positionManager;
    private FragmentState fragmentState;
    // Fragments
    private Fragment saveMe;
    private Fragment ranking;
    private Fragment aboutUs;

    // Widgets
    private ProgressBar progressBar;
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView userPicture;
    private View headerView;



    public PositionManager getPositionManager() {
        return this.positionManager;
    }

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
                case BAD_EMAIL_OR_PSW:
                case INVALID_USER:
                case INVALID_CREDENTIALS:
                case NETWORK_ERROR:
                case UNKNOWN_EVENT:
                    Toast
                            .makeText(MainFragmentActivity.this, getString(R.string.session_expired), Toast.LENGTH_LONG)
                            .show();
                    closeCurrentActivity();
                    break;

                case USER_LOGIN:
                    String currentUser;
                    if (singletonUser.getUsername() != null && !singletonUser.getUsername().isEmpty()) {
                        currentUser = singletonUser.getUsername();
                    } else if (singletonUser.getEmail() != null && !singletonUser.getEmail().isEmpty()) {
                        currentUser = singletonUser.getEmail();
                    } else {
                        currentUser = getString(R.string.user_not_retrieved);
                    }
                    Log.d(TAG, "Log in");
                    /*
                    Toast.makeText(
                            MainFragmentActivity.this,
                            String.format(getString(R.string.sign_in_as), currentUser), Toast.LENGTH_SHORT
                    ).show();
                    */
                    break;

                case USER_LOGOUT:
                    Log.d(TAG, "Log out");
                    /*
                    Toast.makeText(MainFragmentActivity.this, getString(R.string.logging_out), Toast.LENGTH_SHORT).show();
                    */
                    startNewActivityCloseCurrent(MainFragmentActivity.this, SignInActivity.class);
                    break;

                default:
                    Log.d(TAG, "MSG: " + msg.what);
            }
        }
    };

    private void startNewActivityCloseCurrent(Context context, Class newClass) {
        Intent newIntent = new Intent(context, newClass);
        this.finish();
        this.startActivity(newIntent);
    }

    private void openNewActivity(Context context, Class newClass) {
        Intent newIntent = new Intent(context, newClass);
        this.startActivity(newIntent);
    }

    private void closeCurrentActivity() {
        this.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(getApplicationContext(), SwipeClosureHandler.class);
        startService(serviceIntent);
        this.initResources();
        if (savedInstanceState != null) {
            this.onRestoreInstanceState(savedInstanceState);
        }
        this.setContentView(R.layout.activity_main);

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

        //TODO it should refresh automatically
        FirebaseUtility firebaseUtility = new FirebaseUtility();
        firebaseUtility.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
        DatabaseManager.manageDataUserDB();

        this.initWidgets();
    }

    private void initResources() {

        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();
        this.baseProviderArrayList = factoryProviders.getAllProviders();

        this.singletonUser = this.singletonFirebaseProvider.getUserInformations();
        this.positionManager = PositionManager.getInstance(this);
        this.fragmentState = new FragmentState(getSupportFragmentManager());

        // Init fragments
        this.saveMe = new SaveMe();
        this.ranking = new RankingFragment();
        this.aboutUs = new AboutUsActivity();
        //locationUpdater = new LocationUpdater(this, user);
        //locationUpdater.updateLocation();
    }

    private void initWidgets() {
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.usernameTextView = this.headerView.findViewById(R.id.username_text_view);
        this.emailTextView = this.headerView.findViewById(R.id.email_text_view);
        this.userPicture = this.headerView.findViewById(R.id.user_picture);
        if (this.singletonUser != null) {
            Log.d(TAG, "USER: " + this.singletonUser.getEmail() + " / " + this.singletonUser.getUsername() + " / " + this.singletonUser.getPhotoUrl());

            if (this.singletonUser.getEmail() != null) {
                this.emailTextView.setText(this.singletonUser.getEmail());
            }
            if (this.singletonUser.getUsername() != null) {
                this.usernameTextView.setText(this.singletonUser.getUsername());
            }
            if (this.singletonUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(
                                this.singletonUser
                                        .getPhotoUrl()
                                        .toString()
                        )
                        .dontTransform()
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(this.userPicture);
            }
        } else {
            Toast.makeText(MainFragmentActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            this.startNewActivityCloseCurrent(MainFragmentActivity.this, SignInActivity.class);
        }

        ProtectedAppsManager protectedAppsManager = new ProtectedAppsManager(this);
        protectedAppsManager.checkAlert();


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
     * when the singletonUser requests a refresh by selecting the refresh action bar item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
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
            case R.id.garage:
                // Handle the camera action
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER)).cancan();

                this.singletonUser.getVehicles(new SingletonUser.UserDataCallback() {
                    @Override
                    public void onVehiclesReceive() {
                        if (singletonUser.getVehicleArrayList() == null) {
                            Log.d(TAG, "VEICH NULL");
                        } else {
                            for (Vehicle vehicle :
                                    singletonUser.getVehicleArrayList()) {
                                Log.d(TAG, "VEICH: " + vehicle.getNumberPlate() + " / " + vehicle.getType());
                            }
                        }
                    }
                });
                break;

            case R.id.statistics:
                // DEBUG
                SingletonGoogleProvider singletonGoogleProvider = ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER));
                if (singletonGoogleProvider != null) {
                    Log.d(TAG, "SIGN IN: " + singletonGoogleProvider.isSignIn());
                } else {
                    Log.d(TAG, "SING GOOGLE NULL");
                }

                Log.d(TAG, "Singleton user: " + singletonUser.getEmail() + " / " + singletonUser.getUsername());

                if (this.singletonFirebaseProvider.getFirebaseUser() != null) {
                    Log.d(TAG, "USER FIRE: " + this.singletonFirebaseProvider.getFirebaseUser().getEmail() + " / " + this.singletonFirebaseProvider.getFirebaseUser().getUid());
                } else {
                    Log.d(TAG, "USER FIRE NULL");
                }

                Log.d(TAG, "DIO CONNected: " + ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER)).diodio());
                ////
                break;

            case R.id.ranking:
                if (!FragmentState.isFragmentOpen(FragmentState.RANKING_FRAGMENT)) {
                    FragmentState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.ranking
                    );
                    FragmentState.setFragmentState(FragmentState.RANKING_FRAGMENT, true);
                }
                break;

            case R.id.save_me:
                if (!FragmentState.isFragmentOpen(FragmentState.SAVE_ME_FRAGMENT)) {
                    // TODO ferma esecuzione SaveMe in onPause()
                    FragmentState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.saveMe
                    );
                    FragmentState.setFragmentState(FragmentState.SAVE_ME_FRAGMENT, true);
                }
                break;

            case R.id.nav_share:
                // DEBUG
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                        .connectToPlayStore();
                ////
                break;

            case R.id.nav_send:
                break;

            case R.id.about_us:
                if (!FragmentState.isFragmentOpen(FragmentState.ABOUT_US)) {
                    FragmentState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.aboutUs
                    );
                    FragmentState.setFragmentState(FragmentState.ABOUT_US, true);
                }
                break;

            case R.id.nav_logout:
                Log.d(TAG, "Logout pressed");

                this.logoutCurrentProviders();
                SingletonUser.resetSession();
                this.finish();
                break;
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
        this.singletonFirebaseProvider.setContext(this);

        if (this.baseProviderArrayList
                .get(FactoryProviders.GOOGLE_PROVIDER)
                .isSignIn()) {
            ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                    .silentSignIn();
        }
    }

    // Called between onStart() and onPostCreate()
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, ":onRestoreInstanceState");
        // this.restoreProviders(savedInstanceState);
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

        // Check that user exist
        this.singletonFirebaseProvider.reauthenticateUser();
        this.positionManager.getInstance(this).setUserAvailable();
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
        // this.saveProviders(outState);
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
        this.positionManager.setUserUnavailable();
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
                // this.startNewActivityCloseCurrent(MainFragmentActivity.this, SignInActivity.class);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
