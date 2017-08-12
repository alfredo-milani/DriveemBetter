package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;

import java.util.List;

/**
 * Created by alfredo on 10/08/17.
 */

public class User {
    private String username;
    private String email;
    private Uri photoUrl;
    private String uid;
    private boolean emailVerified;
    private String providerId;
    private List providerData;

    public User (String username, String email) {
        this.username = username;
        this.email = email;
    }

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
}
