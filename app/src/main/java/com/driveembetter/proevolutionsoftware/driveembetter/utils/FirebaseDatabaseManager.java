package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.net.Uri;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.LevelMenuFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RankingFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Mean;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanWeek;
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
import java.util.HashMap;
import java.util.Map;


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



    /**
     *  MISCELLANEOUS METHODS
     */
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabaseManager.databaseReference;
    }

    public static void refreshUserToken(String token) {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            DatabaseReference databaseReferencePosition = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(COUNTRY)
                    .child(REGION)
                    .child(SUB_REGION)
                    .child(user.getUid());

            // Set user's token
            Map<String, Object> map = new HashMap<>();
            map.put(ARG_FIREBASE_TOKEN, token);

            databaseReferencePosition.updateChildren(map);
        }
    }

    public static void restoreStatisticsState(DataSnapshot dataSnapshot, MeanDay meanDay, MeanWeek meanWeek) {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null && dataSnapshot != null) {
            if (meanDay != null && dataSnapshot.child(CHILD_STAT_DAY) != null &&
                    dataSnapshot.child(CHILD_STAT_DAY).getValue() != null) {
                DataSnapshot daily = dataSnapshot.child(CHILD_STAT_DAY);

                meanDay.setTimestamp((long) daily.child(CHILD_DATE).getValue());
                for (DataSnapshot value : daily.getChildren()) {
                    if (!value.getKey().equals(CHILD_DATE)) {
                        int velSize = 0, accSize = 0; float velSum = 0, accSum = 0;
                        for (DataSnapshot attribute : value.getChildren()) {
                            switch (attribute.getKey()) {
                                case Mean.SIZE_ACCELERATION:
                                    accSize = Integer.valueOf(attribute.getValue().toString());
                                    break;

                                case Mean.SIZE_VELOCITY:
                                    velSize = Integer.valueOf(attribute.getValue().toString());
                                    break;

                                case Mean.SUM_ACCELERATION:
                                    accSum = Float.valueOf(attribute.getValue().toString());
                                    break;

                                case Mean.SUM_VELOCITY:
                                    velSum = Float.valueOf(attribute.getValue().toString());
                                    break;
                            }
                        }
                        meanDay.getMap().put(Integer.valueOf(value.getKey()), new Mean(accSum, velSum, velSize, accSize));
                    }
                }
            }

            if (meanWeek != null && dataSnapshot.child(CHILD_STAT_WEEK) != null &&
                    dataSnapshot.child(CHILD_STAT_WEEK).getValue() != null) {
                DataSnapshot weekly = dataSnapshot.child(CHILD_STAT_WEEK);

                meanWeek.setTimestamp((long) weekly.child(CHILD_DATE).getValue());
                for (DataSnapshot value : weekly.getChildren()) {
                    if (!value.getKey().equals(CHILD_DATE)) {
                        int velSize = 0, accSize = 0; float velSum = 0, accSum = 0;
                        for (DataSnapshot attribute : value.getChildren()) {
                            switch (attribute.getKey()) {
                                case Mean.SIZE_ACCELERATION:
                                    accSize = Integer.valueOf(attribute.getValue().toString());
                                    break;

                                case Mean.SIZE_VELOCITY:
                                    velSize = Integer.valueOf(attribute.getValue().toString());
                                    break;

                                case Mean.SUM_ACCELERATION:
                                    velSum = Float.valueOf(attribute.getValue().toString());
                                    break;

                                case Mean.SUM_VELOCITY:
                                    accSum = Float.valueOf(attribute.getValue().toString());
                                    break;
                            }
                        }
                        meanWeek.getMap().put(Integer.valueOf(value.getKey()), new Mean(accSum, velSum, velSize, accSize));
                    }
                }
            }
        }
    }

    public static void manageUserStatistics() {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            Log.d(TAG, "Saving statistics");
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid())
                    .child(CHILD_STATISTICS);

            HashMap<String, Object> tmpParsedMean;
            Map<Integer, Mean> tmpOriginalMean;

            // Saving MeanDay
            tmpOriginalMean = user.getMeanDay().getMap();
            tmpParsedMean = new HashMap<>(user.getMeanDay().getMap().size() + 1);
            tmpParsedMean.put(CHILD_DATE, user.getMeanDay().getTimestamp());
            for (Map.Entry<Integer, Mean> entry : tmpOriginalMean.entrySet()) {
                tmpParsedMean.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            if (user.getMeanDay().isClearDay()) {
                user.getMeanDay().setClearDay(false);
                databaseReference
                        .child(CHILD_STAT_DAY)
                        .setValue(tmpParsedMean);
            } else {
                databaseReference
                        .child(CHILD_STAT_DAY)
                        .updateChildren(tmpParsedMean);
            }


            // Saving MeanWeek
            tmpOriginalMean = user.getMeanWeek().getMap();
            tmpParsedMean = new HashMap<>(user.getMeanWeek().getMap().size() + 1);
            tmpParsedMean.put(CHILD_DATE, user.getMeanWeek().getTimestamp());
            for (Map.Entry<Integer, Mean> entry : tmpOriginalMean.entrySet()) {
                tmpParsedMean.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            if (user.getMeanWeek().isClearWeek()) {
                user.getMeanWeek().setClearWeek(false);
                databaseReference
                        .child(CHILD_STAT_WEEK)
                        .setValue(tmpParsedMean);
            } else {
                databaseReference
                        .child(CHILD_STAT_WEEK)
                        .updateChildren(tmpParsedMean);
            }
        }
    }

    public static void manageUserAvailability(String availability) {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null && availability != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid());

            databaseReference
                    .child(CHILD_AVAILABILITY)
                    .setValue(availability);
        }
    }

    public static void managePositionAvailability(String availability) {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null && availability != null &&
                user.getSubRegion() != null && user.getRegion() != null &&
                user.getCountry() != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(user.getCountry())
                    .child(user.getRegion())
                    .child(user.getSubRegion())
                    .child(user.getUid())
                    .child(CHILD_AVAILABILITY);

            databaseReference.setValue(availability);
        }
    }

    public static void removeOldPosition(String[] position) {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(position[0])
                    .child(position[1])
                    .child(position[2])
                    .child(user.getUid());

            databaseReference
                    .child(CHILD_AVAILABILITY)
                    .onDisconnect()
                    .cancel();
            databaseReference.removeValue();
        }
    }

    public static void createNewUserPosition() {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null && user.getSubRegion() != null &&
                user.getRegion() != null && user.getCountry() != null) {
            Log.d(TAG, "user: " + user.getUid() + " / " + user.getPhotoUrl() + " / " + user.getPoints());
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(user.getCountry())
                    .child(user.getRegion())
                    .child(user.getSubRegion())
                    .child(user.getUid());

            Map<String, Object> newUser = new HashMap<>();
            newUser.put(CHILD_USERNAME, user.getUsername());
            newUser.put(CHILD_EMAIL, user.getEmail());
            newUser.put(CHILD_POINTS, String.valueOf(user.getPoints()));
            newUser.put(CHILD_AVAILABILITY, user.getAvailability());
            if (user.getPhotoUrl() != null) {
                newUser.put(CHILD_IMAGE, user.getPhotoUrl().toString());
            }
            if (user.getFeedback() != null) {
                newUser.put(CHILD_FEEDBACK, user.getFeedback().toString());
            }
            databaseReference
                    .child(CHILD_AVAILABILITY)
                    .onDisconnect()
                    .setValue(UNAVAILABLE);

            databaseReference.updateChildren(newUser);
        }
    }

    public static void checkOldPositionData() {
        final SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            final Query query = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(user.getCountry())
                    .child(user.getRegion())
                    .child(user.getSubRegion())
                    .child(user.getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        createNewUserPosition();
                    } else {
                        if (!dataSnapshot.hasChild(CHILD_USERNAME) &&
                                user.getUsername() != null) {
                            dataSnapshot.getRef()
                                    .child(CHILD_USERNAME)
                                    .setValue(user.getUsername());
                        }
                        if (!dataSnapshot.hasChild(CHILD_EMAIL) &&
                                user.getEmail() != null) {
                            dataSnapshot.getRef()
                                    .child(CHILD_EMAIL)
                                    .setValue(user.getEmail());
                        }
                        if (!dataSnapshot.hasChild(CHILD_IMAGE) &&
                                user.getPhotoUrl() != null) {
                            dataSnapshot.getRef()
                                    .child(CHILD_IMAGE)
                                    .setValue(user.getPhotoUrl().toString());
                        }
                        if (!dataSnapshot.hasChild(CHILD_FEEDBACK) &&
                                user.getFeedback() != null) {
                            dataSnapshot.getRef()
                                    .child(CHILD_FEEDBACK)
                                    .setValue(user.getFeedback().toString());
                        }
                        if (!dataSnapshot.hasChild(CHILD_CURRENT_VEHICLE) &&
                                user.getCurrentVehicle() != null) {
                            dataSnapshot.getRef()
                                    .child(CHILD_CURRENT_VEHICLE)
                                    .setValue(user.getCurrentVehicle().getType());
                        }
                        if (!dataSnapshot.hasChild(CHILD_POINTS)) {
                            dataSnapshot.getRef()
                                    .child(CHILD_POINTS)
                                    .setValue(user.getPoints());
                        }
                        if (!dataSnapshot.hasChild(CHILD_CURRENT_POSITION)) {
                            dataSnapshot.getRef()
                                    .child(CHILD_CURRENT_POSITION)
                                    .setValue(StringParser.getStringFromCoordinates(
                                            user.getLatitude(), user.getLongitude()
                                    ));
                        }
                        if (!dataSnapshot.hasChild(CHILD_AVAILABILITY) &&
                                !dataSnapshot.child(CHILD_AVAILABILITY).toString()
                                        .equals(user.getAvailability())) {
                            dataSnapshot.getRef()
                                    .child(CHILD_AVAILABILITY)
                                    .setValue(user.getAvailability());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "The read failed: " + databaseError.getCode());
                    user.getMtxUpdatePosition().unlock();
                }
            });
        }
    }

    /**
     * Update DB with latitude and longitude of the user.
     * Here we are in users node.
     */
    public static void updateUserZonaAndAvailability() {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid());

            Map<String, Object> newPosition = new HashMap<>();
            newPosition.put(
                    CHILD_ZONA,
                    StringParser.fromArrayToString(
                            new String[] {user.getCountry(), user.getRegion(), user.getSubRegion()}
                    )
            );
            newPosition.put(CHILD_AVAILABILITY, user.getAvailability());

            databaseReference.updateChildren(newPosition);
        }
    }

    /**
     * Update DB with latitude and longitude of the user.
     * Here we are in positions node.
     */
    public static void updatePositionCoordAndAvail() {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null && user.getSubRegion() != null &&
                user.getRegion() != null && user.getCountry() != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(user.getCountry())
                    .child(user.getRegion())
                    .child(user.getSubRegion())
                    .child(user.getUid());

            Map<String, Object> newPosition = new HashMap<>();
            newPosition.put(
                    CHILD_CURRENT_POSITION,
                    StringParser.getStringFromCoordinates(
                            user.getLatitude(), user.getLongitude()
                    )
            );
            newPosition.put(CHILD_AVAILABILITY, user.getAvailability());

            databaseReference.updateChildren(newPosition);
        }
    }

    public static void syncCurrentUser() {
        final SingletonUser user = SingletonUser.getInstance();
        if (user == null || user.getUid() == null) {
            return;
        }
        final Query query = FirebaseDatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(user.getUid());

        user.getMtxSyncData().lock();
        Log.e(TAG, "LOCK");
        user.getMtxUpdatePosition().lock();
        // TODO deadlock se non viene fatto unlock a causa di mancanza connessione?
        // Attach a listener to read the data at our posts reference
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // dataSnapshot.exist() --> .child(.getUid) esiste?
                // user == null --> user non ha almeno un figlio?
                if (!dataSnapshot.exists() || dataSnapshot.getChildren() == null ||
                        dataSnapshot.getChildrenCount() < 6) {
                    Log.d(TAG, "Create new user / User data integrity check");
                    if (!dataSnapshot.hasChild(CHILD_POINTS)) {
                        query.getRef()
                                .child(CHILD_POINTS)
                                .setValue(user.getPoints());
                    }
                    if (user.getFeedback() != null &&
                            !dataSnapshot.child(CHILD_STATISTICS).hasChild(CHILD_FEEDBACK)) {
                        query.getRef()
                                .child(CHILD_STATISTICS)
                                .child(CHILD_FEEDBACK)
                                .setValue(user.getFeedback().toString());
                    }

                    if (user.getCurrentVehicle() != null &&
                            !dataSnapshot.hasChild(CHILD_CURRENT_VEHICLE)) {
                        query.getRef()
                                .child(CHILD_CURRENT_VEHICLE)
                                .setValue(user.getCurrentVehicle().toString());
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

                    if (dataSnapshot.hasChild(CHILD_ZONA) &&
                            dataSnapshot.child(CHILD_ZONA).getValue() != null) {
                        updateCurrentUserZona(dataSnapshot.child(CHILD_ZONA).getValue().toString());
                    } else {
                        createNewUserPosition();
                    }
                } else {
                    Log.d(TAG, "Update SingletonUser class data");
                    user.setPoints(
                            (long) dataSnapshot.child(CHILD_POINTS).getValue()
                    );

                    if (dataSnapshot.hasChild(CHILD_ZONA) &&
                            dataSnapshot.child(CHILD_ZONA).getValue() != null) {
                        updateCurrentUserZona(dataSnapshot.child(CHILD_ZONA).getValue().toString());
                    }

                    checkOldPositionData();

                    if (dataSnapshot.child(CHILD_STATISTICS) != null) {
                        FirebaseDatabaseManager.restoreStatisticsState(
                                dataSnapshot.child(CHILD_STATISTICS),
                                user.getMeanDay(),
                                user.getMeanWeek()
                        );
                    }
                }

                dataSnapshot.getRef()
                        .child(CHILD_AVAILABILITY)
                        .onDisconnect()
                        .setValue(UNAVAILABLE);

                user.getMtxSyncData().unlock();
                Log.e(TAG, "UNLOCK");
                user.getMtxUpdatePosition().unlock();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
                user.getMtxSyncData().unlock();
                user.getMtxUpdatePosition().unlock();
            }
        });
    }

    private static void updateCurrentUserZona(String zona) {
        String[] coordinates = StringParser.getCoordinates(zona);
        SingletonUser user = SingletonUser.getInstance();

        if (user != null && coordinates != null &&
                coordinates.length == 3) {
            user.setCountry(coordinates[0]);
            user.setRegion(coordinates[1]);
            user.setSubRegion(coordinates[2]);

            Log.d(TAG, "DB POS: " + user.getCountry() + "/" + user.getRegion() + "/" + user.getSubRegion());
        }
    }



    /**
     *  USER DETAILS
     */
    public static void updateUserData(String child, String newData) {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_USERS)
                    .child(user.getUid());

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(child, newData);

            databaseReference.updateChildren(childUpdates);
        }
    }

    public static void updatePositionData(String child, String newData) {
        SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                    .child(NODE_POSITION)
                    .child(user.getCountry())
                    .child(user.getRegion())
                    .child(user.getSubRegion())
                    .child(user.getUid());

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(child, newData);

            databaseReference.updateChildren(childUpdates);
        }
    }



    /**
     *  STATISTICS DATA
     */
    public interface RetrieveDataDB {
        void onDailyDataReceived(MeanDay meanDay);
        void onWeeklyDataReceived(MeanWeek meanWeek);
    }

    public static void retrieveDailyData(final RetrieveDataDB callback, String userId) {
        if (callback == null) {
            throw new CallbackNotInitialized(TAG);
        } else if (userId == null) {
            return;
        }

        final Query query = FirebaseDatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(userId);

        // Attach a listener to read the data at our posts reference
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MeanDay meanDay = new MeanDay();
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(CHILD_STATISTICS) != null) {
                        FirebaseDatabaseManager.restoreStatisticsState(
                                dataSnapshot.child(CHILD_STATISTICS),
                                meanDay,
                                null
                        );
                    } else {
                        meanDay = null;
                    }
                } else {
                    meanDay = null;
                }

                callback.onDailyDataReceived(meanDay);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }


    /**
     *  GARAGE DATA
     */
    public interface RetrieveVehiclesFromDB {
        void onUserVehiclesReceived(ArrayList<Vehicle> vehicles);
    }

    public static void getCurrentVehicle(final RetrieveVehiclesFromDB retrieveVehiclesFromDB,
                                         final SingletonUser.UserDataCallback userDataCallback)
            throws CallbackNotInitialized {
        if (retrieveVehiclesFromDB == null || userDataCallback == null) {
            throw new CallbackNotInitialized(TAG);
        }

        final SingletonUser user = SingletonUser.getInstance();
        //Create query
        final Query query = FirebaseDatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(user.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(CHILD_CURRENT_VEHICLE)) {
                    String currentVehicle = dataSnapshot.child(CHILD_CURRENT_VEHICLE).getValue().toString();
                    String[] temp1 = currentVehicle.split("=");
                    String[] vehicleData = temp1[1].split(";");
                    user.setCurrentVehicle(new Vehicle(vehicleData[0], vehicleData[1], vehicleData[2], vehicleData[3],vehicleData[4],vehicleData[5]));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "DB Error: " + databaseError.getMessage());
            }
        });
    }

    public static void getVehiclesDB(final RetrieveVehiclesFromDB retrieveVehiclesFromDB,
                                     final SingletonUser.UserDataCallback userDataCallback)
            throws CallbackNotInitialized {
        if (retrieveVehiclesFromDB == null || userDataCallback == null) {
            throw new CallbackNotInitialized(TAG);
        }

        SingletonUser user = SingletonUser.getInstance();
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
                            vehicleArrayList.add(new Vehicle(
                                    parts[0],
                                    parts[1],
                                    parts[2],
                                    parts[3],
                                    parts[4],
                                    parts[5]
                            ));
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


    /**
     *  RANKING DATA
     */
    public interface RetrieveRankFromDB {
        // Result code
        int NOT_ALLOWED = -1;
        int UNKNOWN_ERROR = 0;
        int POSITION_NOT_FOUND = 1;
        int INVALID_POSITION = 2;

        void onErrorReceived(int errorType);
        void onUsersRankingReceived(ArrayList<User> users);
    }

    public static void getUsersRank(final RetrieveRankFromDB retrieveRankFromDB)
            throws CallbackNotInitialized {
        SingletonUser user = SingletonUser.getInstance();

        if (retrieveRankFromDB == null) {
            throw new CallbackNotInitialized(TAG);
        } else if (user == null) {
            retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
            return;
        }

        String nation = user.getCountry();
        String region = user.getRegion();
        String district = user.getSubRegion();

        Query query = FirebaseDatabaseManager.getDatabaseReference()
                .child(NODE_POSITION);
        switch (RankingFragment.getLevel()) {
            case LevelMenuFragment.LevelStateChanged.LEVEL_DISTRICT:
                // All users in same district, even those unavailable
                if (nation == null || region == null || district == null) {
                    retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
                    break;
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
                // All users in same region, even those unavailable
                if (nation == null || region == null) {
                    retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
                    break;
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
                // All users in same nation, even those unavailable
                if (nation == null) {
                    retrieveRankFromDB.onErrorReceived(RetrieveRankFromDB.UNKNOWN_ERROR);
                    break;
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
                // Only available users: if a user has old timestamp he is not taken
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
                                            arrayList.addAll(this.getUsersAvailableList(district));
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

                    private ArrayList<User> getUsersAvailableList(DataSnapshot dataSnapshot) {
                        ArrayList<User> arrayList;
                        Iterable<DataSnapshot> users;
                        if (dataSnapshot.exists() && (users = dataSnapshot.getChildren()) != null) {
                            arrayList = new ArrayList<>();
                            for (DataSnapshot user : users) {
                                if (user.hasChild(CHILD_AVAILABILITY) &&
                                        user.child(CHILD_AVAILABILITY).getValue() != null &&
                                        user.child(CHILD_AVAILABILITY).getValue().equals(AVAILABLE)) {
                                    arrayList.add(FirebaseDatabaseManager.getUserFromData(user));
                                }
                            }
                        } else {
                            arrayList = null;
                        }

                        return arrayList;
                    }
                });
                break;

            case LevelMenuFragment.LevelStateChanged.LEVEL_NOT_TRACEABLE:
                // All not traceable users
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
                // All users in the server
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
        if (dataSnapshot.exists() && (users = dataSnapshot.getChildren()) != null) {
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
        double feedback = 0;
        String token = "";

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
            points = Long.valueOf(user.child(CHILD_POINTS).getValue().toString());
        }
        if (user.hasChild(CHILD_AVAILABILITY) &&
                user.child(CHILD_AVAILABILITY).getValue().toString().equals(AVAILABLE)) {
            availability = AVAILABLE;
        }
        if (user.hasChild(CHILD_FEEDBACK)) {
            feedback = Double.valueOf(user.child(CHILD_FEEDBACK).getValue().toString());
        }
        if (user.hasChild(ARG_FIREBASE_TOKEN)) {
            token = String.valueOf(user.child(ARG_FIREBASE_TOKEN).getValue().toString());
        }

        return new User(user.getKey(), username, email, image, points, availability, feedback, token);
    }

    /**
     * Check if first timestamp arg if older then minValidity
     * @param timestamp: long value of timestamp received from server (in milliseconds)
     * @param minValidity: max validity value accepted from timestamp in minutes
     * @return true if timestamp is still valid, false otherwise
     */
    private static boolean checkTimeStamp(long timestamp, int minValidity) {
        // From minutes to milliseconds
        long validityTime = minValidity * 60 * 1000;
        // TODO evitare di usare il tempo del device corrente: potrebbe esere sbagliato
        long currentTime = System.currentTimeMillis();

        return currentTime - timestamp <= validityTime;
    }



    /**
     *  FEEDBACK DATA
     */
    public static void updateUserFeedback(final String uid, final Double feedback, final String country, final String region,
                                          final String subRegion) {
        final DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference
                .child(NODE_USERS)
                .child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(CHILD_STATISTICS)) {
                    if (dataSnapshot.child(CHILD_STATISTICS).hasChild(CHILD_HISTORICAL_FEEDBACK)) {
                        Map<String, String> historicalFeedback = (Map<String, String>) dataSnapshot.child(CHILD_STATISTICS).child(CHILD_HISTORICAL_FEEDBACK).getValue();


                        historicalFeedback.put(String.valueOf(System.currentTimeMillis()), String.valueOf(feedback));
                        databaseReference.child(CHILD_STATISTICS).child(CHILD_HISTORICAL_FEEDBACK).setValue(historicalFeedback);
                        updateMeanFeedback(historicalFeedback, databaseReference);
                    } else {
                        Map<String, String> newFeedback = new HashMap<>();
                        newFeedback.put(String.valueOf(System.currentTimeMillis()), String.valueOf(feedback));
                        databaseReference.child(CHILD_STATISTICS).child(CHILD_HISTORICAL_FEEDBACK).setValue(newFeedback);
                        databaseReference.child(CHILD_FEEDBACK).setValue(String.valueOf(feedback));
                        updatePositionFeedback(String.valueOf(feedback));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private void updateMeanFeedback(Map<String, String> historicalFeedback, DatabaseReference databaseReference) {
                Double sum = 0.0;
                int size = 0;
                for (String key : historicalFeedback.keySet()) {
                    sum += Double.parseDouble(historicalFeedback.get(key));
                    size ++;
                }
                databaseReference.child(CHILD_FEEDBACK).setValue(String.valueOf(sum / size));
                updatePositionFeedback(String.valueOf(sum/size));
            }

            private void updatePositionFeedback(String feedback) {
                DatabaseReference databaseReference = FirebaseDatabaseManager.databaseReference;
                databaseReference.child(NODE_POSITION)
                        .child(country)
                        .child(region)
                        .child(subRegion)
                        .child(uid)
                        .child(CHILD_FEEDBACK)
                        .setValue(feedback);

            }
        });
    }
}