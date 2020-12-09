package com.callpneck.activity.registrationSecond.helper;

import java.text.DecimalFormat;

public class Constant {
    public static String NAME = "name";
    public static String EMAIL = "email";
    public static String CONTACT = "contact";
    public static String AMOUNT = "amount";
    public static String CURRENCY = "currency";

    public static final int REQUEST_CHECK_SETTINGS = 2542;
    public static int VOLLEY_RETRY_TIMEOUT=15;
    public static final int PERMISSIONS_REQUEST_TOKEN = 2001;
    public static final int LAUNCH_ADDRESS_SET_SCREEN=2342;

    public static String FINAL_TOTAL = "final_total";
    public static Double SETTING_TAX = 0.0;
    public static Double SETTING_DELIVERY_CHARGE = 0.0;
    public static String SETTING_CURRENCY_SYMBOL = "";
    public static Double WALLET_BALANCE = 0.0;
    public static Double SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = 0.0;
    public static DecimalFormat decimalformatData = new DecimalFormat("0.00");
}
