package com.callpneck.utils;

/**
 * Created by mukeshk on 12/13/2016.
 */

public interface DialogListener {


    void onPressOk(int method, String resp);

    void onPressCancel(int method, String resp);
}
