package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class QuestionsModel {

    String QuestionID;
    String UserID;
    String CategoryID;
    String QuestionDescription;
    String QuestionAskedAt;
    String UserImage;
    String UserName;
    String UserCityName;
    String SubCategoryName;
    String CategoryName;
    String CommentCount;
    ArrayList<ProductImagesData> productImagesData;

    public QuestionsModel(String questionID, String userID, String categoryID, String questionDescription, String questionAskedAt, String userImage, String userName, String userCityName, String subCategoryName, String categoryName, String commentCount, ArrayList<ProductImagesData> productImagesData) {
        QuestionID = questionID;
        UserID = userID;
        CategoryID = categoryID;
        QuestionDescription = questionDescription;
        QuestionAskedAt = questionAskedAt;
        UserImage = userImage;
        UserName = userName;
        UserCityName = userCityName;
        SubCategoryName = subCategoryName;
        CategoryName = categoryName;
        CommentCount = commentCount;
        this.productImagesData = productImagesData;
    }

    public String getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(String questionID) {
        QuestionID = questionID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getQuestionDescription() {
        return QuestionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        QuestionDescription = questionDescription;
    }

    public String getQuestionAskedAt() {
        return QuestionAskedAt;
    }

    public void setQuestionAskedAt(String questionAskedAt) {
        QuestionAskedAt = questionAskedAt;
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

    public String getUserCityName() {
        return UserCityName;
    }

    public void setUserCityName(String userCityName) {
        UserCityName = userCityName;
    }

    public String getSubCategoryName() {
        return SubCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        SubCategoryName = subCategoryName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(String commentCount) {
        CommentCount = commentCount;
    }

    public ArrayList<ProductImagesData> getProductImagesData() {
        return productImagesData;
    }

    public void setProductImagesData(ArrayList<ProductImagesData> productImagesData) {
        this.productImagesData = productImagesData;
    }
}
