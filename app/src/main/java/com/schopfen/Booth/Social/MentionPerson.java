package com.schopfen.Booth.Social;

/**
 * Created by ameen on 30/11/17.
 * Happy Coding
 */

public class MentionPerson {

    String FullName;
    String UserID;

    public MentionPerson(){}

    public MentionPerson(String fullName, String userID) {
        FullName = fullName;
        UserID = userID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}