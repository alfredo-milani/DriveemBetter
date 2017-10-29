package com.proevolutionsoftware.driveembetter.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by alfredo on 14/10/17.
 */

public class ManageCache {

    private final static String TAG = ManageCache.class.getSimpleName();



    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            ManageCache.deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = ManageCache.deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else {
            return dir != null && dir.isFile() && dir.delete();
        }
    }
}
