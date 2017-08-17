package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

/**
 * Created by alfredo on 14/08/17.
 */

public interface TypeMessages {
    int UNKNOWN_EVENT = 0;
    int USER_LOGIN_EMAIL_PSW = 1;
    int USER_LOGOUT = 2;
    int USER_ALREADY_EXIST = 3;
    int EMAIL_REQUIRED = 4;
    int PASSWORD_REQUIRED = 5;
    int INVALID_CREDENTIALS = 6;
    int INVALID_USER = 7;
}
