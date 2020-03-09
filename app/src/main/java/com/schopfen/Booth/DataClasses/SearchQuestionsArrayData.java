package com.schopfen.Booth.DataClasses;

import java.util.ArrayList;

public class SearchQuestionsArrayData {
    String profile_image;
    String user_name;
    String place;
    String time;
    String detail;
    String answer_count;
    ArrayList<String> images_list;
    String likes_count;
    String price_count;

    public String getPrice_count() {
        return price_count;
    }

    public void setPrice_count(String price_count) {
        this.price_count = price_count;
    }

    public String getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(String likes_count) {
        this.likes_count = likes_count;
    }

    public ArrayList<String> getImages_list() {
        return images_list;
    }

    public void setImages_list(ArrayList<String> images_list) {
        this.images_list = images_list;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAnswer_count() {
        return answer_count;
    }

    public void setAnswer_count(String answer_count) {
        this.answer_count = answer_count;
    }
}
