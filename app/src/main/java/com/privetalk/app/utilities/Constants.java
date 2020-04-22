package com.privetalk.app.utilities;

/**
 * Created by zachariashad on 21/01/16.
 */
public class Constants {

    public static final String LOGGED = "user-status";

    public static final String CLIENT_ID = "client_id";
    public static final String CODE = "code";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRANT_TYPE = "grant_type";
    public static final String REDIRECT_URL = "redirect_uri";


    public static final String PREFERENCES = "preferences";

    public final static float BIG_SCALE = 1.0f;


    public final static int PAGES = 10;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 10;
    public final static int FIRST_PAGE = 9;
    public final static float SMALL_SCALE = 0.8f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
}
