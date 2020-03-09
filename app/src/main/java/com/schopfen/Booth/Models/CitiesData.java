package com.schopfen.Booth.Models;

public class CitiesData {
    String CityID;
    String CityLat;
    String CityLong;
    String CityPlaceID;
    String Title;

    public CitiesData(String cityID, String cityLat, String cityLong, String cityPlaceID, String title) {
        CityID = cityID;
        CityLat = cityLat;
        CityLong = cityLong;
        CityPlaceID = cityPlaceID;
        Title = title;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public String getCityLat() {
        return CityLat;
    }

    public void setCityLat(String cityLat) {
        CityLat = cityLat;
    }

    public String getCityLong() {
        return CityLong;
    }

    public void setCityLong(String cityLong) {
        CityLong = cityLong;
    }

    public String getCityPlaceID() {
        return CityPlaceID;
    }

    public void setCityPlaceID(String cityPlaceID) {
        CityPlaceID = cityPlaceID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
