package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonFacebookProvider extends FirebaseProvider {

    private final static String TAG = "SFacebookProvider";

    private static SingletonFacebookProvider singletonInstance;

    private SingletonFacebookProvider(Context context, Handler handler) {
        super(context, handler);

        Log.d(TAG, "Instantiated SingletonFacebookProvider.\nContext: " + this.mContext + " Handler: " + this.mContext);
    }



    public static SingletonFacebookProvider getSingletonInstance(Context context, Handler handler){
        if(SingletonFacebookProvider.singletonInstance == null) {
            synchronized (SingletonFacebookProvider.class) {
                if(SingletonFacebookProvider.singletonInstance == null) {
                    SingletonFacebookProvider.singletonInstance =
                            new SingletonFacebookProvider(context, handler);
                }
            }
        }

        return SingletonFacebookProvider.getSingletonInstance();
    }

    public static SingletonFacebookProvider getSingletonInstance() {
        return SingletonFacebookProvider.singletonInstance;
    }



    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signOut() {

    }
}
