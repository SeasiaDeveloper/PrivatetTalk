package com.privetalk.app.mainflow.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pushbots.push.Pushbots;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserProfileSettingsDatasource;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.ProfileSettings;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.MySwitch;
import com.privetalk.app.utilities.NotificationSettingContainer;
import com.privetalk.app.utilities.PrivacySettingContainer;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;
import com.privetalk.app.welcome.WelcomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class SettingsFragment extends FragmentWithTitle {

    private View rootView;

    private static final String SHOW_DISTANCE = "prv_show_distance";
    private static final String SHOW_ONLINE_STATUS = "prv_show_online_status";
    private static final String HIDE_PUBLIC_SEARCH = "prv_hide_public_search";
    private static final String HIDE_HOT_WHEEL = "prv_hide_hot_wheel";
    private static final String NO_MESSAGES_PHONE = "not_messages_mob";
    private static final String NO_MESSAGES_MAIL = "not_messages_mail";
    private static final String NO_VISITORS_PHONE = "not_visitors_mob";
    private static final String NO_VISITORS_MAIL = "not_visitors_mail";
    private static final String NO_FLAMES_PHONE = "not_flames_mob";
    private static final String NO_FLAMES_MAIL = "not_flames_mail";
    private static final String NO_MATCHES_PHONE = "not_matches_mob";
    private static final String NO_MATCHES_MAIL = "not_matches_mail";
    private static final String NO_FAVORITE_PHONE = "not_favored_mob";
    private static final String NO_FAVORITE_MAIL = "not_favored_mail";
    private static final String NO_ALERTS_PHONE = "not_alerts_mob";
    private static final String NO_ALERTS_MAIL = "not_alerts_mail";
    private static final String NO_NEWS_PHONE = "not_news_mob";
    private static final String NO_NEWS_MAIL = "not_news_mail";
    private static final String NO_AWARDS_PHONE = "not_awards_mob";
    private static final String NO_AWARDS_MAIL = "not_awards_mail";


    private Drawable crowDrawable;
    private Drawable whiteDrawable;

    private PrivacySettingContainer showDistance;
    private PrivacySettingContainer showOnlineStatus;
    private PrivacySettingContainer hideFromPublicSearch;
    private PrivacySettingContainer hideFromHotWheel;

    private NotificationSettingContainer messagesNotifications;
    private NotificationSettingContainer visitorNotifications;
    private NotificationSettingContainer flamesNotifications;
    private NotificationSettingContainer matchesNotifications;
    private NotificationSettingContainer favoriteNotifications;
    //    private NotificationSettingContainer alertsNotifications;
    private NotificationSettingContainer newsNotifications;
    private NotificationSettingContainer rewardNotifications;
    private MySwitch deactivateAccount;
    private String title;
    private CurrentUser currentUser;
    private ProfileSettings profileSettingsObject;
    private JSONObject obj;
    private AlertDialog accountDeactivateDialog;
    private JsonObjectRequest changeSettingsRequest;
    private BroadcastReceiver settingsBroadcastReceiver;
    private BroadcastReceiver userDownloadedReceiver;
    private TextView deleteAccount;

    private List<View> allSwitchContainersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }
        obj = new JSONObject();

        profileSettingsObject = CurrentUserProfileSettingsDatasource.getInstance(getContext()).getCurrentUserProfileSettings();

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        getSwitchDrawables();

        initViews();

        updateSettings();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(settingsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateSettings();
            }
        }, new IntentFilter(PriveTalkConstants.BROADCAST_SETTINGS_DOWNLOADED));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(userDownloadedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                PriveTalkUtilities.getCurrentUserProfileSettings();
            }
        }, new IntentFilter(PriveTalkConstants.BROADCAST_CURRENT_USER_UPDATED));

        PriveTalkUtilities.getUserInfoFromServer();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(settingsBroadcastReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(userDownloadedReceiver);
    }

    private void initViews() {

        deleteAccount = (TextView) rootView.findViewById(R.id.deleteAccount);

        //privacy settings
        showOnlineStatus = (PrivacySettingContainer) rootView.findViewById(R.id.switch3);
        showOnlineStatus.setText(R.string.show_online_status);
        showDistance = (PrivacySettingContainer) rootView.findViewById(R.id.switch1);
        showDistance.setText(R.string.show_distance);
        hideFromPublicSearch = (PrivacySettingContainer) rootView.findViewById(R.id.switch4);
        hideFromPublicSearch.setText(R.string.hide_from_public_search);
        hideFromHotWheel = (PrivacySettingContainer) rootView.findViewById(R.id.switch5);
        hideFromHotWheel.setText(R.string.hide_from_hot_wheel);
        messagesNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.messagesNotifications);
        messagesNotifications.setText(R.string.messages);
        visitorNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.visitorsNotifications);
        visitorNotifications.setText(R.string.profileVisitors);
        flamesNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.flamesNotifications);
        flamesNotifications.setText(R.string.flamesIgnited);
        matchesNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.matchesNotifications);
        matchesNotifications.setText(R.string.hotMatches);
        favoriteNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.favoriteNotifications);
        favoriteNotifications.setText(R.string.settings_favorite_you);
//        alertsNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.alertsNotifications);
//        alertsNotifications.setText(R.string.settings_alerts);
        newsNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.newsNotifications);
        newsNotifications.setText(R.string.settings_news);
        rewardNotifications = (NotificationSettingContainer) rootView.findViewById(R.id.rewardsNotifications);
        rewardNotifications.setText(R.string.settings_weekly_awards);

        //deactivate account
        deactivateAccount = (MySwitch) rootView.findViewById(R.id.deactivateAccount);

        //set white thumb to all switches
        showDistance.getMySwitch().setThumbDrawable(whiteDrawable);
        messagesNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
        messagesNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
        visitorNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
        visitorNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
        flamesNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
        flamesNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
        matchesNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
        matchesNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
        favoriteNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
        favoriteNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
//        alertsNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
//        alertsNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
        newsNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
        newsNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
        rewardNotifications.getEmailSwitch().setThumbDrawable(whiteDrawable);
        rewardNotifications.getPhoneSwitch().setThumbDrawable(whiteDrawable);
        deactivateAccount.setThumbDrawable(whiteDrawable);


        //set crown thumb
        showOnlineStatus.getMySwitch().setThumbDrawable(crowDrawable);
        hideFromPublicSearch.getMySwitch().setThumbDrawable(crowDrawable);
        hideFromHotWheel.getMySwitch().setThumbDrawable(crowDrawable);


        allSwitchContainersList = new ArrayList<>();
        allSwitchContainersList.add(showDistance);
        allSwitchContainersList.add(messagesNotifications);
        allSwitchContainersList.add(visitorNotifications);
        allSwitchContainersList.add(flamesNotifications);
        allSwitchContainersList.add(matchesNotifications);
        allSwitchContainersList.add(favoriteNotifications);
//        allSwitchContainersList.add(alertsNotifications);
        allSwitchContainersList.add(newsNotifications);
        allSwitchContainersList.add(rewardNotifications);
        allSwitchContainersList.add(deactivateAccount);
        allSwitchContainersList.add(showOnlineStatus);
        allSwitchContainersList.add(hideFromPublicSearch);
        allSwitchContainersList.add(hideFromHotWheel);


        deactivateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deactivateAccount.isChecked()) {
                    accountDeactivateDialog = new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.you_are_about_deactivate_title))
                            .setMessage(getString(R.string.are_you_sure_deactivate))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    accountActivation(deactivateAccount.isChecked());
                                }
                            })
                            .setNegativeButton(getString(R.string.no_string), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deactivateAccount.setChecked(false);
                                }
                            }).create();
                    accountDeactivateDialog.show();
                } else {
                    accountActivation(deactivateAccount.isChecked());
                }
            }
        });


        View.OnClickListener switchesClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();

                switch (tag) {
                    case SHOW_DISTANCE:
                        toggleSwitch(SHOW_DISTANCE, showDistance.getMySwitch(), showDistance);
                        break;

                    case SHOW_ONLINE_STATUS: {
                        if (currentUser.royal_user)
                            toggleSwitch(SHOW_ONLINE_STATUS, showOnlineStatus.getMySwitch(), showOnlineStatus);
                        else
                            showRoyalDialog();
                    }
                    break;

                    case HIDE_PUBLIC_SEARCH: {
                        if (currentUser.royal_user)
                            toggleSwitch(HIDE_PUBLIC_SEARCH, hideFromPublicSearch.getMySwitch(), hideFromPublicSearch);
                        else
                            showRoyalDialog();
                    }
                    break;

                    case HIDE_HOT_WHEEL: {
                        if (currentUser.royal_user)
                            toggleSwitch(HIDE_HOT_WHEEL, hideFromHotWheel.getMySwitch(), hideFromHotWheel);
                        else
                            showRoyalDialog();
                    }
                    break;

                    case NO_MESSAGES_PHONE:
                        toggleSwitch(NO_MESSAGES_PHONE, messagesNotifications.getPhoneSwitch(), messagesNotifications);
                        break;

                    case NO_MESSAGES_MAIL:
                        toggleSwitch(NO_MESSAGES_MAIL, messagesNotifications.getEmailSwitch(), messagesNotifications);
                        break;

                    case NO_VISITORS_PHONE:
                        toggleSwitch(NO_VISITORS_PHONE, visitorNotifications.getPhoneSwitch(), visitorNotifications);
                        break;

                    case NO_VISITORS_MAIL:
                        toggleSwitch(NO_VISITORS_MAIL, visitorNotifications.getEmailSwitch(), visitorNotifications);
                        break;

                    case NO_FLAMES_PHONE:
                        toggleSwitch(NO_FLAMES_PHONE, flamesNotifications.getPhoneSwitch(), flamesNotifications);
                        break;

                    case NO_FLAMES_MAIL:
                        toggleSwitch(NO_FLAMES_MAIL, flamesNotifications.getEmailSwitch(), flamesNotifications);
                        break;

                    case NO_ALERTS_PHONE: {
//                        toggleSwitch(NO_ALERTS_PHONE, alertsNotifications.getPhoneSwitch(), alertsNotifications);
//                        if (alertsNotifications.getPhoneSwitch().isChecked())
//                            Pushbots.sharedInstance().tag("alerts");
//                        else
//                            Pushbots.sharedInstance().untag("alerts");
                    }
                    break;

                    case NO_ALERTS_MAIL:
//                        toggleSwitch(NO_ALERTS_MAIL, alertsNotifications.getEmailSwitch(), alertsNotifications);
                        break;

                    case NO_FAVORITE_MAIL:
                        toggleSwitch(NO_FAVORITE_MAIL, favoriteNotifications.getEmailSwitch(), favoriteNotifications);
                        break;

                    case NO_FAVORITE_PHONE:
                        toggleSwitch(NO_FAVORITE_PHONE, favoriteNotifications.getPhoneSwitch(), favoriteNotifications);
                        break;

                    case NO_MATCHES_PHONE:
                        toggleSwitch(NO_MATCHES_PHONE, matchesNotifications.getPhoneSwitch(), matchesNotifications);
                        break;

                    case NO_MATCHES_MAIL:
                        toggleSwitch(NO_MATCHES_MAIL, matchesNotifications.getEmailSwitch(), matchesNotifications);
                        break;

                    case NO_NEWS_PHONE: {
                        toggleSwitch(NO_NEWS_PHONE, newsNotifications.getPhoneSwitch(), newsNotifications);
                        if (newsNotifications.getPhoneSwitch().isChecked())
                            Pushbots.sharedInstance().tag("alerts");
                        else
                            Pushbots.sharedInstance().untag("alerts");
                    }
                    break;

                    case NO_NEWS_MAIL:
                        toggleSwitch(NO_NEWS_MAIL, newsNotifications.getEmailSwitch(), newsNotifications);
                        break;

                    case NO_AWARDS_PHONE:
                        toggleSwitch(NO_AWARDS_PHONE, rewardNotifications.getPhoneSwitch(), rewardNotifications);
                        break;

                    case NO_AWARDS_MAIL:
                        toggleSwitch(NO_AWARDS_MAIL, rewardNotifications.getEmailSwitch(), rewardNotifications);
                        break;
                }
            }
        };

        //set click listener to all switches
        setAllSwitchesClickListener(switchesClickListener);

        //set string tags to all switches
        showDistance.getMySwitch().setTag(SHOW_DISTANCE);
        showOnlineStatus.getMySwitch().setTag(SHOW_ONLINE_STATUS);
        hideFromPublicSearch.getMySwitch().setTag(HIDE_PUBLIC_SEARCH);
        hideFromHotWheel.getMySwitch().setTag(HIDE_HOT_WHEEL);
        messagesNotifications.getEmailSwitch().setTag(NO_MESSAGES_MAIL);
        messagesNotifications.getPhoneSwitch().setTag(NO_MESSAGES_PHONE);
        visitorNotifications.getEmailSwitch().setTag(NO_VISITORS_MAIL);
        visitorNotifications.getPhoneSwitch().setTag(NO_VISITORS_PHONE);
        flamesNotifications.getPhoneSwitch().setTag(NO_FLAMES_PHONE);
        flamesNotifications.getEmailSwitch().setTag(NO_FLAMES_MAIL);
        matchesNotifications.getPhoneSwitch().setTag(NO_MATCHES_PHONE);
        matchesNotifications.getEmailSwitch().setTag(NO_MATCHES_MAIL);
        favoriteNotifications.getPhoneSwitch().setTag(NO_FAVORITE_PHONE);
        favoriteNotifications.getEmailSwitch().setTag(NO_FAVORITE_MAIL);
//        alertsNotifications.getPhoneSwitch().setTag(NO_ALERTS_PHONE);
//        alertsNotifications.getEmailSwitch().setTag(NO_ALERTS_MAIL);
        newsNotifications.getPhoneSwitch().setTag(NO_NEWS_PHONE);
        newsNotifications.getEmailSwitch().setTag(NO_NEWS_MAIL);
        rewardNotifications.getPhoneSwitch().setTag(NO_AWARDS_PHONE);
        rewardNotifications.getEmailSwitch().setTag(NO_AWARDS_MAIL);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.are_you_sure_selete_account));
                builder.setMessage(getString(R.string.not_able_to_recover));
                builder.setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.create().show();
            }
        });
    }


    private void setAllSwitchesClickListener(View.OnClickListener onClickListener) {

        for (View view : allSwitchContainersList) {
            if (view instanceof PrivacySettingContainer)
                ((PrivacySettingContainer) view).setSwitchesClickListener(onClickListener);
            else if (view instanceof NotificationSettingContainer)
                ((NotificationSettingContainer) view).setSwitchesClickListener(onClickListener);

        }
    }


    private void allSwitchesSetEnabled(boolean enabled) {
        for (View view : allSwitchContainersList) {
            if (view instanceof PrivacySettingContainer) {
                ((PrivacySettingContainer) view).setSwitchEnable(enabled);
                if (enabled)
                    ((PrivacySettingContainer) view).showProgressBar(false);
            } else if (view instanceof NotificationSettingContainer) {
                ((NotificationSettingContainer) view).setSwitchEnable(enabled);
                if (enabled)
                    ((NotificationSettingContainer) view).showProgressBar(false);
            } else if ((view instanceof MySwitch))
                ((MySwitch) view).setEnabled(enabled);
        }
    }

    private synchronized void updateSettings() {

        if (profileSettingsObject != null) {

            deactivateAccount.setChecked(profileSettingsObject.deactivated);
            showDistance.getMySwitch().setChecked(profileSettingsObject.showDistance);
            showOnlineStatus.getMySwitch().setChecked(profileSettingsObject.showOnlineStatus);
            hideFromPublicSearch.getMySwitch().setChecked(profileSettingsObject.hideFromPublicSearch);
            hideFromHotWheel.getMySwitch().setChecked(profileSettingsObject.hideFromHotWheel);
            messagesNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForMessagesMob);
            messagesNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForMessagesMail);
            visitorNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForVisitorsMob);
            visitorNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForVisitorMail);
            flamesNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForHotFlamesMob);
            flamesNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForHotFlamesMail);
            matchesNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForHotMatchesMob);
            matchesNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForHotMatchesMail);
            favoriteNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForFavoriteYouMob);
            favoriteNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForFavoriteYouMail);
//            alertsNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForAlertsMob);
//            alertsNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForAlertsMail);
            newsNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForNewsMob);
            newsNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForNewsMail);
            rewardNotifications.getPhoneSwitch().setChecked(profileSettingsObject.notificationsForAwardsMob);
            rewardNotifications.getEmailSwitch().setChecked(profileSettingsObject.notificationsForAwardsMail);

            if (profileSettingsObject.notificationsForAlertsMob)
                Pushbots.sharedInstance().tag("alerts");
            else
                Pushbots.sharedInstance().untag("alerts");

            if (profileSettingsObject.notificationsForNewsMob)
                Pushbots.sharedInstance().tag("news");
            else
                Pushbots.sharedInstance().untag("news");
        }
    }


    private void toggleSwitch(String parameterName, SwitchCompat tempSwitchCompat, View view) {
        if (view instanceof PrivacySettingContainer)
            ((PrivacySettingContainer) view).showProgressBar(true);
        else if (view instanceof NotificationSettingContainer)
            ((NotificationSettingContainer) view).showProgressBar(true);
        toggleSwitch(parameterName, tempSwitchCompat);
    }


    private synchronized void toggleSwitch(String parameterName, final SwitchCompat tempSwitchCompat) {

//        tempSwitchCompat.setEnabled(false);
        allSwitchesSetEnabled(false);

        obj = new JSONObject();

        try {
            obj.put(parameterName, tempSwitchCompat.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        changeSettingsRequest = new JsonObjectRequest(Request.Method.POST, Links.SET_PROFILE_SETTINGS, obj,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

//                        tempSwitchCompat.setEnabled(true);
                        allSwitchesSetEnabled(true);
                        profileSettingsObject = new ProfileSettings(response);
                        CurrentUserProfileSettingsDatasource.getInstance(getContext()).saveCurrentUserProfileSettings(profileSettingsObject);
                        updateSettings();

//                        System.out.println("show distance toggle: " + response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                tempSwitchCompat.setEnabled(true);
                allSwitchesSetEnabled(true);
                updateSettings();
                Toast.makeText(getContext(), getString(R.string.error_updating_settings_message), Toast.LENGTH_SHORT).show();
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
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(getActivity().getApplicationContext()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(changeSettingsRequest);
    }


    private void getSwitchDrawables() {
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.switch_crow);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        crowDrawable = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap,
                        getResources().getDimensionPixelSize(R.dimen.switch_thumb_size),
                        getResources().getDimensionPixelSize(R.dimen.switch_thumb_size),
                        true));


        Drawable d1 = ContextCompat.getDrawable(getContext(), R.drawable.switch_normal);
        Bitmap bitmap1 = ((BitmapDrawable) d1).getBitmap();

        whiteDrawable = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap1,
                        getResources().getDimensionPixelSize(R.dimen.switch_thumb_size),
                        getResources().getDimensionPixelSize(R.dimen.switch_thumb_size),
                        true));
    }


    private void accountActivation(final boolean deactivate) {

        changeSettingsRequest = new JsonObjectRequest(Request.Method.POST, deactivate ? Links.DEACTIVATE_ACCOUNT : Links.ACTIVATE_ACCOUNT, new JSONObject(),

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        PriveTalkUtilities.clearAllSavedData();
                        Intent intent1 = new Intent(getActivity(), WelcomeActivity.class);
                        startActivity(intent1);
                        getActivity().finish();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                updateSettings();
                if (error.networkResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
//                        System.out.println("Error Message: " + new String(error.networkResponse.data, "UTF-8"));
//                        System.out.println("Error message: " + jsonObject.getString("error"));
                        Toast.makeText(getContext(), jsonObject.getString("detail"), Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(getActivity().getApplicationContext()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(changeSettingsRequest);


    }

    @Override
    public void onResume() {
        super.onResume();
        updateSettings();
//        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(backPressed, new IntentFilter(SETTINGS_FRAGMENT_BACK_PRESSED));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
//        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).unregisterReceiver(backPressed);
        if (changeSettingsRequest != null)
            changeSettingsRequest.cancel();
        if (accountDeactivateDialog != null)
            accountDeactivateDialog.cancel();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PriveTalkConstants.ACTION_BAR_TITLE, title);
    }

    @Override
    protected String getActionBarTitle() {
        return title;
    }

    private void showRoyalDialog() {
        AlertDialog.Builder builter = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.only_available)).
                setMessage(getString(R.string.royal_user_plans_hidden_mode)).
                setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                        intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ROYAL_USER_BENEFITS_FRAGMENT_ID);
                        intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
                    }
                }).setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateSettings();
            }
        });

        builter.create().show();
    }

    private void deleteAccount() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setMessage(getString(R.string.deleting_account));
        progressDialog.show();
        progressDialog.setCancelable(false);

        JsonObjectRequest deleteAccountRequest = new JsonObjectRequest(Request.Method.POST, Links.DELETE_ACCOUNT, new JSONObject(), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d("Delete", response.toString());
                progressDialog.dismiss();

                PriveTalkUtilities.clearAllSavedData();


                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.delete_account_message)
                        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }).create().show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressDialog != null)
                    progressDialog.dismiss();

                if (error.networkResponse != null) {
                    try {

                        String string = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(string);
                        Toast.makeText(getContext(), jsonObject.optString("detail"), Toast.LENGTH_LONG).show();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
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
        };

        VolleySingleton.getInstance(getContext()).addRequest(deleteAccountRequest);
    }

}
