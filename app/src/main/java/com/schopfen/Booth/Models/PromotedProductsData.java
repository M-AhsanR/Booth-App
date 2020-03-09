package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class PromotedProductsData {
    String BoothImage;
    String CategoryID;
    String CategoryName;
    String CityName;
    String CommentCount;
    String CompressedBoothImage;
    String Currency;
    String CurrencySymbol;
    String BoothName;
    String LikesCount;
    String OutOfStock;
    String ProductDescription;
    String ProductID;
    String ProductPrice;
    String ProductType;
    String ProductVideoThumbnail;
    String ProductVideo;
    String Title;
    String UserID;
    String UserName;
    String SubCategoryName;
    String SubSubCategoryName;
    String ProductBrandName;
    String CreatedAt;
    String Type;
    String PromotionImage;
    String PromotionDescription;
    ArrayList<ProductImagesData> productImagesData;
    String BoothUserName;
    String PromotionType;
    String PromotionUrl;

    public PromotedProductsData(String promotionType, String promotionUrl, String promotionDescription, String promotionImage, String boothImage, String categoryID, String categoryName, String cityName, String commentCount, String compressedBoothImage, String currency, String currencySymbol, String boothName, String likesCount, String outOfStock, String productDescription, String productID, String productPrice, String productType, String productVideoThumbnail, String productVideo, String title, String userID, String userName, String subCategoryName, String subSubCategoryName, String productBrandName, String createdAt, String type, ArrayList<ProductImagesData> productImagesData, String boothUserName) {
        PromotionType = promotionType;
        PromotionUrl = promotionUrl;
        BoothImage = boothImage;
        CategoryID = categoryID;
        CategoryName = categoryName;
        CityName = cityName;
        CommentCount = commentCount;
        CompressedBoothImage = compressedBoothImage;
        Currency = currency;
        CurrencySymbol = currencySymbol;
        BoothName = boothName;
        LikesCount = likesCount;
        OutOfStock = outOfStock;
        ProductDescription = productDescription;
        ProductID = productID;
        ProductPrice = productPrice;
        ProductType = productType;
        ProductVideoThumbnail = productVideoThumbnail;
        ProductVideo = productVideo;
        Title = title;
        UserID = userID;
        UserName = userName;
        SubCategoryName = subCategoryName;
        SubSubCategoryName = subSubCategoryName;
        ProductBrandName = productBrandName;
        CreatedAt = createdAt;
        PromotionImage = promotionImage;
        Type = type;
        PromotionDescription = promotionDescription;
        this.productImagesData = productImagesData;
        BoothUserName = boothUserName;
    }

    public String getPromotionType() {
        return PromotionType;
    }

    public void setPromotionType(String promotionType) {
        PromotionType = promotionType;
    }

    public String getPromotionUrl() {
        return PromotionUrl;
    }

    public void setPromotionUrl(String promotionUrl) {
        PromotionUrl = promotionUrl;
    }

    public String getBoothUserName() {
        return BoothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        BoothUserName = boothUserName;
    }

    public String getPromotionDescription() {
        return PromotionDescription;
    }

    public void setPromotionDescription(String promotionDescription) {
        PromotionDescription = promotionDescription;
    }

    public String getBoothName() {
        return BoothName;
    }

    public void setBoothName(String boothName) {
        BoothName = boothName;
    }

    public String getPromotionImage() {
        return PromotionImage;
    }

    public void setPromotionImage(String promotionImage) {
        PromotionImage = promotionImage;
    }

    public String getProductVideo() {
        return ProductVideo;
    }

    public void setProductVideo(String productVideo) {
        ProductVideo = productVideo;
    }

    public String getBoothImage() {
        return BoothImage;
    }

    public void setBoothImage(String boothImage) {
        BoothImage = boothImage;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(String commentCount) {
        CommentCount = commentCount;
    }

    public String getCompressedBoothImage() {
        return CompressedBoothImage;
    }

    public void setCompressedBoothImage(String compressedBoothImage) {
        CompressedBoothImage = compressedBoothImage;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getCurrencySymbol() {
        return CurrencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        CurrencySymbol = currencySymbol;
    }

    public String getLikesCount() {
        return LikesCount;
    }

    public void setLikesCount(String likesCount) {
        LikesCount = likesCount;
    }

    public String getOutOfStock() {
        return OutOfStock;
    }

    public void setOutOfStock(String outOfStock) {
        OutOfStock = outOfStock;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }

    public String getProductVideoThumbnail() {
        return ProductVideoThumbnail;
    }

    public void setProductVideoThumbnail(String productVideoThumbnail) {
        ProductVideoThumbnail = productVideoThumbnail;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSubCategoryName() {
        return SubCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        SubCategoryName = subCategoryName;
    }

    public String getSubSubCategoryName() {
        return SubSubCategoryName;
    }

    public void setSubSubCategoryName(String subSubCategoryName) {
        SubSubCategoryName = subSubCategoryName;
    }

    public String getProductBrandName() {
        return ProductBrandName;
    }

    public void setProductBrandName(String productBrandName) {
        ProductBrandName = productBrandName;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public ArrayList<ProductImagesData> getProductImagesData() {
        return productImagesData;
    }

    public void setProductImagesData(ArrayList<ProductImagesData> productImagesData) {
        this.productImagesData = productImagesData;
    }
}
