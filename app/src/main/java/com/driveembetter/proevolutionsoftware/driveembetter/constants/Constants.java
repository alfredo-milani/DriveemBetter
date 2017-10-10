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
    int MAX_TIMESTAMP_TIME = 10;    // Minimum value for validity of timestamp (in MINUTES)

    // Database's nodes
    String NODE_POSITION = "position";
    String NODE_USERS = "users";
    String NODE_VEHICLES = "vehicles";
    String NODE_CURRENT_VEHICLE = "current_vehicle";

    // Database's child for entity User
    String CHILD_USERNAME = "username";
    String CHILD_IMAGE = "image";
    String CHILD_POINTS = "points";
    String CHILD_CURRENT_POSITION = "currentUserPosition";
    String CHILD_EMAIL = "email";
    String CHILD_AVAILABILITY = "availability";
    String CHILD_CURRENT_VEHICLE = "current_vehicle";
    String CHILD_FEEDBACK = "feedback";
    String CHILD_HISTORICAL_FEEDBACK = "historicalFeedback";

    // Statistics
    String CHILD_STATISTICS = "statistics";

    String CHILD_STAT_DAY = "dataDaily";
    String CHILD_STAT_WEEK = "dataWeekly";
    String CHILD_DATE = "currentTime";
    String CHILD_DATA = "userData";

    String VELOCITY = "Velocity";
    String ACCELERATION = "Acceleration";
    String STR_DAY = "day";
    String STR_WEEK = "week";
    int HOURS = 24;
    int DAYS = 7;



    // TODO DA TOGLIERE
    String CAR = "Car";
    String MOTO = "Moto";
    String VAN = "Van";
    String EMPTY_TYPE_FIELD = "Please select type";
    String EMPTY_FIELD = "Please insert all informations";
    String CHILD_COUNT = "child_count";
    Integer ADD_VCL_ACTIVITY_ID = 1;
    String PLATE_LIST = "Plate list";
    String VEHICLE_EXISTS_YET = "Plate exists yet";
    String PLATE = "Plate, ";
    String TYPE = "Type, ";
    String INS_DATE = "Insurance date, ";
    String REV_DATE = " Revision date, ";
    String CURRENT = "Current";
    String MODEL = "Model, ";
    String OWNER = "Owner, ";
    String YES = "Yes";
    String NO = "No";


    //Speed and Acceleration limit
    String ENGLISH_ACCELERATION_ALERT = "You're accelerating too much, try to slow down";
    String ENGLISH_BRAKING_ALERT = "please, avoid making abrupt brakes";
    String ITALIAN_ACCELERATION_ALERT = "Evita accelerazioni brusche";
    String ITALIAN_BRAKING_ALERT = "Evita di frenare in maniera troppo energetica";

    //Feedback
    String FEEDBACK_SENT_ITALIAN = "Il tuo feedback è stato inviato!";
    String FEEDBACK_SENT_ENGLISH = "Your feedback has been sent!";
    String FEEDBACK_SENT_BUTTON_ITALIAN = "FATTO!";
    String FEEDBACK_SENT_BUTTON_ENGLISH = "DONE!";
}
