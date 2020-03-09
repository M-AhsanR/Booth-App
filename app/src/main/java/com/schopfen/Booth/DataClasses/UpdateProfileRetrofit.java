package com.schopfen.Booth.DataClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateProfileRetrofit {
    @SerializedName("FullName")
    @Expose
    private String FullName;
    @SerializedName("Bio")
    @Expose
    private String Bio;
    @SerializedName("UserID")
    @Expose
    private String UserID;
    @SerializedName("City")
    @Expose
    private String City;
    @SerializedName("Language")
    @Expose
    private String Language;
    @SerializedName("Phone")
    @Expose
    private String Phone;

    public UpdateProfileRetrofit(String fullName, String bio, String userID, String city, String language, String phone) {
        FullName = fullName;
        Bio = bio;
        UserID = userID;
        City = city;
        Language = language;
        Phone = phone;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
// entity.addPart("FullName", new StringBody(BaseClass.encode(name.getText().toString())));
////                entity.addPart("FullName", new StringBody(name.getText().toString()));
//                entity.addPart("Bio", new StringBody(BaseClass.encode(bio.getText().toString())));
//                entity.addPart("UserID", new StringBody(UserID));
//                entity.addPart("City", new StringBody(city.getText().toString()));
//                entity.addPart("CityID", new StringBody(CityID));
//                entity.addPart("Language", new StringBody(Language));
//                entity.addPart("Phone", new StringBody(BaseClass.encode(phone.getText().toString())));
//
//
//                Log.e("profile", profilebitmapArrayList.size() + "");
//                for (int k = 0; k < profilebitmapArrayList.size(); k++) {
//        Bitmap bitmap1 = profilebitmapArrayList.get(k);
//        Log.e("profile", profilebitmapArrayList.get(k) + "");
//        ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
//        bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, bos1);
//        byte[] data = bos1.toByteArray();
//        entity.addPart("Image", new ByteArrayBody(data,
//                "image/jpeg", params[1]));
//    }
//
//                for (int k = 0; k < profilebitmapArrayList.size(); k++) {
//        Bitmap bitmap1 = profilebitmapArrayList.get(k);
//        Log.e("profile", profilebitmapArrayList.get(k) + "");
//        ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
//        bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, bos1);
//        byte[] data = bos1.toByteArray();
//        entity.addPart("CompressedImage", new ByteArrayBody(data,
//                "image/jpeg", params[1]));
//    }
//
//                mEditor.putString("UserName", name.getText().toString()).commit();
////                }
//                Log.e("coverBitSize", coverbitmapArrayList.size() + "");
//                for (int j = 0; j < coverbitmapArrayList.size(); j++) {
//        Bitmap bitmap = coverbitmapArrayList.get(j);
//        Log.e("coverBit", coverbitmapArrayList.get(j) + "");
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
//        byte[] data = bos.toByteArray();
//        entity.addPart("CoverImage", new ByteArrayBody(data,
//                "image/jpeg", params[1]));