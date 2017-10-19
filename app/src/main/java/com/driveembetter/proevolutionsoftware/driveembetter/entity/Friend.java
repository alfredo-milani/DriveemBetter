package com.driveembetter.proevolutionsoftware.driveembetter.entity;

/**
 * Created by matti on 17/10/2017.
 */

public class Friend {

    private String name;
    private String phoneNo;

    public Friend(String name, String phoneNo) {
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
}
