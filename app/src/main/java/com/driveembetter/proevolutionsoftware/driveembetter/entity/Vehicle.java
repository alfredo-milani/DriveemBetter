package com.driveembetter.proevolutionsoftware.driveembetter.entity;

/**
 * Created by alfredo on 17/08/17.
 */

public class Vehicle {
    private String numberPlate;
    private String owner;
    private int statusTires;
    private int statusBattery;
    private String type;
    private String model;
    private Boolean current_vehicle;

    // TODO decidere se creare classe a sè stante per i veicoli oppure metterli direttamente nell'entità SingletonUser
    private String otherStuff;

    public Vehicle(String type, String model, String numberPlate, String owner) {
        this.type = type;
        this.model = model;
        this.numberPlate = numberPlate;
        this.owner = owner;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
