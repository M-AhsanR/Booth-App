package com.schopfen.Booth.Models;

public class BottomThemeStyleData {
    String BottomStyleImage;
    String BottomStyleTitleAr;
    String BottomStyleTitleEn;
    String BottomThemeID;
    String BottomThemeStyleID;

    public BottomThemeStyleData(String bottomStyleImage, String bottomStyleTitleAr, String bottomStyleTitleEn, String bottomThemeID, String bottomThemeStyleID) {
        BottomStyleImage = bottomStyleImage;
        BottomStyleTitleAr = bottomStyleTitleAr;
        BottomStyleTitleEn = bottomStyleTitleEn;
        BottomThemeID = bottomThemeID;
        BottomThemeStyleID = bottomThemeStyleID;
    }

    public String getBottomStyleImage() {
        return BottomStyleImage;
    }

    public void setBottomStyleImage(String bottomStyleImage) {
        BottomStyleImage = bottomStyleImage;
    }

    public String getBottomStyleTitleAr() {
        return BottomStyleTitleAr;
    }

    public void setBottomStyleTitleAr(String bottomStyleTitleAr) {
        BottomStyleTitleAr = bottomStyleTitleAr;
    }

    public String getBottomStyleTitleEn() {
        return BottomStyleTitleEn;
    }

    public void setBottomStyleTitleEn(String bottomStyleTitleEn) {
        BottomStyleTitleEn = bottomStyleTitleEn;
    }

    public String getBottomThemeID() {
        return BottomThemeID;
    }

    public void setBottomThemeID(String bottomThemeID) {
        BottomThemeID = bottomThemeID;
    }

    public String getBottomThemeStyleID() {
        return BottomThemeStyleID;
    }

    public void setBottomThemeStyleID(String bottomThemeStyleID) {
        BottomThemeStyleID = bottomThemeStyleID;
    }
}
