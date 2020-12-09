package com.callpneck.Language;

import android.content.Context;

public class AppUtils {

    public static int getRawResourceID(Context context, String rawResourceName) {
        return context.getResources().getIdentifier(rawResourceName, "raw", context.getPackageName());
    }
}
