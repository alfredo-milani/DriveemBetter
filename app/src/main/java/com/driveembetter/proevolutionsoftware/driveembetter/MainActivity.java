package com.driveembetter.proevolutionsoftware.driveembetter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.Authentication;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.AuthenticationProviderCreator;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TypeMessages {

    private final static String TAG = "MainActivity";

    private Authentication Provider;
    private User user;

    private Button signin;
    private Button signout;
    private ProgressBar progressBar;

    // Defines a Handler object that's attached to the UI thread
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        /*
         * handleMessage() defines the operations to perform when
         * the Handler receives a new Message to process.
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case USER_LOGIN:
                    user = Provider.getUserInformation();
                    Log.d(TAG, "handleMessage:login");
                    if (user == null) {
                        Log.e(TAG, "handleMessage:error");
                        break;
                    }
                    Toast.makeText(MainActivity.this, "Signed in as: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    break;

                case USER_LOGOUT:
                    Log.d(TAG, "handleMessage:logout");
                    Toast.makeText(MainActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
                    break;

                case USER_ALREADY_EXIST:
                    Log.d(TAG, "handleMessage:user_already_exist");
                    Toast.makeText(MainActivity.this, "User already exist. Try with another email address", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.e(TAG, "handleMessage:error");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        setContentView(R.layout.login_layout);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */


        this.Provider = AuthenticationProviderCreator
                .getSingletonAuthenticationProvider(1, MainActivity.this, this.mHandler);


        signin = (Button) findViewById(R.id.signin_button);
        signout = (Button) findViewById(R.id.google_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Provider.signUp("multidio@gmail.com", "diocanino");
                progressBar.setVisibility(View.GONE);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Provider.signOut();
            }
        });
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.Provider.setStateListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.Provider.removeStateListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.Provider.setStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.Provider.setStateListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.Provider.removeStateListener();
    }
}
