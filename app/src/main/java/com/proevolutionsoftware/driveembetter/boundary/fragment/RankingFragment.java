package com.proevolutionsoftware.driveembetter.boundary.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.adapters.RankingRecyclerViewAdapter;
import com.proevolutionsoftware.driveembetter.boundary.TaskProgressInterface;
import com.proevolutionsoftware.driveembetter.boundary.activity.UserDetailsRankingActivity;
import com.proevolutionsoftware.driveembetter.constants.Constants;
import com.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.proevolutionsoftware.driveembetter.entity.User;
import com.proevolutionsoftware.driveembetter.threads.RetrieveRankingRunnable;
import com.proevolutionsoftware.driveembetter.utils.FragmentsState;
import com.proevolutionsoftware.driveembetter.utils.NetworkConnectionUtil;

import java.util.ArrayList;

import static com.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager.RetrieveRankFromDB.INVALID_POSITION;
import static com.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager.RetrieveRankFromDB.NOT_ALLOWED;
import static com.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager.RetrieveRankFromDB.POSITION_NOT_FOUND;
import static com.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager.RetrieveRankFromDB.UNKNOWN_ERROR;

/**
 * Created by alfredo on 26/08/17.
 */
public class RankingFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        LevelMenuFragment.LevelStateChanged,
        RankingRecyclerViewAdapter.OnItemClickListener,
        Constants,
        TaskProgressInterface,
        RetrieveRankingRunnable.RetrieveListFromRunnable {

    private final static String TAG = RankingFragment.class.getSimpleName();

    // Resource
    private static int level;
    private boolean refreshListOnFirstStart;

    // Fragment dialog
    private LevelMenuFragment levelMenuFragment;

    // Widgets
    private Context context;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout emptyListLayout;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        this.layoutManager = new LinearLayoutManager(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return this.rootView = inflater.inflate(R.layout.fragment_ranking_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initWidgets();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (this.refreshListOnFirstStart) {
            this.onRefresh();
        }
    }

    @Override
    public void retrieveList(ArrayList<User> arrayList, ArrayList<Integer> resultCode) {
        this.showToastFromResult(resultCode);

        RankingRecyclerViewAdapter rankingRecyclerViewAdapter =
                new RankingRecyclerViewAdapter(this.context, arrayList, this);
        // To avoid memory leaks set adapter in onActivityCreated
        this.recycleView.setAdapter(rankingRecyclerViewAdapter);


        this.hideProgress();
        // Check if fragment is attached to activity
        if (this.isAdded()) {
            if (arrayList != null && arrayList.size() > 0) {
                this.emptyListLayout.setVisibility(View.GONE);
                int scrollPosition = this.getPositionCurrentUser(arrayList);
                if (scrollPosition >= 0) {
                    // To show current user on top of the list view
                    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this.context) {
                        @Override
                        protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };
                    smoothScroller.setTargetPosition(scrollPosition);
                    this.layoutManager.startSmoothScroll(smoothScroller);
                }
                Toast.makeText(this.context, getString(R.string.refresh_complete), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.context, getString(R.string.level_empty_list), Toast.LENGTH_SHORT).show();
                this.emptyListLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private int getPositionCurrentUser(ArrayList<User> arrayList) {
        SingletonUser user = SingletonUser.getInstance();
        for (int i = 0; i < arrayList.size(); ++i) {
            if (user != null && arrayList.get(i).getUid() != null &&
                    user.getUid().equals(arrayList.get(i).getUid())) {
                return i;
            }
        }

        return -1;
    }

    private void showToastFromResult(final ArrayList<Integer> result) {
        if (this.getActivity() == null) {
            return;
        }

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG);
                for (int code : result) {
                    switch (code) {
                        case NOT_ALLOWED:
                            try {
                                toast.setText(String.format(
                                        getString(R.string.bad_query_unknown_position),
                                        getString(R.string.filter_not_traceable)
                                ));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            toast.show();
                            break;

                        case UNKNOWN_ERROR:
                            try {
                                toast.setText(getString(R.string.unknown_error));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            toast.show();
                            break;

                        case POSITION_NOT_FOUND:
                            try {
                                toast.setText(getString(R.string.position_not_found));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            toast.show();
                            break;

                        case INVALID_POSITION:
                            try {
                                toast.setText(getString(R.string.invalid_position));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            toast.show();
                            break;

                        default:
                            Log.d(TAG, "onErrorReceived: " + code);
                            try {
                                toast.setText(getString(R.string.unable_load_ranking));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            toast.show();
                    }
                }
            }
        });
    }

    @Override
    public void onLevelChanged(int level) {
        RankingFragment.level = level;

        this.onRefresh();
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
                this.onRefresh();
                return true;

            // Show DialogFragment
            case R.id.menu_selection_level:
                if (!this.levelMenuFragment.isAdded() && this.getFragmentManager() != null) {
                    this.levelMenuFragment.show(this.getFragmentManager(), getString(R.string.dialogue_level_menu));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_selection_level).setVisible(true);
        menu.findItem(R.id.refresh_action).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    private void initResources() {
        this.setRetainInstance(true);

        this.levelMenuFragment = new LevelMenuFragment();
        this.levelMenuFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        this.levelMenuFragment.addLevelListener(this);

        RankingFragment.level = LEVEL_DISTRICT;

        this.refreshListOnFirstStart = true;

        // Check if system clock is correct
        // new Thread(new RetrieveNetworkTime(this)).start();
    }

    private void initWidgets() {
        // Set action bar title
        ((Activity) this.context).setTitle(R.string.ranking);

        // To modify Menu items
        this.setHasOptionsMenu(true);

        this.recycleView = this.rootView.findViewById(R.id.recycler_view_user);
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
        this.swipeRefreshLayout = this.rootView.findViewById(R.id.swiperefresh_ranking);
        this.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this.context, R.color.colorPrimaryDark),
                ContextCompat.getColor(this.context, R.color.colorItemList),
                ContextCompat.getColor(this.context, R.color.colorItemList2)
        );
        this.swipeRefreshLayout.setOnRefreshListener(this);

        this.emptyListLayout = this.rootView.findViewById(R.id.layout_empty_list);
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
    public void onItemClick(User user) {
        Intent userDetail = new Intent(this.getActivity(), UserDetailsRankingActivity.class);
        userDetail.putExtra(USER, user);
        this.startActivity(userDetail);
    }

    @Override
    public void onRefresh() {
        if (!NetworkConnectionUtil.isConnectedToInternet(this.context)) {
            if (this.getActivity() != null) {
                this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (this.swipeRefreshLayout.isRefreshing()) {
                this.hideProgress();
            }
            return;
        }

        if (!this.swipeRefreshLayout.isRefreshing()) {
            this.showProgress();
        }
        new Thread(new RetrieveRankingRunnable(this)).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentsState.setFragmentState(FragmentsState.RANKING_FRAGMENT, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        this.refreshListOnFirstStart = false;
        Log.d(TAG, "onPause");
        FragmentsState.setFragmentState(FragmentsState.RANKING_FRAGMENT, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        FragmentsState.setFragmentState(FragmentsState.RANKING_FRAGMENT, false);
    }
}