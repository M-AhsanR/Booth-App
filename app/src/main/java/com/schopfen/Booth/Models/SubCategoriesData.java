package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class SubCategoriesData {
    String CategoryID;
    String Image;
    String Title;
    ArrayList<SubSubCategoriesData> subSubCategoriesData = new ArrayList<>();

    public SubCategoriesData(String categoryID, String image, String title, ArrayList<SubSubCategoriesData> subSubCategoriesData) {
        CategoryID = categoryID;
        Image = image;
        Title = title;
        this.subSubCategoriesData = subSubCategoriesData;
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

    public ArrayList<SubSubCategoriesData> getSubSubCategoriesData() {
        return subSubCategoriesData;
    }

    public void setSubSubCategoriesData(ArrayList<SubSubCategoriesData> subSubCategoriesData) {
        this.subSubCategoriesData = subSubCategoriesData;
    }
}
