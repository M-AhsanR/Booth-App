package com.schopfen.Booth.Models;

import com.pusher.client.channel.User;

import java.util.ArrayList;
import java.util.Arrays;

public class CommentsModel {

    String CommentedAs;
    String userID;
    String comment;
    String createdAt;
    String image;
    String boothimage;
    String userName;
    String city;
    String BoothUserName;
    String commentID;

    ArrayList<MentionedUsersInfo> mentionedUsersInfos;

    public CommentsModel(String CommentID, String BoothImage, String commentedAs, String userID, String comment, String createdAt, String image, String userName, String city, ArrayList<MentionedUsersInfo> mentionedUsersInfos, String boothUserName) {
        CommentedAs = commentedAs;
        this.userID = userID;
        this.comment = comment;
        this.createdAt = createdAt;
        this.image = image;
        this.userName = userName;
        this.city = city;
        this.mentionedUsersInfos = mentionedUsersInfos;
        this.BoothUserName = boothUserName;
        this.boothimage = BoothImage;
        this.commentID = CommentID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getBoothimage() {
        return boothimage;
    }

    public void setBoothimage(String boothimage) {
        this.boothimage = boothimage;
    }

    public ArrayList<MentionedUsersInfo> getMentionedUsersInfos() {
        return mentionedUsersInfos;
    }

    public void setMentionedUsersInfos(ArrayList<MentionedUsersInfo> mentionedUsersInfos) {
        this.mentionedUsersInfos = mentionedUsersInfos;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getCommentedAs() {
        return CommentedAs;
    }

    public void setCommentedAs(String commentedAs) {
        CommentedAs = commentedAs;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
