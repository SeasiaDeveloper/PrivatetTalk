package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONObject;

/**
 * Created by zachariashad on 18/02/16.
 */
public class ProfileSettings {

    public int currentUserSettingsID;

    public boolean showDistance;
    public boolean showWeeklyRewards;
    public boolean showOnlineStatus;
    public boolean hideFromPublicSearch;
    public boolean hideFromHotWheel;
    public boolean notificationsForMessagesMob;
    public boolean notificationsForMessagesMail;
    public boolean notificationsForVisitorsMob;
    public boolean notificationsForVisitorMail;
    public boolean notificationsForHotFlamesMob;
    public boolean notificationsForHotFlamesMail;
    public boolean notificationsForHotMatchesMob;
    public boolean notificationsForHotMatchesMail;
    public boolean notificationsForFavoriteYouMob;
    public boolean notificationsForFavoriteYouMail;
    public boolean notificationsForAlertsMob;
    public boolean notificationsForAlertsMail;
    public boolean notificationsForNewsMob;
    public boolean notificationsForNewsMail;
    public boolean notificationsForAwardsMob;
    public boolean notificationsForAwardsMail;
    public boolean deactivated;

    public String languageCode;

    public ProfileSettings() {
    }


    public static ContentValues getProfileSettingsContentValues(ProfileSettings profileSettings) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.USER_TOKEN, profileSettings.currentUserSettingsID);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.SHOW_DISTANCE, (profileSettings.showDistance) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.SHOW_WEEKLY_REWARDS, (profileSettings.showWeeklyRewards) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.SHOW_ONLINE_STATUS, (profileSettings.showOnlineStatus) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.HIDE_FROM_PUBLIC_SEARCH, (profileSettings.hideFromPublicSearch) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.HIDE_FROM_HOT_WHEEL, (profileSettings.hideFromHotWheel) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_MESSAGES_MOB, (profileSettings.notificationsForMessagesMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_MESSAGES_MAIL, (profileSettings.notificationsForMessagesMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_VISITORS_MOB, (profileSettings.notificationsForVisitorsMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_VISITORS_MAIL, (profileSettings.notificationsForVisitorMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_FLAMES_MOB, (profileSettings.notificationsForHotFlamesMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_FLAMES_MAIL, (profileSettings.notificationsForHotFlamesMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_MATCHES_MOB, (profileSettings.notificationsForHotMatchesMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_MATCHES_MAIL, (profileSettings.notificationsForHotMatchesMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_FAVORITE_YOU_MOB, (profileSettings.notificationsForFavoriteYouMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_FAVORITE_YOU_MAIL, (profileSettings.notificationsForFavoriteYouMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_ALERTS_MOB, (profileSettings.notificationsForAlertsMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_ALERTS_MAIL, (profileSettings.notificationsForAlertsMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_NEWS_MOB, (profileSettings.notificationsForNewsMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_NEWS_MAIL, (profileSettings.notificationsForNewsMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_AWARDS_MOB, (profileSettings.notificationsForAwardsMob) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_AWARDS_MAIL, (profileSettings.notificationsForAwardsMail) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.DEACTIVATED, (profileSettings.deactivated) ? 1 : 0);
        contentValues.put(PriveTalkTables.CurrentUserProfileSettingsTable.LANGUAGE, profileSettings.languageCode);


        return contentValues;
    }


    public ProfileSettings(Cursor cursor) {
        currentUserSettingsID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.USER_TOKEN));
        showDistance = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.SHOW_DISTANCE)) == 1);
        showWeeklyRewards = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.SHOW_WEEKLY_REWARDS)) == 1);
        showOnlineStatus = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.SHOW_ONLINE_STATUS)) == 1);
        hideFromPublicSearch = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.HIDE_FROM_PUBLIC_SEARCH)) == 1);
        hideFromHotWheel = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.HIDE_FROM_HOT_WHEEL)) == 1);
        notificationsForMessagesMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_MESSAGES_MOB)) == 1);
        notificationsForMessagesMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_MESSAGES_MAIL)) == 1);
        notificationsForVisitorsMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_VISITORS_MOB)) == 1);
        notificationsForVisitorMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_VISITORS_MAIL)) == 1);
        notificationsForHotFlamesMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_FLAMES_MOB)) == 1);
        notificationsForHotFlamesMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_FLAMES_MAIL)) == 1);
        notificationsForHotMatchesMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_MATCHES_MOB)) == 1);
        notificationsForHotMatchesMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_HOT_MATCHES_MAIL)) == 1);
        notificationsForFavoriteYouMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_FAVORITE_YOU_MOB)) == 1);
        notificationsForFavoriteYouMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_FAVORITE_YOU_MAIL)) == 1);
        notificationsForAlertsMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_ALERTS_MOB)) == 1);
        notificationsForAlertsMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_ALERTS_MAIL)) == 1);
        notificationsForNewsMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_NEWS_MOB)) == 1);
        notificationsForNewsMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_NEWS_MAIL)) == 1);
        notificationsForAwardsMob = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_AWARDS_MOB)) == 1);
        notificationsForAwardsMail = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.NOTIFICATIONS_FOR_AWARDS_MAIL)) == 1);
        deactivated = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.DEACTIVATED)) == 1);
        languageCode = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserProfileSettingsTable.LANGUAGE));
    }

    public ProfileSettings(JSONObject obj) {

        currentUserSettingsID = obj.optInt("id");
        languageCode = obj.optString("language");

        showDistance = obj.optBoolean("prv_show_distance");
        showWeeklyRewards = obj.optBoolean("prv_show_weekly_rewards");
        showOnlineStatus = obj.optBoolean("prv_show_online_status");
        hideFromPublicSearch = obj.optBoolean("prv_hide_public_search");
        hideFromHotWheel = obj.optBoolean("prv_hide_hot_wheel");
        notificationsForMessagesMob = obj.optBoolean("not_messages_mob");
        notificationsForMessagesMail = obj.optBoolean("not_messages_mail");
        notificationsForVisitorsMob = obj.optBoolean("not_visitors_mob");
        notificationsForVisitorMail = obj.optBoolean("not_visitors_mail");
        notificationsForHotFlamesMob = obj.optBoolean("not_flames_mob");
        notificationsForHotFlamesMail = obj.optBoolean("not_flames_mail");
        notificationsForHotMatchesMob = obj.optBoolean("not_matches_mob");
        notificationsForHotMatchesMail = obj.optBoolean("not_matches_mail");
        notificationsForFavoriteYouMob = obj.optBoolean("not_favored_mob");
        notificationsForFavoriteYouMail = obj.optBoolean("not_favored_mail");
        notificationsForAlertsMob = obj.optBoolean("not_alerts_mob");
        notificationsForAlertsMail = obj.optBoolean("not_alerts_mail");
        notificationsForNewsMob = obj.optBoolean("not_news_mob");
        notificationsForNewsMail = obj.optBoolean("not_news_mail");
        notificationsForAwardsMob = obj.optBoolean("not_awards_mob");
        notificationsForAwardsMail = obj.optBoolean("not_awards_mail");
        deactivated = obj.optBoolean("deactivated");

    }

}
