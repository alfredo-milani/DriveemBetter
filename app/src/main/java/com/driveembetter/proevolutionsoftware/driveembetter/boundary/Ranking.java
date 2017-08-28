package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.driveembetter.proevolutionsoftware.driveembetter.R;

/**
 * Created by alfredo on 26/08/17.
 */

public class Ranking extends Fragment {

    private final static String TAG = "FragmentRanking";

    private Context context;

    // Widgets
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity().getApplicationContext();

        final View rootView = inflater.inflate(R.layout.fragment_ranking_list, container, false);
        this.rootView = rootView;


        /*
        find widgets
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        locationTxt = (TextView) rootView.findViewById(R.id.positionTxt);
        */

        return rootView;
    }

    private void initWidgets() {
        this.swipeRefreshLayout = (SwipeRefreshLayout) this.rootView.findViewById(R.id.swiperefresh_ranking);

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        this.swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.

                        // stuff
                    }
                }
        );
    }
}
