package com.driveembetter.proevolutionsoftware.driveembetter.exceptions;

/**
 * Created by alfredo on 12/10/17.
 */

public class SingletonNotInitialized extends RuntimeException {

    public SingletonNotInitialized() {

    }

    public SingletonNotInitialized(String className) {
        super(className + ": Callback not initialized!");
    }
}
