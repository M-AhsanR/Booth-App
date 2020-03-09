package com.schopfen.Booth.Models;

public class InboxModel {

    String ChatID;
    String ConversationSenderID;
    String ConversationSenderName;
    String ConversationSenderUserName;
    String ConversationSenderImage;
    String ConversationReceiverID;
    String ConversationReceiverName;
    String ConversationReceiverUserName;
    String ConversationReceiverImage;
    String Type;
    String ReceiverType;
    String ChatMessageID;
    String SenderID;
    String ReceiverID;
    String IsReadBySender;
    String IsReadByReceiver;
    String Image;
    String CompressedImage;
    String Message;
    String CreatedAt;
    String HasUnreadMessage;
    String UnreadMessageCount;

    public InboxModel(String chatID, String conversationSenderID, String conversationSenderName, String conversationSenderUserName, String conversationSenderImage, String conversationReceiverID, String conversationReceiverName, String conversationReceiverUserName, String conversationReceiverImage, String type, String receiverType, String chatMessageID, String senderID, String receiverID, String isReadBySender, String isReadByReceiver, String image, String compressedImage, String message, String createdAt, String hasUnreadMessage, String unreadMessageCount) {
        ChatID = chatID;
        ConversationSenderID = conversationSenderID;
        ConversationSenderName = conversationSenderName;
        ConversationSenderUserName = conversationSenderUserName;
        ConversationSenderImage = conversationSenderImage;
        ConversationReceiverID = conversationReceiverID;
        ConversationReceiverName = conversationReceiverName;
        ConversationReceiverUserName = conversationReceiverUserName;
        ConversationReceiverImage = conversationReceiverImage;
        Type = type;
        ReceiverType = receiverType;
        ChatMessageID = chatMessageID;
        SenderID = senderID;
        ReceiverID = receiverID;
        IsReadBySender = isReadBySender;
        IsReadByReceiver = isReadByReceiver;
        Image = image;
        CompressedImage = compressedImage;
        Message = message;
        CreatedAt = createdAt;
        HasUnreadMessage = hasUnreadMessage;
        UnreadMessageCount = unreadMessageCount;
    }

    public String getChatID() {
        return ChatID;
    }

    public void setChatID(String chatID) {
        ChatID = chatID;
    }

    public String getConversationSenderID() {
        return ConversationSenderID;
    }

    public void setConversationSenderID(String conversationSenderID) {
        ConversationSenderID = conversationSenderID;
    }

    public String getConversationSenderName() {
        return ConversationSenderName;
    }

    public void setConversationSenderName(String conversationSenderName) {
        ConversationSenderName = conversationSenderName;
    }

    public String getConversationSenderUserName() {
        return ConversationSenderUserName;
    }

    public void setConversationSenderUserName(String conversationSenderUserName) {
        ConversationSenderUserName = conversationSenderUserName;
    }

    public String getConversationSenderImage() {
        return ConversationSenderImage;
    }

    public void setConversationSenderImage(String conversationSenderImage) {
        ConversationSenderImage = conversationSenderImage;
    }

    public String getConversationReceiverID() {
        return ConversationReceiverID;
    }

    public void setConversationReceiverID(String conversationReceiverID) {
        ConversationReceiverID = conversationReceiverID;
    }

    public String getConversationReceiverName() {
        return ConversationReceiverName;
    }

    public void setConversationReceiverName(String conversationReceiverName) {
        ConversationReceiverName = conversationReceiverName;
    }

    public String getConversationReceiverUserName() {
        return ConversationReceiverUserName;
    }

    public void setConversationReceiverUserName(String conversationReceiverUserName) {
        ConversationReceiverUserName = conversationReceiverUserName;
    }

    public String getConversationReceiverImage() {
        return ConversationReceiverImage;
    }

    public void setConversationReceiverImage(String conversationReceiverImage) {
        ConversationReceiverImage = conversationReceiverImage;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getReceiverType() {
        return ReceiverType;
    }

    public void setReceiverType(String receiverType) {
        ReceiverType = receiverType;
    }

    public String getChatMessageID() {
        return ChatMessageID;
    }

    public void setChatMessageID(String chatMessageID) {
        ChatMessageID = chatMessageID;
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

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getHasUnreadMessage() {
        return HasUnreadMessage;
    }

    public void setHasUnreadMessage(String hasUnreadMessage) {
        HasUnreadMessage = hasUnreadMessage;
    }

    public String getUnreadMessageCount() {
        return UnreadMessageCount;
    }

    public void setUnreadMessageCount(String unreadMessageCount) {
        UnreadMessageCount = unreadMessageCount;
    }
}
