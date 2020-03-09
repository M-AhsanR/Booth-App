package com.schopfen.Booth.Models;

public class Chat_Data {

    String ChatMessageID;
    String ChatID;
    String SenderID;
    String ReceiverID;
    String UserType;
    String Message;
    String Image;
    String CompressedImage;
    String VideoThumbnail;
    String Video;
    String IsReadBySender;
    String IsReadByReceiver;
    String CreatedAt;

    public Chat_Data(String chatMessageID, String chatID, String senderID, String receiverID, String userType, String message, String image, String compressedImage, String videoThumbnail, String video, String isReadBySender, String isReadByReceiver, String createdAt) {
        ChatMessageID = chatMessageID;
        ChatID = chatID;
        SenderID = senderID;
        ReceiverID = receiverID;
        UserType = userType;
        Message = message;
        Image = image;
        CompressedImage = compressedImage;
        VideoThumbnail = videoThumbnail;
        Video = video;
        IsReadBySender = isReadBySender;
        IsReadByReceiver = isReadByReceiver;
        CreatedAt = createdAt;
    }

    public String getChatMessageID() {
        return ChatMessageID;
    }

    public void setChatMessageID(String chatMessageID) {
        ChatMessageID = chatMessageID;
    }

    public String getChatID() {
        return ChatID;
    }

    public void setChatID(String chatID) {
        ChatID = chatID;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCompressedImage() {
        return CompressedImage;
    }

    public void setCompressedImage(String compressedImage) {
        CompressedImage = compressedImage;
    }

    public String getVideoThumbnail() {
        return VideoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        VideoThumbnail = videoThumbnail;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public String getIsReadBySender() {
        return IsReadBySender;
    }

    public void setIsReadBySender(String isReadBySender) {
        IsReadBySender = isReadBySender;
    }

    public String getIsReadByReceiver() {
        return IsReadByReceiver;
    }

    public void setIsReadByReceiver(String isReadByReceiver) {
        IsReadByReceiver = isReadByReceiver;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }
}
