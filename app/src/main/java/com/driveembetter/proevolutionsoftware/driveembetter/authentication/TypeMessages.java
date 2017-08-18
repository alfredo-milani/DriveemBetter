package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

/**
 * Created by alfredo on 14/08/17.
 */

public interface TypeMessages {
    // Messages received by UI
    int UNKNOWN_EVENT = 0;
    int USER_LOGIN = 1;
    int USER_LOGOUT = 2;
    int USER_LOGIN_EMAIL_PSW = 3;
    int USER_ALREADY_EXIST = 4;
    int EMAIL_REQUIRED = 5;
    int PASSWORD_REQUIRED = 6;
    int INVALID_CREDENTIALS = 7;
    int INVALID_USER = 8;
    int VERIFICATION_EMAIL_SENT = 9;
    int VERIFICATION_EMAIL_NOT_SENT = 10;
    int EMAIL_NOT_VERIFIED = 11;
    int USERNAME_REQUIRED = 12;
}
