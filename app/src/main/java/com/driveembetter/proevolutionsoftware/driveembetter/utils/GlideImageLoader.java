package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.content.Context;
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


    public static void loadImage(final Activity context, ImageView imageView, Uri url, int placeHolderUrl, int errorImageUrl) {
        if (context == null || context.isDestroyed()) return;

        Glide.with(context)
                .load(url)
                .placeholder(placeHolderUrl) // Default image. Loaded at initial time
                .error(errorImageUrl) // In case of any glide exception or not able to download then this image will be appear.
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Using to load into cache then second time it will load fast.
                .animate(R.anim.fade_in_glide)
                .fitCenter()
                .into(imageView);


        /*
        Glide.with(context)
                .load(url)
                .dontTransform()
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this.userPicture);
        */
    }

    public static Uri fromResourceToUri(Context context, int resource) {
        return Uri.parse(String.format(
                Locale.ENGLISH,
                format,
                context.getPackageName(),
                resource
        ));
    }
}
