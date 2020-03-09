package com.schopfen.Booth.Models;

public class TransactionHistoryData {

    String CreatedAt,OrderDate,OrderNumber,Points,TransactionType,Type,UserID,UserPointHistoryID;

    public TransactionHistoryData(String createdAt, String orderDate, String orderNumber, String points, String transactionType, String type, String userID, String userPointHistoryID) {
        CreatedAt = createdAt;
        OrderDate = orderDate;
        OrderNumber = orderNumber;
        Points = points;
        TransactionType = transactionType;
        Type = type;
        UserID = userID;
        UserPointHistoryID = userPointHistoryID;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getPoints() {
        return Points;
    }

    public void setPoints(String points) {
        Points = points;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserPointHistoryID() {
        return UserPointHistoryID;
    }

    public void setUserPointHistoryID(String userPointHistoryID) {
        UserPointHistoryID = userPointHistoryID;
    }
}
