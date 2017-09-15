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

    // Statistics
    int HOURS = 24;

    // PositionManager constants
    String OLD_COUNTRY = "oldCountry";
    String OLD_REGION = "oldRegion";
    String OLD_SUB_REGION = "oldSubRegion";
    String COUNTRY = "country";
    String REGION = "region";
    String SUB_REGION = "subRegion";

    //SaveMeFragment constants
    String AVAILABLE = "Available";
    String UNAVAILABLE = "Unavailable";

    // chat constants
    String ARG_USERS = "users";
    String ARG_RECEIVER = "receiver";
    String ARG_RECEIVER_UID = "receiver_uid";
    String ARG_CHAT_ROOMS = "chat_rooms";
    String ARG_FIREBASE_TOKEN = "firebaseToken";
    String ARG_FRIENDS = "friends";
    String ARG_UID = "uid";

    // Database timestamp
    String CHILD_TIMESTAMP = "timestamp";
    int MAX_TIMESTAMP_TIME = 30;    // Minimum value for validity of timestamp (in MINUTES)

    // Database's nodes
    String NODE_POSITION = "position";
    String NODE_USERS = "users";
    String NODE_VEHICLES = "vehicles";

    // Database's child for entity User
    String CHILD_USERNAME = "username";
    String CHILD_IMAGE = "image";
    String CHILD_POINTS = "points";
    String CHILD_CURRENT_POSITION = "currentUserPosition";
    String CHILD_EMAIL = "email";
    String CHILD_AVAILABILITY = "availability";
}
