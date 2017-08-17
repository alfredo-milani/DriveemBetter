package com.driveembetter.proevolutionsoftware.driveembetter.exeption;

/**
 * Created by alfredo on 17/08/17.
 */

public class ProviderNotFoundExeption extends RuntimeException {
    public ProviderNotFoundExeption() {

    }

    public ProviderNotFoundExeption(String msg) {
        super(msg);
    }
}
