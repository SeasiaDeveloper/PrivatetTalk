package com.privetalk.app.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.database.datasource.BadgesDatasource;
import com.privetalk.app.database.datasource.CoinsPlanDatasource;
import com.privetalk.app.database.datasource.CommunityUsersDatasourse;
import com.privetalk.app.database.datasource.ConfigurationScoreDatasource;
import com.privetalk.app.database.datasource.ConversationsDatasource;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.CurrentUserProfileSettingsDatasource;
import com.privetalk.app.database.datasource.ETagDatasource;
import com.privetalk.app.database.datasource.FavoritesDatasourse;
import com.privetalk.app.database.datasource.HotMatchesDatasource;
import com.privetalk.app.database.datasource.InterestsDatasource;
import com.privetalk.app.database.datasource.MessageDatasource;
import com.privetalk.app.database.datasource.NotificationsDatasource;
import com.privetalk.app.database.datasource.ProfileVisitorsDatasource;
import com.privetalk.app.database.datasource.PromotedUsersDatasource;
import com.privetalk.app.database.datasource.TimeStepsDatasource;
import com.privetalk.app.database.objects.AttributesObject;
import com.privetalk.app.database.objects.BadgesObject;
import com.privetalk.app.database.objects.CoinsPlan;
import com.privetalk.app.database.objects.CommunityUsersObject;
import com.privetalk.app.database.objects.ConfigurationScore;
import com.privetalk.app.database.objects.ConversationObject;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.CurrentUserPhotoObject;
import com.privetalk.app.database.objects.EtagObject;
import com.privetalk.app.database.objects.FavoritesObject;
import com.privetalk.app.database.objects.HotMatchesObject;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.database.objects.MutualFriendsObject;
import com.privetalk.app.database.objects.NotificationObject;
import com.privetalk.app.database.objects.ProfileSettings;
import com.privetalk.app.database.objects.ProfileVisitorsObject;
import com.privetalk.app.database.objects.PromotedUsersObject;
import com.privetalk.app.database.objects.TimeStepsObject;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.adapters.PromotedUsersAdapter;
import com.privetalk.app.mainflow.fragments.ChatFragments;
import com.privetalk.app.mainflow.fragments.OtherUsersProfileInfoFragment;
import com.pushbots.push.Pushbots;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zachariashad on 09/12/15.
 */
public class PriveTalkUtilities {


    public static String encrypt(String message) {

        String encrypt = "";
        try {
            encrypt = AESCrypt.encrypt(Links.ALOUPIA, message);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return encrypt;
    }

    public static String decrypt(String message) {
        String decrypt = "";

        try {
            decrypt = AESCrypt.decrypt(Links.ALOUPIA, message);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return decrypt;
    }

    public static void changeFragment(Context context, boolean addTobackStuck, int fragmentId) {

        changeFragment(context, addTobackStuck, fragmentId, -1);
    }

    public static void changeFragment(Context context, boolean addTobackStuck, int fragmentId, Bundle bundle) {

        changeFragment(context, addTobackStuck, fragmentId, -1, -1, bundle);
    }

    public static void changeFragment(Context context, boolean addTobackStuck, int fragmentId, int fragmentType) {

        changeFragment(context, addTobackStuck, fragmentId, -1, fragmentType, null);
    }

    public static void changeFragment(Context context, boolean addTobackStuck, int fragmentId, int fragmentType, Bundle bundle) {

        changeFragment(context, addTobackStuck, fragmentId, -1, fragmentType, bundle);
    }


    public static void changeFragment(Context context, boolean addTobackStuck, int fragmentId, int position, int fragmentType) {
        changeFragment(context, addTobackStuck, fragmentId, position, fragmentType, null);
    }

    public static void changeFragment(Context context, boolean addTobackStuck, int fragmentId, int position, int fragmentType, Bundle bundle) {

        if (bundle == null)
            bundle = new Bundle();

        bundle.putInt(MainActivity.FRAGMENT_TO_CHANGE, fragmentId);
        bundle.putBoolean(MainActivity.ADD_TO_BACKSTUCK, addTobackStuck);

        if (position != -1)
            bundle.putInt(MainActivity.FRAGMENT_VIEWPAGER_POSITION, position);

        if (fragmentType != -1)
            bundle.putInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE, fragmentType);

        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

    public static void showSomethingWentWrongDialog(Context applicationContext) {

        if (applicationContext == null)
            return;

        Toast.makeText(applicationContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
//        builder.setTitle(applicationContext.getString(R.string.pleaseTryAgain));
//        builder.setMessage(applicationContext.getString(R.string.something_went_wrong));
//        builder.setPositiveButton(applicationContext.getString(R.string.okay), null);
//        builder.create().show();

    }

    public static String getAge(long birthday) {

        Calendar dobDate = Calendar.getInstance();
        dobDate.setTimeInMillis(birthday);
        Calendar today = Calendar.getInstance();
        int curYear = today.get(Calendar.YEAR);
        int year = dobDate.get(Calendar.YEAR);
        int curDayOfYear = today.get(Calendar.DAY_OF_YEAR);
        int dayOfYear = dobDate.get(Calendar.DAY_OF_YEAR);

        if (dayOfYear >= curDayOfYear)
            return String.valueOf(curYear - year - 1);
        else
            return String.valueOf(curYear - year);
    }


    /*
    Static method to send server location
    updates. all data are taken from preferences,
    previously saved by an application service
     */
    public static synchronized void sendLocationToServer() {

        Map<String, Object> postParam = new HashMap<>();
        Map<String, Object> lastLocation = new HashMap<>();

        float previousSendLat = LocationUtilities.getPreviousLatitude();
        float previousSendLng = LocationUtilities.getPreviousLongitude();

        final float lat = LocationUtilities.getLatitude();
        final float lng = LocationUtilities.getLongitude();


        if (previousSendLat == lat && previousSendLng == lng)
           return;

        if (lat == 0 && lng == 0)
            return;

        lastLocation.put("longitude", lng);
        lastLocation.put("latitude", lat);

        postParam.put("last_location", lastLocation);

        postParam.put("iso_country_code", LocationUtilities.getCountryCode());
        postParam.put("postal_code", LocationUtilities.getPostalCode());
        postParam.put("administrative_area", LocationUtilities.getAdminArea());


        JsonObjectRequest postLocation = new JsonObjectRequest(Request.Method.POST, Links.POST_LOCATION, new JSONObject(postParam),

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        LocationUtilities.savePreviousLocation(lat, lng);

                        PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                                .edit().putBoolean(PriveTalkConstants.KEY_SHOULD_UPDATE_COMMUNITY, true).apply();

                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_COMMUNITY_RECEIVER));

                        fetchPromotedUsers();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    //                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                    sendLocationToServer();
//                        Toast.makeText(PriveTalkApplication.getInstance(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                }
            }
        })

        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                netWorkResponse = response;
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(postLocation);

    }


    public static void postJsonObject(final String URL) {
        postJsonObject(URL, new JSONObject());
    }

    /*
    Static method to post jsonobject
    is now online.
     */
    public static void postJsonObject(final String URL, JSONObject object) {

        JsonObjectRequest postLocation = new JsonObjectRequest(Request.Method.POST, URL, object,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("testing50", "onResponse " + response.toString());

                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject = new JSONObject(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int coins = jsonObject.optInt("coins");

                        Log.d("testing50", " coins " + coins);

                        if (URL.equals(Links.CLAIM_REWARD)) {
                            Toast.makeText(PriveTalkApplication.getInstance(), PriveTalkApplication.getInstance().getString(R.string.coins), Toast.LENGTH_SHORT).show();

                            PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                                    .edit().putBoolean(PriveTalkConstants.CLAIM_REWARD, true).apply();

                            Intent intent1 = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                            intent1.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.CLAIM_COINS);
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent1);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                }
            }
        })

        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                netWorkResponse = response;
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(postLocation);

    }


    public static void uploadProfilePicture(Bitmap bmp, Bitmap croppedBitmap, final boolean isProfile) {

        Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
        intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.SHOW_UPLOADING_PICTURE_TOAST);
        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);

        new AsyncTask<Bitmap, Void, String[]>() {

            NetworkResponse netWorkResponse;
            private final int NORMAL_PICTURE = 0;
            private final int CROPPED_PICTURE = 1;

            @Override
            protected String[] doInBackground(Bitmap... params) {

                String bitmapsData[] = new String[2];

                float normalSize = (params[NORMAL_PICTURE].getByteCount() / PriveTalkConstants.KILOBYTE);

                int percent = 100;

                if (normalSize > PriveTalkConstants.MAXIMUM_IMAGE_SIZE_BEFORE_COMPRESSION)
                    percent = (int) (((PriveTalkConstants.MAXIMUM_IMAGE_SIZE_BEFORE_COMPRESSION / normalSize)) * 100);

                percent = percent < PriveTalkConstants.MINIMUM_COMPRESSION_QUALITY ? PriveTalkConstants.MINIMUM_COMPRESSION_QUALITY : percent;


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                params[NORMAL_PICTURE].compress(Bitmap.CompressFormat.JPEG, percent, stream);
                byte[] bitmapByteArray = stream.toByteArray();

//                System.out.println("Normal Img size: " + bitmapByteArray.length / PriveTalkConstants.KILOBYTE + "kb");

                stream = new ByteArrayOutputStream();
                params[CROPPED_PICTURE].compress(Bitmap.CompressFormat.JPEG, percent, stream);
                byte[] croppedBitmapByteArray = stream.toByteArray();

                bitmapsData[NORMAL_PICTURE] = "data:image/jpeg;base64," + Base64.encodeToString(bitmapByteArray, Base64.DEFAULT);
                bitmapsData[CROPPED_PICTURE] = "data:image/jpeg;base64," + Base64.encodeToString(croppedBitmapByteArray, Base64.DEFAULT);

                return bitmapsData;
            }

            @Override
            protected void onPostExecute(String[] pictureData) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("photo", pictureData[NORMAL_PICTURE]);
                    jsonObject.put("square_photo", pictureData[CROPPED_PICTURE]);
                    jsonObject.put("is_main_profile_photo", isProfile);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                JsonObjectRequest uploadPictureRequest = new JsonObjectRequest(Request.Method.POST, Links.PHOTOS, jsonObject,

                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                CurrentUserPhotoObject currentUserPhotoObject = new CurrentUserPhotoObject(response);

                                if (currentUserPhotoObject.is_main_profile_photo) {
                                    CurrentUserPhotoObject previousProfilePhoto = CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).getProfilePhoto();

                                    if (previousProfilePhoto != null) {
                                        previousProfilePhoto.is_main_profile_photo = false;

                                        CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).savePhoto(previousProfilePhoto);
                                    }

                                }

                                CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).savePhoto(currentUserPhotoObject);

                                Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                                intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.SHOW_UPLOADING_PICTURE_FINISHED);
                                LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);

                                Intent uploadPhotoIntent = new Intent(PriveTalkConstants.BROADCAST_NEW_PHOTO_UPLOADED);
                                uploadPhotoIntent.putExtra(PriveTalkConstants.KEY_FAIL, false);
                                LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(uploadPhotoIntent);

                                LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_PHOTO_UPLOADED));

                                CustomEvent customEvent = new CustomEvent(PriveTalkConstants.TAG_UPLOAD_IMAGE);
                                customEvent.putCustomAttribute("Success", CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().userID);
                                Answers.getInstance().logCustom(customEvent);

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                        intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.SHOW_UPLOADING_PROFILE_PHOTO_FAILED);
                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);


                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_PHOTO_UPLOADED));

//                        Intent uploadPhotoIntent = new Intent(PriveTalkConstants.BROADCAST_NEW_PHOTO_UPLOADED);
//                        uploadPhotoIntent.putExtra(PriveTalkConstants.KEY_FAIL, true);
//                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(uploadPhotoIntent);

                        CustomEvent customEvent = new CustomEvent(PriveTalkConstants.TAG_UPLOAD_IMAGE);
                        int id = CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().userID;

                        if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                            customEvent.putCustomAttribute("TimeoutError or NoConnectionError", id);
                        } else if (volleyError instanceof AuthFailureError) {
                            customEvent.putCustomAttribute("AuthFailureError", id);
                        } else if (volleyError instanceof ServerError) {
                            customEvent.putCustomAttribute("ServerError", id);
                        } else if (volleyError instanceof NetworkError) {
                            customEvent.putCustomAttribute("NetworkError", id);
                        } else if (volleyError instanceof ParseError) {
                            customEvent.putCustomAttribute("ParseError", id);
                        }

                        Answers.getInstance().logCustom(customEvent);

                        if (netWorkResponse != null) {

//                                JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        }
                    }
                }) {

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        netWorkResponse = response;
                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        HashMap<String, String> headers = new HashMap<>();

                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                        headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                        return headers;
                    }
                };

                VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(uploadPictureRequest);
            }
        }.execute(bmp, croppedBitmap);
    }

    public static synchronized void startDownloadWithPaging(int method, String url, String intentFilter, HashMap<String, String> headers, JSONObject formPostParameters) {

        if (headers == null)
            headers = new HashMap<>();

        CurrentUser currentUser = CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo();

        if (currentUser == null)
            return;

        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("AUTHORIZATION", "Token " + currentUser.token);
        headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


        recursivePagingDownload(method, intentFilter.equals(PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST) ? url : url + "?page=1", intentFilter, headers, formPostParameters, 1);

    }

    private static synchronized void recursivePagingDownload(final int method, final String url, final String intentFilter,
                                                             final HashMap<String, String> headers, final JSONObject formPostParameters, final int page) {
        Log.d("", "url: " + url);

        final JsonObjectRequestWithResponse recursiveDownload = new JsonObjectRequestWithResponse(method, url, formPostParameters,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        int statusCode = response.optInt("statusCode");
                        final JSONObject data = response.optJSONObject("data");

                        if (statusCode == 304) {

                            Intent intent = new Intent(intentFilter);
                            intent.putExtra(PriveTalkConstants.DOWNLOAD_COMPLETED, true);
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);

                            if (intentFilter != null && intentFilter.equals(PriveTalkConstants.BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED))
                                LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(intentFilter));

                            return;
                        }

                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {

                                switch (intentFilter) {
                                    case PriveTalkConstants.BROADCAST_PHOTOS_DOWNLOADED:
                                        if (page == 1)
                                            CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).deletePhotos();
                                        CurrentUserPhotoObject.saveCurrentUserPhotos(data.optJSONArray("results"));
                                        break;
                                    case PriveTalkConstants.BROADCAST_PROFILE_VISITORS_DOWNLOADED:
                                        if (page == 1)
                                            ProfileVisitorsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteProfileVisitors();
                                        ProfileVisitorsDatasource.getInstance(PriveTalkApplication.getInstance()).saveProfileVisitors(ProfileVisitorsObject.profileVisitorsParser(data.optJSONArray("results")));
                                        break;
                                    case PriveTalkConstants.BROADCAST_FAVORITES_DOWNLOADED:
                                        if (page == 1)
                                            FavoritesDatasourse.getInstance(PriveTalkApplication.getInstance()).deleteFavorites();
                                        FavoritesDatasourse.getInstance(PriveTalkApplication.getInstance()).saveFavorites(FavoritesObject.favoritesObjectsParser(data.optJSONArray("results"), true));
                                        break;
                                    case PriveTalkConstants.BROADCAST_FAVORITED_BY_DOWNLOADED:
                                        if (page == 1)
                                            FavoritesDatasourse.getInstance(PriveTalkApplication.getInstance()).deleteFavoriteBy();
                                        FavoritesDatasourse.getInstance(PriveTalkApplication.getInstance()).saveFavorites(FavoritesObject.favoritesObjectsParser(data.optJSONArray("results"), false));
                                        break;
                                    case PriveTalkConstants.BROADCAST_HOT_MATCHES_DOWNLOADED:
                                        if (page == 1)
                                            HotMatchesDatasource.getInstance(PriveTalkApplication.getInstance()).deleteHotMatches();
                                        HotMatchesDatasource.getInstance(PriveTalkApplication.getInstance()).saveHotMatches(HotMatchesObject.hotMatchesObjectsParser(data.optJSONArray("results")));
                                        break;
                                    case PriveTalkConstants.BROADCAST_CONVERSATIONS_DOWNLOADED:
                                        if (page == 1)
                                            ConversationsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteConversations();
                                        ConversationsDatasource.getInstance(PriveTalkApplication.getInstance()).saveConversations(ConversationObject.conversationParser(data.optJSONArray("results")));
                                        break;
                                    case ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER:
                                        if (url.equals(Links.CONVERSATIONS)) {
                                            if (page == 1)
                                                ConversationsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteConversations();
                                            ConversationsDatasource.getInstance(PriveTalkApplication.getInstance()).saveConversations(ConversationObject.conversationParser(data.optJSONArray("results")));
                                        }
                                        break;

                                    case PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST:
                                        CommunityUsersObject.parseAndSave(response.optJSONObject("data").optJSONArray("results"));
                                        break;

                                    case PriveTalkConstants.BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED:
//                                        if (page == 1)
//                                            CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).deleteTableData();
                                        ConfigurationScore.parseAndSaveResponse(data);
                                        break;
                                    case PriveTalkConstants.BROADCAST_TIMESTEPS_DOWNLOADED:
                                        if (page == 1)
                                            TimeStepsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteTimeSteps();
                                        TimeStepsDatasource.getInstance(PriveTalkApplication.getInstance()).saveTimeSteps(TimeStepsObject.timeStepsObjectsParser(data.optJSONArray("results")));
                                        break;
                                    case PriveTalkConstants.BROADCAST_COINS_PLANS_DOWNLOADED:
                                        CoinsPlanDatasource.getInstance(PriveTalkApplication.getInstance()).saveCoinPlans(CoinsPlan.parseJson(data.optJSONArray("results")));
                                        break;
                                    case Links.ACTIVITIES:
                                    case Links.MUSIC:
                                    case Links.MOVIES:
                                    case Links.LITERATURE:
                                    case Links.PLACES:
                                    case Links.OCCUPATION:
                                        Log.d(" activities data", " type " + intentFilter.toString() + " " + data.toString());
//                                        if (page == 1)
//                                            InterestsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteDataOfType(intentFilter);
                                        InterestsDatasource.getInstance(PriveTalkApplication.getInstance()).saveInterestsOfType(InterestObject.parseInterestsObjectJson(data, intentFilter));
                                        break;
                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {

                                String nextPage = data.optString("next");

                                Intent intent = new Intent(intentFilter);

                                if (nextPage != null && !nextPage.equals("null") && !nextPage.isEmpty()) {
                                    intent.putExtra(PriveTalkConstants.DOWNLOAD_COMPLETED, false);
                                    recursivePagingDownload(method, nextPage, intentFilter, headers, formPostParameters, page + 1);
                                } else {
                                    intent.putExtra(PriveTalkConstants.DOWNLOAD_COMPLETED, true);
                                    if (intentFilter.equals(PriveTalkConstants.BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED))
                                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(intentFilter));
                                }

                                LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);

                            }
                        }.execute();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_RECURSIVE_FAILED));

                switch (intentFilter) {
                    case PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST:
                    case PriveTalkConstants.BROADCAST_PROFILE_VISITORS_DOWNLOADED:
                    case PriveTalkConstants.BROADCAST_FAVORITED_BY_DOWNLOADED:
                    case PriveTalkConstants.BROADCAST_FAVORITES_DOWNLOADED:
                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(intentFilter));
                        break;
                }

                if (error.networkResponse != null) {
                    //                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                } else {
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null
                        && !url.contains(Links.GET_PROFILE_VISITORS) && !intentFilter.equals(PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST)) {
                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);

                }

                System.out.println("headers" + headers.toString());

                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                ETagDatasource.getInstance(PriveTalkApplication.getInstance()).saveETagForLink(new EtagObject(getUrl(), response));
                return super.parseNetworkResponse(response);
            }
        };

        recursiveDownload.setTag(intentFilter);

        recursiveDownload.setShouldCache(false);

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(recursiveDownload);

    }


    public static void getCurrentUserProfileSettings() {

        JsonObjectRequest getProfileSettings = new JsonObjectRequest
                (Request.Method.GET, Links.GET_PROFILE_SETTINGS, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        CurrentUserProfileSettingsDatasource.getInstance(PriveTalkApplication.getInstance()).saveCurrentUserProfileSettings(new ProfileSettings(response));

                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_SETTINGS_DOWNLOADED));

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            //                                JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
//                        Toast.makeText(mContext, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                netWorkResponse = response;
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getProfileSettings);

    }


    public static void fetchPromotedUsers() {

        final SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences("preferences", Context.MODE_PRIVATE);


        JsonArrayRequest getPromotedUsers = new JsonArrayRequest(JsonArrayRequest.Method.GET, Links.PROMOTED_USERS, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
//                System.out.println("Promoted Users: " + response);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        PromotedUsersDatasource.getInstance(PriveTalkApplication.getInstance()).deletePromotedUsers();
                        PromotedUsersDatasource.getInstance(PriveTalkApplication.getInstance()).savePromotedUserts(PromotedUsersObject.parsePromotedUsers(response));
                        preferences.edit().putLong(PriveTalkConstants.TIME_PROMOTED_USERS_UPDATED, System.currentTimeMillis()).commit();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));
                    }

                }.execute();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                return headers;
            }

        };

//        if ((System.currentTimeMillis() - preferences.getLong(PriveTalkConstants.TIME_PROMOTED_USERS_UPDATED, 0)) > 100000)//5 minutes in milliseconds
        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getPromotedUsers);
    }


    public static void getNotifications() {
        JsonArrayRequest getNotifications = new JsonArrayRequest(JsonArrayRequest.Method.GET, Links.NOTIFICATIONS, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
               System.out.println("Promoted Users: " + response);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
//                        NotificationsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteAllNotifications();
                        NotificationsDatasource.getInstance(PriveTalkApplication.getInstance()).saveNotifications(NotificationObject.parseObjects(response));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                        intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.NOTIFICATIONS_UPADATED);
                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
                    }

                }.execute();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                try {
                    headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                    headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getNotifications);
    }


    public static void clearAllSavedData() {
        //delete current user data from databases
        CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).deleteCurrentUser();
        CommunityUsersDatasourse.getInstance(PriveTalkApplication.getInstance()).deleteCommunityUsers();
        FavoritesDatasourse.getInstance(PriveTalkApplication.getInstance()).deleteEverething();
        HotMatchesDatasource.getInstance(PriveTalkApplication.getInstance()).deleteHotMatches();
        PromotedUsersDatasource.getInstance(PriveTalkApplication.getInstance()).deletePromotedUsers();
        ProfileVisitorsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteProfileVisitors();
        CurrentUserProfileSettingsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteCurrentUserProfileSettings();
        CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).deletePhotos();
        CurrentUserDetailsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteCurrentUserDetails();
        ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance()).deleteConfigurations();
        ETagDatasource.getInstance(PriveTalkApplication.getInstance()).deleteEtags();
        ConversationsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteConversations();
        NotificationsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteAllNotifications();
        MessageDatasource.getInstance(PriveTalkApplication.getInstance()).deleteMessages();
        BadgesDatasource.getInstance(PriveTalkApplication.getInstance()).deleteBadges();
        AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).deleteAll();
        InterestsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteAllInterests();
        //clear all user date from preferences
        SharedPreferences.Editor editor = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        editor.putBoolean(PriveTalkConstants.KEY_WELCOME_SCREENS, true).commit();
    }


    public static void saveCurrentUserData(JSONObject jsonObject) {

        Log.d("Save User Info Send", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Links.MY_PROFILE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Save User Info", response.toString());

                        CurrentUser oldUser = CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo();

                        CurrentUser currentUser = new CurrentUser(response, oldUser.token, oldUser.email);

                        CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).deleteCurrentUser();
                        CurrentUserDetailsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteCurrentUserDetails();
                        CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).saveCurrentUser(currentUser);
                        CurrentUserDetailsDatasource.getInstance(PriveTalkApplication.getInstance()).saveCurrentUserDetails(currentUser.currentUserDetails);

                        Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                        intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.CURRENT_USER_SAVED);
                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);

                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {

                Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.CURRENT_USER_SAVE_FAILED);
                LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);


                if (error.networkResponse != null) {
                    //                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
//                        Toast.makeText(mContext, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                }
            }
        }

        )

        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(jsonObjectRequest);

    }


    public static void getAllInterests() {
        startDownloadWithPaging(Request.Method.GET, Links.ACTIVITIES, Links.ACTIVITIES, null, new JSONObject());
        startDownloadWithPaging(Request.Method.GET, Links.MUSIC, Links.MUSIC, null, new JSONObject());
        startDownloadWithPaging(Request.Method.GET, Links.LITERATURE, Links.LITERATURE, null, new JSONObject());
        startDownloadWithPaging(Request.Method.GET, Links.PLACES, Links.PLACES, null, new JSONObject());
        startDownloadWithPaging(Request.Method.GET, Links.MOVIES, Links.MOVIES, null, new JSONObject());
        startDownloadWithPaging(Request.Method.GET, Links.OCCUPATION, Links.OCCUPATION, null, new JSONObject());
    }


    public static void getAttributes() {

        JsonObjectRequestWithResponse attributesRequest = new JsonObjectRequestWithResponse(Request.Method.GET, Links.ALL_ATTRIBUTES, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                int statusCode = response.optInt("statusCode");
                final JSONObject data = response.optJSONObject("data");

                if (statusCode == 304) {
                    PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.CONFIGURATION_SCORES, PriveTalkConstants.BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED, new HashMap<String, String>(), new JSONObject());
//                    System.out.println("304");
                } else {

                    new AsyncTask<JSONObject, Void, Void>() {

                        @Override
                        protected Void doInBackground(JSONObject... params) {

                            Iterator<String> keysIterator = params[0].keys();

                            while (keysIterator.hasNext()) {

                                String key = keysIterator.next();
                                try {
                                    AttributesObject.parseAndSaveAttributeArray(params[0].getJSONArray(key), PriveTalkTables.AttributesTables.getTableString(key));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.CONFIGURATION_SCORES, PriveTalkConstants.BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED, new HashMap<String, String>(), new JSONObject());
                            super.onPostExecute(aVoid);
                        }
                    }.execute(data);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error.networkResponse != null) {
                    //                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                } else {
                }
            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                ETagDatasource.getInstance(PriveTalkApplication.getInstance()).saveETagForLink(new EtagObject(getUrl(), response));
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null) {
                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);
                }

                return headers;
            }


        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(attributesRequest);
    }

    public static Map<String, String> getStandardHeaders() {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
        headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

        return headers;
    }

    public static void parseError(VolleyError error) {

        if (error.networkResponse != null) {
            //                JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
        } else {
        }
    }

    public static void getUserInfoFromServer() {

//        System.out.println("Get user data");

        JsonObjectRequestWithResponse myInfoRequests = new JsonObjectRequestWithResponse(Request.Method.GET, Links.MY_PROFILE, new JSONObject(),

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.PHOTOS, PriveTalkConstants.BROADCAST_PHOTOS_DOWNLOADED, new HashMap<String, String>(), new JSONObject());

                        int statusCode = response.optInt("statusCode");
                        JSONObject data = response.optJSONObject("data");

                        if (statusCode == 304) {
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_CURRENT_USER_UPDATED));
                        } else {

                            CurrentUser temp = CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo();

                            CurrentUser currentUser = new CurrentUser(data, temp == null ? "" : temp.token, temp == null ? "" : temp.email);

                            CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).deleteCurrentUser();
                            CurrentUserDetailsDatasource.getInstance(PriveTalkApplication.getInstance()).deleteCurrentUserDetails();
                            CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).saveCurrentUser(currentUser);
                            CurrentUserDetailsDatasource.getInstance(PriveTalkApplication.getInstance()).saveCurrentUserDetails(currentUser.currentUserDetails);
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_CURRENT_USER_UPDATED));
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).
                                    sendBroadcast((new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER)).putExtra(PriveTalkConstants.KEY_EVENT_TYPE, ChatFragments.REFRESH_USER_DATA));

                        }

                        CurrentUser currentUser = CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo();
                        if (currentUser != null && currentUser.userID != 0) {
                            Pushbots.sharedInstance().setAlias(String.valueOf(currentUser.userID));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null) {
                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);
                }
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                ETagDatasource.getInstance(PriveTalkApplication.getInstance()).saveETagForLink(new EtagObject(getUrl(), response));
                return super.parseNetworkResponse(response);
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(myInfoRequests);
    }


    public static void badgesRequest(boolean isReset, PriveTalkConstants.MenuBadges badgeToReset) {

        final String URL = isReset ? Links.RESET_BADGE : Links.MY_BADGES;
        JSONObject object = new JSONObject();

        if (isReset) {
            try {
                object.put("visitors_badge", badgeToReset == PriveTalkConstants.MenuBadges.VISITORS_BADGE);
                object.put("messages_badge", badgeToReset == PriveTalkConstants.MenuBadges.MESSAGES_BAGE);
                object.put("flames_badge", badgeToReset == PriveTalkConstants.MenuBadges.FLAMES_BADGE);
                object.put("matches_badge", badgeToReset == PriveTalkConstants.MenuBadges.MATCHES_BADGE);
                object.put("favourited_badge", badgeToReset == PriveTalkConstants.MenuBadges.FAVORITES_BADGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final JsonObjectRequestWithResponse badgesRequest = new JsonObjectRequestWithResponse(isReset ? Request.Method.POST : Request.Method.GET, URL, object,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {


                        if (response.optInt("statusCode") == 304)
                            return;

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                switch (URL) {
                                    case Links.MY_BADGES:
                                        BadgesDatasource.getInstance(PriveTalkApplication.getInstance()).saveBadges(new BadgesObject(response.optJSONObject("data")));
                                        break;
                                    case Links.RESET_BADGE:
                                        break;
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                switch (URL) {
                                    case Links.MY_BADGES:
                                        Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.MENU_BADGES_RECEIVED);
                                        intent.putExtras(bundle);
                                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
                                        break;
                                    case Links.RESET_BADGE:
                                        if (response.optJSONObject("data").optBoolean("success"))
                                            badgesRequest(false, null);
                                        break;
                                }
                            }
                        }.execute();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null) {
                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);
                }
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                ETagDatasource.getInstance(PriveTalkApplication.getInstance()).saveETagForLink(new EtagObject(getUrl(), response));
                return super.parseNetworkResponse(response);
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(badgesRequest);
    }


    public static synchronized boolean checkIfPromotedUsersIsScrollable(RecyclerView promotedUsersRecyclerView, PromotedUsersAdapter mAdapter) {

        if (promotedUsersRecyclerView == null || mAdapter == null || mAdapter.promotedUsersObjects == null || promotedUsersRecyclerView.getWidth() == 0)
            return false;

        int promotedusers = mAdapter.promotedUsersObjects.size();
        int promotedUsersPictureWisth = mAdapter.imageViewSize;

        int totalWidth = promotedusers * promotedUsersPictureWisth;

        if (totalWidth > promotedUsersRecyclerView.getWidth())
            return true;
        else return false;

    }


    public static void getGlobalTimeFromServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Links.GLOBAL_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
//                    System.out.println("Date milliseconds: " + PriveTalkConstants.globalDateFormat.parse(response).getTime());
                    setGlobalTimeDifference(PriveTalkConstants.globalDateFormat.parse(response).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(stringRequest);
    }


    public static void setGlobalTimeDifference(long globalTime) {
        PriveTalkApplication.getInstance()
                .getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                .edit().putLong(PriveTalkConstants.KEY_GLOBAL_TIME, System.currentTimeMillis() - globalTime).commit();
    }

    public static synchronized long getGlobalTimeDifference() {
        return PriveTalkApplication.getInstance()
                .getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                .getLong(PriveTalkConstants.KEY_GLOBAL_TIME, 0);
    }


    public static void sharePrive(Activity context, int requestCode) {
        Intent shareIntent = new Intent();

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_TEXT, Links.PRIVETALK_SITE);

        if (context != null)
            context.startActivityForResult(Intent.createChooser(shareIntent, PriveTalkApplication.getInstance().getString(R.string.share_privetalk)), requestCode);
    }


    public static int getZodiacResourceId(int zodiac) {

        switch (zodiac) {
            case PriveTalkConstants.Zodiac.ARIES:
                return R.drawable.zodiac_aries;
            case PriveTalkConstants.Zodiac.TAURUS:
                return R.drawable.zodiac_taurus;
            case PriveTalkConstants.Zodiac.GEMINI:
                return R.drawable.zodiac_gemini;
            case PriveTalkConstants.Zodiac.CANCER:
                return R.drawable.zodiac_cancer;
            case PriveTalkConstants.Zodiac.LEO:
                return R.drawable.zodiac_leo;
            case PriveTalkConstants.Zodiac.VIRGO:
                return R.drawable.zodiac_virgo;
            case PriveTalkConstants.Zodiac.LIBRA:
                return R.drawable.zodiac_libra;
            case PriveTalkConstants.Zodiac.SCORPIO:
                return R.drawable.zodiac_scorpio;
            case PriveTalkConstants.Zodiac.SAGITARIUS:
                return R.drawable.zodiac_sagittarius;
            case PriveTalkConstants.Zodiac.CAPRICORN:
                return R.drawable.zodiac_capricorn;
            case PriveTalkConstants.Zodiac.AQUARIUS:
                return R.drawable.zodiac_acquarius;
            case PriveTalkConstants.Zodiac.PISCES:
                return R.drawable.zodiac_pisces;
            default:
                return R.drawable.profile_personal_info_zodiac;

        }
    }


    /*
    Get Common Facebook Friends
     */
    public static void getCommonFacebookFriends(String fbID, final String intentFilter) {
        if (!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(PriveTalkApplication.getInstance());
        Bundle params = new Bundle();
        params.putString("fields", "context.fields(mutual_friends)");

        if (fbID != null)
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    fbID,
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(final GraphResponse response) {
                            new AsyncTask<Void, ArrayList<MutualFriendsObject>, ArrayList<MutualFriendsObject>>() {
                                @Override
                                protected ArrayList<MutualFriendsObject> doInBackground(Void... params) {
                                    ArrayList<MutualFriendsObject> mutualFriends = new ArrayList<>();
                                    if (response.getJSONObject() != null) {
                                        try {
                                            JSONArray array = response.getJSONObject().optJSONObject("context").optJSONObject("mutual_friends").optJSONArray("data");
                                            for (int i = 0; i < array.length(); i++)
                                                mutualFriends.add(new MutualFriendsObject(array.optJSONObject(i)));
                                        } catch (NullPointerException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                    return mutualFriends;
                                }

                                @Override
                                protected void onPostExecute(ArrayList<MutualFriendsObject> aVoid) {
                                    Intent intent = new Intent(intentFilter);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("action", OtherUsersProfileInfoFragment.RECEIVER_ACTIONS.COMMON_FRIENDS);
                                    bundle.putSerializable("common_friends", aVoid);
                                    intent.putExtras(bundle);
                                    LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
                                }
                            }.execute();
                        }
                    }
            ).executeAsync();
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) PriveTalkApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static String centimetersToFeetAndInches(int centimeters) {

        double feet = centimeters / 30.48;
        double inches = (centimeters / 2.54) - ((int) feet * 12);

        if ((int) Math.round(inches) == 12)
            return String.valueOf((int) Math.round(feet)) + "'0\"";
        else
            return String.valueOf((int) feet) + "'" + String.valueOf((int) Math.round(inches)) + "\"";
    }

    public static String getErrorMessage(VolleyError error) {
        if (error.networkResponse == null || error.networkResponse.data == null)
            return null;

        Log.d("Errormessage", new String(error.networkResponse.data));

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        try {
            jsonObject = new JSONObject(new String(error.networkResponse.data));
        } catch (JSONException ex) {
            ex.printStackTrace();
            if (error.networkResponse.statusCode == 413)
                return "File is too large";
        }

        try {
            jsonArray = new JSONArray(new String(error.networkResponse.data));
        } catch (JSONException ex) {
            ex.printStackTrace();
            if (error.networkResponse.statusCode == 413)
                return "File is too large";
        }

        if (jsonObject == null && jsonArray == null)
            return "Error";

        String message = "";

        // CASE IS JSONOBJECT
        if (jsonObject != null) {

            message = jsonObject.optString("detail");

            if (message.isEmpty()) {
                Iterator<String> keys = jsonObject.keys();
                if (keys.hasNext()) {
                    String next = keys.next();
                    JSONArray array = jsonObject.optJSONArray(next);
                    if (array != null)
                        message = array.optString(0);
                }
            }


        } else {
            message = jsonArray.optString(0);
        }

        return message.isEmpty() ? PriveTalkApplication.getInstance().getString(R.string.no_results) : message;
    }


}
