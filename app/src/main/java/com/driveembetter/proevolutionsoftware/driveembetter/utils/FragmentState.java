package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.exception.WrongResourceType;

/**
 * Created by matti on 23/08/2017.
 */

public class FragmentState extends Application {

    private final static String TAG = FragmentState.class.getSimpleName();

    private static FragmentManager fragmentManager;
    private static int currentInAnimation;
    private static int currentOutAnimation;

    public final static short SAVE_ME_FRAGMENT = 1;
    public final static short STATISTICS_FRAGMENT = 2;
    public final static short RANKING_FRAGMENT = 3;
    public final static short GARAGE_FRAGMENT = 4;

    private static boolean fragmentState[] = new boolean[4];



    public FragmentState(FragmentManager fragmentManager) {
        FragmentState.fragmentManager = fragmentManager;
        FragmentState.currentInAnimation = R.animator.fade_in;
        FragmentState.currentOutAnimation = R.animator.fade_out;
    }



    public static boolean isFragmentOpen(int fragmentType)
            throws WrongResourceType {
        switch (fragmentType) {
            case SAVE_ME_FRAGMENT:
                return FragmentState.fragmentState[SAVE_ME_FRAGMENT];

            case STATISTICS_FRAGMENT:
                return FragmentState.fragmentState[STATISTICS_FRAGMENT];

            case RANKING_FRAGMENT:
                return FragmentState.fragmentState[RANKING_FRAGMENT];

            case GARAGE_FRAGMENT:
                return FragmentState.fragmentState[GARAGE_FRAGMENT];

            default:
                Log.w(TAG, "Error in isFragmentOpen:wrong fragment type: " + fragmentType);
                throw new WrongResourceType("Error in isFragmentOpen:wrong fragment type: " + fragmentType);
        }
    }

    public static void setFragmentState(int fragmentType, boolean status)
            throws WrongResourceType {
        switch (fragmentType) {
            case SAVE_ME_FRAGMENT:
                FragmentState.fragmentState[SAVE_ME_FRAGMENT] = status;
                break;

            case STATISTICS_FRAGMENT:
                FragmentState.fragmentState[STATISTICS_FRAGMENT] = status;
                break;

            case RANKING_FRAGMENT:
                FragmentState.fragmentState[RANKING_FRAGMENT] = status;
                break;

            case GARAGE_FRAGMENT:
                FragmentState.fragmentState[GARAGE_FRAGMENT] = status;
                break;

            default:
                Log.w(TAG, "Error in setFragmentState:wrong fragment type: " + fragmentType);
                throw new WrongResourceType("Error in setFragmentState:wrong fragment type: " + fragmentType);
        }
    }

    public static void addFragmentToUI(int placeholder, Fragment fragment) {
        FragmentTransaction fragmentTransaction = FragmentState.fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                FragmentState.currentInAnimation,
                FragmentState.currentOutAnimation
        );
        fragmentTransaction
                .add(placeholder, fragment)
                .commit();
    }

    public static void replaceFragment(int placeholder, Fragment fragment) {
        FragmentTransaction fragmentTransaction = FragmentState.fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                FragmentState.currentInAnimation,
                FragmentState.currentOutAnimation
        );
        fragmentTransaction
                .replace(placeholder, fragment)
                // to override backButton behavior
                // .addToBackStack(null)
                .commit();
    }

    public static void removeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = FragmentState.fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                FragmentState.currentInAnimation,
                FragmentState.currentOutAnimation
        );
        fragmentTransaction
                .remove(fragment)
                .commit();
    }

    public static void setCurrentInAnimation(int currentInAnimation) {
        FragmentState.currentInAnimation = currentInAnimation;
    }

    public static void setCurrentOutAnimation(int currentOutAnimation) {
        FragmentState.currentOutAnimation = currentOutAnimation;
    }
}