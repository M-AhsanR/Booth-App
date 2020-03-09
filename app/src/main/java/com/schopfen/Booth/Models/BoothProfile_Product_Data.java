package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class BoothProfile_Product_Data {

    String product_name;
    ArrayList<ProductImagesModel> product_image;
    String product_id;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public ArrayList<ProductImagesModel> getProduct_image() {
        return product_image;
    }

    public void setProduct_image(ArrayList<ProductImagesModel> product_image) {
        this.product_image = product_image;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}
