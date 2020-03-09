package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class WishListEtcData {
    String BoothID;
    String BoothName;
    String ProductID;
    String ProductLikeID;
    String Title;
    String UserID;
    ArrayList<ProductImagesData> productImagesData;

    public WishListEtcData(String boothID, String boothName, String productID, String productLikeID, String title, String userID, ArrayList<ProductImagesData> productImagesData) {
        BoothID = boothID;
        BoothName = boothName;
        ProductID = productID;
        ProductLikeID = productLikeID;
        Title = title;
        UserID = userID;
        this.productImagesData = productImagesData;
    }

    public String getBoothID() {
        return BoothID;
    }

    public void setBoothID(String boothID) {
        BoothID = boothID;
    }

    public String getBoothName() {
        return BoothName;
    }

    public void setBoothName(String boothName) {
        BoothName = boothName;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductLikeID() {
        return ProductLikeID;
    }

    public void setProductLikeID(String productLikeID) {
        ProductLikeID = productLikeID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public ArrayList<ProductImagesData> getProductImagesData() {
        return productImagesData;
    }

    public void setProductImagesData(ArrayList<ProductImagesData> productImagesData) {
        this.productImagesData = productImagesData;
    }
}
