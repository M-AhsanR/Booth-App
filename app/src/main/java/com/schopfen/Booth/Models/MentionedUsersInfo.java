package com.schopfen.Booth.Models;

public class MentionedUsersInfo {
    String UserID;
    String FullName;
    String MentionedName;
    String MentionedUserType;

    public MentionedUsersInfo(String userID, String fullName, String mentionedName, String mentionedUserType) {
        UserID = userID;
        FullName = fullName;
        MentionedName = mentionedName;
        MentionedUserType = mentionedUserType;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getMentionedName() {
        return MentionedName;
    }

    public void setMentionedName(String mentionedName) {
        MentionedName = mentionedName;
    }

    public String getMentionedUserType() {
        return MentionedUserType;
    }

    public void setMentionedUserType(String mentionedUserType) {
        MentionedUserType = mentionedUserType;
    }
}
