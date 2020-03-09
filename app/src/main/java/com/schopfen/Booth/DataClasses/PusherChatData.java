package com.schopfen.Booth.DataClasses;

public class PusherChatData {

    String Message;
    String ChatID;
    String SenderID;
    String CreatedAt;
    String ReceiverID;

    public PusherChatData(String message, String chatID, String senderID, String createdAt, String receiverID) {
        Message = message;
        ChatID = chatID;
        SenderID = senderID;
        CreatedAt = createdAt;
        ReceiverID = receiverID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
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

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }
}
