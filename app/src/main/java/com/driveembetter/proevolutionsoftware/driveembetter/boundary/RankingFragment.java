package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.adapters.RankingRecyclerViewAdapter;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.DatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;

import java.util.ArrayList;

/**
 * Created by alfredo on 26/08/17.
 */
public class RankingFragment
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        DatabaseManager.RetrieveRankFromDB,
        LevelMenuFragment.LevelStateChanged,
        RankingRecyclerViewAdapter.OnItemClickListener,
        Constants,
        TaskProgressInterface {

    private final static String TAG = RankingFragment.class.getSimpleName();

    // Resource
    private ArrayList<User> arrayList;
    private static int level;
    private double latitude;
    private double longitude;
    private PositionManager positionManager;

    // Widgets
    private Context context;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;
    private RecyclerView.LayoutManager layoutManager;

    // Error code
    private final static int POSITION_NOT_FOUND = 1;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        /*
        try {
            this.callback = (CallbackToUI) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CallbackToUI");
        }
        */

        this.context = context;
        this.layoutManager = new LinearLayoutManager(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();

        RankingFragment.level = LEVEL_DISTRICT;
        // To modify Menu items
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return this.rootView = inflater.inflate(R.layout.fragment_ranking_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initWidgets();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.startRoutineFillList();
    }

    private void startRoutineFillList() {
        this.showProgress();
        this.latitude = this.positionManager.getLatitude();
        this.longitude = this.positionManager.getLongitude();
        this.retrieveUserData();
    }

    private void retrieveUserData() {
        if (this.latitude == 0 || this.longitude == 0) {
            DatabaseManager.getCoordinates(this);
        } else {
            this.performQuery();
        }
    }

    private void performQuery() {
        Log.d(TAG, "lat: " + latitude + " long: " + longitude);

        String[] location = this.positionManager.getLocationFromCoordinates(
                this.latitude,
                this.longitude,
                1
        );
        // location[0] --> nation; location[1] --> region; location[2] --> district
        String nation = location[0]; String region = location[1]; String district = location[2];
        // DEBUG
        nation = "Italy"; region = "Lazio"; district = "Provincia di Frosinone";
        ////
        if (nation == null || region == null || district == null) {
            this.onErrorReceived(3);
        } else {
            DatabaseManager.getUsersRank(this, location);
        }
    }

    @Override
    public void onErrorReceived(int errorType) {
        String string;
        switch (errorType) {
            case POSITION_NOT_FOUND:

            default:
                Log.d(TAG, "onErrorReceived: " + errorType);
                string = getString(R.string.unable_load_ranking);
        }

        Toast.makeText(this.context, string, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUsersCoordinatesReceived(double latitude, double longitude) {
        Log.d(TAG, "onUserCoordinatesReceived");
        this.latitude = latitude;
        this.longitude = longitude;
        this.performQuery();
    }

    @Override
    public void onUsersRankingReceived(ArrayList<User> arrayList) {
        Log.d(TAG, "onUsersRankingReceived");
        this.arrayList = arrayList;
        this.fillList();
    }

    private void fillList() {
        RankingRecyclerViewAdapter rankingRecyclerViewAdapter =
                new RankingRecyclerViewAdapter(this.context, this.arrayList, this);
        // To avoid memory leaks set adapter in onACtivityCreated
        this.recycleView.setAdapter(rankingRecyclerViewAdapter);

        this.hideProgress();
        if (this.arrayList == null) {
            Toast.makeText(this.context, getString(R.string.empty_user), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.context, getString(R.string.refresh_complete), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Check if user triggered a refresh:
            case R.id.refresh_action:
                Log.i(TAG, "Refresh menu item selected");
                this.startRoutineFillList();
                return true;

            case R.id.menu_selection_level:
                Log.i(TAG, "Level menu item selected");
                LevelMenuFragment levelMenuFragment = new LevelMenuFragment();
                levelMenuFragment.initLevelStateChangedCallback(this);
                // Show DialogFragment
                levelMenuFragment.show(getFragmentManager(), getString(R.string.dialogue_level_menu));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "Called oncreate...");

        menu.findItem(R.id.menu_selection_level).setVisible(true);
        menu.findItem(R.id.refresh_action).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    private void initResources() {
        this.positionManager = PositionManager.getInstance((Activity) this.context);
    }

    private void initWidgets() {
        this.recycleView = (RecyclerView) this.rootView.findViewById(R.id.recycler_view_user);
        this.recycleView.setHasFixedSize(true);
        /*
            This warning occurs because I set adapter in onViewCreated,
            but creating the adapter in callback, onUsersRankingReceived.
            Creating the adapter immediately adds a firebase listener which causes
            notifyItemInserted to be called.
            The error shouldn't really matter: it's just informing that while
            notifyItemInserted was called, no data will be displayed because the
            adapter hasn't been attached yet.
            (The list items should still show up as a batch update once you attach the adapter).
        */
        this.recycleView.setLayoutManager(this.layoutManager);
        this.swipeRefreshLayout = (SwipeRefreshLayout) this.rootView.findViewById(R.id.swiperefresh_ranking);
        this.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.blue_900));
        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        this.swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void hideProgress() {
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showProgress() {
        this.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

        this.startRoutineFillList();
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentState.setFragmentState(FragmentState.RANKING_FRAGMENT, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        FragmentState.setFragmentState(FragmentState.RANKING_FRAGMENT, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        FragmentState.setFragmentState(FragmentState.RANKING_FRAGMENT, false);
    }

    @Override
    public void levelChanged(int level) {
        RankingFragment.level = level;
    }

    public static int getLevel() {
        return RankingFragment.level;
    }

    @Override
    public void onItemClick(User item) {
        Log.d(TAG, "onClick");
        Intent userDetail = new Intent(this.getActivity(), UserDetailsRankingActivity.class);
        userDetail.putExtra(USER, item);
        this.startActivity(userDetail);
    }
}
