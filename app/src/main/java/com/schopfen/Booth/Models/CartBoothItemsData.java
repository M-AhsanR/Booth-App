package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class CartBoothItemsData {
    String BoothID1;
    String Currency;
    String CurrencySymbol;
    String ProductID1;
    String ProductPrice;
    String ProductQuantity1;
    String ProductTitle;
    String TempOrderID1;
    String UserID1;
    String DeliveryCharges;
    ArrayList<ProductImagesData> productImagesData;

    public CartBoothItemsData(String deliveryCharges, String boothID1, String currency, String currencySymbol, String productID1, String productPrice, String productQuantity1, String productTitle, String tempOrderID1, String userID1, ArrayList<ProductImagesData> productImagesData) {
        BoothID1 = boothID1;
        Currency = currency;
        CurrencySymbol = currencySymbol;
        ProductID1 = productID1;
        ProductPrice = productPrice;
        ProductQuantity1 = productQuantity1;
        ProductTitle = productTitle;
        TempOrderID1 = tempOrderID1;
        UserID1 = userID1;
        DeliveryCharges = deliveryCharges;
        this.productImagesData = productImagesData;
    }

    public String getDeliveryCharges() {
        return DeliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        DeliveryCharges = deliveryCharges;
    }

    public String getBoothID1() {
        return BoothID1;
    }

    public void setBoothID1(String boothID1) {
        BoothID1 = boothID1;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getCurrencySymbol() {
        return CurrencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        CurrencySymbol = currencySymbol;
    }

    public String getProductID1() {
        return ProductID1;
    }

    public void setProductID1(String productID1) {
        ProductID1 = productID1;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductQuantity1() {
        return ProductQuantity1;
    }

    public void setProductQuantity1(String productQuantity1) {
        ProductQuantity1 = productQuantity1;
    }

    public String getProductTitle() {
        return ProductTitle;
    }

    public void setProductTitle(String productTitle) {
        ProductTitle = productTitle;
    }

    public String getTempOrderID1() {
        return TempOrderID1;
    }

    public void setTempOrderID1(String tempOrderID1) {
        TempOrderID1 = tempOrderID1;
    }

    public String getUserID1() {
        return UserID1;
    }

    public void setUserID1(String userID1) {
        UserID1 = userID1;
    }

    public ArrayList<ProductImagesData> getProductImagesData() {
        return productImagesData;
    }

    public void setProductImagesData(ArrayList<ProductImagesData> productImagesData) {
        this.productImagesData = productImagesData;
    }
}
