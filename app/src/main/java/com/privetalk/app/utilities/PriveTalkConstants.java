package com.privetalk.app.utilities;

import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by zeniosagapiou on 14/01/16.
 */
public class PriveTalkConstants {

    //    date formats
    public static final SimpleDateFormat BIRTHDAY_FORMAT = new SimpleDateFormat("dd, MMM yyyy", Locale.US);
    public static final SimpleDateFormat PROMOTION_DATE = new SimpleDateFormat("dd, MMM yyyy - HH:mm", Locale.US);
    public static final SimpleDateFormat conversationDateFormat = new SimpleDateFormat("dd, MMM yyyy - HH:mm:ssZ", Locale.US);
    public static final SimpleDateFormat globalDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

    public static final int BOOST_POPULARITY_FRAGMENT_ID = 0;
    public static final int PERSONALEDIT_FRAGMENT_ID = 1;
    public static final int PERSONALEDIT_INFO_PAGER_FRAGMENTPARENT_ID = 2;
    public static final int SEARCH_FILTER_FRAGMENT_ID = 3;
    public static final int PERSONALEDIT_ACTIVITIES_PAGER_FRAGMENTPARENT_ID = 4;
    public static final int ROYAL_USER_PLANS_FRAGMENT_ID = 5;
    public static final int ADD_MORE_PT_COINS_FRAGMENT = 6;
    public static final int ADD_MORE_COINS_USE_FRAGMENT = 7;
    public static final int CHAT_FRAGMENT = 8;
    public static final int FULL_SCREEN_PICTURES_FRAGMENT = 9;
    public static final int OTHER_USER_PROFILE_INFO = 10;
    public static final int OTHER_USER_FULL_SCREEN_PHOTOS = 11;
    public static final int ROYAL_USER_BENEFITS_FRAGMENT_ID = 12;
    public static final int ABOUT_ME_FRAGMENT_ID = 13;
    public static final int MAP_FRAGMENT_ID = 14;
    public static final int SETTINGS_FRAGMENT = 15;
    public static final int PROFILE_FRAGMENT = 16;

    public static final String ACTION_BAR_TITLE = "action-bar-title";

    public static final java.lang.String PROFILE_EDIT_PARENT_TYPE = "profile-edit-parent_type";
    public static final String NO_TOKEN = "no-token";

    public static final String BUNDLE_CURRENT_USER = "bundle_current_user";
    public static final String DOWNLOAD_COMPLETED = "download-completed";
    public static final String BROADCAST_CURRENT_USER_UPDATED = "current-user-updated";
    public static final String BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED = "configurations-scores-downloaded";
    public static final String BROADCAST_COINS_PLANS_DOWNLOADED = "coins-plans-downloaded";
    public static final String BROADCAST_RECURSIVE_FAILED = "recursiveFailed";
    public static final String SELECTED_LATTITUDE = "selected-lattitude";
    public static final String SELECTED_LONGITUDE = "selected-longitude";
    public static final String BROADCAST_SETTINGS_DOWNLOADED = "broadcast_settings_downloaded";
    public static final String CURRENT_CHAT_GUY = "current-chat-guy";
    public static final String KEY_MESSAGE = "key-message";
    public static final String PARTNER_ID = "partner-id";
    public static final String CLAIM_REWARD = "claim-reward";
    public static final String SHARED_APP = "shared_app";
    public static final String COUNTRY_CODE_FOR_SEARCH = "country_code_for_search";
    public static final String CURRENT_LONGITUDE_FOR_SEARCH = "current_longitude_for_search";
    public static final String CURRENT_LATTITUDE_FOR_SEARCH = "current_lattitude_for_search";
    public static final String COMMUNITY_TAG = "community_tag";


    public static Typeface robottoRegular;
    public static Typeface robottoBold;
    public static Typeface robottoLight;

    public static SimpleDateFormat COMMUNITY_USERS_DATE_FORMAT = new SimpleDateFormat("dd, MMM yyyy", Locale.US); //sample date:  23, Nov 1987


    /*
    KEY STRINGS
     */

    public static final boolean LOG_DEBUG = true;

    public static final String CHANGE_ACTIONBAR_TITLE = "change-actionbar-title";

    public static final String PREFERENCES = "preferences";
    public static final String KEYBOARD_HEIGHT = "keyborad-height";
    public static final String KEY_SHOW_HOW_TO_VERIFY = "how-t0-verify";
    public static final String KEY_UP = "key-up";
    public static final String BACK_BUTTON = "back-button";
    public static final String HIDE_KEYBOARD = "hide-keyboard";
    public static final String KEY_OTHER_USER_ID = "other_user_id";
    public static final String KEY_PUSHBOTS_SUBSCRIPTION = "pushbots_subscribed";
    public static final String KEY_SEARCH_FILTER = "search_filter";
    public static final String KEY_EVENT_TYPE = "event_type";
    public static final String KEY_IMAGE_URL = "imageUrl";
    public static final String KEY_EXPIRE_TIME = "expireTime";
    public static final String KEY_PUSH_DATA = "pushData";
    public static final String KEY_CONVERSATION_COST = "conversations-cost";
    public static final String KEY_CONVERSATION_COST_UPDATE = "conversation-cost-last-update";
    public static final String KEY_FIRST_LOGIN = "first-loging";
    public static final String KEY_CURRENT_MAIN_FRAGMENT = "currrent-fragment";
    public static final String KEY_IMAGE_ABSOLUTE_PATH = "image-absolute-path";
    public static final String KEY_PARTNER_ID = "partner_id";
    public static final String KEY_WELCOME_SCREENS = "welcome-screens";
    public static final String KEY_FLAMES_INSTRUCTIONS_READ = "flames-instructions";
    public static final String KEY_HOT_WHEEL_INSTRUCTIONS_READ = "hot-wheel-instructions";
    public static final String KEY_GLOBAL_TIME = "global-time";
    public static final String KEY_MUST_BE_ROYAL = "MUST_BE_ROYAL";
    public static final String PREVIOUS_CURRENT_LATTITUDE = "previous-current-lattitude";
    public static final String PREVIOUS_CURRENT_LONGITUDE = "previous-current-longitude";
    public static final String KEY_SHOULD_UPDATE_COMMUNITY = "update-community";
    public static final String KEY_FAIL = "failed";
    public static final String STOP_COMMUNITY_RECURSIVE = "community-recursive";

    public static final String TAG_UPLOAD_IMAGE = "Upload Image";

    public static final String CURRENT_LATTITUDE = "current-lattitude";
    public static final String CURRENT_LONGITUDE = "current-longtitude";
    public static final String POSTAL_CODE = "postal-code";
    public static final String COUNTRY_CODE = "country-code";
    public static final String ADMINISTRATIVE_AREA = "administrative-area";
    public static final String SELECTED_COUNTRY_CODE = "sel-admin-area";

    public static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA27Vx2UWkxKpkcDV9pSJfb8DDd0FeHd865jPWXlcSHYTK3N253r1RRt3EbnKB0j/cDl9MV8HWfSYGafim6cB4V33MGpOTUV+j2zpQ18JopMwMqN9M9YNqmO//v4X9Eo59Ub1VCZ4bhX13Ok6lO469pU6TzBQPKupNwYU50izXC3PrOuEWvBXv6sX7qJe8CZsl3rLgDEG5/TcMdPfad1EbY/FHjBk12B881ACK2o7JCDGFbzgSg+Mfv01TFDw0tRD4RdlWZpkBUgkQVlqnb4dorXmgO6ONKXZu9NcRqqaqwq6OhSxxkmwQj4a696k0is1T4R679kUMVaRUwHnh8eOOXwIDAQAB";

    /*
    DIAFORA
     */

    public static final int MINIMUM_SEARCH_DISTANCE = 30;
    public static final int MAXIMUM_SEARCH_DISTANCE = 150;
    public static final int MINIMUM_AGE = 18;
    public static final int MAXIMUM_AGE = 80;
    public static final int MINIMUM_HEGHT = 142;
    public static final int MAXIMUM_HEIGHT = 211;
    public static final int MINIMUM_WEIGHT = 40;
    public static final int MAXIMUM_WEIGHT = 145;
    public static final int MAXIMUM_PHOTO_SIZE = 1920;
    public static final int MAXIMUM_MESSAGE_PHOTO_SIZE = 1920;
    public static final long EIGTHEEN_YEARS_SECONDS = 568024668;
    public static final long TWO_HOURS = 7200000;
    public static final int KILOBYTE = 1024;
    public static final float MAXIMUM_IMAGE_SIZE_BEFORE_COMPRESSION = 2500;  //value in kb (raw img size)
    public static final int MINIMUM_COMPRESSION_QUALITY = 55;
    public static final int COMPRESSED_IMAGE_QUALITY_PERCENT = 60;
    public static final int IMAGE_BLURRINESS = 25;


    /**
     * personal info edit ORDER
     **/
    public static final int PROFILE_EDIT_NAME = 0;
    public static final int PROFILE_EDIT_AGE = 1;
    public static final int PROFILE_EDIT_LOCATION = 2;
    public static final int PROFILE_EDIT_RELATIONSHIP = 3;
    public static final int PROFILE_EDIT_SEXUALITY = 4;
    public static final int PROFILE_EDIT_HEIGHT = 5;
    public static final int PROFILE_EDIT_WEIGHT = 6;
    public static final int PROFILE_EDIT_BODY_TYPE = 7;
    public static final int PROFILE_EDIT_COLOUR_OF_HAIR = 8;
    public static final int PROFILE_EDIT_COLOUR_OF_EYES = 9;
    public static final int PROFILE_EDIT_SMOKING = 10;
    public static final int PROFILE_EDIT_DRINKING = 11;
    public static final int PROFILE_EDIT_EDUCATION = 12;
    public static final int PROFILE_EDIT_WORK = 13;
    public static final int PROFILE_EDIT_LANGUAGE = 14;
    public static final int PROFILE_EDIT_RELIGION = 15;

    public static final int INTERESTS_EDIT_ACTIVITIES = 0;
    public static final int INTERESTS_EDIT_MUSIC = 1;
    public static final int INTERESTS_EDIT_MOVIES = 2;
    public static final int INTERESTS_EDIT_LITERATURE = 3;
    public static final int INTERESTS_EDIT_PLACES = 4;


    /*UTILITY CODES*/
    public static final String UTILITY_ACTION = "utility_action";
    public static final int PICTURE_MAXIMUM_THRESHOLD = 1920;

    public enum UtilityCommands {
        SHOW_UPLOADING_PICTURE_TOAST, SHOW_UPLOADING_PICTURE_FINISHED, SHOW_UPLOADING_PROFILE_PHOTO_FAILED,
        SHOW_UPLOADING_PICTURES_FINISHED, SHOW_UPLOADING_PROFILE_PHOTOS_FAILED, CURRENT_USER_SAVE_FAILED, USER_BLOCKED, USER_REPORTED, ANAUTHORIZED_SESSION,
        BLOCK_FAILED, REPORT_FAILED, SHOW_VISIBILITY_CHANGED, CURRENT_USER_SAVED, NOTIFICATIONS_UPADATED, SHOW_MY_PROFILE, MENU_BADGES_RECEIVED, VERIFICATION_PENDING, CLAIM_COINS
    }

    public enum MenuBadges {VISITORS_BADGE, MESSAGES_BAGE, FLAMES_BADGE, MATCHES_BADGE, FAVORITES_BADGE}

    /*PROFILE PHOTOS BROADCAST STUFF*/
    public enum ProfilePhotosActions {
        PHOTOS_UPLOADED, PHOTOS_DOWNLOADED
    }

    public static final String TIME_PROMOTED_USERS_UPDATED = "last_time_updated_promoted";

    public static final String BROADCAST_NEW_PHOTO_UPLOADED = "new-photo-uploaded";
    public static final String BROADCAST_PHOTOS_DOWNLOADED = "new-photo-downloaded";

    /* COMMUNITY BROADCAST STUFF */
    public static final String COMMUNITY_DOWNLOAD_BROADCAST = "community-downloaded";

    /**/
//    public static final int MAXIMUM_PERSON_HEIGHT = 240;
//    public static final int MINIMUM_PERSON_HEIGHT = 140;

    public static final int MAXIMUM_PERSON_WEIGHT = 145;
    public static final int MINIMUM_PERSON_WEIGHT = 40;
    public static final float ONE_KILOGRAM_TO_LBS = 2.20462f;

    /* PROMOTED USERS DOWNLODED BROADCAST RECEIVER*/
    public static final String BROADCAST_PROMOTED_USERS_DOWNLOADED = "promoted-users-downloaded";

    /* PROFILE VISITORS BROADCAST*/
    public static final String BROADCAST_PROFILE_VISITORS_DOWNLOADED = "profile-visitors-downloaded";

    /* FAVORITES BROADCAST*/
    public static final String BROADCAST_FAVORITES_DOWNLOADED = "favorites-downloded";
    public static final String BROADCAST_FAVORITED_BY_DOWNLOADED = "favorite-by-downloaded";

    /* HOT MATCHES BROADCAST*/
    public static final String BROADCAST_HOT_MATCHES_DOWNLOADED = "hot-matches-downloded";

    /*CONVERSATIONS BROADCAST*/
    public static final String BROADCAST_CONVERSATIONS_DOWNLOADED = "conversations-downloaded";

    /*MESSAGE RECEIVED BROADCAST*/
    public static final String BROADCAST_MESSAGE_RECEIVED = "message_received";

    /* TIMESTEPS DOWNLOADED BROADCAST*/
    public static final String BROADCAST_TIMESTEPS_DOWNLOADED = "timesteps-downloaded";

    /* BROADCAST GIFT PRESSED*/
    public static final String BROADCAST_GIFT_PRESSED = "broadcast-gift-pressed";

    /*BROADCAST CHAT ADAPTER CLICKS*/
    public static final String BROADCAST_CHAT_ADAPTER_CLICK = "broadcast-chat-adapter-click";

    /*OTHER USERS PROFILE RECEIVER*/
    public static final String BROADCAST_OTHER_USERS_RECEIVER = "other_users-receiver";

    /*COMMUNITY RECEIVER*/
    public static final String BROADCAST_COMMUNITY_RECEIVER = "community-receiver";

    /*PHOTO UPLOAD*/
    public static final String BROADCAST_PHOTO_UPLOADED = "photo-uploaded";

    public static final String LOGIN_WITH_EMAIL = "LOGIN_WITH_EMAIL";


//    /*BROADCAST NOTIFICATIONS DOWNLOADED*/
//    public static final String BROADCAST_NOTIFICATIONS_DOWNLOADED = "broadcast-notifications-downloaded";

    public class Zodiac {
        public static final int UNSPECIFIED = 0;
        public static final int ARIES = 1;
        public static final int TAURUS = 2;
        public static final int GEMINI = 3;
        public static final int CANCER = 4;
        public static final int LEO = 5;
        public static final int VIRGO = 6;
        public static final int LIBRA = 7;
        public static final int SCORPIO = 8;
        public static final int SAGITARIUS = 9;
        public static final int CAPRICORN = 10;
        public static final int AQUARIUS = 11;
        public static final int PISCES = 12;
    }

    public class Interests {
        public static final String ACTIVITY = "a";
        public static final String LITERATURE = "l";
        public static final String MUSIC = "m";
        public static final String PLACES = "p";
        public static final String MOVIES = "mo";
    }

    public final static ArrayList<Integer> height = new ArrayList<>();

    static {
        {
            height.add(142);
            height.add(145);
            height.add(147);
            height.add(150);
            height.add(152);
            height.add(155);
            height.add(157);
            height.add(160);
            height.add(163);
            height.add(165);
            height.add(168);
            height.add(170);
            height.add(173);
            height.add(175);
            height.add(178);
            height.add(180);
            height.add(183);
            height.add(185);
            height.add(188);
            height.add(191);
            height.add(193);
            height.add(196);
            height.add(198);
            height.add(201);
            height.add(203);
            height.add(206);
            height.add(208);
            height.add(211);
        }
    }


    public static final ArrayList<String> PLANS = new ArrayList<>();

    static {
        PLANS.add("one_month");
        PLANS.add("three_month");
        PLANS.add("six_month");
        PLANS.add("twelve_month");
//        PLANS.add("coins_plan_a");
//        PLANS.add("coins_plan_b");
//        PLANS.add("coins_plan_c");
//        PLANS.add("coins_plan_test");
//        PLANS.add("plan_test");
    }

}
