package com.driveembetter.proevolutionsoftware.driveembetter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingGraphFragment;

/**
 * Created by alfredo on 15/10/17.
 */

public class RankingGraphPageAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private final static String TAG = RankingGraphPageAdapter.class.getSimpleName();

    private static int NUM_ITEMS = 4;

    public RankingGraphPageAdapter(FragmentManager fragmentManager, String userID) {
        super(fragmentManager);

        RankingGraphFragment.setUserID(userID);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "SCROLLED: " + position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "SELECTED: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "SCROLLSTATE: " + state);
    }

    @Override
    public int getCount() {
        return RankingGraphPageAdapter.NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return RankingGraphFragment.newInstance(RankingGraphFragment.VELOCITY_GRAPH_DAILY);

            case 1: return RankingGraphFragment.newInstance(RankingGraphFragment.ACCELERATION_GRAPH_DAILY);

            case 2: return RankingGraphFragment.newInstance(RankingGraphFragment.VELOCITY_GRAPH_WEEKLY);

            case 3: return RankingGraphFragment.newInstance(RankingGraphFragment.ACCELERATION_GRAPH_WEEKLY);

            // case 4: return RankingGraphFragment.newInstance(RankingGraphFragment.FEEDBACK_GRAPH);

            // case 5: return RankingGraphFragment.newInstance(RankingGraphFragment.POINTS_GRAPH);

            default:
                Log.e(TAG, "RankingGraphPageAdapter - Position not found: " + position);
                return null;
        }
    }
}