package com.driveembetter.proevolutionsoftware.driveembetter.exception;

/**
 * Created by alfredo on 29/08/17.
 */

public class CallbackNotInitialized
        extends RuntimeException {

    public CallbackNotInitialized() {

    }

    public CallbackNotInitialized(String msg) {
        super(msg);
    }
}
