package com.schopfen.Booth.Social;

public class CheckOutForProductsRVModel {

    String pro_title,num_items,price;

    public CheckOutForProductsRVModel(String pro_title, String num_items, String price) {
        this.pro_title = pro_title;
        this.num_items = num_items;
        this.price = price;
    }

    public String getPro_title() {
        return pro_title;
    }

    public void setPro_title(String pro_title) {
        this.pro_title = pro_title;
    }

    public String getNum_items() {
        return num_items;
    }

    public void setNum_items(String num_items) {
        this.num_items = num_items;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
