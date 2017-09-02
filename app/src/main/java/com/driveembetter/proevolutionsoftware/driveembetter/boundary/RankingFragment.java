package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

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

import java.util.ArrayList;

/**
 * Created by alfredo on 26/08/17.
 */
public class RankingFragment
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        DatabaseManager.SendData,
        LevelMenuFragment.LevelStateChanged,
        RankingRecyclerViewAdapter.OnItemClickListener,
        Constants {

    private final static String TAG = RankingFragment.class.getSimpleName();

    // Resource
    private ArrayList<User> arrayList;
    private static int level;

    // Widgets
    private Context context;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;
    private RecyclerView.LayoutManager layoutManager;



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

        RankingFragment.level = LEVEL_DISTRICT;
        // To modify Menu items
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_ranking_list, container, false);

        this.initWidgets();

        return this.rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.swipeRefreshLayout.setRefreshing(true);
        // TODO:
        // -trova posizione da API locali e convertile in Nazione/Regione/ecc...
        // -query al nodo position per avere tutti gli utenti del livello desiderato
        // -per ogni utente trovato fai query al nodo users per prendere info
        // -riempi la recyclerView
        DatabaseManager.getUserRank(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Check if user triggered a refresh:
            case R.id.refresh_action:
                Log.i(TAG, "Refresh menu item selected");
                // Signal SwipeRefreshLayout to start the progress indicator
                this.swipeRefreshLayout.setRefreshing(true);
                DatabaseManager.getUserRank(this);
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

    private void initWidgets() {
        this.recycleView = (RecyclerView) this.rootView.findViewById(R.id.recycler_view_user);
        this.recycleView.setHasFixedSize(true);
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
    public void onRefresh() {
        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

        DatabaseManager.getUserRank(this);
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
    public void dataReceived(ArrayList<User> users) {
        this.arrayList = users;

        RankingRecyclerViewAdapter rankingRecyclerViewAdapter =
                new RankingRecyclerViewAdapter(this.context, this.arrayList, this);
        // To avoid memory leaks set adapter in onACtivityCreated
        this.recycleView.setAdapter(rankingRecyclerViewAdapter);

        this.swipeRefreshLayout.setRefreshing(false);
        if (this.arrayList == null) {
            Toast.makeText(this.context, getString(R.string.empty_user), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.context, getString(R.string.refresh_complete), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void stateChanged(int level) {
        RankingFragment.level = level;
    }

    public static int getLevel() {
        return RankingFragment.level;
    }

    @Override
    public void onItemClick(User item) {
        Log.d(TAG, "onClick");
        Intent userDetail = new Intent(this.getActivity(), UserDetailsRanking.class);
        userDetail.putExtra(USER, item);
        this.startActivity(userDetail);
    }
}
