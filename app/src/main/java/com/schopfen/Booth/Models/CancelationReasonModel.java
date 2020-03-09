package com.schopfen.Booth.Models;

public class CancelationReasonModel {

    String OrderCancellationReasonID;
    String CancellationReasonEn;
    String CancellationReasonAr;
    String IsActive;

    public CancelationReasonModel(String orderCancellationReasonID, String cancellationReasonEn, String cancellationReasonAr, String isActive) {
        OrderCancellationReasonID = orderCancellationReasonID;
        CancellationReasonEn = cancellationReasonEn;
        CancellationReasonAr = cancellationReasonAr;
        IsActive = isActive;
    }

    public String getOrderCancellationReasonID() {
        return OrderCancellationReasonID;
    }

    public void setOrderCancellationReasonID(String orderCancellationReasonID) {
        OrderCancellationReasonID = orderCancellationReasonID;
    }

    public String getCancellationReasonEn() {
        return CancellationReasonEn;
    }

    public void setCancellationReasonEn(String cancellationReasonEn) {
        CancellationReasonEn = cancellationReasonEn;
    }

    public String getCancellationReasonAr() {
        return CancellationReasonAr;
    }

    public void setCancellationReasonAr(String cancellationReasonAr) {
        CancellationReasonAr = cancellationReasonAr;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }
}
