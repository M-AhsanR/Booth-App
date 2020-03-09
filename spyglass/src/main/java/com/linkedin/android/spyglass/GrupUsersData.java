package com.linkedin.android.spyglass;

public class GrupUsersData {
    String FullName;
    String UserID;

    public GrupUsersData(String fullName, String userID) {
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
