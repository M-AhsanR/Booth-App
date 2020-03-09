package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class OrderRequestsData {
    String BoothCityTitle;
    String BoothID;
    String BoothName;
    String CompressedBoothImage;
    String OrderRequestID;
    String OrderStatus;
    String OrderStatusID;
    String OrderTrackID;
    String UserName;
    ArrayList<OrderItemsData> orderItemsData;

    public OrderRequestsData(String boothCityTitle, String boothID, String boothName, String compressedBoothImage, String orderRequestID, String orderStatus, String orderStatusID, String orderTrackID, String userName, ArrayList<OrderItemsData> orderItemsData) {
        BoothCityTitle = boothCityTitle;
        BoothID = boothID;
        BoothName = boothName;
        CompressedBoothImage = compressedBoothImage;
        OrderRequestID = orderRequestID;
        OrderStatus = orderStatus;
        OrderStatusID = orderStatusID;
        OrderTrackID = orderTrackID;
        UserName = userName;
        this.orderItemsData = orderItemsData;
    }

    public String getBoothCityTitle() {
        return BoothCityTitle;
    }

    public void setBoothCityTitle(String boothCityTitle) {
        BoothCityTitle = boothCityTitle;
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

    public String getCompressedBoothImage() {
        return CompressedBoothImage;
    }

    public void setCompressedBoothImage(String compressedBoothImage) {
        CompressedBoothImage = compressedBoothImage;
    }

    public String getOrderRequestID() {
        return OrderRequestID;
    }

    public void setOrderRequestID(String orderRequestID) {
        OrderRequestID = orderRequestID;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderStatusID() {
        return OrderStatusID;
    }

    public void setOrderStatusID(String orderStatusID) {
        OrderStatusID = orderStatusID;
    }

    public String getOrderTrackID() {
        return OrderTrackID;
    }

    public void setOrderTrackID(String orderTrackID) {
        OrderTrackID = orderTrackID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public ArrayList<OrderItemsData> getOrderItemsData() {
        return orderItemsData;
    }

    public void setOrderItemsData(ArrayList<OrderItemsData> orderItemsData) {
        this.orderItemsData = orderItemsData;
    }
}
