package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class MainChatData {
    String date;
    ArrayList<Chat_Data> chat_data;

    public MainChatData(String date, ArrayList<Chat_Data> chat_data) {
        this.date = date;
        this.chat_data = chat_data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Chat_Data> getChat_data() {
        return chat_data;
    }

    public void setChat_data(ArrayList<Chat_Data> chat_data) {
        this.chat_data = chat_data;
    }
}
