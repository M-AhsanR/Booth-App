package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class CartBoothsData {
    String BoothID;
    String BoothName;
    String ProductID;
    String ProductQuantity;
    String TempOrderID;
    String UserID;
    String VatPercentage;
    ArrayList<CartBoothItemsData> cartBoothItemsData;

    public CartBoothsData(String vatPercentage, String boothID, String boothName, String productID, String productQuantity, String tempOrderID, String userID, ArrayList<CartBoothItemsData> cartBoothItemsData) {
        BoothID = boothID;
        BoothName = boothName;
        ProductID = productID;
        ProductQuantity = productQuantity;
        TempOrderID = tempOrderID;
        UserID = userID;
        VatPercentage = vatPercentage;
        this.cartBoothItemsData = cartBoothItemsData;
    }

    public String getVatPercentage() {
        return VatPercentage;
    }

    public void setVatPercentage(String vatPercentage) {
        VatPercentage = vatPercentage;
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

    public String getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        ProductQuantity = productQuantity;
    }

    public String getTempOrderID() {
        return TempOrderID;
    }

    public void setTempOrderID(String tempOrderID) {
        TempOrderID = tempOrderID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public ArrayList<CartBoothItemsData> getCartBoothItemsData() {
        return cartBoothItemsData;
    }

    public void setCartBoothItemsData(ArrayList<CartBoothItemsData> cartBoothItemsData) {
        this.cartBoothItemsData = cartBoothItemsData;
    }
}
