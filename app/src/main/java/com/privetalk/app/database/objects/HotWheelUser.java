package com.privetalk.app.database.objects;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.utilities.PriveTalkConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by zachariashad on 26/02/16.
 */
public class HotWheelUser {

    public int userID;
    public String userName;
    public AttributesObject gender;
    public AttributesObject lookingFor;
    public long userBirthdate;
    public int userAge;
    public boolean socialVerified;
    public boolean royalUser;
    public boolean photosVerified;
    public ArrayList<ProfilePhoto> profilePhotos;
    public RealLocation realLocation;


    public HotWheelUser() {

    }

    public HotWheelUser(JSONObject obj) {

        JSONArray photoArray;
        profilePhotos = new ArrayList<>();

        this.userID = obj.optInt("id");
        this.userName = obj.optString("name");
        this.gender = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"), obj.optString("gender"));
        this.lookingFor = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"), obj.optString("looking_for"));
        try {
            this.userBirthdate = PriveTalkConstants.BIRTHDAY_FORMAT.parse(obj.optString("birthday")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userAge = obj.optInt("age");
        this.socialVerified = obj.optBoolean("social_verified");
        this.photosVerified = obj.optBoolean("photos_verified");
        this.royalUser = obj.optBoolean("royal_user");

        if (obj.optJSONObject("real_location") != null)
            this.realLocation = new RealLocation(obj.optJSONObject("real_location"));


        photoArray = obj.optJSONArray("profile_photos");
        for (int i = 0; i < photoArray.length(); i++) {
            ProfilePhoto photo = new ProfilePhoto(photoArray.optJSONObject(i));
            if (photo.isMainProfilePhoto)
                profilePhotos.add(0, new ProfilePhoto(photoArray.optJSONObject(i)));
            else
                profilePhotos.add(new ProfilePhoto(photoArray.optJSONObject(i)));
        }

    }
//
//    public HotWheelUser(Cursor cursor) {
//
//        this.realLocation = new RealLocation();
////        this.profilePhoto = new ProfilePhoto();
//
//        this.userID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.MATCH_ID));
//        this.userName = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.NAME));
//        this.gender = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"),
//                String.valueOf(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.GENDER))));
//        this.lookingFor = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"),
//                String.valueOf(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.LOOKING_FOR))));
//
//        this.userBirthdate = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.BIRTDATE));
//        this.userAge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.AGE));
//        this.socialVerified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.SOCIAL_VERIFIED)) == 1;
//        this.royalUser = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.ROYAL_USER)) == 1;
//        this.photosVerified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.PHOTOS_VERIFIED)) == 1;
//        this.realLocation.latitude = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.LATITUDE));
//        this.realLocation.longitute = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.LONGITUDE));
//        this.realLocation.administrativeArea = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.ADMIN_AREA));
//        this.realLocation.postalCode = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.POSTAL_CODE));
//        this.realLocation.lastUpdate = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.LAST_LOCATION_UPDATE));
//        this.realLocation.isoCountryCode = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotWheelTable.ISO_COUNTRY_CODE));
//
//    }
//
//    public ContentValues getHotWheelObjectContentValues() {
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(PriveTalkTables.HotWheelTable.MATCH_ID, this.userID);
//        contentValues.put(PriveTalkTables.HotWheelTable.NAME, this.userName);
//        contentValues.put(PriveTalkTables.HotWheelTable.GENDER, this.gender.value);
//        contentValues.put(PriveTalkTables.HotWheelTable.LOOKING_FOR, this.lookingFor.value);
//        contentValues.put(PriveTalkTables.HotWheelTable.BIRTDATE, this.userBirthdate);
//        contentValues.put(PriveTalkTables.HotWheelTable.SOCIAL_VERIFIED, this.socialVerified ? 1 : 0);
//        contentValues.put(PriveTalkTables.HotWheelTable.ROYAL_USER, this.royalUser ? 1 : 0);
//        contentValues.put(PriveTalkTables.HotWheelTable.PHOTOS_VERIFIED, this.photosVerified ? 1 : 0);
//        contentValues.put(PriveTalkTables.HotWheelTable.LATITUDE, this.realLocation.latitude);
//        contentValues.put(PriveTalkTables.HotWheelTable.LONGITUDE, this.realLocation.longitute);
//        contentValues.put(PriveTalkTables.HotWheelTable.ADMIN_AREA, this.realLocation.administrativeArea);
//        contentValues.put(PriveTalkTables.HotWheelTable.POSTAL_CODE, this.realLocation.postalCode);
//        contentValues.put(PriveTalkTables.HotWheelTable.LAST_LOCATION_UPDATE, this.realLocation.lastUpdate);
//        contentValues.put(PriveTalkTables.HotWheelTable.ISO_COUNTRY_CODE, this.realLocation.isoCountryCode);
//
//
//        return contentValues;
//    }


}
