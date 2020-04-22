package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 18/02/16.
 */
public class CurrentUserPhotoObject implements Serializable{

    public int id;
    public String photo = "";
    public String thumb= "";
    public String medium_thumb= "";
    public String large_thumb= "";

    public String square_photo= "";
    public String square_thumb= "";
    public String medium_square_thumb= "";
    public String large_square_thumb= "";

    public boolean is_main_profile_photo;
    public boolean verified_photo;

    public CurrentUserPhotoObject(){}

    public CurrentUserPhotoObject(JSONObject jsonObject){

        if (jsonObject == null)
            return;

        id = jsonObject.optInt("id");
        photo = jsonObject.optString("photo");
        thumb = jsonObject.optString("thumb");
        medium_thumb = jsonObject.optString("medium_thumb");
        large_thumb = jsonObject.optString("large_thumb");

        square_photo = jsonObject.optString("square_photo");
        square_thumb = jsonObject.optString("square_thumb");
        medium_square_thumb = jsonObject.optString("medium_square_thumb");
        large_square_thumb = jsonObject.optString("large_square_thumb");

        is_main_profile_photo = jsonObject.optBoolean("is_main_profile_photo");
        verified_photo = jsonObject.optBoolean("verified_photo");
    }

    public CurrentUserPhotoObject(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.ID));
        photo = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.PHOTO));
        thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.THUMB));
        medium_thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.MEDIUM_THUMB));
        large_thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.LARGE_THUMB));

        square_photo = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.SQUARE_PHOTO));
        square_thumb =cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.SQUARE_THUMB));
        medium_square_thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.MEDIUM_SQUARE_THUMB));
        large_square_thumb =cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.LARGE_SQUARE_THUMB));

        is_main_profile_photo = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.IS_MAIN_PROFILE_PHOTO)) > 0;
        verified_photo = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserPhotosTable.VERIFIED_PHOTO)) > 0;
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.ID, id);
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.PHOTO, photo);
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.THUMB, thumb);
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.MEDIUM_THUMB, medium_thumb);
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.LARGE_THUMB, large_thumb);

        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.SQUARE_PHOTO, square_photo);
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.SQUARE_THUMB, square_thumb);
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.MEDIUM_SQUARE_THUMB, medium_square_thumb);
        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.LARGE_SQUARE_THUMB, large_square_thumb);

        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.IS_MAIN_PROFILE_PHOTO, is_main_profile_photo);

        contentValues.put(PriveTalkTables.CurrentUserPhotosTable.VERIFIED_PHOTO, verified_photo);

        return contentValues;
    }

    public static void saveCurrentUserPhotos(JSONArray jsonArray) {

        if (jsonArray == null || jsonArray.length() == 0)
            return;

        ArrayList<CurrentUserPhotoObject> currentUserPhotoObjectArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                currentUserPhotoObjectArrayList.add(new CurrentUserPhotoObject(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<ContentValues> currentUserPhotosContentValues =
                CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).createCurrentUserPhotosContentValues(currentUserPhotoObjectArrayList);

        CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).saveCurrentUserPhotoContentValues(currentUserPhotosContentValues);

    }

    public JSONObject getJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("photo", photo);
        jsonObject.put("thumb", thumb);
        jsonObject.put("medium_thumb", medium_thumb);
        jsonObject.put("large_thumb", large_thumb);

        jsonObject.put("square_photo", square_photo);
        jsonObject.put("square_thumb", square_thumb);
        jsonObject.put("medium_square_thumb", medium_square_thumb);
        jsonObject.put("large_square_thumb", large_square_thumb);

        jsonObject.put("is_main_profile_photo", is_main_profile_photo);
        jsonObject.put("verified_photo", verified_photo);

        return jsonObject;
    }
}
