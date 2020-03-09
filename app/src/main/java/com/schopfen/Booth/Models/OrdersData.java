package com.schopfen.Booth.Models;

import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersData {
    String OrderRequestID;
    String OrderID;
    String OrderTrackID;
    String OrderStatusID;
    String BoothID;
    String OrderLastStatusID;
    String OrderRequestVerificationCode;
    String TotalAmount;
    String ActualDeliveryCharges;
    String AdditionalDeliveryCharges;
    String Discount;
    String VatPercentageApplied;
    String VatAmountApplied;
    String GrandTotal;
    String BoothImage;
    String BoothUserName;
    String BoothEmail;
    String BoothMobile;
    String BoothName;
    String UserID;
    String UserImage;
    String UserName;
    String UserEmail;
    String UserMobile;
    String FullName;
    String OrderStatus;
    String BoothCityTitle;
    String UserCityTitle;
    String OrderReceivedAt;
    String AddressTitle;
    String RecipientName;
    String AddressEmail;
    String AddressMobile;
    String AddressGender;
    String ApartmentNo;
    String Address1;
    String Address2;
    String AddressCity;
    String AddressLatitude;
    String AddressLongitude;
    String AddressIsDefault;
    String VatPercentage;
    String DeliveryDate;
    ArrayList<OrderItemsData> OrderItems;

    public OrdersData(String deliveryDate, String orderRequestID, String orderID, String orderTrackID, String orderStatusID, String boothID, String orderLastStatusID, String orderRequestVerificationCode, String totalAmount, String actualDeliveryCharges, String additionalDeliveryCharges, String discount, String vatPercentageApplied, String vatAmountApplied, String grandTotal, String boothImage, String boothUserName, String boothEmail, String boothMobile, String boothName, String userID, String userImage, String userName, String userEmail, String userMobile, String fullName, String orderStatus, String boothCityTitle, String userCityTitle, String orderReceivedAt, String addressTitle, String recipientName, String addressEmail, String addressMobile, String addressGender, String apartmentNo, String address1, String address2, String addressCity, String addressLatitude, String addressLongitude, String addressIsDefault, String vatPercentage, ArrayList<OrderItemsData> orderItems) {
        OrderRequestID = orderRequestID;
        OrderID = orderID;
        OrderTrackID = orderTrackID;
        OrderStatusID = orderStatusID;
        BoothID = boothID;
        OrderLastStatusID = orderLastStatusID;
        OrderRequestVerificationCode = orderRequestVerificationCode;
        TotalAmount = totalAmount;
        ActualDeliveryCharges = actualDeliveryCharges;
        AdditionalDeliveryCharges = additionalDeliveryCharges;
        Discount = discount;
        VatPercentageApplied = vatPercentageApplied;
        VatAmountApplied = vatAmountApplied;
        GrandTotal = grandTotal;
        BoothImage = boothImage;
        BoothUserName = boothUserName;
        BoothEmail = boothEmail;
        BoothMobile = boothMobile;
        BoothName = boothName;
        UserID = userID;
        UserImage = userImage;
        UserName = userName;
        UserEmail = userEmail;
        UserMobile = userMobile;
        FullName = fullName;
        OrderStatus = orderStatus;
        BoothCityTitle = boothCityTitle;
        UserCityTitle = userCityTitle;
        OrderReceivedAt = orderReceivedAt;
        AddressTitle = addressTitle;
        RecipientName = recipientName;
        AddressEmail = addressEmail;
        AddressMobile = addressMobile;
        AddressGender = addressGender;
        ApartmentNo = apartmentNo;
        Address1 = address1;
        Address2 = address2;
        AddressCity = addressCity;
        AddressLatitude = addressLatitude;
        AddressLongitude = addressLongitude;
        AddressIsDefault = addressIsDefault;
        VatPercentage = vatPercentage;
        OrderItems = orderItems;
        DeliveryDate = deliveryDate;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getOrderRequestID() {
        return OrderRequestID;
    }

    public void setOrderRequestID(String orderRequestID) {
        OrderRequestID = orderRequestID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getOrderTrackID() {
        return OrderTrackID;
    }

    public void setOrderTrackID(String orderTrackID) {
        OrderTrackID = orderTrackID;
    }

    public String getOrderStatusID() {
        return OrderStatusID;
    }

    public void setOrderStatusID(String orderStatusID) {
        OrderStatusID = orderStatusID;
    }

    public String getBoothID() {
        return BoothID;
    }

    public void setBoothID(String boothID) {
        BoothID = boothID;
    }

    public String getOrderLastStatusID() {
        return OrderLastStatusID;
    }

    public void setOrderLastStatusID(String orderLastStatusID) {
        OrderLastStatusID = orderLastStatusID;
    }

    public String getOrderRequestVerificationCode() {
        return OrderRequestVerificationCode;
    }

    public void setOrderRequestVerificationCode(String orderRequestVerificationCode) {
        OrderRequestVerificationCode = orderRequestVerificationCode;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getActualDeliveryCharges() {
        return ActualDeliveryCharges;
    }

    public void setActualDeliveryCharges(String actualDeliveryCharges) {
        ActualDeliveryCharges = actualDeliveryCharges;
    }

    public String getAdditionalDeliveryCharges() {
        return AdditionalDeliveryCharges;
    }

    public void setAdditionalDeliveryCharges(String additionalDeliveryCharges) {
        AdditionalDeliveryCharges = additionalDeliveryCharges;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getVatPercentageApplied() {
        return VatPercentageApplied;
    }

    public void setVatPercentageApplied(String vatPercentageApplied) {
        VatPercentageApplied = vatPercentageApplied;
    }

    public String getVatAmountApplied() {
        return VatAmountApplied;
    }

    public void setVatAmountApplied(String vatAmountApplied) {
        VatAmountApplied = vatAmountApplied;
    }

    public String getGrandTotal() {
        return GrandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        GrandTotal = grandTotal;
    }

    public String getBoothImage() {
        return BoothImage;
    }

    public void setBoothImage(String boothImage) {
        BoothImage = boothImage;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getBoothEmail() {
        return BoothEmail;
    }

    public void setBoothEmail(String boothEmail) {
        BoothEmail = boothEmail;
    }

    public String getBoothMobile() {
        return BoothMobile;
    }

    public void setBoothMobile(String boothMobile) {
        BoothMobile = boothMobile;
    }

    public String getBoothName() {
        return BoothName;
    }

    public void setBoothName(String boothName) {
        BoothName = boothName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String userMobile) {
        UserMobile = userMobile;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getBoothCityTitle() {
        return BoothCityTitle;
    }

    public void setBoothCityTitle(String boothCityTitle) {
        BoothCityTitle = boothCityTitle;
    }

    public String getUserCityTitle() {
        return UserCityTitle;
    }

    public void setUserCityTitle(String userCityTitle) {
        UserCityTitle = userCityTitle;
    }

    public String getOrderReceivedAt() {
        return OrderReceivedAt;
    }

    public void setOrderReceivedAt(String orderReceivedAt) {
        OrderReceivedAt = orderReceivedAt;
    }

    public String getAddressTitle() {
        return AddressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        AddressTitle = addressTitle;
    }

    public String getRecipientName() {
        return RecipientName;
    }

    public void setRecipientName(String recipientName) {
        RecipientName = recipientName;
    }

    public String getAddressEmail() {
        return AddressEmail;
    }

    public void setAddressEmail(String addressEmail) {
        AddressEmail = addressEmail;
    }

    public String getAddressMobile() {
        return AddressMobile;
    }

    public void setAddressMobile(String addressMobile) {
        AddressMobile = addressMobile;
    }

    public String getAddressGender() {
        return AddressGender;
    }

    public void setAddressGender(String addressGender) {
        AddressGender = addressGender;
    }

    public String getApartmentNo() {
        return ApartmentNo;
    }

    public void setApartmentNo(String apartmentNo) {
        ApartmentNo = apartmentNo;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getAddressCity() {
        return AddressCity;
    }

    public void setAddressCity(String addressCity) {
        AddressCity = addressCity;
    }

    public String getAddressLatitude() {
        return AddressLatitude;
    }

    public void setAddressLatitude(String addressLatitude) {
        AddressLatitude = addressLatitude;
    }

    public String getAddressLongitude() {
        return AddressLongitude;
    }

    public void setAddressLongitude(String addressLongitude) {
        AddressLongitude = addressLongitude;
    }

    public String getAddressIsDefault() {
        return AddressIsDefault;
    }

    public void setAddressIsDefault(String addressIsDefault) {
        AddressIsDefault = addressIsDefault;
    }

    public String getVatPercentage() {
        return VatPercentage;
    }

    public void setVatPercentage(String vatPercentage) {
        VatPercentage = vatPercentage;
    }

    public ArrayList<OrderItemsData> getOrderItems() {
        return OrderItems;
    }

    public void setOrderItems(ArrayList<OrderItemsData> orderItems) {
        OrderItems = orderItems;
    }
}
