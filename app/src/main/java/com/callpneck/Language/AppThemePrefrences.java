package com.callpneck.Language;

import android.content.Context;
import android.content.SharedPreferences;

public class AppThemePrefrences {

    public static final String APP_LANGAUGE = "APP_LANGAUGE";
    public static final String TAB_ORDER = "TAB_ORDER";

    public static void SetSharedPreference(Context ctx, String Key, String Value) {
        SharedPreferences pref = ctx.getSharedPreferences("com.callpneck", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Key, Value);
        editor.apply();
    }


    public static String GetSharedPreference(Context ctx, String Key) {
        SharedPreferences pref = ctx.getSharedPreferences("com.callpneck", Context.MODE_PRIVATE);

        if (pref.contains(Key)) {
            return pref.getString(Key, "");
        } else
            return null;
    }
    public static void SetBooleanSharedPreference(Context ctx, String Key, boolean Value) {
        SharedPreferences pref = ctx.getSharedPreferences("com.callpneck", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }
}
