package com.proevolutionsoftware.driveembetter.exceptions;

/**
 * Created by alfredo on 17/08/17.
 */

public class ResourcesNotInitialized extends RuntimeException {

    public ResourcesNotInitialized() {

    }

    public ResourcesNotInitialized(String msg) {
        super(msg);
    }
}
