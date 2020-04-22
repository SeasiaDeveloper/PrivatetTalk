package com.privetalk.app.database;

/**
 * Created by zachariashad on 12/01/16.
 */
public class PriveTalkTables {

    public static class CurrentUserDetailsTable {

        public static final String TABLE_NAME = "CurrentUserDetailsTable";

        public static final String RELATIONSHIP_STATUS = "relationship_status";
        public static final String SEXUALITY_STATUS = "sexuality_status";
        public static final String SMOKING_STATUS = "smoking_status";
        public static final String DRINKING_STATUS = "drinking_status";
        public static final String EDUCATION_STATUS = "education_status";
        public static final String ZODIAC = "zodiac";
        public static final String HEIGHT = "height";
        public static final String WEIGHT = "weight";
        public static final String BODY_TYPE = "body_type";
        public static final String HAIR_COLOR = "hair_color";
        public static final String EYES_COLOR = "eyes_color";
        public static final String WORK = "work";
        public static final String ABOUT = "about";
        public static final String LANGUAGES = "languages";
        public static final String FAITH = "faith";
        public static final String INTERESTS = "interests";
        public static final String ID = "id";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                + RELATIONSHIP_STATUS + " INTEGER, " + SEXUALITY_STATUS + " INTEGER, " + SMOKING_STATUS + " INTEGER, "
                + DRINKING_STATUS + " INTEGER, " + EDUCATION_STATUS + " INTEGER, " + ZODIAC + " INTEGER, "
                + HEIGHT + " INTEGER, " + WEIGHT + " INTEGER, " + BODY_TYPE + " INTEGER, "
                + HAIR_COLOR + " INTEGER, " + EYES_COLOR + " INTEGER, " + WORK + " TEXT, "
                + ABOUT + " TEXT, " + LANGUAGES + " TEXT, " + FAITH + " INTEGER, "
                + INTERESTS + " TEXT " + ")";

    }

    public static class CurrentUserTable {

        public static final String TABLE_NAME = "CurrentUserTable";

        public static final String ID = "id";
        public static final String ROYAL_USER = "royal_user";


        public static final String FB_ID = "fbId";
        public static final String FB_USERNAME = "fbUsername";
        public static final String GPLUS_ID = "gplusId";
        public static final String VK_ID = "vkId";
        public static final String NAME = "member_name";
        public static final String LOCATION = "member_location";
        public static final String MAIL = "member_mail";
        public static final String GENRE = "member_genre";
        public static final String LOOKING_FOR = "looking_for";
        public static final String BIRTHDAY = "member_birthday";

        public static final String TOKEN = "member_token";

        public static final String PRV_HIDE_PUBLIC_SEARCH = "prv_hide_public_search"; // INT
        public static final String COINS = "coins"; // INT
        public static final String PHOTOS_VERIFIED = "photos_verified"; // INT
        public static final String SOCIAL_VERIFIED = "social_verified"; // INT
        public static final String VERIFIED_PHOTO_FRONT = "verified_photo_front";// STRING
        public static final String VERIFIED_PHOTO_LEFT = "verified_photo_left";// STRING
        public static final String VERIFIED_PHOTO_RIGHT = "verified_photo_right";// STRING
        public static final String IS_SUPER_USER = "is_super_user";

        public static final String THIS_WEEK_PROFILE_VISITS = "this_week_profile_visits";
        public static final String THIS_WEEK_CONVERSATIONS = "this_week_conversations";
        public static final String THIS_WEEK_REPLY_PERCENTAGE = "this_week_reply_percentage";
        public static final String THIS_WEEK_VOTES_CASTED = "this_week_votes_casted";
        public static final String THIS_WEEK_POSITIVE_VOTES = "this_week_positive_votes";
        public static final String THIS_WEEK_FREE_COINS = "this_week_free_coins";
        public static final String THIS_WEEK_PURCHASE_BONUS = "this_week_purchase_bonus";
        public static final String HOTNESS_PERCENTAGE = "hotness_percentage";
        public static final String PROMOTED = "promoted";
        public static final String LAST_WEEK_PURCHASE_BONUS = "last_week_purchase_bonus";
        public static final String LAST_WEEK_PURCHASE_BONUS_USED = "last_week_purchase_bonus_used";
        public static final String NAME_EDITED = "name_edited";
        public static final String BIRTHDAY_EDITED = "birthday_edited";
        public static final String PROFILE_PHOTO = "profilePhoto";
        public static final String HIDDEN_MODE_ON = "hidden_mode_on";
        public static final String LAST_LOGIN = "last_login";
        public static final String PHOTOS_VERIFIED_REJECTED = "photos_verified_rejected";
        public static final String SHARED_APP = "shared_app";

        //shared_app_tag
//        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
//                + NAME + " TEXT, " + ROYAL_USER + " INTEGER, " + THIS_WEEK_PROFILE_VISITS + " INTEGER, "
//                + BIRTHDAY + " INTEGER, " + LOCATION + " TEXT, " + GENRE + " INTEGER, "
//                + LOOKING_FOR + " INTEGER, " + MAIL + " TEXT, " + PRV_HIDE_PUBLIC_SEARCH + " INTEGER, "
//                + FB_ID + " TEXT, " + FB_USERNAME + " TEXT, " + COINS + " INTEGER, "
//                + GPLUS_ID + " TEXT, " + VK_ID + " TEXT, " + PHOTOS_VERIFIED + " INTEGER, "
//                + SOCIAL_VERIFIED + " INTEGER, " + VERIFIED_PHOTO_FRONT + " TEXT, " + LAST_LOGIN + " TEXT, "
//                + VERIFIED_PHOTO_RIGHT + " TEXT, " + IS_SUPER_USER + " TEXT, " + VERIFIED_PHOTO_LEFT + " TEXT, "
//                + THIS_WEEK_CONVERSATIONS + " INTEGER, " + THIS_WEEK_REPLY_PERCENTAGE + " REAL, " + THIS_WEEK_VOTES_CASTED + " INTEGER, "
//                + THIS_WEEK_POSITIVE_VOTES + " INTEGER, " + THIS_WEEK_FREE_COINS + " INTEGER, " + THIS_WEEK_PURCHASE_BONUS + " INTEGER, "
//                + HOTNESS_PERCENTAGE + " REAL, " + PROMOTED + " INTEGER, " + LAST_WEEK_PURCHASE_BONUS + " INTEGER, "
//                + LAST_WEEK_PURCHASE_BONUS_USED + " INTEGER, " + NAME_EDITED + " INTEGER, " + BIRTHDAY_EDITED + " INTEGER, "
//                + PROFILE_PHOTO + " INTEGER, " + HIDDEN_MODE_ON + " INTEGER, " + PHOTOS_VERIFIED_REJECTED +  "  INTEGER, "
//                + TOKEN + " TEXT, " + SHARED_APP + " INTEGER " + ")";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                + NAME + " TEXT, " + ROYAL_USER + " INTEGER, " + THIS_WEEK_PROFILE_VISITS + " INTEGER, "
                + BIRTHDAY + " INTEGER, " + LOCATION + " TEXT, " + GENRE + " INTEGER, "
                + LOOKING_FOR + " INTEGER, " + MAIL + " TEXT, " + PRV_HIDE_PUBLIC_SEARCH + " INTEGER, "
                + FB_ID + " TEXT, " + FB_USERNAME + " TEXT, " + COINS + " INTEGER, "
                + GPLUS_ID + " TEXT, " + VK_ID + " TEXT, " + PHOTOS_VERIFIED + " INTEGER, "
                + SOCIAL_VERIFIED + " INTEGER, " + VERIFIED_PHOTO_FRONT + " TEXT, " + LAST_LOGIN + " TEXT, "
                + VERIFIED_PHOTO_RIGHT + " TEXT, " + IS_SUPER_USER + " TEXT, " + VERIFIED_PHOTO_LEFT + " TEXT, "
                + THIS_WEEK_CONVERSATIONS + " INTEGER, " + THIS_WEEK_REPLY_PERCENTAGE + " REAL, " + THIS_WEEK_VOTES_CASTED + " INTEGER, "
                + THIS_WEEK_POSITIVE_VOTES + " INTEGER, " + THIS_WEEK_FREE_COINS + " INTEGER, " + THIS_WEEK_PURCHASE_BONUS + " INTEGER, "
                + HOTNESS_PERCENTAGE + " REAL, " + PROMOTED + " INTEGER, " + LAST_WEEK_PURCHASE_BONUS + " INTEGER, "
                + LAST_WEEK_PURCHASE_BONUS_USED + " INTEGER, " + NAME_EDITED + " INTEGER, " + BIRTHDAY_EDITED + " INTEGER, "
                + PROFILE_PHOTO + " INTEGER, " + HIDDEN_MODE_ON + " INTEGER, " + PHOTOS_VERIFIED_REJECTED +  "  INTEGER, "
                + TOKEN + " TEXT " + ")";


    }

    public static class CurrentUserPhotosTable {

        public static final String TABLE_NAME = "CurrentUserPhotosTable";

        public static final String ID = "id";
        public static final String PHOTO = "photo";
        public static final String THUMB = "thumb";
        public static final String SQUARE_PHOTO = "square_photo";
        public static final String SQUARE_THUMB = "square_thumb";
        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";
        public static final String MEDIUM_THUMB = "medium_thumb";
        public static final String LARGE_THUMB = "large_thumb";
        public static final String MEDIUM_SQUARE_THUMB = "medium_square_thumb";
        public static final String LARGE_SQUARE_THUMB = "large_square_thumb";
        public static final String VERIFIED_PHOTO = "verified_photo";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                + SQUARE_PHOTO + " TEXT, " + SQUARE_THUMB + " TEXT, " + PHOTO + " TEXT, "
                + MEDIUM_THUMB + " TEXT, " + LARGE_THUMB + " TEXT, " + MEDIUM_SQUARE_THUMB + " TEXT, " + LARGE_SQUARE_THUMB + " TEXT, "
                + IS_MAIN_PROFILE_PHOTO + " INTEGER, " + VERIFIED_PHOTO + " INTEGER, " + THUMB + " TEXT )";


    }

    public static class ProfilePhotosTable {

        public static final String TABLE_NAME = "ProfilePhotosTable";

        public static final String ID = "id";
        public static final String PHOTO = "photo";
        public static final String THUMB = "thumb";
        public static final String SQUARE_PHOTO = "square_photo";
        public static final String SQUARE_THUMB = "square_thumb";
        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";
        public static final String MEDIUM_THUMB = "medium_thumb";
        public static final String LARGE_THUMB = "large_thumb";
        public static final String MEDIUM_SQUARE_THUMB = "medium_square_thumb";
        public static final String LARGE_SQUARE_THUMB = "large_square_thumb";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                + SQUARE_PHOTO + " TEXT, " + SQUARE_THUMB + " TEXT, " + PHOTO + " TEXT, "
                + MEDIUM_THUMB + " TEXT, " + LARGE_THUMB + " TEXT, " + MEDIUM_SQUARE_THUMB + " TEXT, " + LARGE_SQUARE_THUMB + " TEXT, "
                + IS_MAIN_PROFILE_PHOTO + " INTEGER, " + THUMB + " TEXT )";

    }


    public static class ETagTable {

        public static final String TABLE_NAME = "ETagTable";

        public static final String LINK = "link";
        public static final String ETAG = "etag";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + LINK + " TEXT PRIMARY KEY NOT NULL, "
                + ETAG + " TEXT " + ")";
    }

    public static class CoinsPlansTable {

        public static final String TABLE_NAME = "CoinsPlansTable";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String COINS = "coins";
        public static final String EXTRA_COINS = "extra_coins";
        public static final String ANDROID_PRODUCT_ID = "android_product_id";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                + NAME + " TEXT, " + COINS + " INTEGER, " + EXTRA_COINS + " INTEGER, "
                + ANDROID_PRODUCT_ID + " TEXT " + ")";
    }

    public static class AttributesTables {

        public static final int GENDERS = 0;
        public static final int BODY_TYPES = 1;
        public static final int HAIR_COLORS = 2;
        public static final int RELATIONSHIP_STATUSES = 3;
        public static final int SEXUALITY_STATUSES = 4;
        public static final int SMOKING_HABITS = 5;
        public static final int DRINKING_HABITS = 6;
        public static final int EDUCATION_LEVEL = 7;
        public static final int ZODIAC_SIGNS = 8;
        public static final int LANGUAGES = 9;
        public static final int SPEAKING_LEVELS = 10;
        public static final int FAITH = 11;
        public static final int FAITH_LEVELS = 12;
        public static final int USER_INTERESTS = 13;
        public static final int EYE_COLOR = 14;

        public static final String[] TABLES_NAME =
                {
                        "genders",
                        "body_types",
                        "hair_colors",
                        "relationship_statuses",
                        "sexuality_statuses",
                        "smoking_habits",
                        "drinking_habits",
                        "education_level",
                        "zodiac_signs",
                        "languages",
                        "speaking_levels",
                        "religions",
                        "faith_levels",
                        "user_interests",
                        "eyes_color" // WE NEED EYE COLORS!!!

                };

        public static final String VALUE = "value";
        public static final String DISPLAY = "display";

        public static String getCreateTableString(String tableName) {

            return "CREATE TABLE " + tableName + "(" + VALUE + " TEXT PRIMARY KEY NOT NULL, "
                    + DISPLAY + " TEXT " + ")";
        }


        public static synchronized String getTableString(String stringToCompare) {

            if (TABLES_NAME[GENDERS].equals(stringToCompare) || "gender".equals(stringToCompare)) {
                return TABLES_NAME[GENDERS];
            } else if (TABLES_NAME[BODY_TYPES].equals(stringToCompare) || "body_type".equals(stringToCompare)) {
                return TABLES_NAME[BODY_TYPES];
            } else if (TABLES_NAME[HAIR_COLORS].equals(stringToCompare) || "hair_color".equals(stringToCompare)) {
                return TABLES_NAME[HAIR_COLORS];
            } else if (TABLES_NAME[RELATIONSHIP_STATUSES].equals(stringToCompare) || "relationship_status".equals(stringToCompare)) {
                return TABLES_NAME[RELATIONSHIP_STATUSES];
            } else if (TABLES_NAME[SEXUALITY_STATUSES].equals(stringToCompare) || "sexuality_status".equals(stringToCompare)) {
                return TABLES_NAME[SEXUALITY_STATUSES];
            } else if (TABLES_NAME[SMOKING_HABITS].equals(stringToCompare) || "smoking_status".equals(stringToCompare)) {
                return TABLES_NAME[SMOKING_HABITS];
            } else if (TABLES_NAME[DRINKING_HABITS].equals(stringToCompare) || "drinking_status".equals(stringToCompare)) {
                return TABLES_NAME[DRINKING_HABITS];
            } else if (TABLES_NAME[EDUCATION_LEVEL].equals(stringToCompare) || "education_status".equals(stringToCompare)) {
                return TABLES_NAME[EDUCATION_LEVEL];
            } else if (TABLES_NAME[ZODIAC_SIGNS].equals(stringToCompare) || "zodiac".equals(stringToCompare)) {
                return TABLES_NAME[ZODIAC_SIGNS];
            } else if (TABLES_NAME[LANGUAGES].equals(stringToCompare) || "language".equals(stringToCompare)) {
                return TABLES_NAME[LANGUAGES];
            } else if (TABLES_NAME[FAITH].equals(stringToCompare) || "faith".equals(stringToCompare)) {
                return TABLES_NAME[FAITH];
            } else if (TABLES_NAME[USER_INTERESTS].equals(stringToCompare) || "interests".equals(stringToCompare)) {
                return TABLES_NAME[USER_INTERESTS];
            } else if (TABLES_NAME[SPEAKING_LEVELS].equals(stringToCompare) || "speaking_level".equals(stringToCompare)) {
                return TABLES_NAME[SPEAKING_LEVELS];
            } else if (TABLES_NAME[FAITH_LEVELS].equals(stringToCompare) || "faith_level".equals(stringToCompare)) {
                return TABLES_NAME[FAITH_LEVELS];
            } else if (TABLES_NAME[EYE_COLOR].equals(stringToCompare) || "eye_colors".equals(stringToCompare) || "eye_color".equals(stringToCompare) || "eyes_colors".equals(stringToCompare)) {
                return TABLES_NAME[EYE_COLOR];
            }

            return "";
        }
    }


    public static class CurrentUserProfileSettingsTable {

        public static final String TABLE_NAME = "CurrentUserProfileSettingsTable";

        //all rows
        public static final String USER_TOKEN = "user_settings_id";
        public static final String SHOW_DISTANCE = "show_distance";
        public static final String SHOW_WEEKLY_REWARDS = "show_weekly_rewards";
        public static final String SHOW_ONLINE_STATUS = "show_online_status";
        public static final String HIDE_FROM_PUBLIC_SEARCH = "hide_from_public_search";
        public static final String HIDE_FROM_HOT_WHEEL = "hide_from_hot_wheel";
        public static final String NOTIFICATIONS_FOR_MESSAGES_MOB = "notifications_for_messages_mob";
        public static final String NOTIFICATIONS_FOR_MESSAGES_MAIL = "notification_for_messages_mail";
        public static final String NOTIFICATIONS_FOR_VISITORS_MOB = "notifications_for_visitors_mob";
        public static final String NOTIFICATIONS_FOR_VISITORS_MAIL = "notifications_for_visitors_mail";
        public static final String NOTIFICATIONS_FOR_HOT_FLAMES_MOB = "notifications_for_hot_flames_mob";
        public static final String NOTIFICATIONS_FOR_HOT_FLAMES_MAIL = "notifications_for_hot_flames_mail";
        public static final String NOTIFICATIONS_FOR_HOT_MATCHES_MOB = "notifications_for_hot_matches_mob";
        public static final String NOTIFICATIONS_FOR_HOT_MATCHES_MAIL = "notifications_for_hot_matches_mail";
        public static final String NOTIFICATIONS_FOR_FAVORITE_YOU_MOB = "notifications_for_favorite_you_mob";
        public static final String NOTIFICATIONS_FOR_FAVORITE_YOU_MAIL = "notifications_for_favorite_you_mail";
        public static final String NOTIFICATIONS_FOR_ALERTS_MOB = "notifications_for_alerts_mob";
        public static final String NOTIFICATIONS_FOR_ALERTS_MAIL = "notifications_for_alerts_mail";
        public static final String NOTIFICATIONS_FOR_NEWS_MOB = "notifications_for_news_mob";
        public static final String NOTIFICATIONS_FOR_NEWS_MAIL = "notifications_for_news_mail";
        public static final String NOTIFICATIONS_FOR_AWARDS_MOB = "notifications_for_awards_mob";
        public static final String NOTIFICATIONS_FOR_AWARDS_MAIL = "notifications_for_awards_mail";
        public static final String DEACTIVATED = "deactivated_account";
        public static final String LANGUAGE = "user_language";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( " + USER_TOKEN + " INTEGER PRIMARY KEY NOT NULL, " +
                SHOW_DISTANCE + " INTEGER, " + SHOW_WEEKLY_REWARDS + " INTEGER, " + SHOW_ONLINE_STATUS + " INTEGER, " + HIDE_FROM_PUBLIC_SEARCH + " INTEGER, " +
                HIDE_FROM_HOT_WHEEL + " INTEGER, " + NOTIFICATIONS_FOR_MESSAGES_MOB + " INTEGER, " + NOTIFICATIONS_FOR_MESSAGES_MAIL + " INTEGER, " +
                NOTIFICATIONS_FOR_VISITORS_MOB + " INTEGER, " + NOTIFICATIONS_FOR_VISITORS_MAIL + " INTEGER, " + NOTIFICATIONS_FOR_HOT_FLAMES_MOB + " INTEGER, " +
                NOTIFICATIONS_FOR_HOT_FLAMES_MAIL + " INTEGER, " + NOTIFICATIONS_FOR_HOT_MATCHES_MOB + " INTEGER, " + NOTIFICATIONS_FOR_HOT_MATCHES_MAIL + " INTEGER, " +
                NOTIFICATIONS_FOR_FAVORITE_YOU_MOB + " INTEGER, " + NOTIFICATIONS_FOR_FAVORITE_YOU_MAIL + " INTEGER, " + NOTIFICATIONS_FOR_ALERTS_MOB + " INTEGER, " +
                NOTIFICATIONS_FOR_ALERTS_MAIL + " INTEGER, " + NOTIFICATIONS_FOR_NEWS_MOB + " INTEGER, " + NOTIFICATIONS_FOR_NEWS_MAIL + " INTEGER, " +
                NOTIFICATIONS_FOR_AWARDS_MOB + " INTEGER, " + NOTIFICATIONS_FOR_AWARDS_MAIL + " INTEGER, " + DEACTIVATED + " INTEGER, " + LANGUAGE + " TEXT " + ")";


    }


    public static class CommunityUsersTable {

        public static final String TABLE_NAME = "CommunityUsersTable";

        public static final String USER_ID = "user_id";
        public static final String NAME = "user_name";
        public static final String BIRTDATE = "user_birthdate";
        public static final String AGE = "user_age";
        public static final String IS_ONLINE = "is_online";
        public static final String SOCIAL_VERIFIED = "social_verified";
        public static final String ROYAL_USER = "royal_user";
        public static final String PHOTOS_VERIFIED = "photos_verified";
        public static final String PHOTO_ID = "photo_id";

        public static final String PHOTO = "photo";
        public static final String MEDIUM_THUMB = "medium_thumb";
        public static final String LARGE_THUMB = "large_thumb";
        public static final String THUMB = "thumb";

        public static final String SQUARE_PHOTO = "square_photo";
        public static final String SQUARE_THUMB = "square_thumb";
        public static final String MEDIUM_SQUARE_THUMB = "medium_square_thumb";
        public static final String LARGE_SQUARE_THUMB = "large_square_thumb";
        public static final String ORDER = "_order";
//        public static final String WAS_SEEN = "was_seen";

        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + USER_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + NAME + " TEXT, " + BIRTDATE + " INTEGER, " + AGE + " INTEGER, " + IS_ONLINE + " INTEGER, " + SOCIAL_VERIFIED + " INTEGER, "
                + ROYAL_USER + " INTEGER, " + PHOTO_ID + " INTEGER, " + PHOTO + " TEXT," + THUMB + " TEXT," + SQUARE_PHOTO + " TEXT, "
                + MEDIUM_THUMB + " TEXT," + LARGE_THUMB + " TEXT, " + MEDIUM_SQUARE_THUMB + " TEXT," + LARGE_SQUARE_THUMB + " TEXT, "
                + SQUARE_THUMB + " TEXT, " + IS_MAIN_PROFILE_PHOTO + " INTEGER, " + ORDER + " INTEGER, " + PHOTOS_VERIFIED + " INTEGER " + ")";

    }


    public static class PromotedUsersTable {

        public static final String TABLE_NAME = "PromotedUsersTable";

        public static final String OBJECT_ID = "object_id";
        public static final String PROMOTION_ID = "promotion_id";
        public static final String USER_ID = "user_id";
        public static final String NAME = "name";
        public static final String BIRTDAY = "birthday";
        public static final String AGE = "age";
        public static final String IS_ONLINE = "is_online";
        public static final String PHOTO_ID = "photo_id";
        public static final String PHOTO = "photo";
        public static final String THUMB = "thumb";
        public static final String SQUARE_PHOTO = "square_photo";
        public static final String SQUARE_THUMB = "square_thumb";
        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";
        public static final String PROMOTION_DATE = "promotion_date";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + OBJECT_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                PROMOTION_ID + " INTEGER, " + USER_ID + " INTEGER, " + NAME + " TEXT, " + BIRTDAY + " INTEGER, " + AGE + " INTEGER, " +
                IS_ONLINE + " INTEGER, " + PHOTO_ID + " INTEGER, " + PHOTO + " TEXT, " + THUMB + " TEXT, " + SQUARE_PHOTO + " TEXT, " +
                SQUARE_THUMB + " TEXT, " + IS_MAIN_PROFILE_PHOTO + " INTEGER, " + PROMOTION_DATE + " INTEGER " + ")";

    }


    public static class ProfileVisitorsTable {

        public static final String TABLE_NAME = "ProfileVisitosTable";

        public static final String VISIT_ID = "visit_id";
        public static final String USER_ID = "user_id";
        public static final String NAME = "user_name";
        public static final String BIRTHDAY = "birthday";
        public static final String AGE = "age";
        public static final String PROFILE_PHOTO_ID = "profile_photo_id";
        public static final String PHOTO = "photo";
        public static final String THUMB = "thumb";
        public static final String SQUARE_PHOTO = "square_photo";
        public static final String SQUARE_THUMB = "square_thumb";
        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";
        public static final String IS_ONLINE = "is_online";
        public static final String SOCIAL_VERIFIED = "social_verified";
        public static final String ROYAL_USER = "royal_user";
        public static final String PHOTOS_VERIFIED = "photos_verified";
        public static final String WEEK = "week";
        public static final String YEAR = "year";
        public static final String LAST_VISIT_DATE = "last_visit_date";
        public static final String PROFILE = "profile";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + VISIT_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                USER_ID + " INTEGER, " + NAME + " TEXT, " + BIRTHDAY + " INTEGER, " + AGE + " INTEGER, " + PROFILE_PHOTO_ID + " INTEGER, " +
                PHOTO + " TEXT, " + THUMB + " TEXT, " + SQUARE_PHOTO + " TEXT, " + SQUARE_THUMB + " TEXT, " + IS_MAIN_PROFILE_PHOTO + " INTEGER, " +
                IS_ONLINE + " INTEGER, " + SOCIAL_VERIFIED + " INTEGER, " + ROYAL_USER + " INTEGER, " + PHOTOS_VERIFIED + " INTEGER, " +
                WEEK + " INTEGER, " + YEAR + " INTEGER, " + LAST_VISIT_DATE + " INTEGER, " + PROFILE + " INTEGER " + ")";
    }


    public static class FavoritesTable {

        public static final String TABLE_NAME = "FavoritesTable";

        public static final String ID = "id_";
        public static final String USER_ID = "user_id";
        public static final String NAME = "user_name";
        public static final String BIRTDATE = "user_birthdate";
        public static final String AGE = "user_age";
        public static final String IS_ONLINE = "is_online";
        public static final String SOCIAL_VERIFIED = "social_verified";
        public static final String ROYAL_USER = "royal_user";
        public static final String PHOTOS_VERIFIED = "photos_verified";
        public static final String PHOTO_ID = "photo_id";
        public static final String PHOTO = "photo";
        public static final String THUMB = "thumb";
        public static final String SQUARE_PHOTO = "square_photo";
        public static final String SQUARE_THUMB = "square_thumb";
        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";
        public static final String IS_FAVORITE = "is_favorite";
        public static final String LAST_UPDATE = "last_update";
        public static final String FAVORITE_BY = "favorite_by";
        public static final String PROFILE = "profile";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, " +
                USER_ID + " INTEGER, " + NAME + " TEXT, " + BIRTDATE + " INTEGER, " + AGE + " INTEGER, " + IS_ONLINE + " INTEGER, " +
                SOCIAL_VERIFIED + " INTEGER, " + ROYAL_USER + " INTEGER, " + PHOTO_ID + " INTEGER, " + PHOTO + " TEXT, " +
                THUMB + " TEXT, " + SQUARE_PHOTO + " TEXT, " + SQUARE_THUMB + " TEXT, " + IS_MAIN_PROFILE_PHOTO + " INTEGER, " +
                IS_FAVORITE + " INTEGER, " + LAST_UPDATE + " INTEGER, " + FAVORITE_BY + " INTEGER, " + PROFILE + " INTEGER, "
                + PHOTOS_VERIFIED + " INTEGER " + ")";

    }


    public static class HotMatchesTable {

        public static final String TABLE_NAME = "HotMatchesTable";

        public static final String MATCH_ID = "user_id";
        public static final String NAME = "user_name";
        public static final String GENDER = "gender";
        public static final String LOOKING_FOR = "looking_for";
        public static final String BIRTDATE = "user_birthdate";
        public static final String AGE = "user_age";
        public static final String SOCIAL_VERIFIED = "social_verified";
        public static final String ROYAL_USER = "royal_user";
        public static final String PHOTOS_VERIFIED = "photos_verified";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String LAST_LOCATION_UPDATE = "last_update";
        public static final String ISO_COUNTRY_CODE = "iso_country_code";
        public static final String POSTAL_CODE = "postal_code";
        public static final String ADMIN_AREA = "admin_area";
        public static final String PHOTO_ID = "photo_id";
        public static final String PHOTO = "photo";
        public static final String THUMB = "thumb";
        public static final String SQUARE_PHOTO = "square_photo";
        public static final String SQUARE_THUMB = "square_thumb";
        public static final String MEDIUM_THUMB = "medium_thumb";
        public static final String LARGE_THUMB = "large_thumb";
        public static final String MEDIUM_SQUARE_THUMB = "medium_square_thumb";
        public static final String LARGE_SQUARE_THUMB = "large_square_thumb";
        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";
        public static final String ORDER = "order_";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + MATCH_ID + " INTEGER PRIMARY KEY NOT NULL, " + GENDER + " INTEGER, "
                + LOOKING_FOR + " INTEGER, " + NAME + " TEXT, " + BIRTDATE + " INTEGER, " + AGE + " INTEGER, " + LATITUDE + " TEXT, " + LONGITUDE + " TEXT, "
                + PHOTO_ID + " INTEGER, " + PHOTO + " TEXT, " + THUMB + " TEXT, " + SQUARE_PHOTO + " TEXT, " + SQUARE_THUMB + " TEXT, " + IS_MAIN_PROFILE_PHOTO + " INTEGER, "
                + LAST_LOCATION_UPDATE + " INTEGER, " + ISO_COUNTRY_CODE + " TEXT, " + POSTAL_CODE + " TEXT, " + ADMIN_AREA + " TEXT, " + SOCIAL_VERIFIED + " INTEGER, "
                + MEDIUM_THUMB + " TEXT, " + LARGE_THUMB + " TEXT, " + MEDIUM_SQUARE_THUMB + " TEXT, " + LARGE_SQUARE_THUMB + " TEXT, "
                + ORDER + " INTEGER, " + ROYAL_USER + " INTEGER, " + PHOTOS_VERIFIED + " INTEGER " + ")";


    }


    public static class ConfigurationScoresTable {

        public static final String TABLE_NAME = "ConfigurationScoresTable";

        public static final String TYPE = "type";

        public static final String MAX = "max";
        public static final String SORT_WEIGHT = "sort_weight";

        public static final String BENCHMARK1_VALUE = "benchmark1_value";
        public static final String BENCHMARK1_PURCHASE_BONUS = "benchmark1_purchase_bonus";
        public static final String BENCHMARK1_COINS = "benchmark1_coins";
        public static final String BENCHMARK2_VALUE = "benchmark2_value";
        public static final String BENCHMARK2_PURCHASE_BONUS = "benchmark2_purchase_bonus";
        public static final String BENCHMARK2_COINS = "benchmark2_coins";
        public static final String BENCHMARK3_VALUE = "benchmark3_value";
        public static final String BENCHMARK3_PURCHASE_BONUS = "benchmark3_purchase_bonus";
        public static final String BENCHMARK3_COINS = "benchmark3_coins";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + TYPE + " TEXT PRIMARY KEY NOT NULL, "
                + MAX + " INTEGER, " + SORT_WEIGHT + " REAL, " + BENCHMARK1_VALUE + " INTEGER, "
                + BENCHMARK1_PURCHASE_BONUS + " INTEGER, " + BENCHMARK1_COINS + " INTEGER, "
                + BENCHMARK2_VALUE + " INTEGER, " + BENCHMARK2_PURCHASE_BONUS + " INTEGER, " + BENCHMARK2_COINS
                + " INTEGER, " + BENCHMARK3_VALUE + " INTEGER, " + BENCHMARK3_PURCHASE_BONUS + " INTEGER, "
                + BENCHMARK3_COINS + " INTEGER " + ")";

    }


    public static class ConversationsTable {

        public static final String TABLE_NAME = "ConversationsTable";

        public static final String PARTNER_ID = "partner_id";
        public static final String PARTNER_NAME = "partner_name";
        public static final String IS_ROYAL_USER = "is_royal_user";
        public static final String VERIFICATION_LEVEL = "verification_level";
        public static final String AVATAR_IMG = "avatar_img";
        public static final String AGE = "age";
        public static final String DESCRIPTION = "description";
        public static final String IS_HOT_MATCH = "is_hot_match";
        public static final String LAST_MSG_TIMESTAMP = "last_msg_tms";
        public static final String IS_READ = "is_read";
        public static final String CAN_VOTE = "can_vote";
        public static final String VOTE_CASTED = "vote_casted";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + PARTNER_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + PARTNER_NAME + " TEXT, " + IS_ROYAL_USER + " TEXT, " + VERIFICATION_LEVEL + " INTEGER, " + VOTE_CASTED + " INTEGER, "
                + AVATAR_IMG + " TEXT, " + AGE + " INTEGER, " + DESCRIPTION + " TEXT, " + LAST_MSG_TIMESTAMP
                + " INTEGER, " + IS_READ + " INTEGER, " + CAN_VOTE + " INTEGER, " + IS_HOT_MATCH + " INTEGER " + ")";


    }


    public static class MessagesTable {

        public static final String TABLE_NAME = "MessagesTable";

        public static final String MSG_ID = "msg_id";
        public static final String SENDER_ID = "sender_id";
        public static final String RECEIVER_ID = "receiver_id";
        public static final String MTYPE = "mtype";
        public static final String DATA = "data";
        public static final String LIFESPAN = "lifespan";
        public static final String EXPIRES_ON = "expires_on";
        public static final String CREATED_ON = "created_on";
        public static final String ATTACHEMENT_DATA = "attachement_data";
        public static final String CAN_VOTE = "can_vote";
        public static final String VOTE_CASTED = "vote_casted";
        public static final String GLOBAL_TIME = "global_time";
        public static final String EXPIRED = "expired";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + MSG_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + SENDER_ID + " INTEGER, " + RECEIVER_ID + " INTEGER, " + MTYPE + " TEXT, " + DATA + " TEXT, "
                + LIFESPAN + " INTEGER, " + EXPIRES_ON + " INTEGER, " + CAN_VOTE + " INTEGER, " + VOTE_CASTED + " INTEGER, "
                + GLOBAL_TIME + " INTEGER, " + CREATED_ON + " INTEGER, " + EXPIRED + " INTEGER, " + ATTACHEMENT_DATA + " TEXT " + ")";
    }


    public static class GiftsTables {

        public static final String TABLE_NAME = "GiftsTable";

        public static final String GIFT_ID = "gift_id";
        public static final String COST = "cost";
        public static final String ALT_TEXT = "alt_text";
        public static final String THUMB = "thumb";
        public static final String ORDER = "order_";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + GIFT_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + COST + " INTEGER, " + ALT_TEXT + " TEXT, " + ORDER + " INTEGER, " + THUMB + " TEXT " + ")";
    }


    public static class TimeStepsTable {

        public static final String TABLE_NAME = "TimeStepsObject";

        public static final String STAMP_ID = "stamp_id";
        public static final String SECONDS = "seconds";
        public static final String MINUTES = "minutes";
        public static final String HOURS = "hours";
        public static final String DAYS = "days";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + STAMP_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                SECONDS + " INTEGER, " + MINUTES + " REAL, " + HOURS + " REAL, " + DAYS + " REAL " + ")";

    }


    public class InterestTable {

        public static final String TABLE_NAME = "InterestTable";

        public static final String ID = "id";
        public static final String INTEREST_TYPE = "interest_type";
        public static final String TITLE = "title";
        public static final String USER_ADDED = "user_added";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " TEXT PRIMARY KEY NOT NULL, "
                + USER_ADDED + " INTEGER, " + INTEREST_TYPE + " TEXT, " + TITLE + " TEXT " + ")";

    }


    public static class NotificationsTable {

        public static final String TABLE_NAME = "NotificationsTable";

        public static final String ID = "_id";
        public static final String SENDER_ID = "sender_id";
        public static final String SENDER_NAME = "sender_name";
        public static final String THUMB_PHOTO = "thumb_photo";
        public static final String MESSAGE = "message";
        public static final String TYPE = "type";
        public static final String CREATED_ON = "created_on";
        public static final String RECEIVER = "receiver";
        public static final String NOTIFICATION_READ = "_read";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                + SENDER_ID + " INTEGER, " + SENDER_NAME + " TEXT, " + THUMB_PHOTO + " TEXT, " + MESSAGE + " TEXT, "
                + TYPE + " TEXT, " + CREATED_ON + " INTEGER, " + NOTIFICATION_READ + " INTEGER, " + RECEIVER + " INTEGER " + ")";
    }


    public static class BadgesTable {

        public static final String TABLE_NAME = "BadgesTable";

        public static final String ID = "id";
        public static final String VISITORS = "visitors";
        public static final String MESSAGES = "messages";
        public static final String FLAMES = "flames";
        public static final String MATCHES = "matches";
        public static final String FAVORITES = "favorites";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                + VISITORS + " TEXT, " + MATCHES + " TEXT, " + MESSAGES + " TEXT, " + FLAMES + " TEXT, " + FAVORITES + " TEXT " + ")";
    }

//    public static class HotWheelTable {
//
//
//        public static final String TABLE_NAME = "HotWheelTable";
//
//        public static final String USER_ID = "user_id";
//        public static final String NAME = "user_name";
//        public static final String GENDER = "gender";
//        public static final String LOOKING_FOR = "looking_for";
//        public static final String BIRTDATE = "user_birthdate";
//        public static final String AGE = "user_age";
//        public static final String SOCIAL_VERIFIED = "social_verified";
//        public static final String ROYAL_USER = "royal_user";
//        public static final String PHOTOS_VERIFIED = "photos_verified";
//        public static final String LATITUDE = "latitude";
//        public static final String LONGITUDE = "longitude";
//        public static final String LAST_LOCATION_UPDATE = "last_update";
//        public static final String ISO_COUNTRY_CODE = "iso_country_code";
//        public static final String POSTAL_CODE = "postal_code";
//        public static final String ADMIN_AREA = "admin_area";
//
//
//        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + MATCH_ID + " INTEGER PRIMARY KEY NOT NULL, " + GENDER + " INTEGER, " +
//                LOOKING_FOR + " INTEGER, " + NAME + " TEXT, " + BIRTDATE + " INTEGER, " + AGE + " INTEGER, " + LATITUDE + " TEXT, " + LONGITUDE + " TEXT, " +
//                LAST_LOCATION_UPDATE + " INTEGER, " + ISO_COUNTRY_CODE + " TEXT, " + POSTAL_CODE + " TEXT, " + ADMIN_AREA + " TEXT, " + SOCIAL_VERIFIED + " INTEGER, " +
//                ROYAL_USER + " INTEGER, " + PHOTOS_VERIFIED + " INTEGER " + ")";
//
////                + PHOTO_ID + " INTEGER, " + PHOTO + " TEXT, " + THUMB + " TEXT, " + SQUARE_PHOTO + " TEXT, " + SQUARE_THUMB + " TEXT, " +
////                IS_MAIN_PROFILE_PHOTO +
//    }
//
//    public static class HotWheelPicturesTable {
//
//
//        public static final String TABLE_NAME = "HotWheelPicturesTable";
//
//        public static final String MATCH_ID = "user_id";
//        public static final String PHOTO_ID = "photo_id";
//        public static final String PHOTO = "photo";
//        public static final String THUMB = "thumb";
//        public static final String SQUARE_PHOTO = "square_photo";
//        public static final String SQUARE_THUMB = "square_thumb";
//        public static final String IS_MAIN_PROFILE_PHOTO = "is_main_profile_photo";
//
//        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + PHOTO_ID + " INTEGER PRIMARY KEY NOT NULL, " + MATCH_ID + " INTEGER, " + PHOTO + " TEXT, "
//                + THUMB + " TEXT, " + SQUARE_PHOTO + " TEXT, " + SQUARE_THUMB + " TEXT, " + IS_MAIN_PROFILE_PHOTO + " INTEGER " + ")";
//
//
//    }


}
