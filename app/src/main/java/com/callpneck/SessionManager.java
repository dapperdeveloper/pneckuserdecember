package com.callpneck;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jai Mata Di on 7/26/2017.
 */

public class SessionManager {

    //---------------------Billing addredd preferences--------------------//
    public static final String USER_NAME = "user_name";
    public static final String USER_MOBILE = "user_mobile";
    public static final String USER_ADDRESS = "user_address";

    public static final String USER_CURRENT_ADDRESS = "user_current_address";
    public static final String USER_STATE = "user_state";
    public static final String USER_PIN = "user_pin";
    public static final String USER_MAIL = "user_mail";
    public static final String USER_LASTNAME = "user_lastname" ;
    public static final String USER_TOKEN = "user_token";
    public static final String EMPLOYEE_ADDRESS = "employee_address";
    public static final String EMPLOYEE_GENDER = "employee_gender";
    public static final String SES_BOOKING_ID = "ses_booking_id";
    public static final String IS_LOGGED_IN="is_logged_in";
    private static final String CURRENT_USER_PROFILE_PIC="current_user_profile_pic";
    public static final String USER_LATITUDE="user_latitude";
    public static final String USER_SCREEN_ADDRESS = "user_screen_address";
    public static final String USER_LONGITUDE="user_longitude";


    public static final String CURRENT_ORDER_RESPONSE="current_order_response";

    public static final String USER_PASS_WORD="user_pass_word";

    public static final String RECEIVER_ID="receiver_id";


    public void saveUserMobileAndPass(String mobile,String pass){
        editor.putString(USER_PASS_WORD,pass);
        editor.putString(USER_MOBILE,mobile);
        editor.commit();
    }

    public String getUserPassword(){
        return pref.getString(USER_PASS_WORD,"");
    }

    public String getMobileNo(){
        return pref.getString(USER_MOBILE,"");
    }


    public static final String LANGUAGE = "language";
    /*---------------userId-----------*/
    public static final String USERID = "user_id";
    public static final String SUCCESS = "success";
    public static final String LOGINTYPE = "login_type";

    //--------------------------------------Login information preferences------------------------ //

    public static final String LOGIN_NAME = "login_name";


    private static final String PREF_NAME = "P@#ne#$%ck@Us$er9)";
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    // Context
    private Context _context;
    // Shared pref mode
    private int PRIVATE_MODE = 0;


    public boolean clearOrderSession(){
        editor.putString(SES_BOOKING_ID,null);
        editor.putString(CURRENT_ORDER_RESPONSE,"");
        editor.commit();
        return true;
    }

    public String getLanguage() {
        return pref.getString(LANGUAGE, null);
    }

    public void setLanguage(String language) {
        editor.putString(LANGUAGE, language);
        editor.commit();
    }
    public void setUserScreenAddress(String screenAddress) {
        editor.putString(USER_SCREEN_ADDRESS, screenAddress);
        editor.commit();
    }

    public String getCurrentOrderResponse(){
        return pref.getString(CURRENT_ORDER_RESPONSE,"");
    }
    public boolean setCurrentOrderResponse(String response){
        editor.putString(CURRENT_ORDER_RESPONSE, ""+response);
        editor.commit();
        return true;
    }


    public boolean setUserLocation(String latitude,String longitude){
        editor.putString(USER_LONGITUDE, ""+longitude);
        editor.putString(USER_LATITUDE, ""+latitude);
        editor.commit();
        return true;
    }

    public void saveUserImage(String userImage){
        editor.putString(CURRENT_USER_PROFILE_PIC,userImage);
        editor.commit();
    }

    public String getUserImage(){
        return pref.getString(CURRENT_USER_PROFILE_PIC,"");
    }

    public String getUserLatitude(){
        return pref.getString(USER_LATITUDE,"");
    }
    public String getUserScreenAddress(){
        return pref.getString(USER_SCREEN_ADDRESS,"");
    }
    public String getUserLongitude(){
        return pref.getString(USER_LONGITUDE,"");
    }

    public String getSesBookingId() {
        return pref.getString(SES_BOOKING_ID, null);
    }

    public void setSesBookingId(String sesBookingId) {
        editor.putString(SES_BOOKING_ID, sesBookingId);
        editor.commit();
    }


    public String getUserLastname() {
        return pref.getString(USER_LASTNAME, null);
    }

    public void setUserLastname(String userLastname) {
        editor.putString(USER_LASTNAME, userLastname);
        editor.commit();
    }

    public String getEmployeeAddress() {
        return pref.getString(EMPLOYEE_ADDRESS, null);
    }

    public void setEmployeeAddress(String employeeAddress) {
        editor.putString(EMPLOYEE_ADDRESS, employeeAddress);
        editor.commit();
    }

    // ------------------EMPLOYEE GENDER --------------------//
    public String getEmployeeGender() {
        return pref.getString(EMPLOYEE_GENDER, null);
    }

    public void setEmployeeGender(String employeeGender) {
        editor.putString(EMPLOYEE_GENDER, employeeGender);
        editor.commit();
    }
    //


    public String getUserToken() {
        return pref.getString(USER_TOKEN, null);
    }

    public void setUserToken(String userToken) {
        editor.putString(USER_TOKEN, userToken);
        editor.commit();
    }

    // ------------------getter & setter of billing address--------------------//

    public String getUserName() {
        return pref.getString(USER_NAME, null);
    }

    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public String getUserMobile() {
        return pref.getString(USER_MOBILE, null);
    }

    public void setReceiverId(String receiverId) {
        editor.putString(RECEIVER_ID, receiverId);
        editor.commit();
    }
    public String getReceiverId() {
        return pref.getString(RECEIVER_ID, null);
    }

    public void setUserMobile(String userMobile) {
        editor.putString(USER_MOBILE, userMobile);
        editor.commit();
    }

    public String getUserAddress() {
        return pref.getString(USER_ADDRESS, null);
    }

    public String getUserCurrentAddress() {
        return pref.getString(USER_CURRENT_ADDRESS, null);
    }

    public void setUserCurrentAddress(String currentAddress) {
        editor.putString(USER_CURRENT_ADDRESS, currentAddress);
        editor.commit();
    }
    public void setUserAddress(String userAddress) {
        editor.putString(USER_ADDRESS, userAddress);
        editor.commit();
    }

    public String getUserState() {
        return pref.getString(USER_STATE, null);
    }

    public void setUserState(String userState) {
        editor.putString(USER_STATE, userState);
        editor.commit();
    }

    public String getUserPin() {
        return pref.getString(USER_PIN, null);
    }

    public void setUserPin(String userPin) {
        editor.putString(USER_PIN, userPin);
        editor.commit();
    }


    public String getUserMail() {
        return pref.getString(USER_MAIL, null);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN,false);
    }

    public void createSession(String userName,String userMobile,String userid,String userLastname,
                              String userToken,String userMail,String image){
        editor.putBoolean(IS_LOGGED_IN,true);
        editor.putString(USER_NAME, userName);
        editor.putString(USER_MOBILE, userMobile);
        editor.putString(USERID, userid);
        editor.putString(USER_LASTNAME, userLastname);
        editor.putString(USER_TOKEN, userToken);
        editor.putString(USER_MAIL, userMail);
        editor.putString(CURRENT_USER_PROFILE_PIC,image);
        editor.commit();
    }
    public void setUserMail(String userMail) {
        editor.putString(USER_MAIL, userMail);
        editor.commit();
    }

    public String getUserid() {
        return pref.getString(USERID, null);
    }

    public Boolean getSuccess() {
        return pref.getBoolean(SUCCESS, false);
    }

    public void setSuccess(boolean value) {
        editor.putBoolean(SUCCESS, value);
        editor.commit();
    }

    public void setUserid(String userid) {
        editor.putString(USERID, userid);
        editor.commit();
    }

    public void setLoginType(String type) {
        editor.putString(LOGINTYPE, type);
        editor.commit();
    }

    public String getLoginType() {
        return pref.getString(LOGINTYPE, null);
    }
    //********** *********************//

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
        Log.d("TAG", "Deleted all user info from shared preference");
    }

    public void setDestination(String desLatti, String desLongi) {
        editor.putString("deslatti", desLatti);
        editor.putString("deslongi", desLongi);
        editor.commit();
    }

    public void setOtpVerified(boolean b) {
        editor.putBoolean("isotpverified", b);
        editor.commit();
    }

    public boolean getOtpVerified() {
        return pref.getBoolean("isotpverified", false);
    }

    public String getDesLattitude() {
        return pref.getString("deslatti",null);
    }

    public String getDesLongitude() {
        return pref.getString("deslongi",null);
    }


    public void setDestinationAddress(String locality) {
        editor.putString("locality", locality);
        editor.commit();
    }

    public String getDestinationLocality() {
        return pref.getString("locality",null);
    }

    public void setPayAmount(String pay_amount) {
        editor.putString("payamt", pay_amount);
        editor.commit();
    }

    public String getPayAmount() {
        return pref.getString("payamt",null);
    }

    public void setOrderStatus(String status) {
        editor.putString("orderStatus", status);
        editor.commit();
    }

    public String getOrderStatus() {
        return pref.getString("orderStatus",null);
    }

    public void setDeliveryOtp(String delivery) {
        editor.putString("deliveryotp", delivery);
        editor.commit();
    }

    public String getDeliveryOtp() {
        return pref.getString("deliveryotp",null);
    }

    public void setUserValues(String name, String mobile, String vehicleNumber, String vehicleImage,String otp) {
        editor.putString("name", name);
        editor.putString("mobile", mobile);
        editor.putString("vehiclenumber", vehicleNumber);
        editor.putString("vehicleImage", vehicleImage);
        editor.putString("potp", otp);
        editor.commit();

    }

    public String getDname() {
        return pref.getString("name",null);
    }

    public String getpickUpotp() {
        return pref.getString("potp",null);
    }

    public String getDrivermobile() {
        return pref.getString("mobile",null);
    }

    public String getvehiclenumber() {
        return pref.getString("vehiclenumber",null);
    }

    public String getvehicleImage() {
        return pref.getString("vehicleImage",null);
    }
    public String getPickUpOtp() {
        return pref.getString("potp",null);
    }


    public void setEmpLattitudeLongitude(String lattitude, String longitude) {
        editor.putString("emplattitude", lattitude);
        editor.putString("emplongitude", longitude);
        editor.commit();
    }

    public String getEmpLattitude() {
        return pref.getString("emplattitude",null);
    }

    public String getEmplongitude() {
        return pref.getString("emplongitude",null);
    }
}
