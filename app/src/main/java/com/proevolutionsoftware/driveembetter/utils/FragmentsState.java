package com.proevolutionsoftware.driveembetter.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.boundary.fragment.AboutUsFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.ChartFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.GarageFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.HomeFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.RankingFragment;
import com.proevolutionsoftware.driveembetter.boundary.fragment.SaveMeFragment;
import com.proevolutionsoftware.driveembetter.exceptions.WrongResourceType;

/**
 * Created by matti on 23/08/2017.
 */

public class FragmentsState {

    private final static String TAG = FragmentsState.class.getSimpleName();

    // Resources
    private static FragmentManager fragmentManager;
    private static int currentInAnimation;
    private static int currentOutAnimation;

    // Constants
    public final static short SAVE_ME_FRAGMENT = 0;
    public final static short STATISTICS_FRAGMENT = 1;
    public final static short RANKING_FRAGMENT = 2;
    public final static short GARAGE_FRAGMENT = 3;
    public final static short ABOUT_US = 4;
    public final static short HOME_FRAGMENT = 5;

    // State
    private static boolean[] fragmentState = new boolean[6];
    private static String[] fragmetsTag = new String[] {
            SaveMeFragment.class.getSimpleName(),
            ChartFragment.class.getSimpleName(),
            RankingFragment.class.getSimpleName(),
            GarageFragment.class.getSimpleName(),
            AboutUsFragment.class.getSimpleName(),
            HomeFragment.class.getSimpleName()
    };



    public FragmentsState(FragmentManager fragmentManager) {
        FragmentsState.fragmentManager = fragmentManager;
        FragmentsState.currentInAnimation = R.anim.fragment_fade_in;
        FragmentsState.currentOutAnimation = R.anim.fragment_fade_out;
    }



    public static boolean isFragmentOpen(int fragmentType)
            throws WrongResourceType {
        switch (fragmentType) {
            case SAVE_ME_FRAGMENT:
                return FragmentsState.fragmentState[SAVE_ME_FRAGMENT];

            case STATISTICS_FRAGMENT:
                return FragmentsState.fragmentState[STATISTICS_FRAGMENT];

            case RANKING_FRAGMENT:
                return FragmentsState.fragmentState[RANKING_FRAGMENT];

            case GARAGE_FRAGMENT:
                return FragmentsState.fragmentState[GARAGE_FRAGMENT];

            case ABOUT_US:
                return FragmentsState.fragmentState[ABOUT_US];

            case HOME_FRAGMENT:
                return FragmentsState.fragmentState[HOME_FRAGMENT];

            default:
                Log.w(TAG, "Error in isFragmentOpen:wrong fragment type: " + fragmentType);
                throw new WrongResourceType("Error in isFragmentOpen:wrong fragment type: " + fragmentType);
        }
    }

    public static boolean wasFragmentCreated(int fragmentType) {
        return FragmentsState.fragmentManager.findFragmentByTag(
                FragmentsState.fragmetsTag[fragmentType]
        ) != null;
    }

    public static void setFragmentState(int fragmentType, boolean status)
            throws WrongResourceType {
        switch (fragmentType) {
            case SAVE_ME_FRAGMENT:
                FragmentsState.fragmentState[SAVE_ME_FRAGMENT] = status;
                break;

            case STATISTICS_FRAGMENT:
                FragmentsState.fragmentState[STATISTICS_FRAGMENT] = status;
                break;

            case RANKING_FRAGMENT:
                FragmentsState.fragmentState[RANKING_FRAGMENT] = status;
                break;

            case GARAGE_FRAGMENT:
                FragmentsState.fragmentState[GARAGE_FRAGMENT] = status;
                break;

            case ABOUT_US:
                FragmentsState.fragmentState[ABOUT_US] = status;
                break;

            case HOME_FRAGMENT:
                FragmentsState.fragmentState[HOME_FRAGMENT] = status;
                break;

            default:
                Log.w(TAG, "Error in setFragmentState:wrong fragment type: " + fragmentType);
                throw new WrongResourceType("Error in setFragmentState:wrong fragment type: " + fragmentType);
        }
    }

    public static void addFragmentToUI(int placeholder, Fragment fragment) {
        FragmentTransaction fragmentTransaction = FragmentsState.fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                FragmentsState.currentInAnimation,
                FragmentsState.currentOutAnimation
        );
        fragmentTransaction
                .add(placeholder, fragment)
                .commit();
    }

    public void replaceFragment(int placeholder, Fragment fragment) {
        FragmentTransaction fragmentTransaction = FragmentsState.fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                FragmentsState.currentInAnimation,
                FragmentsState.currentOutAnimation
        );
        fragmentTransaction
                .replace(placeholder, fragment, fragment.getClass().getSimpleName())
                // to override backButton behavior
                //.addToBackStack(null)
                .commit();
    }

    public void removeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = FragmentsState.fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                FragmentsState.currentInAnimation,
                FragmentsState.currentOutAnimation
        );
        fragmentTransaction
                .remove(fragment)
                .commit();
    }

    public static void setCurrentInAnimation(int currentInAnimation) {
        FragmentsState.currentInAnimation = currentInAnimation;
    }

    public static void setCurrentOutAnimation(int currentOutAnimation) {
        FragmentsState.currentOutAnimation = currentOutAnimation;
    }
}