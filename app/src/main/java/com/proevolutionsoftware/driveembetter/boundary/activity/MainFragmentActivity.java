package com.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.proevolutionsoftware.driveembetter.authentication.factoryProvider.FactoryProviders;
import com.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonGoogleProvider;
import com.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.proevolutionsoftware.driveembetter.boundary.fragment.AboutUsFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.GarageFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.HomeFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.RankingFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.SaveMeFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.StatisticsFragment;
import com.proevolutionsoftware.driveembetter.constants.Constants;
import com.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.proevolutionsoftware.driveembetter.services.SwipeClosureHandler;
import com.proevolutionsoftware.driveembetter.threads.ChartAsyncTask;
import com.proevolutionsoftware.driveembetter.threads.ReauthenticateUserRunnable;
import com.proevolutionsoftware.driveembetter.threads.SaveUserStatisticsRunnable;
import com.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.proevolutionsoftware.driveembetter.utils.FragmentsState;
import com.proevolutionsoftware.driveembetter.utils.GlideImageLoader;
import com.proevolutionsoftware.driveembetter.utils.NetworkConnectionUtil;
import com.proevolutionsoftware.driveembetter.utils.PermissionManager;
import com.proevolutionsoftware.driveembetter.utils.PositionManager;
import com.proevolutionsoftware.driveembetter.utils.ProtectedAppsManager;
import com.proevolutionsoftware.driveembetter.utils.SensorHandler;
import com.proevolutionsoftware.driveembetter.utils.SharedPrefUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alfredo on 17/08/17.
 */

public class MainFragmentActivity extends AppCompatActivity
        implements Constants, TypeMessages,
        NavigationView.OnNavigationItemSelectedListener,
        TaskProgressInterface, View.OnClickListener {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    public final static String DIRECT_ACCESS = "direct_access";

    // Resources
    private ArrayList<BaseProvider> baseProviderArrayList;
    private SingletonFirebaseProvider singletonFirebaseProvider;
    private SingletonUser singletonUser;
    private PositionManager positionManager;
    private Thread reauthenticationThread;
    private Thread saveUserDataThread;
    private SharedPrefUtil sharedPrefUtil;

    // Fragments
    private FragmentsState fragmentsState;
    private Fragment saveMe;
    private Fragment ranking;
    private Fragment aboutUs;
    private Fragment garage;
    private Fragment home;
    private Fragment statistics;

    // Widgets
    private ShareActionProvider mShareActionProvider;
    private ProgressBar progressBar;
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView userPicture;
    private View headerView;
    private ImageButton settingsImageButton;
    private DrawerLayout drawerLayout;
    private Button emergencyButton;


    private ProgressDialog progress;
    private ChartAsyncTask task;



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
                    logoutCurrentProviders();
                    break;

                case USER_LOGIN:
                    Log.d(TAG, "Log in");
                    break;

                case USER_LOGOUT:
                    Log.d(TAG, "Log out");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FirebaseInstanceId.getInstance().deleteInstanceId();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    FirebaseDatabaseManager.manageUserAvailability(UNAVAILABLE);
                    FirebaseDatabaseManager.managePositionAvailability(UNAVAILABLE);
                    if (positionManager != null) {
                        positionManager.removeLocationUpdates();
                    }
                    saveUserDataThread.interrupt();
                    reauthenticationThread.interrupt();
                    SingletonUser.resetSession();
                   sharedPrefUtil.saveBoolean(MainFragmentActivity.DIRECT_ACCESS, false);
                    startNewActivityCloseCurrent(MainFragmentActivity.this, SignInActivity.class);
                    break;

                case USER_SYNC_REQUEST:
                    Log.d(TAG, "User resync request");

                    userSync();
                    break;

                case INTERNAL_FIREBASE_ERROR_LOGIN:
                    Log.d(TAG, "Internal firebase error login");
                    Toast
                            .makeText(MainFragmentActivity.this, getString(R.string.internal_firebase_error_login), Toast.LENGTH_LONG)
                            .show();
                    logoutCurrentProviders();
                    break;

                default:
                    Log.d(TAG, "MSG: " + msg.what);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();

        this.setContentView(R.layout.activity_main);

        this.initWidgets();

        this.manageActionOnNavigationItemSelected(R.id.home);
    }

    private void initResources() {
        this.sharedPrefUtil = new SharedPrefUtil(this);
        this.sharedPrefUtil.saveBoolean(MainFragmentActivity.DIRECT_ACCESS, true);
        // Get all providers to manage user's connection state
        FactoryProviders factoryProviders = new FactoryProviders(this, this.handler);
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();
        if (this.singletonFirebaseProvider == null) {
            this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance(this, this.handler);
        }
        this.baseProviderArrayList = factoryProviders.getAllProviders();

        // Start reauthentication thread
        this.reauthenticationThread = new Thread(new ReauthenticateUserRunnable(this));
        this.reauthenticationThread.start();

        // Start thread to save user data
        this.saveUserDataThread = new Thread(new SaveUserStatisticsRunnable(this));
        this.saveUserDataThread.start();

        // Init user
        this.singletonUser = this.singletonFirebaseProvider.getUserInformations();

        // Sync SingletonUser with DB data
        if (this.singletonUser.getUid() == null ||
                !this.singletonUser.getUid().isEmpty()) {
            Log.d(TAG, "Sync user data");
            FirebaseDatabaseManager.syncCurrentUser();
        }

        // Init fragments
        this.fragmentsState = new FragmentsState(getSupportFragmentManager());
        this.saveMe = new SaveMeFragment();
        this.ranking = new RankingFragment();
        this.aboutUs = new AboutUsFragment();
        this.garage = new GarageFragment();
        this.home = new HomeFragment();
        this.statistics = new StatisticsFragment();

        // Start service to manage task manager behaviour
        Intent serviceIntent = new Intent(getApplicationContext(), SwipeClosureHandler.class);
        this.startService(serviceIntent);

        // Check protected app feature
        ProtectedAppsManager protectedAppsManager = new ProtectedAppsManager(this);
        protectedAppsManager.checkAlert();

        // Start listening to update position
        this.positionManager = PositionManager.getInstance(this);
        // Ask user to enable GPS if it is disabled
        if (!this.positionManager.isGPSEnabled()) {
            Toast.makeText(this, R.string.gps_ask_enable, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.ASK_FOR_LOCATION_POS_MAN:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PermissionManager.PERM_OK) {
                    if (this.positionManager == null) {
                        this.positionManager = PositionManager.getInstance(this);
                    }
                    this.positionManager.updatePosition();
                } else {
                    Toast.makeText(this, this.getString(R.string.accept_permissions), Toast.LENGTH_SHORT).show();
                    this.logoutCurrentProviders();
                }
                return;

            case PermissionManager.ASK_FOR_LOCATION_SAVE_ME:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PermissionManager.PERM_OK) {
                    if (saveMe != null) {
                        ((SaveMeFragment) saveMe).initMapListener();
                        if (this.positionManager != null) {
                            this.positionManager.updatePosition();
                        }
                    }
                } else {
                    Toast.makeText(this, this.getString(R.string.accept_permissions), Toast.LENGTH_SHORT).show();
                    this.logoutCurrentProviders();
                }
                return;

            default:
                Log.d(TAG, "onRequestPermissionResult: " + requestCode);
        }
    }

    private void userSync() {
        this.singletonUser = SingletonUser.getInstance();
        if (this.singletonUser != null) {
            if (this.singletonUser.getEmail() != null) {
                this.emailTextView.setText(this.singletonUser.getEmail());
            }
            if (this.singletonUser.getUsername() != null) {
                this.usernameTextView.setText(this.singletonUser.getUsername());
            }
            GlideImageLoader.loadImageUri(
                    this,
                    this.userPicture,
                    this.singletonUser.getPhotoUrl(),
                    R.mipmap.user_icon,
                    R.mipmap.user_icon);
        } else {
            Toast.makeText(MainFragmentActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            this.startNewActivityCloseCurrent(MainFragmentActivity.this, SignInActivity.class);
        }
    }

    private void initWidgets() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.emergencyButton = findViewById(R.id.emergency_button);
        SensorHandler sensorHandler = new SensorHandler(this);
        sensorHandler.setEmergencyButton(emergencyButton);
        sensorHandler.setContext(this);
        sensorHandler.startSensorHandler();
        this.drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.headerView =  navigationView.getHeaderView(0);

        this.progressBar = findViewById(R.id.progress_bar);
        this.usernameTextView = this.headerView.findViewById(R.id.username_text_view);
        this.emailTextView = this.headerView.findViewById(R.id.email_text_view);
        this.userPicture = this.headerView.findViewById(R.id.user_picture);
        this.settingsImageButton = this.headerView.findViewById(R.id.imageViewSettings);
        this.userSync();
        this.settingsImageButton.setOnClickListener(this);
    }

    private void startNewActivityCloseCurrent(Context context, Class newClass) {
        Intent newIntent = new Intent(context, newClass);
        this.startActivity(newIntent);
        this.finish();
    }

    private void startNewActivity(Context context, Class newClass) {
        Intent newIntent = new Intent(context, newClass);
        this.startActivity(newIntent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imageViewSettings:
                this.startNewActivity(MainFragmentActivity.this, EditProfileDataActivity.class);
                break;
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

    private void logoutCurrentProviders() {
        for (BaseProvider baseProvider : this.baseProviderArrayList) {
            if (baseProvider.isSignIn()) {
                Log.d(TAG, "Logging out: " + baseProvider.getClass().toString());
                this.showProgress();
                baseProvider.signOut();
            }
        }

        if (this.singletonFirebaseProvider.getFirebaseUser() != null) {
            this.singletonFirebaseProvider.signOut();
        }

        //delete position token
        FirebaseDatabaseManager.deletePositionToken();

    }

    private void manageReathenticationThreadStatus() {
        // java.lang.Thread.State can be NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED
        // thread.getState() --> stato thread; thread.isAlive --> true sse il thread è in esecuzione
        switch (this.reauthenticationThread.getState()) {
            case BLOCKED: Log.d(TAG, "thread REAUTH:blocked");
            case TERMINATED: Log.d(TAG, "thread REAUTH:terminated");
                this.reauthenticationThread.interrupt();
                this.reauthenticationThread = new Thread(new ReauthenticateUserRunnable(this));
                this.reauthenticationThread.start();
                break;

            case NEW: Log.d(TAG, "thread REAUTH:new");
            case RUNNABLE: Log.d(TAG, "thread REAUTH:runnable");
            case TIMED_WAITING: Log.d(TAG, "thread REAUTH:timed_waiting");
            case WAITING: Log.d(TAG, "thread REAUTH:waiting");
                break;
        }
    }

    private void manageSaveUserDataThreadStatus() {
        // java.lang.Thread.State can be NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED
        // thread.getState() --> stato thread; thread.isAlive --> true sse il thread è in esecuzione
        switch (this.reauthenticationThread.getState()) {
            case BLOCKED: Log.d(TAG, "thread SAVE_DATA:blocked");
            case TERMINATED: Log.d(TAG, "thread SAVE_DATA:terminated");
                this.saveUserDataThread.interrupt();
                this.saveUserDataThread = new Thread(new SaveUserStatisticsRunnable(this));
                this.saveUserDataThread.start();
                break;

            case NEW: Log.d(TAG, "thread SAVE_DATA:new");
            case RUNNABLE: Log.d(TAG, "thread SAVE_DATA:runnable");
            case TIMED_WAITING: Log.d(TAG, "thread SAVE_DATA:timed_waiting");
            case WAITING: Log.d(TAG, "thread SAVE_DATA:waiting");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_fragment_level, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                this.startNewActivity(MainFragmentActivity.this, SettingsActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void manageActionOnNavigationItemSelected(int action) {
        Log.e(TAG, "ENTER_DISPACHER: " + action);
        switch (action) {
            case R.id.home:
                if (!FragmentsState.isFragmentOpen(FragmentsState.HOME_FRAGMENT)) {
                    this.fragmentsState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.home
                    );

                    FragmentsState.setFragmentState(FragmentsState.HOME_FRAGMENT, true);
                }
                break;

            case R.id.garage:
                if (!FragmentsState.isFragmentOpen(FragmentsState.GARAGE_FRAGMENT)) {
                    this.fragmentsState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.garage
                    );
                    FragmentsState.setFragmentState(FragmentsState.GARAGE_FRAGMENT, true);
                }
                break;

            case R.id.statistics:
                if (!FragmentsState.isFragmentOpen(FragmentsState.STATISTICS_FRAGMENT)) {
                    this.fragmentsState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.statistics
                    );
                    FragmentsState.setFragmentState(FragmentsState.STATISTICS_FRAGMENT, true);
                }

                // this.startNewActivity(MainFragmentActivity.this, ChartActivity.class);
                break;

            case R.id.ranking:
                if (!FragmentsState.isFragmentOpen(FragmentsState.RANKING_FRAGMENT)) {
                    this.fragmentsState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.ranking
                    );
                    FragmentsState.setFragmentState(FragmentsState.RANKING_FRAGMENT, true);
                }
                break;

            case R.id.save_me:
                if (!FragmentsState.isFragmentOpen(FragmentsState.SAVE_ME_FRAGMENT)) {
                    this.fragmentsState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.saveMe
                    );
                    FragmentsState.setFragmentState(FragmentsState.SAVE_ME_FRAGMENT, true);
                }
                break;

            case R.id.nav_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(
                        this.getString(R.string.link_app),
                        this.getString(R.string.app_name),
                        Constants.APP_LINK_TO_GOOGLE_PLAY_STORE
                ));

                try {
                    this.startActivity(Intent.createChooser(sharingIntent, this.getString(R.string.share_app)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainFragmentActivity.this, this.getString(R.string.error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;

            case R.id.about_us:
                if (!FragmentsState.isFragmentOpen(FragmentsState.ABOUT_US)) {
                    this.fragmentsState.replaceFragment(
                            R.id.fragment_placeholder,
                            this.aboutUs
                    );
                    FragmentsState.setFragmentState(FragmentsState.ABOUT_US, true);
                }
                break;

            case R.id.nav_logout:
                Log.d(TAG, "Logout pressed");

                this.logoutCurrentProviders();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        this.manageActionOnNavigationItemSelected(item.getItemId());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SingletonGoogleProvider.RC_SIGN_IN:
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
        this.singletonFirebaseProvider.setContext(this);
        this.singletonFirebaseProvider.setHandler(this.handler);

        if (NetworkConnectionUtil.isConnectedToInternet(this)) {
            if (this.baseProviderArrayList
                    .get(FactoryProviders.GOOGLE_PROVIDER)
                    .isSignIn()) {
                ((SingletonGoogleProvider) this.baseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
                        .silentSignIn();
            }
        } else {
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
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

        this.manageReathenticationThreadStatus();
        this.manageSaveUserDataThreadStatus();
        Log.d(TAG, ":resume");
        this.singletonFirebaseProvider.setStateListener(this.hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, ":pause");
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());
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

        this.reauthenticationThread.interrupt();
        this.saveUserDataThread.interrupt();
        FirebaseDatabaseManager.manageUserAvailability(UNAVAILABLE);
        FirebaseDatabaseManager.manageUserStatistics();
        FirebaseDatabaseManager.managePositionAvailability(UNAVAILABLE);
        this.singletonFirebaseProvider.removeStateListener(this.hashCode());

        if (this.positionManager != null) {
            this.positionManager.removeTextToSpeech();
        }
        //((SingletonGoogleProvider) this.singletonFirebaseProviderArrayList.get(FactoryProviders.GOOGLE_PROVIDER))
        //.removeGoogleClient();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
