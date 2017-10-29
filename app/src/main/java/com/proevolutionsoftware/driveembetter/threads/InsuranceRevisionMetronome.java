package com.proevolutionsoftware.driveembetter.threads;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.proevolutionsoftware.driveembetter.utils.PointManager;

import static com.proevolutionsoftware.driveembetter.constants.Constants.MILL_IN_A_DAY;

/**
 * Created by matteo on 17/10/17.
 */

public class InsuranceRevisionMetronome {

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private long current_millis;

    public InsuranceRevisionMetronome() {
        current_millis = 0;
    }


    public void one_day_passed(){

        System.out.println("INSURANCE METRONOME ");
        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/current_millis");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() == null) {
                        ref.setValue(String.valueOf(System.currentTimeMillis()));
                        return;
                    } else {
                        current_millis = Long.parseLong(dataSnapshot.getValue().toString());
                        if (System.currentTimeMillis() - current_millis > MILL_IN_A_DAY) {
                            long days = (System.currentTimeMillis() - current_millis)/MILL_IN_A_DAY;
                            for (long i=0; i<days; i++){
                                PointManager.updatePoints(3, 0, 0);
                            }
                            ref.setValue(System.currentTimeMillis());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }



    public void four_year_passed(){

        System.out.println("INSURANCE METRONOME ");
        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/current_millis");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    ref.setValue(String.valueOf(System.currentTimeMillis()));
                    return;
                } else {
                    current_millis = Long.parseLong(dataSnapshot.getValue().toString());
                    if (System.currentTimeMillis() - current_millis > MILL_IN_A_DAY) {
                        long days = (System.currentTimeMillis() - current_millis)/MILL_IN_A_DAY;
                        for (long i=0; i<days; i++){
                            PointManager.updatePoints(3, 0, 0);
                        }
                        ref.setValue(System.currentTimeMillis());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}



