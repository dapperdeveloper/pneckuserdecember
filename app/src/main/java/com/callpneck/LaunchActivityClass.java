package com.callpneck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.callpneck.activity.BookingCompleted;
import com.callpneck.activity.DoPaymentScreen;
import com.callpneck.activity.GoogleMapTrackingAddressSet;
import com.callpneck.activity.JobRealTimeTrackingScreen;
import com.callpneck.activity.OrderCompleteHappyScreen;
import com.callpneck.activity.PneckMapLocation;
import com.callpneck.activity.Registration.LoginActivity;
import com.callpneck.activity.Registration.SignupActivity;
import com.callpneck.activity.Registration.VerifyOTPForgetPassword;
import com.callpneck.activity.Registration.VerifyOtpActivity;
import com.callpneck.activity.SideMenuScreens.AboutUsScreen;
import com.callpneck.activity.SideMenuScreens.EditProfileScreen;
import com.callpneck.activity.SideMenuScreens.HelpScreen;
import com.callpneck.activity.SideMenuScreens.MyUserOrderList;
import com.callpneck.activity.SideMenuScreens.NotificationsScreen;
import com.callpneck.activity.SideMenuScreens.ReferAndEarn;
import com.callpneck.activity.SideMenuScreens.SettingScreen;
import com.callpneck.activity.SideMenuScreens.UserFeedbackScreen;
import com.callpneck.activity.SideMenuScreens.WalletScreen;
import com.callpneck.activity.SideMenuScreens.WebPageScreen;
import com.callpneck.activity.Vendor.DisplayCompleteVendor;
import com.callpneck.activity.Vendor.VendorMainPage;
import com.callpneck.activity.registrationSecond.LoginScreenMain;
import com.callpneck.taxi.TaxiMainActivity;

import static com.callpneck.activity.registrationSecond.helper.Constant.LAUNCH_ADDRESS_SET_SCREEN;

public class LaunchActivityClass {

    public static void LaunchMainActivity(Activity activity) {
        Intent intent=new Intent(activity, TaxiMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void LaunchOTPActivity(Activity activity, Bundle bundle) {
        Intent intent=new Intent(activity, VerifyOtpActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }
    public static void LaunchAddressSetActivity(Activity activity) {
        Intent intent=new Intent(activity, GoogleMapTrackingAddressSet.class);
        activity.startActivityForResult(intent,LAUNCH_ADDRESS_SET_SCREEN);
    }

    public static void LaunchLoginScreen(Activity activity) {
    //    Intent intent=new Intent(activity, LoginActivity.class);
        Intent intent=new Intent(activity, LoginScreenMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }

    public static void LaunchForgotPasswordScreen(Activity activity) {
        Intent intent=new Intent(activity, VerifyOTPForgetPassword.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }

    public static void LaunchSignUpScreen(Activity activity) {
        Intent intent=new Intent(activity, SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }

    public static void LaunchSignInScreen(Activity activity) {
        Intent intent=new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }

    public static void LaunchMyOrdersScreen(Activity activity) {
        Intent intent=new Intent(activity, MyUserOrderList.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }


    public static void LaunchRealTimeOrderTracking(Activity activity) {
        Intent intent=new Intent(activity, JobRealTimeTrackingScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }

    public static void LaunchHappyFaceScreen(Activity activity) {
        Intent intent=new Intent(activity, OrderCompleteHappyScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }

    public static void LaunchBookingCompleted(Activity activity,Bundle bundle) {
        Intent intent=new Intent(activity, BookingCompleted.class);
        activity.startActivity(intent);
        intent.putExtras(bundle);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }
    public static void LaunchAboutUs(Activity activity) {
        Intent intent=new Intent(activity, AboutUsScreen.class);
        activity.startActivity(intent);
    }
    public static void LaunchHelp(Activity activity) {
        Intent intent=new Intent(activity, HelpScreen.class);
        activity.startActivity(intent);
    }
    public static void LaunchREferAndEarn(Activity activity) {
        Intent intent=new Intent(activity, ReferAndEarn.class);
        activity.startActivity(intent);
    }

    public static void LaunchNotification(Activity activity) {
        Intent intent=new Intent(activity, NotificationsScreen.class);
        activity.startActivity(intent);
    }
    public static void LaunchSetting(Activity activity) {
        Intent intent=new Intent(activity, SettingScreen.class);
        activity.startActivity(intent);
    }

    public static void LaunchProfile(Activity activity) {
        Intent intent=new Intent(activity, EditProfileScreen.class);
        activity.startActivity(intent);
    }
    public static void LaunchWallet(Activity activity) {
        Intent intent=new Intent(activity, WalletScreen.class);
        activity.startActivity(intent);
    }

//    public static void LaunchChangePassword(Activity activity) {
//        Intent intent=new Intent(activity, ChangePasswordScreen.class);
//        activity.startActivity(intent);
//
//    }

    public static void LaunchPaymentScreen(Activity activity,Bundle bundle) {
        Intent intent=new Intent(activity, DoPaymentScreen.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);

    }
    public static void LaunchOrderCompleteHappyScreen(Activity activity) {
        Intent intent=new Intent(activity, OrderCompleteHappyScreen.class);
        activity.startActivity(intent);

    }

    public static void LaunchWebScreen(Activity activity,Bundle bundle) {
        Intent intent=new Intent(activity, WebPageScreen.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);

    }

    public static void LaunchFeedbackScreen(Activity activity) {
        Intent intent=new Intent(activity, UserFeedbackScreen.class);
        activity.startActivity(intent);
    }

    public static void LaunchVendorMainActivity(Activity activity,Bundle bundle) {
        Intent intent=new Intent(activity, VendorMainPage.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void LaunchCompleteVendorDisplay(Activity activity,Bundle bundle) {
        Intent intent=new Intent(activity, DisplayCompleteVendor.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}
