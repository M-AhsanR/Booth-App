package com.schopfen.Booth.Models;

public class SimilarProductsModel {

    String ProductID;
    String UserID;
    String CategoryID;
    String Title;
    String BoothUserName;
    String BoothName;
    String ProductImage;

    public SimilarProductsModel(String productID, String userID, String categoryID, String title, String boothUserName, String boothName, String productImage) {
        ProductID = productID;
        UserID = userID;
        CategoryID = categoryID;
        Title = title;
        BoothUserName = boothUserName;
        BoothName = boothName;
        ProductImage = productImage;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getBoothName() {
        return BoothName;
    }

    public void setBoothName(String boothName) {
        BoothName = boothName;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }
}
