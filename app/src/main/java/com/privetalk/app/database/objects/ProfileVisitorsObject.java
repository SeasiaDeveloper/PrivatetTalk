package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.utilities.PriveTalkConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachariashad on 25/02/16.
 */
public class ProfileVisitorsObject {

    public int visitId;
    public int userID;
    public String name;
    public long birthday;
    public int age;
    public int profilePhotoID;
    public String photo;
    public String thumb;
    public String squarePhoto;
    public String squareThumb;
    public boolean isMainProfilePhoto;
    public boolean isOnline;
    public boolean socialVerified;
    public boolean royalUser;
    public boolean photosVerified;
    public int week;
    public int year;
    public long lastVisitDate;
    public int profile;

    public ProfileVisitorsObject() {

    }


    public ProfileVisitorsObject(JSONObject jsonObject) {

        JSONObject obj = jsonObject;
        JSONObject visitorObj;
        JSONObject photoObj;

//        for (int i = 0; i < visitorsArray.length(); i++) {
//            obj = visitorsArray.optJSONObject(i);
        visitorObj = obj.optJSONObject("visitor");
        photoObj = visitorObj.optJSONObject("main_profile_photo");


        this.visitId = obj.optInt("id");
        this.userID = visitorObj.optInt("id");
        this.name = visitorObj.optString("name");
        try {
            this.birthday = PriveTalkConstants.BIRTHDAY_FORMAT.parse(visitorObj.optString("birthday")).getTime();
            this.lastVisitDate = PriveTalkConstants.PROMOTION_DATE.parse(obj.optString("last_visit_date")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.age = visitorObj.optInt("age");
        if (photoObj != null) {
            this.profilePhotoID = photoObj.optInt("id");
            this.photo = photoObj.optString("photo");
            this.thumb = photoObj.optString("thumb");
            this.squarePhoto = photoObj.optString("square_photo");
            this.squareThumb = photoObj.optString("square_thumb");
            this.isMainProfilePhoto = photoObj.optBoolean("is_main_profile_photo");
        }
        this.isOnline = visitorObj.optBoolean("is_online");
        this.socialVerified = visitorObj.optBoolean("social_verified");
        this.royalUser = visitorObj.optBoolean("royal_user");
        this.photosVerified = visitorObj.optBoolean("photos_verified");
        this.week = obj.optInt("week");
        this.year = obj.optInt("year");
        this.profile = obj.optInt("profile");

//        }

    }


    public ProfileVisitorsObject(Cursor cursor) {

        this.visitId = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.VISIT_ID));
        this.userID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.USER_ID));
        this.name = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.NAME));
        this.birthday = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.BIRTHDAY));
        this.age = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.AGE));
        this.profilePhotoID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.PROFILE_PHOTO_ID));
        this.photo = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.PHOTO));
        this.thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.THUMB));
        this.squarePhoto = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.SQUARE_PHOTO));
        this.squareThumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.SQUARE_THUMB));
        this.isMainProfilePhoto = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.VISIT_ID)) == 1;
        this.isOnline = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.IS_ONLINE)) == 1;
        this.socialVerified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.SOCIAL_VERIFIED)) == 1;
        this.royalUser = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.ROYAL_USER)) == 1;
        this.photosVerified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.PHOTOS_VERIFIED)) == 1;
        this.week = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.WEEK));
        this.year = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.YEAR));
        this.lastVisitDate = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.LAST_VISIT_DATE));
        this.profile = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ProfileVisitorsTable.PROFILE));


    }

    public ContentValues getProfileVisitorContentValues() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.ProfileVisitorsTable.VISIT_ID, this.visitId);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.USER_ID, this.userID);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.NAME, this.name);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.BIRTHDAY, this.birthday);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.AGE, this.age);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.PROFILE_PHOTO_ID, this.profilePhotoID);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.PHOTO, this.photo);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.THUMB, this.thumb);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.SQUARE_PHOTO, this.squarePhoto);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.SQUARE_THUMB, this.squareThumb);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.IS_MAIN_PROFILE_PHOTO, this.isMainProfilePhoto ? 1 : 0);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.IS_ONLINE, this.isOnline ? 1 : 0);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.SOCIAL_VERIFIED, this.socialVerified ? 1 : 0);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.ROYAL_USER, this.royalUser ? 1 : 0);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.PHOTOS_VERIFIED, this.photosVerified ? 1 : 0);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.WEEK, this.week);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.YEAR, this.year);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.LAST_VISIT_DATE, this.lastVisitDate);
        contentValues.put(PriveTalkTables.ProfileVisitorsTable.PROFILE, this.profile);

        return contentValues;
    }


    public static synchronized List<ProfileVisitorsObject> profileVisitorsParser(JSONArray array) {
        List<ProfileVisitorsObject> objects = new ArrayList<>();

        for (int i = 0; i < array.length(); i++)
            objects.add(new ProfileVisitorsObject(array.optJSONObject(i)));

        return objects;
    }

}
