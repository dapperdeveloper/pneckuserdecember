package com.callpneck.Language;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class ThemeUtils {

    public static void setLanguage(Context context) {
        try {
            Resources res = context.getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            String language = AppThemePrefrences.GetSharedPreference(context, AppThemePrefrences.APP_LANGAUGE);
            if ((!(language == null)) || !(language.equalsIgnoreCase(""))) {

                String[] arr = language.split("_");
                if (arr.length > 1) {
                    conf.locale = new Locale(arr[0], arr[1].toUpperCase());
                } else {
                    conf.locale = new Locale(language.toLowerCase());
                }
                res.updateConfiguration(conf, dm);
            }
        } catch (Exception e) {
            Log.d("Error ", e.toString());
        }

    }
}
