package com.driveembetter.proevolutionsoftware.driveembetter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.PageFragment;

/**
 * Created by alfredo on 15/10/17.
 */

public class PageAdapter extends FragmentPagerAdapter {

    private final static String TAG = PageAdapter.class.getSimpleName();

    private static int NUM_ITEMS = 5;
    private final String userID;

    public PageAdapter(FragmentManager fragmentManager, String userID) {
        super(fragmentManager);

        this.userID = userID;
    }



    @Override
    public int getCount() {
        return PageAdapter.NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return PageFragment.newInstance(PageFragment.VELOCITY_GRAPH_DAILY, this.userID);

            case 1: return PageFragment.newInstance(PageFragment.ACCELERATION_GRAPH_DAILY, this.userID);

            case 2: return PageFragment.newInstance(PageFragment.VELOCITY_GRAPH_WEEKLY, this.userID);

            case 3: return PageFragment.newInstance(PageFragment.ACCELERATION_GRAPH_WEEKLY, this.userID);

            case 4: return PageFragment.newInstance(PageFragment.FEEDBACK_GRAPH, this.userID);

            default: return PageFragment.newInstance(PageFragment.VELOCITY_GRAPH_DAILY, this.userID);
        }
    }
}