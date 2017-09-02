package com.driveembetter.proevolutionsoftware.driveembetter.constants;

/**
 * Created by alfredo on 10/08/17.
 */

public interface Constants {
    // Informations exchanged between Activities
    String USER = "user_info";
    String PROVIDER_TYPE = "provider_type";

    // Authentication providers
    String FIREBASE_PROVIDER = "firebase_provider";
    String GOOGLE_PROVIDER = "google_provider";
    String FACEBOOK_PROVIDER = "facebook_provider";
    String TWITTER_PROVIDER = "twitter_provider";


    // chat constants
    String ARG_USERS = "users";
    String ARG_RECEIVER = "receiver";
    String ARG_RECEIVER_UID = "receiver_uid";
    String ARG_CHAT_ROOMS = "chat_rooms";
    String ARG_FIREBASE_TOKEN = "firebaseToken";
    String ARG_FRIENDS = "friends";
    String ARG_UID = "uid";



    // Database's nodes
    String NODE_POSITION = "position";
    String NODE_USERS = "users";
    String NODE_VEHICLES = "vehicles";

    // Database's child
    String CHILD_USERNAME = "username";
    String CHILD_IMAGE = "image";
    String CHILD_POINTS = "points";



    // Users divided by location
    int LEVEL_NATION = 1;
    int LEVEL_REGION = 2;
    int LEVEL_DISTRICT = 3;
}
