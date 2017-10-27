package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_FIRST_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_PHONE_NO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_SECOND_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.NODE_USERS;

/**
 * Created by Mattia on 24/10/2017.
 */

public class EmergencyActivity extends AppCompatActivity {
    TextView emergencyText;
    Button emergencyButton;
    private Boolean canceled = false;
    private TextToSpeech tts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_emergency);

        init();
    }

    private void init() {

        //createNotification();

        this.emergencyButton = findViewById(R.id.emergency_button);
        this.emergencyText = findViewById(R.id.emergency_text);

        //add animation
        AlphaAnimation blinkanimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(1000); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
        emergencyText.startAnimation(blinkanimation);

        startEmergency();
    }

    private void startEmergency() {

        Toast.makeText(this, getResources().getString(R.string.crash_detected), Toast.LENGTH_LONG).show();

        //start timer
        final CountDownTimer myCountDownTimerObject = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                if ((double) (millisUntilFinished / 1000) % 2 < 200) {
                    emergencyButton.setText("" + millisUntilFinished / 1000);
                }
            }

            public void onFinish() {
                if (!canceled) {
                    emergencyButton.setVisibility(View.GONE);
                    final SingletonUser user = SingletonUser.getInstance();
                    final DatabaseReference databaseReference = FirebaseDatabaseManager.getDatabaseReference()
                            .child(NODE_USERS)
                            .child(user.getUid());
                    //CASE: FRIEND 1
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(CHILD_FIRST_FRIEND)) {
                                String phoneNo = dataSnapshot.child(CHILD_FIRST_FRIEND).child(CHILD_PHONE_NO).getValue().toString();

                                String myAddress = user.getAddress();
                                if (myAddress.equals("address"))
                                    myAddress = "";

                                sendSMS(phoneNo, getResources().getString(R.string.help_request) +
                                        myAddress);
                            }
                            if (dataSnapshot.hasChild(CHILD_SECOND_FRIEND)) {
                                String phoneNo = dataSnapshot.child(CHILD_SECOND_FRIEND).child(CHILD_PHONE_NO).getValue().toString();

                                sendSMS(phoneNo, getResources().getString(R.string.help_request) +
                                        user.getAddress());
                            }
                            canceled = false;//?
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

        };
        myCountDownTimerObject.start();
        emergencyButton.setVisibility(View.VISIBLE);
        emergencyButton.setText("30");
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canceled = true;
                myCountDownTimerObject.cancel();
                emergencyButton.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.canceled_action), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(this, getResources().getString(R.string.sms_sent),
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {


        //Close the Text to Speech Library
        if(tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


}
