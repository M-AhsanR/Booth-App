package com.schopfen.Booth.Models;

import java.util.ArrayList;

public class ProductsData {
    String questionID;
    String itemType;
    String BoothImage;
    String CategoryID;
    String CategoryName;
    String CityName;
    String CommentCount;
    String CompressedBoothImage;
    String Currency;
    String CurrencySymbol;
    String DeliveryTime;
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
    String IsLiked;
    ArrayList<ProductImagesData> productImagesData;
    String boothUserName;
    String IsPromotionApproved;

    public ProductsData(String QuestionID, String ItemType, String boothImage, String categoryID, String categoryName, String cityName, String commentCount, String compressedBoothImage, String currency, String currencySymbol, String deliveryTime, String likesCount, String outOfStock, String productDescription, String productID, String productPrice, String productType, String productVideoThumbnail, String productVideo, String title, String userID, String userName, String subCategoryName, String subSubCategoryName, String productBrandName, String createdAt, String type, ArrayList<ProductImagesData> productImagesData, String isLiked, String BoothUserName, String isPromotionApproved) {
        questionID = QuestionID;
        itemType = ItemType;
        BoothImage = boothImage;
        CategoryID = categoryID;
        CategoryName = categoryName;
        CityName = cityName;
        CommentCount = commentCount;
        CompressedBoothImage = compressedBoothImage;
        Currency = currency;
        CurrencySymbol = currencySymbol;
        DeliveryTime = deliveryTime;
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
        Type = type;
        this.productImagesData = productImagesData;
        IsLiked = isLiked;
        boothUserName = BoothUserName;
        IsPromotionApproved = isPromotionApproved;
    }

    public String getIsPromotionApproved() {
        return IsPromotionApproved;
    }

    public void setIsPromotionApproved(String isPromotionApproved) {
        IsPromotionApproved = isPromotionApproved;
    }

    public String getBoothUserName() {
        return boothUserName;
    }

    public void setBoothUserName(String boothUserName) {
        this.boothUserName = boothUserName;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        itemType = itemType;
    }

    public String getIsLiked() {
        return IsLiked;
    }

    public void setIsLiked(String isLiked) {
        this.IsLiked = isLiked;
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

    public String getDeliveryTime() {
        return DeliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        DeliveryTime = deliveryTime;
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
