package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class SearchQuestionsData {
    String BoothName;
    String BoothUserName;
    String CategoryID;
    String CategoryName;
    String FullName;
    String QuestionAskedAt;
    String QuestionDescription;
    String QuestionID;
    String SubCategoryName;
    String UserCityName;
    String UserID;
    String UserImage;
    String UserName;
    String CommentCount;
    ArrayList<ProductImagesData> questionImages;

    public SearchQuestionsData(String boothName, String boothUserName, String categoryID, String categoryName, String fullName, String questionAskedAt, String questionDescription, String questionID, String subCategoryName, String userCityName, String userID, String userImage, String userName, String commentCount, ArrayList<ProductImagesData> questionImages) {
        BoothName = boothName;
        BoothUserName = boothUserName;
        CategoryID = categoryID;
        CategoryName = categoryName;
        FullName = fullName;
        QuestionAskedAt = questionAskedAt;
        QuestionDescription = questionDescription;
        QuestionID = questionID;
        SubCategoryName = subCategoryName;
        UserCityName = userCityName;
        UserID = userID;
        UserImage = userImage;
        UserName = userName;
        CommentCount = commentCount;
        this.questionImages = questionImages;
    }

    public String getBoothName() {
        return BoothName;
    }

    public void setBoothName(String boothName) {
        BoothName = boothName;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getQuestionAskedAt() {
        return QuestionAskedAt;
    }

    public void setQuestionAskedAt(String questionAskedAt) {
        QuestionAskedAt = questionAskedAt;
    }

    public String getQuestionDescription() {
        return QuestionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        QuestionDescription = questionDescription;
    }

    public String getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(String questionID) {
        QuestionID = questionID;
    }

    public String getSubCategoryName() {
        return SubCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        SubCategoryName = subCategoryName;
    }

    public String getUserCityName() {
        return UserCityName;
    }

    public void setUserCityName(String userCityName) {
        UserCityName = userCityName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(String commentCount) {
        CommentCount = commentCount;
    }

    public ArrayList<ProductImagesData> getQuestionImages() {
        return questionImages;
    }

    public void setQuestionImages(ArrayList<ProductImagesData> questionImages) {
        this.questionImages = questionImages;
    }
}
