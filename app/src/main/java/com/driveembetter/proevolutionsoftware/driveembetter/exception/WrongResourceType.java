package com.driveembetter.proevolutionsoftware.driveembetter.exception;

/**
 * Created by alfredo on 30/08/17.
 */

public class WrongResourceType
        extends RuntimeException {

    public WrongResourceType() {

    }

    public WrongResourceType(String msg) {
        super(msg);
    }
}
