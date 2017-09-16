package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_DISTRICT;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_NATION;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_NOT_TRACEABLE;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_REGION;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.COUNTRY;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.REGION;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.SUB_REGION;

/**
 * Created by alfredo on 08/09/17.
 */

public class RetrieveRankingRunnable
        implements Runnable,
        FirebaseDatabaseManager.RetrieveRankFromDB {

    private final static String TAG = RetrieveRankingRunnable.class.getSimpleName();

    // Resources
    private RankingFragment rankingFragment;
    private RetrieveListFromRunnable callback;
    private ArrayList<Integer> errorCode;

    public interface RetrieveListFromRunnable {
        void retrieveList(ArrayList<User> arrayList, ArrayList<Integer> resultCode);
    }

    public RetrieveRankingRunnable(RankingFragment rankingFragment) {
        this.rankingFragment = rankingFragment;
        this.errorCode = new ArrayList<>();
        this.onAttach();
    }



    @Override
    public void run() {
        if (SingletonUser.getInstance() == null) {
            return;
        }
        Log.d(TAG, "positionManager: lat: " + SingletonUser.getInstance().getLatitude() + " long: " + SingletonUser.getInstance().getLongitude());
        this.performQuery();
    }

    private void onAttach() {
        try {
            this.callback = (RetrieveListFromRunnable) this.rankingFragment;
        } catch (ClassCastException e) {
            throw new CallbackNotInitialized(TAG);
        }
    }

    private void performQuery() {
        String country = SingletonUser.getInstance().getCountry();
        String region = SingletonUser.getInstance().getRegion();
        String subRegion = SingletonUser.getInstance().getSubRegion();

        Log.d(TAG, "performQuery: " + country + "/" + region + "/" + subRegion);
        if (subRegion.equals(SUB_REGION) || region.equals(REGION) || country.equals(COUNTRY)) {
            // Indefinite position
            switch (RankingFragment.getLevel()) {
                case LEVEL_NATION:
                case LEVEL_REGION:
                case LEVEL_DISTRICT:
                    RankingFragment.setLevel(LEVEL_NOT_TRACEABLE);
                    this.onErrorReceived(NOT_ALLOWED);
                    break;
            }
        }

        FirebaseDatabaseManager.getUsersRank(this);
    }

    @Override
    public void onErrorReceived(int errorType) {
        Log.d(TAG, "Runnable, error: " + errorType);

        this.errorCode.add(errorType);
        if (errorType == UNKNOWN_ERROR) {
            this.callback.retrieveList(null, this.errorCode);
        }
    }

    @Override
    public void onUsersRankingReceived(ArrayList<User> arrayList) {
        Log.d(TAG, "onUsersRankingReceived");
        // Descending order
        if (arrayList != null) {
            Collections.sort(arrayList, new Comparator<User>() {
                @Override
                public int compare(User user1, User user2) {
                    return user1.getPoints() < user2.getPoints() ?
                            1 : -1;
                }
            });
        }

        this.callback.retrieveList(arrayList, this.errorCode);
    }
}
