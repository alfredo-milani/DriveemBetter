package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.driveembetter.proevolutionsoftware.driveembetter.adapters.DividerItemDecoration;
import com.driveembetter.proevolutionsoftware.driveembetter.adapters.RankingRecyclerViewAdapter;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.UserDetailsRankingActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.threads.RetrieveRankingRunnable;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;

import java.util.ArrayList;

/**
 * Created by alfredo on 26/08/17.
 */
public class RankingFragment
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        LevelMenuFragment.LevelStateChanged,
        RankingRecyclerViewAdapter.OnItemClickListener,
        Constants,
        TaskProgressInterface {

    private final static String TAG = RankingFragment.class.getSimpleName();

    // Resource
    private static int level;

    // Dialog fragment
    private LevelMenuFragment levelMenuFragment;

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

        this.initResources();
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

        this.showProgress();
        new Thread(new RetrieveRankingRunnable(this)).start();
    }

    public void fillList(ArrayList<User> arrayList) {
        this.recycleView.addItemDecoration(new DividerItemDecoration(this.context));
        RankingRecyclerViewAdapter rankingRecyclerViewAdapter =
                new RankingRecyclerViewAdapter(this.context, arrayList, this);
        // To avoid memory leaks set adapter in onActivityCreated
        this.recycleView.setAdapter(rankingRecyclerViewAdapter);

        this.hideProgress();
        if (arrayList != null) {
            Toast.makeText(this.context, getString(R.string.refresh_complete), Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(this.context, getString(R.string.level_empty_list), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLevelChanged(int level) {
        RankingFragment.level = level;
        this.showProgress();

        new Thread(new RetrieveRankingRunnable(this)).start();
    }

    public static int getLevel() {
        return RankingFragment.level;
    }

    public static void setLevel(int level) {
        RankingFragment.level = level;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Check if user triggered a refresh:
            case R.id.refresh_action:
                Log.d(TAG, "Refresh menu item selected");
                this.showProgress();
                new Thread(new RetrieveRankingRunnable(this)).start();
                return true;

            case R.id.menu_selection_level:
                Log.d(TAG, "Level menu item selected");
                // Show DialogFragment
                this.levelMenuFragment.show(getFragmentManager(), getString(R.string.dialogue_level_menu));
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
        this.levelMenuFragment = new LevelMenuFragment();
        this.levelMenuFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        this.levelMenuFragment.addLevelListener(this);

        RankingFragment.level = LEVEL_DISTRICT;
    }

    private void initWidgets() {
        // To modify Menu items
        this.setHasOptionsMenu(true);

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
        this.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.blue_800),
                ContextCompat.getColor(getContext(), R.color.blue_600),
                ContextCompat.getColor(getContext(), R.color.blue_400)
        );
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

        this.showProgress();
        new Thread(new RetrieveRankingRunnable(this)).start();
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
    public void onItemClick(User user) {
        Log.d(TAG, "onItemClick");
        Intent userDetail = new Intent(this.getActivity(), UserDetailsRankingActivity.class);
        userDetail.putExtra(USER, user);
        this.startActivity(userDetail);
    }
}
