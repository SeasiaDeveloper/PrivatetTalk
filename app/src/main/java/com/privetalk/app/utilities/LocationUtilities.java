package com.privetalk.app.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.privetalk.app.PriveTalkApplication;

/**
 * Created by zachariashad on 18/05/16.
 */
public class LocationUtilities {


    public static void saveSelectedLocation(float latitude, float longitude) {

        try {
            longitude = Float.parseFloat(String.format("%.8f", longitude));
            latitude = Float.parseFloat(String.format("%.8f", latitude));
        }catch (Exception e){
            e.printStackTrace();
        }

        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putFloat(PriveTalkConstants.SELECTED_LATTITUDE, latitude).apply();
        preferences.edit().putFloat(PriveTalkConstants.SELECTED_LONGITUDE, longitude).apply();
    }

    public static void saveLocation(float latitude, float longitude) {
        if (latitude == 0f && longitude == 0f)
            return;
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putFloat(PriveTalkConstants.CURRENT_LATTITUDE, latitude).apply();
        preferences.edit().putFloat(PriveTalkConstants.CURRENT_LONGITUDE, longitude).apply();
    }

    public static void savePreviousLocation(float latitude, float longitude) {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putFloat(PriveTalkConstants.PREVIOUS_CURRENT_LATTITUDE, latitude).apply();
        preferences.edit().putFloat(PriveTalkConstants.PREVIOUS_CURRENT_LONGITUDE, longitude).apply();
    }


    public static float getSelectedLatitude() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(PriveTalkConstants.SELECTED_LATTITUDE, getLatitude());
    }

    public static float getSelectedLongitude() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(PriveTalkConstants.SELECTED_LONGITUDE, getLongitude());
    }

    public static float getLatitude() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(PriveTalkConstants.CURRENT_LATTITUDE, 0f);
    }

    public static float getPreviousLatitude() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(PriveTalkConstants.PREVIOUS_CURRENT_LATTITUDE, 0f);
    }

    public static float getLongitude() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(PriveTalkConstants.CURRENT_LONGITUDE, 0f);
    }

    public static float getPreviousLongitude() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(PriveTalkConstants.PREVIOUS_CURRENT_LONGITUDE, 0f);
    }

    public static void setPostalCode(String postalCode) {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(PriveTalkConstants.POSTAL_CODE, postalCode).apply();
    }

    public static void setCountryCode(String countryCode) {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(PriveTalkConstants.COUNTRY_CODE, countryCode).apply();
    }

    public static void setSelectedCountryCode(String countryCode) {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(PriveTalkConstants.SELECTED_COUNTRY_CODE, countryCode).apply();
    }

    public static void setAdminArea(String adminArea) {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(PriveTalkConstants.ADMINISTRATIVE_AREA, adminArea).apply();
    }


    public static String getPostalCode() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(PriveTalkConstants.POSTAL_CODE, null);
    }


    public static String getSelectedCountryCode() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(PriveTalkConstants.SELECTED_COUNTRY_CODE, null);
    }

    public static String getAdminArea() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(PriveTalkConstants.ADMINISTRATIVE_AREA, null);
    }

    public static String getCountryCode() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(PriveTalkConstants.COUNTRY_CODE, null);
    }

}
