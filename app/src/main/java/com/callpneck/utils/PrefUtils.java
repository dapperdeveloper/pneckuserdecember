package com.callpneck.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {
    public static final String AuthID = "AuthID";
    public static final String CLID = "CLID";
    public static final String UserName="UserName";
    public static final String RegId="RegId";
    public static final String setFlag = "setFlag";
    public static final String SessionID="SessionID";
    public static final String ProjectName="ProjectName";
    public static final String Logo="Logo";
    public static final String AppColor="AppColor";
    public static final String DLID="DLID";
    public static final String Main="Main";
    public static final String CurrentImagePro="CurrentImagePro";
    public static final String CART="CART";
    public static final String BiLogo ="BiLogo";
    public static final String CartCount="CartCount";
    public static final String NewUser="NewUser";


    public static String saveToPrefs(Context context, String key, String value) {
        if (key.equalsIgnoreCase("customerMasterId")) {
        }
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        return key;
    }


    public static void logout(Context c) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static String getFromPrefs(Context context, String key) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, "");

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}