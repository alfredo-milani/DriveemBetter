package com.proevolutionsoftware.driveembetter.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by alfredo on 23/10/17.
 */

public class PermissionManager {

    private final static String TAG = PermissionManager.class.getSimpleName();

    public final static int ASK_FOR_LOCATION_POS_MAN = 139;
    public final static int ASK_FOR_LOCATION_SAVE_ME = 189;
    public final static int ASK_FOR_ACCIDENT = 142;
    public final static int ASK_FOR_ALL = 189;
    public final static int ASK_FOR_READ_CONTACTS = 140;
    public final static int ASK_FOR_SEND_SMS = 141;

    public final static int PERM_OK = PackageManager.PERMISSION_GRANTED;
    public final static int PERM_NOT_OK = PackageManager.PERMISSION_DENIED;

    public final static int PERM_NOT_NECCESSARY = -1;

    public final static int FINE_LOCATION = 1;
    public final static String FINE_LOCATION_MANIFEST = Manifest.permission.ACCESS_FINE_LOCATION;

    public final static int COARSE_LOCATION = 2;
    public final static String COARSE_LOCATION_MANIFEST = Manifest.permission.ACCESS_COARSE_LOCATION;

    public final static int WAKE_LOCK = 3;
    public final static String WAKE_LOCK_MANIFEST = Manifest.permission.WAKE_LOCK;

    public final static int DISABLE_KEYGUARD = 4;
    public final static String DISABLE_KEYGUARD_MANIFEST = Manifest.permission.DISABLE_KEYGUARD;

    public final static int READ_CONTACTS = 5;
    public final static String READ_CONTACTS_MANIFEST = Manifest.permission.READ_CONTACTS;

    public final static int SEND_SMS = 6;
    public final static String SEND_SMS_MANIFEST = Manifest.permission.SEND_SMS;

    private final static int[] allPerms = {
            FINE_LOCATION,
            COARSE_LOCATION,
            WAKE_LOCK,
            DISABLE_KEYGUARD,
            READ_CONTACTS,
            SEND_SMS
    };



    // TODO se vengono revocati i permessi dalle impostazioni -> PermissionException
    public static void checkAndAskPermission(Activity activity, int[] permissions, int code) {
        int permsLen = permissions.length;
        if (permsLen < 1)   return;
        for (int i = 0; i < permsLen; ++i) {
            switch (permissions[i]) {
                case FINE_LOCATION:
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PERM_OK) {
                        permissions[i] = PERM_NOT_NECCESSARY;
                    }
                    break;

                case COARSE_LOCATION:
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PERM_OK) {
                        permissions[i] = PERM_NOT_NECCESSARY;
                    }
                    break;

                case WAKE_LOCK:
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.WAKE_LOCK
                    ) == PERM_OK) {
                        permissions[i] = PERM_NOT_NECCESSARY;
                    }
                    break;

                case DISABLE_KEYGUARD:
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.DISABLE_KEYGUARD
                    ) == PERM_OK) {
                        permissions[i] = PERM_NOT_NECCESSARY;
                    }
                    break;

                case READ_CONTACTS:
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.READ_CONTACTS
                    ) == PERM_OK) {
                        permissions[i] = PERM_NOT_NECCESSARY;
                    }
                    break;

                case SEND_SMS:
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.SEND_SMS
                    ) == PERM_OK) {
                        permissions[i] = PERM_NOT_NECCESSARY;
                    }
                    break;
            }
        }

        int requiredPermsLen = 0;
        for (int perm : permissions) {
            if (perm != PERM_NOT_NECCESSARY) {
                ++requiredPermsLen;
            }
        }
        String[] perms = new String[requiredPermsLen];
        for (int i = 0; i < requiredPermsLen; ++i) {
            switch (permissions[i]) {
                case FINE_LOCATION:
                    perms[i] = FINE_LOCATION_MANIFEST;
                    break;

                case COARSE_LOCATION:
                    perms[i] = COARSE_LOCATION_MANIFEST;
                    break;

                case WAKE_LOCK:
                    perms[i] = WAKE_LOCK_MANIFEST;
                    break;

                case DISABLE_KEYGUARD:
                    perms[i] = DISABLE_KEYGUARD_MANIFEST;
                    break;

                case READ_CONTACTS:
                    perms[i] = READ_CONTACTS_MANIFEST;
                    break;

                case SEND_SMS:
                    perms[i] = SEND_SMS_MANIFEST;
                    break;
            }
        }

        if (requiredPermsLen != 0) {
            ActivityCompat.requestPermissions(
                    activity,
                    perms,
                    code
            );
        }
    }

    public static void checkAllPerms(Activity activity) {
        PermissionManager.checkAndAskPermission(activity, PermissionManager.allPerms, ASK_FOR_ALL);
    }

    public static void askForPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(
                activity,
                permissions,
                requestCode
        );
    }

    public static boolean isAllowed(Activity activity, String permissionToCheck) {
        return ContextCompat.checkSelfPermission(
                activity,
                permissionToCheck
        ) == PERM_OK;
    }
}