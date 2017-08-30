package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alfredo on 26/08/17.
 */

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private final static String TAG = "ImageLoadTask";

    private String url;
    private ImageView imageView;

    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Log.d(TAG, "doInBackground");
        try {
            URL urlConnection = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Log.d(TAG, "onPostExecute");
        super.onPostExecute(result);
        this.imageView.setImageBitmap(result);
    }

}