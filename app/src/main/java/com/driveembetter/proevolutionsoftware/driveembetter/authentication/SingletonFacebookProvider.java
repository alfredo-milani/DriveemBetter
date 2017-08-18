package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonFacebookProvider extends FirebaseProvider {

    private final static String TAG = "SFacebookProvider";

    private SingletonFacebookProvider(Context context, Handler handler) {
        super(context, handler);

        Log.d(TAG, "Instantiated SingletonFacebookProvider.\nContext: " + this.mContext + " Handler: " + this.mContext);
    }



    // Fast and thread-safe Singleton
    private static class SingletonFacebookProviderContainer {
        @SuppressLint("StaticFieldLeak")
        private static Context contextContainer;
        private static Handler handlerContainer;

        private SingletonFacebookProviderContainer(Context context, Handler handler) {
            SingletonFacebookProvider.SingletonFacebookProviderContainer.contextContainer = context;
            SingletonFacebookProvider.SingletonFacebookProviderContainer.handlerContainer = handler;
        }

        @SuppressLint("StaticFieldLeak")
        private final static SingletonFacebookProvider singletonInstance =
                new SingletonFacebookProvider(
                        SingletonFacebookProvider.SingletonFacebookProviderContainer.contextContainer,
                        SingletonFacebookProvider.SingletonFacebookProviderContainer.handlerContainer
                );
    }

    public static SingletonFacebookProvider getSingletonInstance() {
        if (SingletonFacebookProvider.SingletonFacebookProviderContainer.contextContainer == null ||
                SingletonFacebookProvider.SingletonFacebookProviderContainer.handlerContainer == null) {
            Log.w(
                    TAG, "FacebookProvider:getSingleton: context: " +
                            SingletonFacebookProvider.SingletonFacebookProviderContainer.contextContainer +
                            " handler: " +
                            SingletonFacebookProvider.SingletonFacebookProviderContainer.handlerContainer
            );
        }

        return SingletonFacebookProvider.SingletonFacebookProviderContainer.singletonInstance;
    }

    public static SingletonFacebookProvider getSingletonInstance(Context context, Handler handler) {
        if (context != null && handler != null) {
            SingletonFacebookProvider.bindingProviderToUI(context, handler);
        }

        return SingletonFacebookProvider.getSingletonInstance();
    }

    private static void bindingProviderToUI(Context context, Handler handler) {
        Log.d(TAG, "bindingProviderToUI: contex" + context + " handler: " + handler);
        new SingletonFacebookProvider.SingletonFacebookProviderContainer(context, handler);
    }



    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signOut() {

    }
}
