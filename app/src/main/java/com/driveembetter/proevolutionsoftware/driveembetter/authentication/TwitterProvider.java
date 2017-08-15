package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;

/**
 * Created by alfredo on 15/08/17.
 */

public class TwitterProvider extends Authentication {

    public TwitterProvider(Context context, Handler handler) {
        super(context, handler);
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
