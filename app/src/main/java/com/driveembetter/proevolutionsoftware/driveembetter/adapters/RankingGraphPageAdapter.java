package com.driveembetter.proevolutionsoftware.driveembetter.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingGraphFragment;

/**
 * Created by alfredo on 15/10/17.
 */

public class RankingGraphPageAdapter extends FragmentPagerAdapter {

    private final static String TAG = RankingGraphPageAdapter.class.getSimpleName();

    private static int NUM_ITEMS = 4;

    private final Activity activity;

    public RankingGraphPageAdapter(Activity activity, FragmentManager fragmentManager, String userID) {
        super(fragmentManager);

        RankingGraphFragment.setUserID(userID);
        this.activity = activity;
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

    /*
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        ImageButton btn;

        LayoutInflater inflater = (LayoutInflater) this.activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.test_ranki, container,
                false);

        btn = viewLayout.findViewById(R.id.fullscreenImageButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("Info - ", "" + position);
                } catch (ActivityNotFoundException e) {
                    Log.d("Error", e.toString());
                }
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
    */
}