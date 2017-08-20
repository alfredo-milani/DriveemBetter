package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

/**
 * Created by alfredo on 14/08/17.
 */

public interface TypeMessages {
    // Messages received by UI
    int UNKNOWN_EVENT = 0;

    int NETWORK_ERROR = 24;

    int USER_LOGIN = 22;
    int USER_LOGIN_EMAIL_PSW = 3;
    int USER_LOGIN_GOOGLE = 12;
    int USER_LOGIN_FACEBOOK = 13;
    int USER_LOGIN_TWITTER = 14;

    int USER_LOGOUT = 23;
    int USER_LOGOUT_EMAIL_PSW = 18;
    int USER_LOGOUT_GOOGLE = 19;
    int USER_LOGOUT_FACEBOOK = 20;
    int USER_LOGOUT_TWITTER = 21;

    int USER_ALREADY_EXIST = 4;
    int EMAIL_REQUIRED = 5;
    int PASSWORD_REQUIRED = 6;
    int INVALID_CREDENTIALS = 7;
    int BAD_FORMATTED_EMAIL = 15;
    int PASSWORD_INVALID = 16;
    int INVALID_USER = 8;
    int VERIFICATION_EMAIL_SENT = 9;
    int VERIFICATION_EMAIL_NOT_SENT = 10;
    int EMAIL_NOT_VERIFIED = 11;
    int RESEND_VERIFICATION_EMAIL = 17;
}
