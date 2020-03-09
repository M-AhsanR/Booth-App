package com.schopfen.Booth.ApiStructure;

import java.util.Base64;

/**
 * Created by M on 3/7/2018.
 */

public interface Constants {

    interface URL {
        String BASE_URL = "https://baac.booth-in.com/api/";
        String IMG_URL = "https://baac.booth-in.com/";

        String LOGIN = BASE_URL + "login";
        String SIGNUP = BASE_URL + "signUp";
        String GENERATETOKEN = BASE_URL + "generateTokenByApi";
        String CITIES = BASE_URL + "cities";
        String getUserDetail = BASE_URL + "getUserDetail?UserID=";
        String forgotPassword = BASE_URL + "forgotPassword";
        String logout = BASE_URL + "logout?UserID=";
        String updateProfile = BASE_URL + "updateProfile";
        String getNotifications = BASE_URL + "getNotifications?UserID=";
        String suggestedBooths = BASE_URL + "suggestedBooths";
        String follow = BASE_URL + "follow";
        String about_us = BASE_URL + "getPageDetail?";
        String about_usTerms = BASE_URL + "getPageDetail?";
        String categories = BASE_URL + "categories?";
        String themes = BASE_URL + "themes?UserID=";
        String giveFeedback = BASE_URL + "giveFeedback";
        String setupboothprofile = BASE_URL + "customizeProfile";
        String ADDPRODUCT = BASE_URL + "addProduct";
        String updateProduct = BASE_URL + "updateProduct";
        String getFollowers = BASE_URL + "getFollowers?UserID=";
        String products = BASE_URL + "products";
        String HomeProducts = BASE_URL + "productsRegardingCategories";
        String PRODUCT_DETAILS = BASE_URL + "productDetail?UserID=";
        String QUESTION_DETAILS = BASE_URL + "getQuestions";
        String getFollowing = BASE_URL + "getFollowing?UserID=";
        String BoothProducts = BASE_URL + "productsRegardingBooth";
        String ReportProduct = BASE_URL + "reportProduct";
        String ReportUser = BASE_URL + "reportUser";
        String BlockUser = BASE_URL + "blockUser";
        String LikeProduct = BASE_URL + "likeProduct";
        String AddToCart = BASE_URL + "addToCart";
        String GetCartItems = BASE_URL + "getCartItems?UserID=";
        String WishListLikesETC = BASE_URL+"getUserOrdersLikesWishlist?UserID=";
        String addToWishlist = BASE_URL + "addToWishlist";
        String deleteFromCart = BASE_URL+"deleteCartItem?UserID=";
        String updateCartQuantity  = BASE_URL +"updateCart";
        String addAddressUrl = BASE_URL+"addAddress";
        String deleteProduct = BASE_URL+"deleteProduct";
        String getAllAddress = BASE_URL+"getAllAddress?UserID=";
        String deleteAddress = BASE_URL + "deleteAddress";
        String ask_A_Question = BASE_URL+"askQuestion";
        String PLACE_ORDER = BASE_URL+"placeOrder";
        String ORDER_ITEMS = BASE_URL+"orderItems";
        String PRODUCTCOMMENT = BASE_URL+"productComment";
        String GETCATEGORIES = BASE_URL+"getCategoriesForUser";
        String GETQUESTIONCOMMENTS = BASE_URL+"getQuestionComments";
        String QUESTIONCOMMENT = BASE_URL+"questionComment";
        String DELETEQUESTION = BASE_URL+"deleteQuestion";
        String DELETEPRODUCT = BASE_URL+"deleteProduct";
        String REPORTQUESTION = BASE_URL+"reportQuestion";
        String CHANGEPASSWORD = BASE_URL+"changePassword";
        String GETPRODUCTCOMMENTS = BASE_URL+"getProductComments";
        String GETORDERS = BASE_URL + "getOrders";
        String UPDATEORDERSTATE = BASE_URL + "updateOrderStatus";
        String getOrderRequestDetail = BASE_URL + "getOrderRequestDetail";
        String GETQUESTIONS = BASE_URL + "getQuestions";
        String STARTCHAT = BASE_URL+"startChat";
        String DELETECHATROOM = BASE_URL+"deleteChatRoom?UserID=";
        String SENDMESSAGE = BASE_URL+"sendMessage";
        String APPLYCODE = BASE_URL+"applyCoupon";
        String PROMOTEDPRODUCT = BASE_URL+"getBoothPromotedProduct";
        String PROMOTEMYPRODUCT = BASE_URL+"promoteMyProduct";
        String INVITE = BASE_URL+"invite";
        String INBOX = BASE_URL+"getChatRooms";
        String VIDEOUPLOAD = BASE_URL+"updateVideoForProduct";
        String GETFRIENDSACTIVITIES = BASE_URL+"getFriendsActivities?UserID=";
        String search = BASE_URL + "search";
        String getProductsForSearchGrid = BASE_URL + "getProductsForSearchGrid";
        String getPromoCodeDisclaimer = BASE_URL + "getPromoCodeDisclaimer?UserID=";
        String createPromoCode = BASE_URL + "createPromoCode";
        String getPromoCodes = BASE_URL + "getPromoCodes";
        String cancellation_reasons = BASE_URL + "cancellation_reasons?UserID=";
        String rateUserForOrderRequest = BASE_URL + "rateUserForOrderRequest";
        String rateOrderRequest = BASE_URL + "rateOrderRequest";
        String rateProduct = BASE_URL + "rateProduct";
        String markAllMsgReadForChat = BASE_URL + "markAllMsgReadForChat";
        String markNotificationAsRead = BASE_URL + "markNotificationAsRead?UserID=";

        String BLOCKEDUSERS = BASE_URL + "blockedUsers?UserID=";
        String SENDOTP = BASE_URL + "sendOTP";
        String VERIFYOTP = BASE_URL + "verifyOTP";
        String GETPRODUCTDISCLAIMER = BASE_URL + "getProductDisclaimer";
        String getUserRatings = BASE_URL + "getUserRatings";
        String getProductRatings = BASE_URL + "getProductRatings";
        String getBoothRatings = BASE_URL + "getBoothRatings";
        String TRANSACTIONHISTORY = BASE_URL+"getPointsHistory";
        String deleteProductComment = BASE_URL+"deleteProductComment";
        String deletequestionComment = BASE_URL+"deletequestionComment";
        String reportComment = BASE_URL+"reportComment";
        String deletePromoCode = BASE_URL+"deletePromoCode";
    }
}
