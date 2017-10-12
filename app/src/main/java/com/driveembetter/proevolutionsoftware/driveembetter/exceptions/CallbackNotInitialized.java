package com.driveembetter.proevolutionsoftware.driveembetter.exceptions;

/**
 * Created by alfredo on 29/08/17.
 */

public class CallbackNotInitialized extends RuntimeException {

    public CallbackNotInitialized() {

    }

    public CallbackNotInitialized(String className) {
        super(className + ": Callback not initialized!");
    }
}
