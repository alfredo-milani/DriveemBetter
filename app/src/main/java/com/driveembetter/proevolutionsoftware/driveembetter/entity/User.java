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
        parcel.writeString(this.getUid());
        parcel.writeString(this.getUsername());
        parcel.writeString(this.getEmail());
        parcel.writeString(this.getAvailability());
        parcel.writeLong(this.getPoints());
        if (this.getPhotoUrl() != null) {
            parcel.writeString(this.getPhotoUrl().toString());
        }
    }

    /**
     * Retrieving Movie data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private User(Parcel in){
        this.setUid(in.readString());
        this.setUsername(in.readString());
        this.setEmail(in.readString());
        this.setAvailability(in.readString());
        this.setPoints(in.readLong());
        this.setPhotoUrl(this.getUriIfExist(in.readString()));
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

    // TODO impedisci di avere valori null come URI
    private Uri getUriIfExist(String s) {
        return s != null ?
                Uri.parse(s) : null;
    }



    private String uid;
    private String username;
    private String email;
    private String availability;
    private Uri photoUrl;
    private long points;

    public User(String uid, String username, String email, Uri photoUrl, long points, String availability) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.availability = availability;
        this.photoUrl = photoUrl;
        this.points = points;
    }



    public String getUsername() {
        return this.username;
    }

    public String getUsernameFromUid() {
        return "User_".concat(this.uid.substring(
                this.uid.length() / 2,
                this.uid.length() * 3 / 4
        ).toLowerCase());
            /*
            int randomInt = new Random().nextInt(Integer.MAX_VALUE);
            String username = this.context.getString(R.string.user_item)
                    .concat(String.valueOf(randomInt));
                    */
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

    public String getAvailability() {
        return this.availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
