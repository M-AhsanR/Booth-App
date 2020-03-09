package com.schopfen.Booth.Models;

public class SelectedCateModel {
    String UserCategoryID;
    String UserID;
    String CategoryID;
    String Type;
    String Title;
    String Image;

    public SelectedCateModel(String userCategoryID, String userID, String categoryID, String type, String title, String image) {
        UserCategoryID = userCategoryID;
        UserID = userID;
        CategoryID = categoryID;
        Type = type;
        Title = title;
        Image = image;
    }

    public String getUserCategoryID() {
        return UserCategoryID;
    }

    public void setUserCategoryID(String userCategoryID) {
        UserCategoryID = userCategoryID;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
