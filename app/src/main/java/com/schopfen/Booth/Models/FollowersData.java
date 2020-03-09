package com.schopfen.Booth.Models;

public class FollowersData {
    String BoothCoverImage;
    String BoothImage;
    String BoothName;
    String CityTitle;
    String CompressedBoothCoverImage;
    String CompressedBoothImage;
    String CompressedCoverImage;
    String CompressedImage;
    String FullName;
    String Image;
    String UserID;
    String UserName;
    String BoothUserName;
    String LastState;

    public FollowersData(String userName, String boothCoverImage, String boothImage, String boothName, String cityTitle, String compressedBoothCoverImage, String compressedBoothImage, String compressedCoverImage, String compressedImage, String fullName, String image, String userID, String boothUserName, String lastState) {
        BoothCoverImage = boothCoverImage;
        BoothImage = boothImage;
        BoothName = boothName;
        CityTitle = cityTitle;
        CompressedBoothCoverImage = compressedBoothCoverImage;
        CompressedBoothImage = compressedBoothImage;
        CompressedCoverImage = compressedCoverImage;
        CompressedImage = compressedImage;
        FullName = fullName;
        Image = image;
        UserID = userID;
        UserName = userName;
        BoothUserName = boothUserName;
        LastState = lastState;
    }

    public String getLastState() {
        return LastState;
    }

    public void setLastState(String lastState) {
        LastState = lastState;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBoothCoverImage() {
        return BoothCoverImage;
    }

    public void setBoothCoverImage(String boothCoverImage) {
        BoothCoverImage = boothCoverImage;
    }

    public String getBoothImage() {
        return BoothImage;
    }

    public void setBoothImage(String boothImage) {
        BoothImage = boothImage;
    }

    public String getBoothName() {
        return BoothName;
    }

    public void setBoothName(String boothName) {
        BoothName = boothName;
    }

    public String getCityTitle() {
        return CityTitle;
    }

    public void setCityTitle(String cityTitle) {
        CityTitle = cityTitle;
    }

    public String getCompressedBoothCoverImage() {
        return CompressedBoothCoverImage;
    }

    public void setCompressedBoothCoverImage(String compressedBoothCoverImage) {
        CompressedBoothCoverImage = compressedBoothCoverImage;
    }

    public String getCompressedBoothImage() {
        return CompressedBoothImage;
    }

    public void setCompressedBoothImage(String compressedBoothImage) {
        CompressedBoothImage = compressedBoothImage;
    }

    public String getCompressedCoverImage() {
        return CompressedCoverImage;
    }

    public void setCompressedCoverImage(String compressedCoverImage) {
        CompressedCoverImage = compressedCoverImage;
    }

    public String getCompressedImage() {
        return CompressedImage;
    }

    public void setCompressedImage(String compressedImage) {
        CompressedImage = compressedImage;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
