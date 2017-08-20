package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

/**
 * Created by alfredo on 20/08/17.
 */

public interface BaseProvider {
    void signIn(String email, String password);
    void signOut();
    boolean isSignIn();
}
