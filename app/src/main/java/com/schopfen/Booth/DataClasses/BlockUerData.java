package com.schopfen.Booth.DataClasses;

import com.pusher.client.channel.User;

public class BlockUerData {

    String userID;
    String userName;
    String compressedImage;
    String boothCompressImage;
    String Type;
    String BoothUserName;
    String BlockedAt;
    String CityID;

    public BlockUerData(String cityID, String blockedAt, String userID, String UserName, String compressedImage, String boothCompressImage, String type, String boothUserName) {
        this.userID = userID;
        this.compressedImage = compressedImage;
        this.boothCompressImage = boothCompressImage;
        Type = type;
        userName = UserName;
        BoothUserName = boothUserName;
        BlockedAt = blockedAt;
        CityID = cityID;
    }

    public String getBlockedAt() {
        return BlockedAt;
    }

    public void setBlockedAt(String blockedAt) {
        BlockedAt = blockedAt;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCompressedImage() {
        return compressedImage;
    }

    public void setCompressedImage(String compressedImage) {
        this.compressedImage = compressedImage;
    }

    public String getBoothCompressImage() {
        return boothCompressImage;
    }

    public void setBoothCompressImage(String boothCompressImage) {
        this.boothCompressImage = boothCompressImage;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
