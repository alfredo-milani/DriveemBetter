package com.driveembetter.proevolutionsoftware.driveembetter.entity;

/**
 * Created by alfredo on 17/08/17.
 */

public class Vehicle {

    private final static String TAG = Vehicle.class.getSimpleName();

    public final static String CAR = "Car";
    public final static String MOTO = "Moto";
    public final static String VAN = "Van";

    private String numberPlate;
    private String owner;
    private int statusTires;
    private int statusBattery;
    private String type;
    private String model;
    private Boolean current_vehicle;
    private String insurance_date;
    private String revision_date;


    public Vehicle(String type, String model, String numberPlate, String owner, String insurance_date, String revision_date) {

        this.type = type;
        this.model = model;
        this.numberPlate = numberPlate;
        this.owner = owner;
        this.insurance_date = insurance_date;
        this.revision_date = revision_date;
    }

    public int getStatusTires() {
        return statusTires;
    }

    public void setStatusTires(int statusTires) {
        this.statusTires = statusTires;
    }

    public String getInsurance_date() {
        return insurance_date;
    }

    public void setInsurance_date(String insurance_date) {
        this.insurance_date = insurance_date;
    }

    public String getRevision_date() {
        return revision_date;
    }

    public void setRevision_date(String revision_date) {
        this.revision_date = revision_date;
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
