package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class HomeFriendsModel {
    String UserFriendActivityID;
    String LoggedInUserID;
    String UserType;
    String Type;
    String UserID;
    String NotificationTypeID;
    String NotificationTextEn;
    String NotificationTextAr;
    String ProductID;
    String ProductCommentID;
    String QuestionID;
    String QuestionCommentID;
    String IsRead;
    String CreatedAt;
    String LoggedInUserName;
    String LoggedInUserImage;
    String UserName;
    String UserImage;
    String NotificationText;

    ArrayList<ProductsData> Product;
    CommentsModel ProductComment;
    ProductsData Question;
    CommentsModel QuestionComment;
    ArrayList<MentionedUsersInfo> mentionedUsersInfos;

    public HomeFriendsModel(String userFriendActivityID, String loggedInUserID, String userType, String type, String userID, String notificationTypeID, String notificationTextEn, String notificationTextAr, String productID, String productCommentID, String questionID, String questionCommentID, String isRead, String createdAt, String loggedInUserName, String loggedInUserImage, String userName, String userImage, String notificationText, ArrayList<ProductsData> product, CommentsModel productComment, ProductsData question, CommentsModel questionComment, ArrayList<MentionedUsersInfo> MentionedUsersInfos) {
        UserFriendActivityID = userFriendActivityID;
        LoggedInUserID = loggedInUserID;
        UserType = userType;
        Type = type;
        UserID = userID;
        NotificationTypeID = notificationTypeID;
        NotificationTextEn = notificationTextEn;
        NotificationTextAr = notificationTextAr;
        ProductID = productID;
        ProductCommentID = productCommentID;
        QuestionID = questionID;
        QuestionCommentID = questionCommentID;
        IsRead = isRead;
        CreatedAt = createdAt;
        LoggedInUserName = loggedInUserName;
        LoggedInUserImage = loggedInUserImage;
        UserName = userName;
        UserImage = userImage;
        NotificationText = notificationText;
        Product = product;
        ProductComment = productComment;
        Question = question;
        QuestionComment = questionComment;
        mentionedUsersInfos = MentionedUsersInfos;

    }

    public ArrayList<MentionedUsersInfo> getMentionedUsersInfos() {
        return mentionedUsersInfos;
    }

    public void setMentionedUsersInfos(ArrayList<MentionedUsersInfo> mentionedUsersInfos) {
        this.mentionedUsersInfos = mentionedUsersInfos;
    }

    public String getUserFriendActivityID() {
        return UserFriendActivityID;
    }

    public void setUserFriendActivityID(String userFriendActivityID) {
        UserFriendActivityID = userFriendActivityID;
    }

    public String getLoggedInUserID() {
        return LoggedInUserID;
    }

    public void setLoggedInUserID(String loggedInUserID) {
        LoggedInUserID = loggedInUserID;
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

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getNotificationTypeID() {
        return NotificationTypeID;
    }

    public void setNotificationTypeID(String notificationTypeID) {
        NotificationTypeID = notificationTypeID;
    }

    public String getNotificationTextEn() {
        return NotificationTextEn;
    }

    public void setNotificationTextEn(String notificationTextEn) {
        NotificationTextEn = notificationTextEn;
    }

    public String getNotificationTextAr() {
        return NotificationTextAr;
    }

    public void setNotificationTextAr(String notificationTextAr) {
        NotificationTextAr = notificationTextAr;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductCommentID() {
        return ProductCommentID;
    }

    public void setProductCommentID(String productCommentID) {
        ProductCommentID = productCommentID;
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

    public String getLoggedInUserName() {
        return LoggedInUserName;
    }

    public void setLoggedInUserName(String loggedInUserName) {
        LoggedInUserName = loggedInUserName;
    }

    public String getLoggedInUserImage() {
        return LoggedInUserImage;
    }

    public void setLoggedInUserImage(String loggedInUserImage) {
        LoggedInUserImage = loggedInUserImage;
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

    public ArrayList<ProductsData> getProduct() {
        return Product;
    }

    public void setProduct(ArrayList<ProductsData> product) {
        Product = product;
    }

    public CommentsModel getProductComment() {
        return ProductComment;
    }

    public void setProductComment(CommentsModel productComment) {
        ProductComment = productComment;
    }

    public ProductsData getQuestion() {
        return Question;
    }

    public void setQuestion(ProductsData question) {
        Question = question;
    }

    public CommentsModel getQuestionComment() {
        return QuestionComment;
    }

    public void setQuestionComment(CommentsModel questionComment) {
        QuestionComment = questionComment;
    }
}
