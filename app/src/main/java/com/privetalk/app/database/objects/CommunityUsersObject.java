package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.CommunityUsersDatasourse;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zachariashad on 19/02/16.
 */
public class CommunityUsersObject {

    public int order;
    public int userID;
    public String userName;
    public long userBirthdate;
    public int userAge;
    public boolean isOnline;
    public boolean socialVerified;
    public boolean royalUser;
    public boolean photosVerified;
    public ProfilePhoto profilePhoto;
    public boolean wasSeen = false;

    public CommunityUsersObject() {
    }


    public CommunityUsersObject(JSONObject obj, int order) {

        Log.d("testingcommunity", "order number : " + order);

        this.order = order;
        profilePhoto = new ProfilePhoto();

        userID = obj.optInt("id");
        userName = obj.optString("name");

        try {
            userBirthdate = PriveTalkConstants.COMMUNITY_USERS_DATE_FORMAT.parse(obj.optString("birthday")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        userAge = obj.optInt("age");
        isOnline = obj.optBoolean("is_online");
        socialVerified = obj.optBoolean("social_verified");
        royalUser = obj.optBoolean("royal_user");
        photosVerified = obj.optBoolean("photos_verified");

        JSONObject photoObj = obj.optJSONObject("main_profile_photo");

        if (photoObj != null)
            profilePhoto = new ProfilePhoto(photoObj);

    }

    public CommunityUsersObject(Cursor cursor) {

        userID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.USER_ID));
        userName = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.NAME));
        userBirthdate = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.BIRTDATE));
        userAge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.AGE));
        isOnline = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.IS_ONLINE)) == 1);
        socialVerified = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.SOCIAL_VERIFIED)) == 1);
        royalUser = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.ROYAL_USER)) == 1);
        photosVerified = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.PHOTOS_VERIFIED)) == 1);
        order = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.ORDER));
//        wasSeen = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.WAS_SEEN)) == 1;
        profilePhoto = new ProfilePhoto(cursor);

    }

    public ContentValues getCommunityUsersObjectContentValues() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.CommunityUsersTable.USER_ID, this.userID);
        contentValues.put(PriveTalkTables.CommunityUsersTable.NAME, this.userName);
        contentValues.put(PriveTalkTables.CommunityUsersTable.BIRTDATE, this.userBirthdate);
        contentValues.put(PriveTalkTables.CommunityUsersTable.AGE, this.userAge);
        contentValues.put(PriveTalkTables.CommunityUsersTable.IS_ONLINE, this.isOnline ? 1 : 0);
        contentValues.put(PriveTalkTables.CommunityUsersTable.SOCIAL_VERIFIED, this.socialVerified ? 1 : 0);
        contentValues.put(PriveTalkTables.CommunityUsersTable.ROYAL_USER, this.royalUser ? 1 : 0);
        contentValues.put(PriveTalkTables.CommunityUsersTable.PHOTOS_VERIFIED, this.photosVerified ? 1 : 0);
//        contentValues.put(PriveTalkTables.CommunityUsersTable.PHOTOS_VERIFIED, this.wasSeen ? 1 : 0);
        contentValues.put(PriveTalkTables.CommunityUsersTable.ORDER, this.order);

        profilePhoto.addContentValues(contentValues);

        return contentValues;
    }


    public static synchronized void parseAndSave(JSONArray response) {
        if (response == null)
            return;

        List<CommunityUsersObject> communityUsersObjects = new ArrayList<>();
        int orderNumber = CommunityUsersDatasourse.getInstance(PriveTalkApplication.getInstance()).getCount();

        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonObject = response.optJSONObject(i);
            if (jsonObject == null)
                continue;
            communityUsersObjects.add(new CommunityUsersObject(jsonObject, orderNumber + i));
        }

        CommunityUsersDatasourse.getInstance(PriveTalkApplication.getInstance()).saveCommunityUsers(communityUsersObjects);
    }


    public static void markAsSeen(final List<CommunityUsersObject> communityUsersObjects) {

        new AsyncTask<Void, JSONArray, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params) {

                JSONArray array = new JSONArray();
                try {

                    for (CommunityUsersObject communityUsersObject : communityUsersObjects)
                        if (communityUsersObject.wasSeen) {

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", communityUsersObject.userID);
                            array.put(jsonObject);
                        }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return array;
            }


            @Override
            protected void onPostExecute(final JSONArray jsonArray) {
                if (jsonArray.length() <= 0)
                    return;


                StringRequest recordViews = new StringRequest(Request.Method.POST, Links.RECORD_VIEWS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                        headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                        return headers;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return jsonArray.toString().getBytes("utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(recordViews);

            }
        }.execute();

    }

}
