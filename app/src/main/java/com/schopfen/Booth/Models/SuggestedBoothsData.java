package com.schopfen.Booth.Models;

public class SuggestedBoothsData {
    String BoothCoverImage;
    String BoothImage;
    String BoothName;
    String BoothType;
    String CityID;
    String CompressedBoothCoverImage;
    String CompressedBoothImage;
    String CompressedCoverImage;
    String CompressedImage;
    String OnlineStatus;
    String CityTitle;
    String UserID;

    public SuggestedBoothsData(String boothCoverImage, String boothImage, String boothName, String boothType, String cityID, String compressedBoothCoverImage, String compressedBoothImage, String compressedCoverImage, String compressedImage, String onlineStatus, String cityTitle, String userID) {
        BoothCoverImage = boothCoverImage;
        BoothImage = boothImage;
        BoothName = boothName;
        BoothType = boothType;
        CityID = cityID;
        CompressedBoothCoverImage = compressedBoothCoverImage;
        CompressedBoothImage = compressedBoothImage;
        CompressedCoverImage = compressedCoverImage;
        CompressedImage = compressedImage;
        OnlineStatus = onlineStatus;
        CityTitle = cityTitle;
        UserID = userID;
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

    public String getBoothType() {
        return BoothType;
    }

    public void setBoothType(String boothType) {
        BoothType = boothType;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
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

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
    }

    public String getCityTitle() {
        return CityTitle;
    }

    public void setCityTitle(String cityTitle) {
        CityTitle = cityTitle;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
