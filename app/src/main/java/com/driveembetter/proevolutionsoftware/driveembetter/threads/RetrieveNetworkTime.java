package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;

/**
 * Created by alfredo on 15/09/17.
 */

public class RetrieveNetworkTime
        implements Runnable {

    private final static String TAG = RetrieveNetworkTime.class.getSimpleName();

    // Resources
    private final CallbackTime callbackTime;
    private final static int maxLatency = 5 * 60 * 1000;    // Max latency in minutes

    public interface CallbackTime {
        void onTimeRetrieved(boolean result);
    }

    public RetrieveNetworkTime(CallbackTime callbackTime) {
        this.callbackTime = callbackTime;
        if (callbackTime == null) {
            throw new CallbackNotInitialized(TAG);
        }
    }



    @Override
    public void run() {
        long diff = this.checkTimeServer() - System.currentTimeMillis();
        if (diff > 0 && diff <= maxLatency) {
            this.callbackTime.onTimeRetrieved(true);
            return;
        }
        this.callbackTime.onTimeRetrieved(false);
    }

    private long checkTimeServer() {
        long currentTime = 0;

        try {
            // TODO addattare codice per tempo calcolato in funzione dellle time-zones
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName("0.europe.pool.ntp.org");
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

            /*
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALIAN);
            Date dateTimestamp = new Date(currentTime);
            Log.d(TAG, "TIME RETRIEVED: " + simpleDateFormat.format(dateTimestamp));
            */
        } catch (Exception e) {
            Log.v(TAG,"Time server error - "+e.getLocalizedMessage());
        }

        return currentTime;
    }
}
