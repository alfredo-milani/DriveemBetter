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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by alfredo on 01/09/17.
 */

public class FirebaseDatabaseManager
        implements Constants {

    // Medium cohesion and *** high coupling ***
    // FirebaseDatabaseManager -> all others classes

    private final static String TAG = FirebaseDatabaseManager.class.getSimpleName();

    private final static DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReference();
    private final static SingletonUser user = SingletonUser.getInstance();



    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabaseManager.databaseReference;
    }

    public static void disconnectReference() {
        FirebaseDatabaseManager.databaseReference.onDisconnect();
    }

    public static void refreshUserToken(String token) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid());

            // Set user's token
            databaseReference
                    .child(ARG_FIREBASE_TOKEN)
                    .setValue(token);
        }
    }

    /**
     * Update DB with latitude and longitude of the user.
     * Here we are in users node.
     */
    public static void updateUserCoordAndAvail() {
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid());

            databaseReference
                    .child(CHILD_CURRENT_POSITION)
                    .setValue(user.getLatitude(), user.getLongitude());
            databaseReference
                    .child(CHILD_AVAILABILITY)
                    .setValue(user.getAvailability());
        }
    }

    public static void manageUserAvailability(String availability) {
        if (user != null &&
                availability != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid())
                    .child(CHILD_AVAILABILITY);
            databaseReference.setValue(availability);
        }
    }

    public static void manageUserUsername(String username) {
        if (user != null &&
                username != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid())
                    .child(CHILD_USERNAME);
            databaseReference.setValue(username);
        }
    }

    public static void manageUserPoints(long points) {
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid())
                    .child(CHILD_POINTS);
            databaseReference.setValue(points);
        }
    }

    public static void manageUserEmail(String email) {
        if (user != null &&
                email != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid())
                    .child(CHILD_EMAIL);
            databaseReference.setValue(email);
        }
    }

    public static void manageUserPhoto(Uri photo) {
        if (user != null &&
                photo != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid())
                    .child(CHILD_IMAGE);
            databaseReference.setValue(photo);
        }
    }

    public static void removeOldPosition(String[] position) {
        if (position != null && position[0] != null &&
                position[1] != null && position[2] != null &&
                user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(position[0])
                    .child(position[1])
                    .child(position[2])
                    .child(user.getUid());
            databaseReference.removeValue();
        }
    }

    public static void createNewUserPosition() {
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(user.getCountry())
                    .child(user.getRegion())
                    .child(user.getSubRegion())
                    .child(user.getUid());

            databaseReference
                    .child(CHILD_USERNAME)
                    .setValue(user.getUsername());
            databaseReference
                    .child(CHILD_EMAIL)
                    .setValue(user.getEmail());
            databaseReference
                    .child(CHILD_IMAGE)
                    .setValue(user.getPhotoUrl());
            databaseReference
                    .child(CHILD_POINTS)
                    .setValue(user.getPoints());
        }
    }

    /**
     * Update DB with latitude and longitude of the user.
     * Here we are in positions node.
     */
    public static void upPositionCoordAndAvail() {
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(user.getCountry())
                    .child(user.getRegion())
                    .child(user.getSubRegion())
                    .child(user.getUid());

            databaseReference
                    .child(CHILD_CURRENT_POSITION)
                    .setValue(StringParser.getStringFromCoordinates(
                            user.getLatitude(), user.getLongitude()
                    ));
            databaseReference
                    .child(CHILD_AVAILABILITY)
                    .setValue(user.getAvailability());
        }
    }

    public static void updateCurrentPoint(long points, String[] location) {
        String country = location[0]; String region = location[1]; String subRegion = location[2];
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(country)
                    .child(region)
                    .child(subRegion)
                    .child(user.getUid())
                    .child(CHILD_POINTS);
            databaseReference.setValue(points);
        }
    }

    public static void checkUnknownPosition() {
        final Query query = FirebaseDatabaseManager.databaseReference
                .child(NODE_POSITION)
                .child(COUNTRY)
                .child(REGION)
                .child(SUB_REGION)
                .equalTo(user.getUid());

        // Attach a listener to read the data at our posts reference
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Writing on DB about user unknown position");
                if (!dataSnapshot.exists()) {
                    if (user != null) {
                        query.getRef()
                                .child(user.getUid())
                                .child(CHILD_AVAILABILITY)
                                .setValue(UNAVAILABLE);
                        if (user.getUsername() != null) {
                            query.getRef()
                                    .child(user.getUid())
                                    .child(CHILD_USERNAME)
                                    .setValue(user.getUsername());
                        }
                        query.getRef()
                                .child(user.getUid())
                                .child(CHILD_POINTS)
                                .setValue(user.getPoints());
                        if (user.getEmail() != null) {
                            query.getRef()
                                    .child(user.getUid())
                                    .child(CHILD_EMAIL)
                                    .setValue(user.getEmail());
                        }
                        if (user.getPhotoUrl() != null) {
                            query.getRef()
                                    .child(user.getUid())
                                    .child(CHILD_IMAGE)
                                    .setValue(user.getPhotoUrl().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

    public static void manageCurrentUserDataDB() {
        if (user.getUid() == null) {
            return;
        }
        final Query query = FirebaseDatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(user.getUid());

        // Attach a listener to read the data at our posts reference
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // dataSnapshot.exist() --> .child(.getUid) esiste?
                // user == null --> user non ha almeno un figlio?
                if (!dataSnapshot.exists() || dataSnapshot.getChildren() == null ||
                        dataSnapshot.getChildrenCount() < 6) {
                    Log.d(TAG, "User data integrity check");
                    if (!dataSnapshot.hasChild(CHILD_POINTS)) {
                        query.getRef()
                                .child(CHILD_POINTS)
                                .setValue(user.getPoints());
                    }

                    if (user.getPhotoUrl() != null &&
                            !dataSnapshot.hasChild(CHILD_IMAGE)) {
                        query.getRef()
                                .child(CHILD_IMAGE)
                                .setValue(user.getPhotoUrl().toString());
                    }

                    if (user.getUsername() != null &&
                            !user.getUsername().isEmpty() &&
                            !dataSnapshot.hasChild(CHILD_USERNAME)) {
                        query.getRef()
                                .child(CHILD_USERNAME)
                                .setValue(user.getUsername());
                    }

                    if (user.getEmail() != null &&
                            !user.getEmail().isEmpty() &&
                            !dataSnapshot.hasChild(CHILD_EMAIL)) {
                        query.getRef()
                                .child(CHILD_EMAIL)
                                .setValue(user.getEmail());
                    }

                    if (dataSnapshot.hasChild(CHILD_CURRENT_POSITION) &&
                            dataSnapshot.child(CHILD_CURRENT_POSITION).getValue() != null) {
                        String[] coordinates = StringParser.getCoordinates(
                                dataSnapshot.child(CHILD_CURRENT_POSITION).getValue().toString()
                        );
                        user.setLatitude(Double.parseDouble(coordinates[0]));
                        user.setLongitude(Double.parseDouble(coordinates[1]));
                    }
                    if (dataSnapshot.hasChild(CHILD_AVAILABILITY)) {
                        user.setAvailability(UNAVAILABLE);
                        dataSnapshot.getRef().child(CHILD_AVAILABILITY).setValue(UNAVAILABLE);
                    }
                } else {
                    // TODO prima di leggere i punti devo aspettare il risultato di quest query --> SERVONO LOCKS
                    Log.d(TAG, "Update SingletonUser class data: " + dataSnapshot);
                    user.setPoints(
                            (long) dataSnapshot.child(CHILD_POINTS).getValue()
                    );

                    if (dataSnapshot.child(CHILD_CURRENT_POSITION).getValue() != null) {
                        String[] coordinates = StringParser.getCoordinates(
                                dataSnapshot.child(CHILD_CURRENT_POSITION).getValue().toString()
                        );
                        user.setLatitude(Double.parseDouble(coordinates[0]));
                        user.setLongitude(Double.parseDouble(coordinates[1]));
                    }
                    if (dataSnapshot.hasChild(CHILD_AVAILABILITY)) {
                        user.setAvailability(UNAVAILABLE);
                        dataSnapshot.getRef().child(CHILD_AVAILABILITY).setValue(UNAVAILABLE);
                    }
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
            throw new CallbackNotInitialized(TAG);
        }

        // Create query
        final Query query = FirebaseDatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(user.getUid())
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
        int NOT_ALLOWED = -1;
        int UNKNOWN_ERROR = 0;
        int POSITION_NOT_FOUND = 1;
        int INVALID_POSITION = 2;
        int OK = Integer.MAX_VALUE;

        void onErrorReceived(int errorType);
        void onUsersRankingReceived(ArrayList<User> users);
        void onUsersCoordinatesReceived(double[] position);
    }

    public static void getCoordinates(final RetrieveRankFromDB retrieveRankFromDB)
    throws CallbackNotInitialized {
        if (retrieveRankFromDB == null) {
            throw new CallbackNotInitialized(TAG);
        }

        Query query = FirebaseDatabaseManager.getDatabaseReference()
                .child(NODE_USERS)
                .child(user.getUid())
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
        String nation = null;
        String region = null;
        String district = null;
        // DEBUG
        // nation = "Italy";
        // region = "Lazio";
        // district = "Provincia di Frosinone";
        ////

        if (retrieveRankFromDB == null) {
            throw new CallbackNotInitialized(TAG);
        } else if (location == null) {
                if (RankingFragment.getLevel() != LevelMenuFragment.LevelStateChanged.LEVEL_UNAVAILABLE &&
                        RankingFragment.getLevel() != LevelMenuFragment.LevelStateChanged.LEVEL_AVAILABLE &&
                        RankingFragment.getLevel() != LevelMenuFragment.LevelStateChanged.LEVEL_ALL) {
                    retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
                    return;
                }
        } else {
            // location[0] --> nation; location[1] --> region; location[2] --> district
            nation = location[0];
            region = location[1];
            district = location[2];
        }

        Query query = FirebaseDatabaseManager.getDatabaseReference()
                .child(NODE_POSITION);
        switch (RankingFragment.getLevel()) {
            case LevelMenuFragment.LevelStateChanged.LEVEL_DISTRICT:
                Log.d(TAG, "DISTRICT");
                if (nation == null || region == null || district == null) {
                    retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
                    return;
                }
                query = query.getRef()
                        .child(nation)
                        .child(region)
                        .child(district);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        retrieveRankFromDB.onUsersRankingReceived(
                                FirebaseDatabaseManager.getUsersList(dataSnapshot)
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_REGION:
                Log.d(TAG, "REGION");
                if (nation == null || region == null) {
                    retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
                    return;
                }
                query = query.getRef()
                        .child(nation)
                        .child(region);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<User> arrayList = null;
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> districts = dataSnapshot.getChildren();
                            arrayList = new ArrayList<User>();
                            for (DataSnapshot district : districts) {
                                arrayList.addAll(FirebaseDatabaseManager.getUsersList(district));
                            }
                        }
                        retrieveRankFromDB.onUsersRankingReceived(arrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_NATION:
                Log.d(TAG, "NATION");
                if (nation == null) {
                    retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
                    return;
                }
                query = query.getRef()
                        .child(nation);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<User> arrayList = null;
                        if (dataSnapshot.exists()) {
                            arrayList = new ArrayList<User>();
                            Iterable<DataSnapshot> regions = dataSnapshot.getChildren();
                            for (DataSnapshot region : regions) {
                                Iterable<DataSnapshot> districts = region.getChildren();
                                for (DataSnapshot district : districts) {
                                    arrayList.addAll(FirebaseDatabaseManager.getUsersList(district));
                                }
                            }
                        }
                        retrieveRankFromDB.onUsersRankingReceived(arrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_AVAILABLE:
                Log.d(TAG, "AVAILABLE");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<User> arrayList = null;
                        if (dataSnapshot.exists()) {
                            arrayList = new ArrayList<User>();
                            Iterable<DataSnapshot> nations = dataSnapshot.getChildren();
                            for (DataSnapshot nation : nations) {
                                if (!nation.getKey().equals(COUNTRY)) {
                                    Iterable<DataSnapshot> regions = nation.getChildren();
                                    for (DataSnapshot region : regions) {
                                        Iterable<DataSnapshot> districts = region.getChildren();
                                        for (DataSnapshot district : districts) {
                                            arrayList.addAll(FirebaseDatabaseManager.getUsersList(district));
                                        }
                                    }
                                }
                            }
                        }
                        retrieveRankFromDB.onUsersRankingReceived(arrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_UNAVAILABLE:
                Log.d(TAG, "UNAVAIABLE");
                query = query.getRef()
                        .child(COUNTRY)
                        .child(REGION)
                        .child(SUB_REGION);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        retrieveRankFromDB.onUsersRankingReceived(
                                FirebaseDatabaseManager.getUsersList(dataSnapshot)
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_ALL:
                Log.d(TAG, "ALL");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<User> arrayList = null;
                        if (dataSnapshot.exists()) {
                            arrayList = new ArrayList<User>();
                            Iterable<DataSnapshot> nations = dataSnapshot.getChildren();
                            for (DataSnapshot nation : nations) {
                                Iterable<DataSnapshot> regions = nation.getChildren();
                                for (DataSnapshot region : regions) {
                                    Iterable<DataSnapshot> districts = region.getChildren();
                                    for (DataSnapshot district : districts) {
                                        arrayList.addAll(FirebaseDatabaseManager.getUsersList(district));
                                    }
                                }
                            }
                        }
                        retrieveRankFromDB.onUsersRankingReceived(arrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
                break;

            default:
                Log.w(TAG, "Error in level selection: " + RankingFragment.getLevel());
        }
    }

    private static ArrayList<User> getUsersList(DataSnapshot dataSnapshot) {
        ArrayList<User> arrayList;
        Iterable<DataSnapshot> users;
        if (dataSnapshot.exists() &&
                (users = dataSnapshot.getChildren()) != null) {
            arrayList = new ArrayList<>();
            for (DataSnapshot user : users) {
                arrayList.add(FirebaseDatabaseManager.getUserFromData(user));
            }
        } else {
            arrayList = null;
        }

        return arrayList;
    }

    private static User getUserFromData(DataSnapshot user) {
        String username = null;
        String email = null;
        Uri image = null;
        long points = 0;
        String availability = UNAVAILABLE;

        if (user.hasChild(CHILD_USERNAME) &&
                user.child(CHILD_USERNAME).getValue() != null) {
            username = user.child(CHILD_USERNAME).getValue().toString();
        }
        if (user.hasChild(CHILD_EMAIL) &&
                user.child(CHILD_EMAIL).getValue() != null) {
            email = user.child(CHILD_EMAIL).getValue().toString();
        }
        if (user.hasChild(CHILD_IMAGE) &&
                user.child(CHILD_IMAGE).getValue() != null) {
            image = Uri.parse(user.child(CHILD_IMAGE).getValue().toString());
        }
        if (user.hasChild(CHILD_POINTS)) {
            points = (long) user.child(CHILD_POINTS).getValue();
        }
        if (user.hasChild(CHILD_AVAILABILITY) &&
                user.child(CHILD_AVAILABILITY).getValue().toString().equals(AVAILABLE)) {
            availability = AVAILABLE;
        }

        return new User(user.getKey(), username, email, image, points, availability);
    }
}
