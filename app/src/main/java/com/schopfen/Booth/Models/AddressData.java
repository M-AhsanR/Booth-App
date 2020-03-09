package com.schopfen.Booth.Models;

public class AddressData {
    String Address1;
    String Address2;
    String AddressID;
    String AddressTitle;
    String ApartementNo;
    String City;
    String Email;
    String Gender;
    String IsDefault;
    String Mobile;
    String RecipientName;
    String UserID;

    public AddressData(String address1, String address2, String addressID, String addressTitle, String apartementNo, String city, String email, String gender, String isDefault, String mobile, String recipientName, String userID) {
        Address1 = address1;
        Address2 = address2;
        AddressID = addressID;
        AddressTitle = addressTitle;
        ApartementNo = apartementNo;
        City = city;
        Email = email;
        Gender = gender;
        IsDefault = isDefault;
        Mobile = mobile;
        RecipientName = recipientName;
        UserID = userID;
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

    public String getAddressID() {
        return AddressID;
    }

    public void setAddressID(String addressID) {
        AddressID = addressID;
    }

    public String getAddressTitle() {
        return AddressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        AddressTitle = addressTitle;
    }

    public String getApartementNo() {
        return ApartementNo;
    }

    public void setApartementNo(String apartementNo) {
        ApartementNo = apartementNo;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(String isDefault) {
        IsDefault = isDefault;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getRecipientName() {
        return RecipientName;
    }

    public void setRecipientName(String recipientName) {
        RecipientName = recipientName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
