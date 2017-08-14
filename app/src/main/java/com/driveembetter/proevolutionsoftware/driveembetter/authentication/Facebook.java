package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;

/**
 * Created by alfredo on 14/08/17.
 */

class Facebook extends Authentication {

    public Facebook(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signOut() {

    }

    @Override
    public void signUp(String email, String password) {

    }
}
