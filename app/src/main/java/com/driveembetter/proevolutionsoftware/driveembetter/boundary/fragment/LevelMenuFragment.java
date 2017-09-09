package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.adapters.LevelMenuRecyclerViewAdapter;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;

/**
 * Created by sonu on 08/02/17.
 */
public class LevelMenuFragment
        extends DialogFragment
        implements View.OnClickListener,
        Constants {

    private final static String TAG = LevelMenuFragment.class.getSimpleName();

    // Resources
    private LevelMenuRecyclerViewAdapter adapter;
    private SparseIntArray sparseIntArray;
    private LevelStateChanged callback;

    // Widgets
    private Context context;
    private View rootView;
    private RecyclerView recycleView;
    private RecyclerView.LayoutManager layoutManager;



    public interface LevelStateChanged {
        // Users divided by location
        int LEVEL_NATION = 1;
        int LEVEL_REGION = 2;
        int LEVEL_DISTRICT = 3;
        int LEVEL_AVAILABLE = 4;
        int LEVEL_UNAVAILABLE = 5;
        int LEVEL_ALL = 6;

        void onLevelChanged(int level);
    }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            // Change the title divider color
            final Resources res = getResources();
            final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
            Log.d(TAG, "Title: " + titleDividerId + " / " + getDialog());
            final View titleDivider = getDialog().findViewById(titleDividerId);
            titleDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_800));
        }

        return this.rootView = inflater.inflate(R.layout.fragment_level_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initWidgets();
    }

    private void initResources() {
        // Key value of item level and corresponding string
        this.sparseIntArray = new SparseIntArray(6);
        this.sparseIntArray.append(LevelStateChanged.LEVEL_NATION, R.string.level_nation);
        this.sparseIntArray.append(LevelStateChanged.LEVEL_REGION, R.string.level_region);
        this.sparseIntArray.append(LevelStateChanged.LEVEL_DISTRICT, R.string.level_district);
        this.sparseIntArray.append(LevelStateChanged.LEVEL_AVAILABLE, R.string.level_available);
        this.sparseIntArray.append(LevelStateChanged.LEVEL_UNAVAILABLE, R.string.level_unavailable);
        this.sparseIntArray.append(LevelStateChanged.LEVEL_ALL, R.string.level_all);
    }

    private void initWidgets() {
        this.recycleView = (RecyclerView) this.rootView.findViewById(R.id.recycler_view);
        this.recycleView.setHasFixedSize(true);
        this.recycleView.setLayoutManager(this.layoutManager);

        this.adapter = new LevelMenuRecyclerViewAdapter(this.context, this.sparseIntArray);
        // Set district by default
        this.adapter.setRadioButtonValue(RankingFragment.getLevel());
        this.recycleView.setAdapter(this.adapter);

        this.getDialog().setTitle(getString(R.string.dialogue_level_menu));
        this.getDialog().setCancelable(false);

        ((Button) this.rootView.findViewById(R.id.ok)).setOnClickListener(this);
        ((Button) this.rootView.findViewById(R.id.cancel)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.dismiss();

        int id = view.getId();
        switch (id) {
            case R.id.ok:
                if (this.callback == null) {
                    throw new CallbackNotInitialized(TAG);
                }
                this.callback.onLevelChanged(this.adapter.getSelectedItem());
                break;

            case R.id.cancel:
                break;
        }
    }

    public void addLevelListener(LevelStateChanged levelStateChanged) {
        this.callback = levelStateChanged;
    }
}
