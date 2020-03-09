package com.schopfen.Booth.Models;

public class MessageModel {
    String ReceiverType;

    public MessageModel(String receiverType) {
        ReceiverType = receiverType;
    }

    public String getReceiverType() {
        return ReceiverType;
    }

    public void setReceiverType(String receiverType) {
        ReceiverType = receiverType;
    }
}
