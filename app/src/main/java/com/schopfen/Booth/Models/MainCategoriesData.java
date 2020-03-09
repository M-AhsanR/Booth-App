package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class MainCategoriesData {
    String CategoryID;
    String Image;
    String Title;
    ArrayList<SubCategoriesData> subCategoriesData = new ArrayList<>();


    public MainCategoriesData(String categoryID, String image, String title, ArrayList<SubCategoriesData> subCategoriesData) {
        CategoryID = categoryID;
        Image = image;
        Title = title;
        this.subCategoriesData = subCategoriesData;
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

    public ArrayList<SubCategoriesData> getSubCategoriesData() {
        return subCategoriesData;
    }

    public void setSubCategoriesData(ArrayList<SubCategoriesData> subCategoriesData) {
        this.subCategoriesData = subCategoriesData;
    }
}
