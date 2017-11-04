package com.proevolutionsoftware.driveembetter.boundary.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.adapters.RankingGraphPageAdapter;
import com.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.proevolutionsoftware.driveembetter.utils.FragmentsState;

/**
 * Created by alfredo on 03/11/17.
 */

public class StatisticsFragment extends Fragment {

    private final static String TAG = StatisticsFragment.class.getSimpleName();

    // Resources
    private SingletonUser user;

    // Widgets
    private PagerAdapter pagerAdapter;
    private View rootView;
    private Context context;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return this.rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initWidgets();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initResources() {
        this.setRetainInstance(true);
        this.user = SingletonUser.getInstance();
    }

    private void initWidgets() {
        // Set action bar title
        ((Activity) this.context).setTitle(R.string.statistics);

        final ViewPager pager = this.rootView.findViewById(R.id.vpPager);
        this.pagerAdapter = new RankingGraphPageAdapter(
                this.getFragmentManager(),
                this.user.getUid()
        );
        pager.setAdapter(this.pagerAdapter);
        // Increase cache limit
        // TODO: 18/10/17 Per ora ci sono 5 tipi di grafici, quindi con un valore come 4 non viene distrutto nessun fragment. Nel caso in cui si qualche fragment venisse distrutto (aumento numero fragments o diminuzione valore di offset) gestire la ricostruzione del fragment (rendering legenda ecc...)
        // pager.setOffscreenPageLimit(4); // TODO: 26/10/17 BUG DATO DA QUESTA RIGA DI CODICE
        // pager.setPageTransformer(true, new AccordionTransformer());
        pager.setPageTransformer(true, new DepthPageTransformer());
        // pager.setPageTransformer(true, new ZoomOutSlideTransformer());
        // pager.setPageTransformer(true, new CubeInTransformer());
        // pager.setPageTransformer(true, new FlipHorizontalTransformer());
        // To set default page fragment
        // pager.setCurrentItem(int currentItem);
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentsState.setFragmentState(FragmentsState.STATISTICS_FRAGMENT, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        FragmentsState.setFragmentState(FragmentsState.STATISTICS_FRAGMENT, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        FragmentsState.setFragmentState(FragmentsState.STATISTICS_FRAGMENT, false);
    }
}
