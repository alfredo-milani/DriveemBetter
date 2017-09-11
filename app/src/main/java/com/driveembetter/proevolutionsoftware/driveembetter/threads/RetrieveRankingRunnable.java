package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_DISTRICT;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_NATION;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_REGION;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LevelStateChanged.LEVEL_UNAVAILABLE;
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
    private PositionManager positionManager;
    private RankingFragment rankingFragment;
    private RetrieveListFromRunnable callback;
    private ArrayList<Integer> errorCode;

    public interface RetrieveListFromRunnable {
        void retrieveList(ArrayList<User> arrayList, ArrayList<Integer> resultCode);
    }

    public RetrieveRankingRunnable(RankingFragment rankingFragment) {
        this.rankingFragment = rankingFragment;
        this.positionManager = PositionManager.getInstance(rankingFragment.getActivity());
        this.errorCode = new ArrayList<>();
        this.onAttach();
    }



    @Override
    public void run() {
        double latitude = this.positionManager.getLatitude();
        double longitude = this.positionManager.getLongitude();

        Log.d(TAG, "positionManager: lat: " + latitude + " long: " + longitude);
        if (latitude == 0 || longitude == 0) {
            FirebaseDatabaseManager.getCoordinates(this);
        } else {
            this.performQuery(new double[] {latitude, longitude});
        }
    }

    private void onAttach() {
        try {
            this.callback = (RetrieveListFromRunnable) this.rankingFragment;
        } catch (ClassCastException e) {
            throw new CallbackNotInitialized(TAG);
        }
    }

    private void performQuery(double[] position) {
        String[] location = this.positionManager.getLocationFromCoordinates(
                position[0],
                position[1],
                1
        );
        // location[0] -> nation; location[1] -> region; location[2] -> district
        String nation = location[0]; String region = location[1]; String district = location[2];
        // DEBUG
        // nation = "Italy"; region = "Lazio"; district = "Provincia di Frosinone";
        ////
        Log.d(TAG, "performQuery: " + nation + "/" + region + "/" + district);
        if (nation == null || region == null || district == null) {
            // Unknown error in PositionManager
            this.onErrorReceived(POSITION_NOT_FOUND);
        } else if (nation.equals(COUNTRY) || region.equals(REGION) || district.equals(SUB_REGION)) {
            // Indefinite position
            switch (RankingFragment.getLevel()) {
                case LEVEL_NATION:
                case LEVEL_REGION:
                case LEVEL_DISTRICT:
                    RankingFragment.setLevel(LEVEL_UNAVAILABLE);
                    this.onErrorReceived(NOT_ALLOWED);
                    break;
            }
            FirebaseDatabaseManager.getUsersRank(this, new String[] {COUNTRY, REGION, SUB_REGION});
        } else {
            FirebaseDatabaseManager.getUsersRank(this, new String[] {nation, region, district});
        }
    }

    @Override
    public void onErrorReceived(int errorType) {
        Log.d(TAG, "Runnable, error: " + errorType);
        this.errorCode.add(errorType);
    }

    @Override
    public void onUsersCoordinatesReceived(double[] position) {
        if (position != null) {
            Log.d(TAG, "onUserCoordinatesReceived: position: " + position[0] + "/" + position[1]);
            this.performQuery(position);
        } else {
            Log.d(TAG, "onUserCoordinatesReceived: position NULL");
            // Indefinite position
            switch (RankingFragment.getLevel()) {
                case LEVEL_NATION:
                case LEVEL_REGION:
                case LEVEL_DISTRICT:
                    RankingFragment.setLevel(LEVEL_UNAVAILABLE);
                    this.onErrorReceived(NOT_ALLOWED);
                    break;
            }
            FirebaseDatabaseManager.getUsersRank(this, null);
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

        this.errorCode.add(OK);
        this.callback.retrieveList(arrayList, this.errorCode);
    }
}
