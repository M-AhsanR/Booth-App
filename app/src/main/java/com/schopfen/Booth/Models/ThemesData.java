package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class ThemesData {
    String Color;
    String Image;
    String ThemeID;
    ArrayList<TopThemeStyleData> topThemeStyleData;

    public ThemesData(String color, String image, String themeID, ArrayList<TopThemeStyleData> topThemeStyleData) {
        Color = color;
        Image = image;
        ThemeID = themeID;
        this.topThemeStyleData = topThemeStyleData;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getThemeID() {
        return ThemeID;
    }

    public void setThemeID(String themeID) {
        ThemeID = themeID;
    }

    public ArrayList<TopThemeStyleData> getTopThemeStyleData() {
        return topThemeStyleData;
    }

    public void setTopThemeStyleData(ArrayList<TopThemeStyleData> topThemeStyleData) {
        this.topThemeStyleData = topThemeStyleData;
    }
}
