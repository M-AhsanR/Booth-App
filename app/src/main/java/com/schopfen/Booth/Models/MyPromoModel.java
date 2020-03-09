package com.schopfen.Booth.Models;

public class MyPromoModel {

    String CouponID;
    String UserID;
    String CouponCode;
    String DiscountType;
    String DiscountFactor;
    String ExpiryDate;
    String UsageCount;
    String TotalCount;
    String IsActive;
    String CreatedAt;
    String CouponTextID;
    String Title;
    String SystemLanguageID;

    public MyPromoModel(String couponID, String userID, String couponCode, String discountType, String discountFactor, String expiryDate, String usageCount, String totalCount, String isActive, String createdAt, String couponTextID, String title, String systemLanguageID) {
        CouponID = couponID;
        UserID = userID;
        CouponCode = couponCode;
        DiscountType = discountType;
        DiscountFactor = discountFactor;
        ExpiryDate = expiryDate;
        UsageCount = usageCount;
        TotalCount = totalCount;
        IsActive = isActive;
        CreatedAt = createdAt;
        CouponTextID = couponTextID;
        Title = title;
        SystemLanguageID = systemLanguageID;
    }

    public String getCouponID() {
        return CouponID;
    }

    public void setCouponID(String couponID) {
        CouponID = couponID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCouponCode() {
        return CouponCode;
    }

    public void setCouponCode(String couponCode) {
        CouponCode = couponCode;
    }

    public String getDiscountType() {
        return DiscountType;
    }

    public void setDiscountType(String discountType) {
        DiscountType = discountType;
    }

    public String getDiscountFactor() {
        return DiscountFactor;
    }

    public void setDiscountFactor(String discountFactor) {
        DiscountFactor = discountFactor;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getUsageCount() {
        return UsageCount;
    }

    public void setUsageCount(String usageCount) {
        UsageCount = usageCount;
    }

    public String getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(String totalCount) {
        TotalCount = totalCount;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getCouponTextID() {
        return CouponTextID;
    }

    public void setCouponTextID(String couponTextID) {
        CouponTextID = couponTextID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSystemLanguageID() {
        return SystemLanguageID;
    }

    public void setSystemLanguageID(String systemLanguageID) {
        SystemLanguageID = systemLanguageID;
    }
}
