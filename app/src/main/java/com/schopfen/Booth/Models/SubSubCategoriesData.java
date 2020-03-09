package com.schopfen.Booth.Models;

public class SubSubCategoriesData {
    String CategoryID;
    String Image;
    String Title;

    public SubSubCategoriesData(String categoryID, String image, String title) {
        CategoryID = categoryID;
        Image = image;
        Title = title;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
