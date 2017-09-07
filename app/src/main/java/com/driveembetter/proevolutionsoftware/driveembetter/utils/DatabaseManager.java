package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.net.Uri;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment;
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
import java.util.Random;

/**
 * Created by alfredo on 01/09/17.
 */

public class DatabaseManager
        implements Constants {

    // Medium cohesion and *** high coupling ***
    // DatabaseManager -> all others classes

    private final static String TAG = DatabaseManager.class.getSimpleName();

    private final static DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReference();



    public static DatabaseReference getDatabaseReference() {
        return DatabaseManager.databaseReference;
    }

    public static void disconnectReference() {
        DatabaseManager.databaseReference.onDisconnect();
    }

    /**
     * Update DB with latitude and longitude of the user.
     * Here we are in users node.
     * @param position:
     *                position[0] -> latitude
     *                position[1] -> longitude.
     */
    public static void setDataOnLocationChange(double[] position) {
        if (SingletonUser.getInstance() != null) {
            DatabaseReference databaseReference = DatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(SingletonUser.getInstance().getUid())
                    .child(CHILD_CURRENT_POSITION);
            databaseReference.setValue(
                    StringParser.getStringFromCoordinates(position[0], position[1])
            );
        }
    }

    /**
     * Update DB with latitude and longitude of the user.
     * Here we are in positions node.
     * @param position:
     *                position[0] -> latitude
     *                position[1] -> longitude.
     */
    public static void updateCurrentPosition(double[] position, String[] location) {
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
                    StringParser.getStringFromCoordinates(position[0], position[1])
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

    public static void manageUserAvailability(String availability) {
        if (SingletonUser.getInstance() != null) {
            DatabaseReference databaseReference = DatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(SingletonUser.getInstance().getUid())
                    .child(CHILD_AVAILABILITY);
            databaseReference.setValue(availability);
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
                Iterable<DataSnapshot> userData = dataSnapshot.getChildren();
                // dataSnapshot.exist() --> .child(.getUid) esiste?
                // user == null --> user non ha almeno un figlio?
                if (!dataSnapshot.exists() || userData == null || dataSnapshot.getChildrenCount() < 5) {
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

                    if (!dataSnapshot.hasChild(CHILD_CURRENT_POSITION)) {
                        query.getRef()
                                .child(CHILD_AVAILABILITY)
                                .setValue(UNAVAILABLE);
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



    public interface RetrieveVehiclesFromDB {
        void onUserVehiclesReceived(ArrayList<Vehicle> vehicles);
    }

    public static void getVehiclesDB(final RetrieveVehiclesFromDB retrieveVehiclesFromDB,
                                     final SingletonUser.UserDataCallback userDataCallback)
            throws CallbackNotInitialized {
        if (retrieveVehiclesFromDB == null || userDataCallback == null) {
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



    public interface RetrieveRankFromDB {
        // Result code
        int POSITION_NOT_FOUND = 1;
        int NOT_INITIALIZED = 2;

        void onErrorReceived(int errorType);
        void onUsersRankingReceived(ArrayList<User> users);
        void onUsersCoordinatesReceived(double[] position);
    }

    public static void getCoordinates(final RetrieveRankFromDB retrieveRankFromDB)
    throws CallbackNotInitialized {
        if (retrieveRankFromDB == null) {
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
                            if (!coordinates.isEmpty()) {
                                String[] strings = StringParser.getCoordinates(coordinates);
                                retrieveRankFromDB.onUsersCoordinatesReceived(new double[] {
                                                Double.parseDouble(strings[0]),
                                                Double.parseDouble(strings[1])
                                });
                            } else {
                                // key's value not found
                                retrieveRankFromDB.onUsersCoordinatesReceived(null);
                            }
                        } else {
                            // Key not found
                            retrieveRankFromDB.onUsersCoordinatesReceived(null);
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
            throw new CallbackNotInitialized("Callback not initialized");
        }

        // location[0] --> nation; location[1] --> region; location[2] --> district
        String nation, region, district;
        nation = region = district = null;
        if (location != null) {
            nation = location[0];
            region = location[1];
            district = location[2];
            // DEBUG
            nation = "Italy";
            region = "Lazio";
            district = "Provincia di Frosinone";
            ////
        }

        Query query = DatabaseManager.getDatabaseReference()
                .child(NODE_POSITION);
        switch (RankingFragment.getLevel()) {
            case LevelMenuFragment.LevelStateChanged.LEVEL_DISTRICT:
                if (nation == null || region == null || district == null) {
                    Log.w(TAG, "district, region, nation NULL");
                    break;
                }
                Log.d(TAG, "DISTRICT");
                query = query.getRef()
                        .child(nation)
                        .child(region)
                        .child(district)
                        .orderByChild(CHILD_POINTS);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<User> arrayList;
                        Iterable<DataSnapshot> users;
                        if (dataSnapshot.exists() &&
                                (users = dataSnapshot.getChildren()) != null) {
                            arrayList = new ArrayList<>();
                            for (DataSnapshot user : users) {
                                arrayList.add(DatabaseManager.getUserFromData(user));
                            }
                        } else {
                            // TODO non c'è alcun utente neanche quello corrente
                            arrayList = null;
                        }
                        retrieveRankFromDB.onUsersRankingReceived(arrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_REGION:
                if (nation == null || region == null) {
                    Log.w(TAG, "district, region, nation NULL");
                    break;
                }
                Log.d(TAG, "REGION");
                query = query.getRef()
                        .child(nation)
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

            case LevelMenuFragment.LevelStateChanged.LEVEL_NATION:
                if (nation == null) {
                    Log.w(TAG, "district, region, nation NULL");
                    break;
                }
                Log.d(TAG, "NATION");
                query.getRef()
                        .child(nation);
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

            case LevelMenuFragment.LevelStateChanged.LEVEL_AVAILABLE:
                if (nation == null || region == null || district == null) {
                    Log.w(TAG, "district, region, nation NULL");
                    break;
                }
                Log.d(TAG, "AVAIABLE");
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_UNAVAILABLE:
                Log.d(TAG, "UNAVAIABLE");
                query.getRef()
                        .child(COUNTRY)
                        .child(REGION)
                        .child(SUB_REGION);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<User> arrayList;
                        Iterable<DataSnapshot> users;
                        if (dataSnapshot.exists() &&
                                (users = dataSnapshot.getChildren()) != null) {
                            arrayList = new ArrayList<>();
                            for (DataSnapshot user : users) {
                                arrayList.add(DatabaseManager.getUserFromData(user));
                            }
                        } else {
                            arrayList = null;
                        }
                        retrieveRankFromDB.onUsersRankingReceived(arrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_ALL:
                if (nation == null || region == null || district == null) {
                    Log.w(TAG, "district, region, nation NULL");
                    break;
                }
                Log.d(TAG, "ALL");
                break;

            default:
                Log.w(TAG, "Error in level selection: " + RankingFragment.getLevel());
        }
    }

    private static User getUserFromData(DataSnapshot user) {
        String username = "User" + new Random().nextInt(Integer.MAX_VALUE);
        long points = 0;
        Uri image = null;

        if (user.hasChild(CHILD_USERNAME) &&
                user.child(CHILD_USERNAME).getValue() != null) {
            username = user.child(CHILD_USERNAME).getValue().toString();
        }
        if (user.hasChild(CHILD_POINTS)) {
            points = (long) user.child(CHILD_POINTS).getValue();
        }
        if (user.hasChild(CHILD_IMAGE) &&
                user.child(CHILD_IMAGE).getValue() != null) {
            image = Uri.parse(user.child(CHILD_IMAGE).getValue().toString());
        }

        return new User(username, image, points);
    }
}
