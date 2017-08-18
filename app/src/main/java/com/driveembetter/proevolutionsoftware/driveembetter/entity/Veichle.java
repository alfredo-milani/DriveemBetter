package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import java.util.ArrayList;

/**
 * Created by alfredo on 17/08/17.
 */

public class Veichle {
    private String numberPlate;
    private ArrayList<User> owner;
    private int statusTires;
    private int statusBattery;

    // TODO decidere se creare classe a sè stante per i veicoli oppure metterli direttamente nell'entità User
    private String otherStuff;
}
