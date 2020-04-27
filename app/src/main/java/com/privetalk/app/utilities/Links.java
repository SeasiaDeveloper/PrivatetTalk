package com.privetalk.app.utilities;

/**
 * Created by zachariashad on 13/01/16.
 */
public class Links {

    public static final String MAIN_URL = "http://np.seasiafinishingschool.com:7083";

//      public static final String MAIN_URL = "https://privetalk.herokuapp.com";
//testing url
//    public static final String MAIN_URL = "http://192.168.5.194:8000";

    public static final String VERIFY = MAIN_URL + "/api/accounts/verify/";

    public static final String REGISTER = MAIN_URL + "/api/accounts/register/";

    public static final String LOGIN = MAIN_URL + "/api/accounts/login/";

    public static final String MY_PROFILE = MAIN_URL + "/api/profiles/my/";

    public static final String GET_INSTAGRAM_TOKEN = "https://api.instagram.com/oauth/access_token";

    public static final String GET_INSTAGRAM_USER_DATA = "https://graph.instagram.com/";

    public static final String POST_LOCATION = MAIN_URL + "/api/profiles/my/location/";

    public static final String GO_ONLINE = MAIN_URL + "/api/profiles/my/actions/go_online/";

    public static final String GO_OFFLINE = MAIN_URL + "/api/profiles/my/actions/go_offline/";

    public static final String SET_PROFILE_SETTINGS = MAIN_URL + "/api/profiles/my/settings/";

    public static final String ALL_ATTRIBUTES = MAIN_URL + "/api/attributes/all_attributes/";

    public static final String PHOTOS = MAIN_URL + "/api/profiles/my/photos/";

    public static final String GET_PROFILE_SETTINGS = MAIN_URL + "/api/profiles/my/settings/";

    public static final String GET_COMMUNITY = MAIN_URL + "/api/profiles/my/community/";

    public static final String DEACTIVATE_ACCOUNT = MAIN_URL + "/api/profiles/my/actions/deactivate/";

    public static final String ACTIVATE_ACCOUNT = MAIN_URL + "/api/profiles/my/actions/activate/";

    public static final String CONFIGURATION_SCORES = MAIN_URL + "/api/configurations/scores/";

    public static final String PROMOTED_USERS = MAIN_URL + "/api/promotions/promoted_users/";

    public static final String VISIT_USER = MAIN_URL + "/api/profiles/my/actions/visit_profile/";

    public static final String GET_PROFILE_VISITORS = MAIN_URL + "/api/profiles/my/visitors/";

    public static final String GET_FAVORITES = MAIN_URL + "/api/profiles/my/favorites/";

    public static final String GET_FAVORITED_BY = MAIN_URL + "/api/profiles/my/favorited_by/";

    public static final String FAVORITE_USER = MAIN_URL + "/api/profiles/my/actions/make_favorite/";

    public static final String HOT_WHEEL = MAIN_URL + "/api/matches/hotwheel/";

    public static final String FLAMES_IGNITED = MAIN_URL + "/api/matches/flamesignited/";

    public static final String HOT_MATCHES = MAIN_URL + "/api/matches/hotmatches/";

    public static final String CONVERSATION_COST = MAIN_URL + "/api/configurations/chat/";

    public static final String HIDE_MATCH = "/hide_match/";

    public static final String COINS_BALANCE = MAIN_URL + "/api/profiles/my/coins_balance/";

    public static final String POSSIBLE_MATCHES = MAIN_URL + "/api/profiles/my/community/possible_matches/";

    public static final String PROMOTE_PICTURE = MAIN_URL + "/api/promotions/buy/";

    public static final String CONVERSATIONS = MAIN_URL + "/api/chats/conversations/";

    public static final String CREATE_MATCH = HOT_WHEEL + "%s/create_match/";

    public static final String RESPOND_MATCH = FLAMES_IGNITED + "%s/respond_to_match/";

    public static final String UNDO_MATCH = HOT_WHEEL + "%s/undo_match/";

    public static final String BLOCK_USER = MAIN_URL + "/api/profiles/my/block_user/";

    public static final String REPORT_USER = MAIN_URL + "/api/profiles/my/report_user/";

    public static final String GIFTS = MAIN_URL + "/api/chats/gifts/";

    public static final String MARK_AS_READ = "/mark-as-read/";

    public static final String VOTE = "/vote/";

    public static final String TIME_STEPS = MAIN_URL + "/api/chats/time_steps/";

    public static final String SUBSCRIBE = MAIN_URL + "/api/subscriptions/buy_android_subscription/";

    public static final String RECORD_VIEWS = MAIN_URL + "/api/profiles/my/actions/record_views/";

    public static final String CLAIM_REWARD = MAIN_URL + "/api/profiles/my/actions/claim_share_coins_reward/";

    public static final String ACTIVITIES = MAIN_URL + "/api/interests/activities/";
    public static final String MUSIC = MAIN_URL + "/api/interests/music/";
    public static final String LITERATURE = MAIN_URL + "/api/interests/literature/";
    public static final String PLACES = MAIN_URL + "/api/interests/places/";
    public static final String MOVIES = MAIN_URL + "/api/interests/movies/";
    public static final String OCCUPATION = MAIN_URL + "/api/interests/occupations/";
    public static final String SET_VISIBLE = MAIN_URL + "/api/profiles/my/actions/make_visible/";
    public static final String SET_HIDDEN = MAIN_URL + "/api/profiles/my/actions/make_hidden/";
    public static final String SEARCH_COMMUNITY = MAIN_URL + "/api/profiles/search/search/";
    public static final String COINS_PLANS = MAIN_URL + "/api/coins/plans/";
    public static final String BUY_ANDROID_PLAN = MAIN_URL + "/api/coins/buy_android_plan/";
    public static final String FIND_VOUCHER = MAIN_URL + "/api/coins/find_voucher_code/";

    public static final String ADVANCED_SEARCH_COMMUNITY = MAIN_URL + "/api/profiles/search/advanced_search/";

    public static final String NOTIFICATIONS = MAIN_URL + "/api/message_centre/notifications/";

    public static final String HIDE_CONVERSATION = "/mark-as-hidden/";

    public static final String PRIVACY_AND_POLICY = "http://app.privetalk.com/doc/privetalk-privacy-policy.pdf";
    public static final String TERMS_AND_CONDITIONS = "http://app.privetalk.com/doc/privetalk-terms-&-conditions.pdf";
    public static final String DATING_SAFETY = "http://app.privetalk.com/doc/privetalk-dating-safety-guide.pdf";
    public static final String FORGOT_PASSWORD = MAIN_URL + "/api/password/reset/";
    public static final String EXTRA_CHATS = MAIN_URL + "/api/chats/conversations/buy_extra_chats/";

    public static final String MY_BADGES = MAIN_URL + "/api/profiles/my/badges/";

    public static final String RESET_BADGE = MAIN_URL + "/api/profiles/my/badges/reset_badges/";
    public static final String DELETE_ACCOUNT = MAIN_URL + "/api/profiles/my/actions/delete_my_acount/";

    public static final String PRIVETALK_SITE = "app.privetalk.com";

    public static final String GLOBAL_TIME = "http://www.timeapi.org/utc/now";

    private static final String COMMUNITY_WITH_PAGING = MAIN_URL + "/api/profiles/my/community/?cache_key=";


    public static String getHideConversationUrl(int partnerID) {
        return CONVERSATIONS + String.valueOf(partnerID) + HIDE_CONVERSATION;
    }

    public static String getHideMatchUrl(int matchID) {
        return HOT_MATCHES + String.valueOf(matchID) + HIDE_MATCH;
    }

    public static String getMarkAsRead(int partnerID) {
        return CONVERSATIONS + String.valueOf(partnerID) + MARK_AS_READ;
    }

    public static String getVoteUrl(int partnerID) {
        return CONVERSATIONS + String.valueOf(partnerID) + VOTE;
    }


    public static String getCommunityWithPagingUrl() {
        return COMMUNITY_WITH_PAGING + String.valueOf(System.currentTimeMillis()) + "&page_size=15&pagination_on=Y";
//        + "&page_size=25";

    }

    public static final String ALOUPIA = "justForTesting";
}
