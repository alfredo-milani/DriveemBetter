package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

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
        parcel.writeDouble(this.getFeedback());
        parcel.writeLong(this.getPoints());
        parcel.writeString(this.getToken());
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
        this.setFeedback(in.readDouble());
        this.setPoints(in.readLong());
        this.setToken(in.readString());
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
    private Double feedback;
    private Uri photoUrl;
    private long points;
    private String token;
    // To delete copy
    private long timestamp;
    // TODO: 19/10/17 cerca di rimuovere l attributo datasnapshot. per l eliminazione maniteni 2 liste: una con datasnapshot e una con lista effettiva di users
    private DataSnapshot dataSnapshot;

    public User(String uid, String username, String email, Uri photoUrl, long points, String availability, Double feedback, String token) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.availability = availability;
        this.photoUrl = photoUrl;
        this.points = points;
        this.feedback = feedback;
        this.token = token;
    }

    public User(String uid, String username, String email, Uri photoUrl, long points, String availability, Double feedback, String token, long timestamp, DataSnapshot dataSnapshot) {
        this(uid, username, email, photoUrl, points, availability, feedback, token);
        this.timestamp = timestamp;
        this.dataSnapshot = dataSnapshot;
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

    public Double getFeedback() {
        return this.feedback;
    }

    public void setFeedback(Double feedback) {
        this.feedback = feedback;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public DataSnapshot getDataSnapshot() {
        return this.dataSnapshot;
    }

    public void setDataSnapshot(DataSnapshot dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (!(obj instanceof User)) {
            return false;
        }
        User user = (User) obj;
        return Objects.equals(uid, user.uid);
    }
}
