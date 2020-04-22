package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.utilities.PriveTalkConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachariashad on 25/02/16.
 */
public class HotMatchesObject implements Serializable {

    public int matchID;
    public String name;
    public AttributesObject gender;
    public AttributesObject lookingFor;
    public long birthday;
    public int age;
    public boolean photosVerified;
    public boolean socialVerified;
    public boolean royalUser;
    public RealLocation realLocation;
    public ProfilePhoto profilePhoto;
    private int order;

    public HotMatchesObject() {
    }

    public HotMatchesObject(JSONObject obj, int order) {

        this.order = order;
        JSONArray profilePhotosArray = obj.optJSONArray("profile_photos");
        JSONObject photoObj;

        this.matchID = obj.optInt("id");
        this.name = obj.optString("name");
        this.gender = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"), obj.optString("gender"));
        this.lookingFor = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"), obj.optString("looking_for"));

        try {
            this.birthday = PriveTalkConstants.BIRTHDAY_FORMAT.parse(obj.optString("birthday")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.age = obj.optInt("age");
        this.photosVerified = obj.optBoolean("photos_verified");
        this.socialVerified = obj.optBoolean("social_verified");
        this.royalUser = obj.optBoolean("royal_user");

        if (obj.optJSONObject("real_location") != null)
            this.realLocation = new RealLocation(obj.optJSONObject("real_location"));

        for (int i = 0; i < profilePhotosArray.length(); i++) {
            photoObj = profilePhotosArray.optJSONObject(i);
            if (photoObj.optBoolean("is_main_profile_photo")) {
                this.profilePhoto = new ProfilePhoto(photoObj);
            }
        }
    }


    public HotMatchesObject(Cursor cursor) {

        this.order = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.ORDER));
        this.realLocation = new RealLocation();
        this.matchID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.MATCH_ID));
        this.name = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.NAME));

        this.gender = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"),
                String.valueOf(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.GENDER))));

        this.lookingFor = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"),
                String.valueOf(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.LOOKING_FOR))));

        this.birthday = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.BIRTDATE));
        this.age = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.AGE));
        this.socialVerified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.SOCIAL_VERIFIED)) == 1;
        this.royalUser = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.ROYAL_USER)) == 1;
        this.photosVerified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.PHOTOS_VERIFIED)) == 1;
        this.realLocation.latitude = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.LATITUDE));
        this.realLocation.longitute = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.LONGITUDE));
        this.realLocation.administrativeArea = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.ADMIN_AREA));
        this.realLocation.postalCode = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.POSTAL_CODE));
        this.realLocation.lastUpdate = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.LAST_LOCATION_UPDATE));
        this.realLocation.isoCountryCode = cursor.getString(cursor.getColumnIndex(PriveTalkTables.HotMatchesTable.ISO_COUNTRY_CODE));
        this.profilePhoto = new ProfilePhoto(cursor);

    }

    public ContentValues getHotMatchObjectContentValues() {

        ContentValues contentValues = new ContentValues();

        if (this.profilePhoto == null)
            this.profilePhoto = new ProfilePhoto();
        if (this.realLocation == null)
            realLocation = new RealLocation();

        contentValues.put(PriveTalkTables.HotMatchesTable.MATCH_ID, this.matchID);
        contentValues.put(PriveTalkTables.HotMatchesTable.NAME, this.name);
        contentValues.put(PriveTalkTables.HotMatchesTable.AGE, this.age);
        contentValues.put(PriveTalkTables.HotMatchesTable.GENDER, this.gender.value);
        contentValues.put(PriveTalkTables.HotMatchesTable.LOOKING_FOR, this.lookingFor.value);
        contentValues.put(PriveTalkTables.HotMatchesTable.BIRTDATE, this.birthday);
        contentValues.put(PriveTalkTables.HotMatchesTable.SOCIAL_VERIFIED, this.socialVerified ? 1 : 0);
        contentValues.put(PriveTalkTables.HotMatchesTable.ROYAL_USER, this.royalUser ? 1 : 0);
        contentValues.put(PriveTalkTables.HotMatchesTable.PHOTOS_VERIFIED, this.photosVerified ? 1 : 0);
        contentValues.put(PriveTalkTables.HotMatchesTable.LATITUDE, this.realLocation.latitude);
        contentValues.put(PriveTalkTables.HotMatchesTable.LONGITUDE, this.realLocation.longitute);
        contentValues.put(PriveTalkTables.HotMatchesTable.ADMIN_AREA, this.realLocation.administrativeArea);
        contentValues.put(PriveTalkTables.HotMatchesTable.POSTAL_CODE, this.realLocation.postalCode);
        contentValues.put(PriveTalkTables.HotMatchesTable.LAST_LOCATION_UPDATE, this.realLocation.lastUpdate);
        contentValues.put(PriveTalkTables.HotMatchesTable.ISO_COUNTRY_CODE, this.realLocation.isoCountryCode);
        contentValues.put(PriveTalkTables.HotMatchesTable.ORDER, this.order);
        profilePhoto.addContentValues(contentValues);


        return contentValues;
    }


    public static synchronized List<HotMatchesObject> hotMatchesObjectsParser(JSONArray array) {

        Log.wtf("Matches Resp.", array.toString());

        List<HotMatchesObject> objects = new ArrayList<>();

        for (int i = 0; i < array.length(); i++)
            objects.add(new HotMatchesObject(array.optJSONObject(i), i));

        return objects;
    }


}