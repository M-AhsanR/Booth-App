package com.schopfen.Booth.DataClasses;

import com.schopfen.Booth.Models.MentionedUsersInfo;

import java.util.ArrayList;

public class NotificationsData {
    String UserNotificationID;
    String UserType;
    String Type;
    String UserIDofNoti;
    String ProductID;
    String QuestionID;
    String QuestionCommentID;
    String IsRead;
    String CreatedAt;
    String UserName;
    String UserImage;
    String NotificationText;
    String ProductImage;
    String QuestionImage;
    String ActivityDoneAs;
    String BoothUserName;
    String BoothImage;
    String OrderRequestID;

    ArrayList<MentionedUsersInfo> mentionedUsersInfos;

    public NotificationsData(String orderRequestID, String activityDoneAs, String boothUserName, String boothImage, String userNotificationID, String userType, String type, String userIDofNoti, String productID, String questionID, String questionCommentID, String isRead, String createdAt, String userName, String userImage, String notificationText, String productImage, String questionImage, ArrayList<MentionedUsersInfo> mentionedUsersInfos) {
        UserNotificationID = userNotificationID;
        UserType = userType;
        Type = type;
        UserIDofNoti = userIDofNoti;
        ProductID = productID;
        QuestionID = questionID;
        OrderRequestID = orderRequestID;
        QuestionCommentID = questionCommentID;
        IsRead = isRead;
        CreatedAt = createdAt;
        UserName = userName;
        UserImage = userImage;
        NotificationText = notificationText;
        ProductImage = productImage;
        QuestionImage = questionImage;
        ActivityDoneAs = activityDoneAs;
        BoothUserName = boothUserName;
        BoothImage = boothImage;
        this.mentionedUsersInfos = mentionedUsersInfos;
    }

    public String getOrderRequestID() {
        return OrderRequestID;
    }

    public void setOrderRequestID(String orderRequestID) {
        OrderRequestID = orderRequestID;
    }

    public String getActivityDoneAs() {
        return ActivityDoneAs;
    }

    public void setActivityDoneAs(String activityDoneAs) {
        ActivityDoneAs = activityDoneAs;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getBoothImage() {
        return BoothImage;
    }

    public void setBoothImage(String boothImage) {
        BoothImage = boothImage;
    }

    public ArrayList<MentionedUsersInfo> getMentionedUsersInfos() {
        return mentionedUsersInfos;
    }

    public void setMentionedUsersInfos(ArrayList<MentionedUsersInfo> mentionedUsersInfos) {
        this.mentionedUsersInfos = mentionedUsersInfos;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getQuestionImage() {
        return QuestionImage;
    }

    public void setQuestionImage(String questionImage) {
        QuestionImage = questionImage;
    }

    public String getUserNotificationID() {
        return UserNotificationID;
    }

    public void setUserNotificationID(String userNotificationID) {
        UserNotificationID = userNotificationID;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUserIDofNoti() {
        return UserIDofNoti;
    }

    public void setUserIDofNoti(String userIDofNoti) {
        UserIDofNoti = userIDofNoti;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(String questionID) {
        QuestionID = questionID;
    }

    public String getQuestionCommentID() {
        return QuestionCommentID;
    }

    public void setQuestionCommentID(String questionCommentID) {
        QuestionCommentID = questionCommentID;
    }

    public String getIsRead() {
        return IsRead;
    }

    public void setIsRead(String isRead) {
        IsRead = isRead;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getNotificationText() {
        return NotificationText;
    }

    public void setNotificationText(String notificationText) {
        NotificationText = notificationText;
    }
}
