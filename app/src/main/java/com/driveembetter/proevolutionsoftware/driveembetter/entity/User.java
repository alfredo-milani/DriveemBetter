package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;

/**
 * Created by alfredo on 31/08/17.
 */

public class User {

    private final static String TAG = User.class.getSimpleName();

    private String username;
    private String email;
    private Uri photoUrl;
    private String uid;
    private long points;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, Uri photoUrl, long points) {
        this.username = username;
        this.photoUrl = photoUrl;
        this.points = points;
    }

    public User(String username, String email, Uri photoUrl, String uid) {
        this(username, email);
        this.photoUrl = photoUrl;
        this.uid = uid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getPoints() {
        return this.points;
    }

    public void setPoints(long points) {
        this.points = points;
    }
}
