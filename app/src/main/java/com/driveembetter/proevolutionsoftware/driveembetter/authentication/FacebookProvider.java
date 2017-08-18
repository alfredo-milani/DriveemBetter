package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;

/**
 * Created by alfredo on 17/08/17.
 */

public class FacebookProvider extends FirebaseProvider {

    private final static String TAG = "FacebookProvider";

    public FacebookProvider(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signOut() {

    }
}
