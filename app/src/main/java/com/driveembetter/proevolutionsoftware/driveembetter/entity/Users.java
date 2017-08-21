package com.driveembetter.proevolutionsoftware.driveembetter.entity;

/**
 * Created by matti on 21/08/2017.
 */

public class Users {

    private  String emailId;
    private String lastMessage;
    private int notifCount;

    public String getEmailId() {
        return this.emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setNotifCount(int notifCount) {
        this.notifCount = notifCount;
    }

    public int getNotifCount() {
        return this.notifCount;
    }
}
