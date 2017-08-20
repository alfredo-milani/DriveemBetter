package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredo on 10/08/17.
 */

public class User {
    // TODO utilizza l'interfaccia Parcelable se si deve passare User da un activity ad un'altra
    // Data from Firebase Authentication
    private String username;
    private String email;
    private Uri photoUrl;
    private String uid;
    private boolean emailVerified;
    private String providerId;
    private List providerData;

    // User data to store in Firebase DB
    private float currentUserLatitude;
    private float currentUserLongitude;
    private ArrayList<Veichle> veichleArrayList;

    // Miscellaneous user data
    private Veichle currentVeichle;

    // Short constructor
    public User (String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Extended constructor
    public User (String username, String email, Uri photoUrl, String uid, boolean emailVerified, String providerId, List providerData) {
        this(username, email);
        this.photoUrl = photoUrl;
        this.uid = uid;
        this.emailVerified = emailVerified;
        this.providerId = providerId;
        this.providerData = providerData;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List getProviderData() {
        return providerData;
    }

    public void setProviderData(List providerData) {
        this.providerData = providerData;
    }

    public float getCurrentUserLatitude() {
        return currentUserLatitude;
    }

    public void setCurrentUserLatitude(float currentUserLatitude) {
        this.currentUserLatitude = currentUserLatitude;
    }

    public float getCurrentUserLongitude() {
        return currentUserLongitude;
    }

    public void setCurrentUserLongitude(float currentUserLongitude) {
        this.currentUserLongitude = currentUserLongitude;
    }

    public ArrayList<Veichle> getVeichleArrayList() {
        return veichleArrayList;
    }

    public void setVeichleArrayList(ArrayList<Veichle> veichleArrayList) {
        this.veichleArrayList = veichleArrayList;
    }

    public Veichle getCurrentVeichle() {
        return currentVeichle;
    }

    public void setCurrentVeichle(Veichle currentVeichle) {
        this.currentVeichle = currentVeichle;
    }
}
