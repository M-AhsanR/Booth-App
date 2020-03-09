package com.schopfen.Booth.Social;

public class MainCatData {

    String img_url,itemTitle,subTitle;

    public MainCatData(String img_url, String itemTitle, String subTitle) {
        this.img_url = img_url;
        this.itemTitle = itemTitle;
        this.subTitle = subTitle;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
