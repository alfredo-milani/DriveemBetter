package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alfredo on 31/08/17.
 */

public class User
        implements Parcelable {

    private final static String TAG = User.class.getSimpleName();

    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the Movie data to Parcel object
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.username);
        parcel.writeString(this.email);
        if (this.photoUrl != null) {
            parcel.writeString(this.photoUrl.toString());
        }
        parcel.writeString(this.uid);
        parcel.writeLong(this.points);
    }

    /**
     * Retrieving Movie data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private User(Parcel in){
        this.username = in.readString();
        this.email = in.readString();
        this.photoUrl = this.getUriIfExist(in.readString());
        this.uid = in.readString();
        this.points = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private Uri getUriIfExist(String s) {
        return s != null ? Uri.parse(s) : null;
    }



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

    public User(String uid, String username, Uri photoUrl, long points) {
        this.uid = uid;
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
