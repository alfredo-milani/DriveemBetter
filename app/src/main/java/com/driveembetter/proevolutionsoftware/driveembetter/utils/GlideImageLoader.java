package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.driveembetter.proevolutionsoftware.driveembetter.R;

import java.util.Locale;

/**
 * Created by alfredo on 12/10/17.
 */

public class GlideImageLoader {

    private final static String TAG = GlideImageLoader.class.getSimpleName();

    private final static String format = "android.resource://%s/%d";
    private final static String format2 = "%s://%s/%s/%s";


    public static void loadImageUri(final Activity context, ImageView imageView, Uri url, int placeHolderUrl, int errorImageUrl) {
        if (context == null || context.isDestroyed()) return;

        Glide.with(context)
                .load(url)
                .placeholder(placeHolderUrl) // Default image. Loaded at initial time
                .error(errorImageUrl) // In case of any glide exception or not able to download then this image will be appear.
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Using to load into cache then second time it will load fast.
                .animate(R.anim.glide_fade_in)
                .fitCenter()
                .into(imageView);
    }

    public static void loadImageID(final Activity context, ImageView imageView, int resID, int errorImageUrl) {
        if (context == null || context.isDestroyed()) return;

        Glide.with(context)
                .load(resID)
                .error(errorImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(R.anim.glide_fade_in)
                .fitCenter()
                .into(imageView);
    }

    public static Uri fromResourceToUri(Context context, int resource) {
        return Uri.parse(String.format(
                Locale.ENGLISH,
                format,
                context.getPackageName(),
                resource
        ));
    }

    public static Uri fromResourceToUri2(Context context, int resource) {
        Resources resources = context.getResources();

        return Uri.parse(String.format(
                Locale.ENGLISH,
                format2,
                ContentResolver.SCHEME_ANDROID_RESOURCE,
                resources.getResourcePackageName(resource),
                resources.getResourceTypeName(resource),
                resources.getResourceEntryName(resource)
        ));
    }
}
