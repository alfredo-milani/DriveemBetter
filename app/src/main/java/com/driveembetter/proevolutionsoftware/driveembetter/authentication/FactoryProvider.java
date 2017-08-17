package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.exeption.ProviderNotFoundExeption;

/**
 * Created by alfredo on 17/08/17.
 */

public class FactoryProvider {

    private final static String TAG = "FactoryProvider";

    private final Context mContext;
    private final Handler mHandler;

    public FactoryProvider(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public Provider createProvider(int provider) throws ProviderNotFoundExeption {
        switch (provider) {
            case 1:
                Log.d(TAG, "Created object " + provider);
                return new EmailAndPasswordProvider(this.mContext, this.mHandler);

            case 2:
                Log.d(TAG, "Created object " + provider);
                return new GoogleProvider(this.mContext, this.mHandler);

            case 3:
                Log.d(TAG, "Created object " + provider);
                return new FacebookProvider(this.mContext, this.mHandler);

            case 4:
                Log.d(TAG, "Created object " + provider);
                return new TwitterProvider(this.mContext, this.mHandler);

            default:
                Log.d(TAG, "Object not found: " + provider);
                throw new ProviderNotFoundExeption(
                        String.format(
                                this.mContext.getString(R.string.invalid_provider_type),
                                provider
                        )
                );
        }
    }

    public Provider EmailAndPasswordProvider(Context context, Handler handler) {
        return new EmailAndPasswordProvider(this.mContext, this.mHandler);
    }

    public Provider createGoogleProvider(Context context, Handler handler) {
        return new GoogleProvider(this.mContext, this.mHandler);
    }

    public Provider createFacebookProvider(Context context, Handler handler) {
        return new FacebookProvider(this.mContext, this.mHandler);
    }

    public Provider createTwitterProvider(Context context, Handler handler) {
        return new TwitterProvider(this.mContext, this.mHandler);
    }
}