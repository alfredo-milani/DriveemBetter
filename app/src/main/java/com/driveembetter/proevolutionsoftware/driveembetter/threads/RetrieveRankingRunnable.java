package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.util.Log;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.DatabaseManager;
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
        DatabaseManager.RetrieveRankFromDB {

    private final static String TAG = RetrieveRankingRunnable.class.getSimpleName();

    // Resources
    private PositionManager positionManager;
    private RankingFragment rankingFragment;

    public RetrieveRankingRunnable(RankingFragment rankingFragment) {
        this.rankingFragment = rankingFragment;
        this.positionManager = PositionManager.getInstance(this.rankingFragment.getActivity());
    }



    @Override
    public void run() {
        double latitude = this.positionManager.getLatitude();
        double longitude = this.positionManager.getLongitude();

        Log.d(TAG, "positionManager: lat: " + latitude + " long: " + longitude);
        if (latitude == 0 || longitude == 0) {
            DatabaseManager.getCoordinates(this);
        } else {
            this.performQuery(new double[] {latitude, longitude});
        }
    }

    private void performQuery(double[] position) {
        // TODO getLocationFromCoordinates() NON FUNZIONA PORCODDIO!!!
        String[] location = this.positionManager.getLocationFromCoordinates(
                position[0],
                position[1],
                1
        );
        // location[0] --> nation; location[1] --> region; location[2] --> district
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
            DatabaseManager.getUsersRank(this, new String[] {COUNTRY, REGION, SUB_REGION});
        } else {
            DatabaseManager.getUsersRank(this, new String[] {nation, region, district});
        }
    }

    @Override
    public void onErrorReceived(int errorType) {
        String string;
        switch (errorType) {
            case NOT_ALLOWED:
                string = String.format(
                        this.rankingFragment.getContext().getString(R.string.bad_query_unknown_position),
                        this.rankingFragment.getContext().getString(R.string.filter_unavailable)
                );
                break;

            case UNKNOWN_ERROR:
                string = this.rankingFragment.getContext().getString(R.string.unknown_error);
                break;

            case POSITION_NOT_FOUND:
                string = this.rankingFragment.getContext().getString(R.string.position_not_found);
                break;

            case INVALID_POSITION:
                string = this.rankingFragment.getContext().getString(R.string.invalid_position);
                break;

            default:
                Log.d(TAG, "onErrorReceived: " + errorType);
                string = this.rankingFragment.getContext().getString(R.string.unable_load_ranking);
        }

        this.rankingFragment.fillList(null);
        Toast.makeText(this.rankingFragment.getContext(), string, Toast.LENGTH_LONG).show();
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
            DatabaseManager.getUsersRank(this, null);
        }
    }

    @Override
    public void onUsersRankingReceived(ArrayList<User> arrayList) {
        Log.d(TAG, "onUsersRankingReceived: " + arrayList);
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

        this.rankingFragment.fillList(arrayList);
    }
}
