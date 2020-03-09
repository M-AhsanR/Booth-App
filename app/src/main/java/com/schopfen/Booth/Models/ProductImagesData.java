package com.schopfen.Booth.Models;

import java.io.Serializable;

public class ProductImagesData implements Serializable {
    String ProductCompressedImage;
    String ProductImage;

    public ProductImagesData(String productCompressedImage, String productImage) {
        ProductCompressedImage = productCompressedImage;
        ProductImage = productImage;
    }

    public String getProductCompressedImage() {
        return ProductCompressedImage;
    }

    public void setProductCompressedImage(String productCompressedImage) {
        ProductCompressedImage = productCompressedImage;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }
}
