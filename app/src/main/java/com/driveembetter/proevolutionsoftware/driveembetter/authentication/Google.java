package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.util.Log;

/**
 * Created by alfredo on 11/08/17.
 */

class Google extends Authentication {

    private static final String TAG = "Google";

    Google(Context context) {
        super(context);
        Log.d(TAG, "Instantiated Google class");
    }

    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signUp(String email, String password) {

    }

    @Override
    public void signOut() {

    }
}
