package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.net.Uri;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LEVEL_ALL;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LEVEL_ALL_AVAILABLE;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LEVEL_ALL_UNAVAILABLE;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LEVEL_DISTRICT;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LEVEL_NATION;
import static com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment.LEVEL_REGION;

/**
 * Created by alfredo on 01/09/17.
 */

public class DatabaseManager
        implements Constants {

    private final static String TAG = DatabaseManager.class.getSimpleName();

    private final static DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReference();



    public interface RetrieveRankFromDB {
        void onErrorReceived(int errorType);
        void onUsersRankingReceived(ArrayList<User> users);
        void onUsersCoordinatesReceived(double latitude, double longitude);
    }

    public interface RetrieveVehiclesFromDB {
        void onUserVehiclesReceived(ArrayList<Vehicle> vehicles);
    }

    public static DatabaseReference getDatabaseReference() {
        return DatabaseManager.databaseReference;
    }

    public static void setDataOnLocationChange(double latitude, double longitude) {
        if (SingletonUser.getInstance() != null) {
            DatabaseReference databaseReference = DatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(SingletonUser.getInstance().getUid())
                    .child(CHILD_CURRENT_POSITION);
            databaseReference.setValue(
                    StringParser.getStringFromCoordinates(latitude, longitude)
            );
        }
    }

    public static void updateCurrentPosition(double latitude, double longitude, String[] location) {
        String country = location[0]; String region = location[1]; String subRegion = location[2];
        if (SingletonUser.getInstance() != null) {
            DatabaseReference databaseReference = DatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(country)
                    .child(region)
                    .child(subRegion)
                    .child(SingletonUser.getInstance().getUid())
                    .child(CHILD_CURRENT_POSITION);
            databaseReference.setValue(
                    StringParser.getStringFromCoordinates(latitude, longitude)
            );
        }
    }

    public static void updateCurrentPoint(long points, String[] location) {
        String country = location[0]; String region = location[1]; String subRegion = location[2];
        if (SingletonUser.getInstance() != null) {
            DatabaseReference databaseReference = DatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(country)
                    .child(region)
                    .child(subRegion)
                    .child(SingletonUser.getInstance().getUid())
                    .child(CHILD_POINTS);
            databaseReference.setValue(points);
        }
    }

    public static void manageDataUserDB() {
        final Query query = DatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(SingletonUser
                        .getInstance()
                        .getUid());

        // Attach a listener to read the data at our posts reference
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO controlla bene
                Iterable<DataSnapshot> userData = dataSnapshot.getChildren();
                // dataSnapshot.exist() --> .child(.getUid) esiste?
                // user == null --> user non ha almeno un figlio?
                if (!dataSnapshot.exists() || userData == null || dataSnapshot.getChildrenCount() < 4) {
                    Log.d(TAG, "Create user data");
                    if (!dataSnapshot.hasChild(CHILD_POINTS)) {
                        query.getRef()
                                .child(CHILD_POINTS)
                                .setValue(SingletonUser.getInstance().getPoints());
                    }

                    if (SingletonUser.getInstance().getPhotoUrl() != null &&
                            !dataSnapshot.hasChild(CHILD_IMAGE)) {
                        query.getRef()
                                .child(CHILD_IMAGE)
                                .setValue(SingletonUser.getInstance().getPhotoUrl().toString());
                    }

                    if (SingletonUser.getInstance().getUsername() != null &&
                            !SingletonUser.getInstance().getUsername().isEmpty() &&
                            !dataSnapshot.hasChild(CHILD_USERNAME)) {
                        query.getRef()
                                .child(CHILD_USERNAME)
                                .setValue(SingletonUser.getInstance().getUsername());
                    } else if (SingletonUser.getInstance().getEmail() != null &&
                            !SingletonUser.getInstance().getEmail().isEmpty() &&
                            !dataSnapshot.hasChild(CHILD_USERNAME)) {
                        query.getRef()
                                .child(CHILD_USERNAME)
                                .setValue(SingletonUser.getInstance().getEmail());
                    }
                } else {
                    SingletonUser
                        .getInstance()
                        .setPoints(
                                (long) dataSnapshot.child(CHILD_POINTS).getValue()
                        );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

    public static void getVehiclesDB(final RetrieveVehiclesFromDB retrieveVehiclesFromDB,
                                     final SingletonUser.UserDataCallback userDataCallback)
            throws CallbackNotInitialized {
        if (retrieveVehiclesFromDB == null || userDataCallback == null) {
            Log.w(TAG, "Callback not initialized");
            throw new CallbackNotInitialized("Callback not initialized");
        }

        // Create query
        final Query query = DatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(SingletonUser.getInstance().getUid())
                .child(NODE_VEHICLES);

        // Attach a listener to read the data at our posts reference
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> vehiclesList = dataSnapshot.getChildren();
                ArrayList<Vehicle> vehicleArrayList;
                if (!dataSnapshot.exists() || vehiclesList == null) {
                    vehicleArrayList = null;
                } else {
                    vehicleArrayList = new ArrayList<Vehicle>();
                    for (DataSnapshot vehicle : vehiclesList) {
                        if (vehicle.getValue() != null) {
                            String[] parts = vehicle.getValue().toString().split(";");
                            vehicleArrayList.add(
                                    new Vehicle(
                                            parts[0],
                                            parts[1],
                                            parts[2],
                                            parts[3]
                                    )
                            );
                        } else {
                            Log.d(TAG, "Error while retrieve data from database");
                            vehicleArrayList = null;
                            break;
                        }
                    }
                }
                retrieveVehiclesFromDB.onUserVehiclesReceived(vehicleArrayList);
                userDataCallback.onVehiclesReceive();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static void getCoordinates(final RetrieveRankFromDB retrieveRankFromDB)
    throws CallbackNotInitialized {
        if (retrieveRankFromDB == null) {
            Log.w(TAG, "Callback not initialized");
            throw new CallbackNotInitialized("Callback not initialized");
        }

        Query query = DatabaseManager.getDatabaseReference()
                .child(NODE_USERS)
                .child(SingletonUser.getInstance().getUid())
                .child(CHILD_CURRENT_POSITION);
        query
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String coordinates = dataSnapshot.getValue().toString();
                            if (coordinates != null) {
                                String[] strings = StringParser.getCoordinates(coordinates);
                                retrieveRankFromDB.onUsersCoordinatesReceived(
                                        Double.parseDouble(strings[0]),
                                        Double.parseDouble(strings[1])
                                );
                            } else {
                                // TODO valore della chiave non trovato -> errore interno
                                retrieveRankFromDB.onErrorReceived(1);
                            }
                        } else {
                            retrieveRankFromDB.onErrorReceived(2);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
    }

    public static void getUsersRank(final RetrieveRankFromDB retrieveRankFromDB, String[] location)
            throws CallbackNotInitialized {
        if (retrieveRankFromDB == null) {
            Log.w(TAG, "Callback not initialized");
            throw new CallbackNotInitialized("Callback not initialized");
        }

        // location[0] --> nation; location[1] --> region; location[2] --> district
        String nation = location[0]; String region = location[1]; String district = location[2];
        // DEBUG
        nation = "Italy"; region = "Lazio"; district = "Provincia di Frosinone";
        ////

        Query query = DatabaseManager.getDatabaseReference()
                .child(NODE_POSITION)
                .child(nation);
        switch (RankingFragment.getLevel()) {
            case LEVEL_DISTRICT:
                Log.d(TAG, "DISTRICT");
                query = query.getRef()
                        .child(region)
                        .child(district);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                            if  (users != null) {
                                ArrayList<User> arrayList = new ArrayList<>();
                                for (DataSnapshot user : users) {
                                    if (user.child(CHILD_CURRENT_POSITION) != null &&
                                            user.child(CHILD_CURRENT_POSITION).getValue() != null) {
                                        arrayList.add(new User(
                                                user.child(CHILD_EMAIL).getValue().toString(),
                                                Uri.parse("dio"),
                                                243
                                        ));
                                    }
                                }
                                retrieveRankFromDB.onUsersRankingReceived(arrayList);
                            }
                        } else {
                            // TODO non c'è alcun utente (neanche l'utente corrente)
                            retrieveRankFromDB.onErrorReceived(5);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LEVEL_REGION:
                Log.d(TAG, "REGION");
                query = query.getRef()
                        .child(region);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LEVEL_NATION:
                Log.d(TAG, "NATION");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LEVEL_ALL_AVAILABLE:
                break;

            case LEVEL_ALL_UNAVAILABLE:
                break;

            case LEVEL_ALL:
                break;

            default:
                Log.w(TAG, "Error in level selection: " + RankingFragment.getLevel());
        }
    }

    public static void disconnectReference() {
        DatabaseManager.databaseReference.onDisconnect();
    }
}
