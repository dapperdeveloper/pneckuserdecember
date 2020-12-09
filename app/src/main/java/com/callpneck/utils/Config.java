package com.callpneck.utils;

import com.callpneck.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tibi on 13/05/16.
 */
public class Config {

    //// Log settings

    public static boolean ISDEBUG = BuildConfig.DEBUG;
    public static String LOG_TAG = "ZippySpot";

    ////

    //// Beacon settings

    // public static String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"; //Estimote

    public static String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24," +
            "d:41-41,d:42-42,d:43-43,d:44-44"; //Kontakt IO

    public static String DEFAULT_UUID = "0be0e745-bfb0-46b1-a69a-6732ad21f51f";

    // Background Beacon Scan
    public static final long BACKGROUND_SCAN_PERIOD = 2000L; // 2 sec
    public static final long BACKGROUND_SCAN_PERIOD_GAP = 20000L; // 20 sec

    // Foreground Beacon Scan
    public static final long FOREGROUND_SCAN_PERIOD = 1500L; // 1.5 sec
    public static final long FOREGROUND_SCAN_PERIOD_GAP = 0L; // 0 sec

    public static final double BEACON_PROXIMITY = 1.1; // Meters

    ////

//    public static final String API_TOKEN = "2TpmvypsUVYEyKEtuVXsLUDVV8g3kEp9";

    public static final String API_TOKEN = "FUXzTKazanmX7yys_wJYEErSKZudNH5Xw_XtzHwQX9TLqkJzo8kJ5kY8n-oB5_1y";

    //    public static final String SERVER_URL = "http://dev.zippyspot.io/";
//    public static final String SERVER_URL = "https://zippyprod.herokuapp.com/";
    public static final String SERVER_URL = "https://zippyspot.net/";
    public static final String REST_URL = SERVER_URL + "api/1/"; //"api/1/"

    public static final String CHARACTER_ENCODING = "UTF-8";

    public static final boolean USE_HTTPS_CONNECTION = SERVER_URL.toLowerCase().startsWith("https");

    public static final int CONNECTION_TIMEOUT = 20; //in sec
    public static final int READ_TIMEOUT = 20; //in sec
    public static final int WRITE_TIMEOUT = 20; //in sec

    //    public static final String GOOGLE_CLIENT_ID = "856146939850-gbdn70o7cc3et60gbc4jc1hn062vjmb8.apps.googleusercontent.com";
    public static final String GOOGLE_CLIENT_ID = "1017053909191-e1aihcioh5f5j7htjonn2j0thn6obplj.apps.googleusercontent.com";
    public static final long ANIMATION_DURATION = 800L;
    public static final long ANIMATION_DELAY = 400L;

    public static final String[] CATEGORY_NAMES = new String[]{"all", "art_gallery",
            "beauty_salon",
            "cafe",
            "clothing_store",
            "grocery_or_supermarket",
            "restaurant",
            "shoe_store",
            "shopping_mall",
            "spa"};

    public static final String[] CATEGORY_WS_NAMES = new String[]{"all", "art_gallery",
            "beauty_salon",
            "cafe",
            "clothing_store",
            "grocery_or_supermarket",
            "restaurant",
            "shoe_store",
            "shopping_mall",
            "spa"};

    public static List<String> demo = new ArrayList<>();
    public static  List<String> demo1 = new ArrayList<>();
//    public static String DEFAULT_CATEGORY_FOR_WS = String.valueOf(demo1);
//    public static String DEFAULT_CATEGORY_FOR_NAME = String.valueOf(demo);
    public static String DEFAULT_CATEGORY_FOR_WS = CATEGORY_WS_NAMES[0];
    public static String DEFAULT_CATEGORY_FOR_NAME = CATEGORY_NAMES[0];

    //Status code config
    public static final int AUTH_ERROR_CODE = 401;

    //Push OS
    public static final String PUSH_OS = "android";

}
