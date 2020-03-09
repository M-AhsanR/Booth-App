package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class TopThemeStyleData {
    String TopStyleImage;
    String TopStyleTitleAr;
    String TopStyleTitleEn;
    String TopThemeID;
    String TopThemeStyleID;
    ArrayList<BottomThemeStyleData> BottomArrayList;

    public TopThemeStyleData(String topStyleImage, String topStyleTitleAr, String topStyleTitleEn, String topThemeID, String topThemeStyleID, ArrayList<BottomThemeStyleData> bottomArrayList) {
        TopStyleImage = topStyleImage;
        TopStyleTitleAr = topStyleTitleAr;
        TopStyleTitleEn = topStyleTitleEn;
        TopThemeID = topThemeID;
        TopThemeStyleID = topThemeStyleID;
        BottomArrayList = bottomArrayList;
    }

    public String getTopStyleImage() {
        return TopStyleImage;
    }

    public void setTopStyleImage(String topStyleImage) {
        TopStyleImage = topStyleImage;
    }

    public String getTopStyleTitleAr() {
        return TopStyleTitleAr;
    }

    public void setTopStyleTitleAr(String topStyleTitleAr) {
        TopStyleTitleAr = topStyleTitleAr;
    }

    public String getTopStyleTitleEn() {
        return TopStyleTitleEn;
    }

    public void setTopStyleTitleEn(String topStyleTitleEn) {
        TopStyleTitleEn = topStyleTitleEn;
    }

    public String getTopThemeID() {
        return TopThemeID;
    }

    public void setTopThemeID(String topThemeID) {
        TopThemeID = topThemeID;
    }

    public String getTopThemeStyleID() {
        return TopThemeStyleID;
    }

    public void setTopThemeStyleID(String topThemeStyleID) {
        TopThemeStyleID = topThemeStyleID;
    }

    public ArrayList<BottomThemeStyleData> getBottomArrayList() {
        return BottomArrayList;
    }

    public void setBottomArrayList(ArrayList<BottomThemeStyleData> bottomArrayList) {
        BottomArrayList = bottomArrayList;
    }
}
