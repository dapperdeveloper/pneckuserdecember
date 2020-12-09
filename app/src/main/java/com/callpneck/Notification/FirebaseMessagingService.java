package com.callpneck.Notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.RemoteMessage;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.activity.DoPaymentScreen;
import com.callpneck.activity.JobRealTimeTrackingScreen;
import com.callpneck.activity.PneckMapLocation;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG =  "FirebaseMessagingSlfad";
    public static int StrangersChatNOtificationId=242326;
    public static int StrangersChatChatNotificationId=23324632;
    public static String NOTIFICATION_SHARED_PREF_NAME="corporat32$^#3";
    private String MyDefaultClickAction="com.peppty.corporator.corporater_app_MAIN_ACTIVITY_TARGET";


    @Override
    public void onNewToken(String s) {

        super.onNewToken(s);
        UpdateTokenToServer(s);
        Log.e("dsadasdasdasdas",  "device token   "+s);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("fsfsdfsfsffa","firebase service is called ");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }else {
            notificationManager=getManager();
        }



        Log.d("fsfsdfsfsffa", "From: "+remoteMessage.getData());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getData().containsKey("image")){
            new createImageNotification(this,remoteMessage,
                    remoteMessage.getData().get("image")).execute();
        }else {
            sendNotification(remoteMessage);
        }
    }

    public class createImageNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private RemoteMessage remoteMessage;
        private String  imageUrl;

        public createImageNotification(Context context, RemoteMessage remoteMessage, String imageUrl) {
            super();
            this.mContext = context;
            this.remoteMessage=remoteMessage;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            showImageNotification(result,remoteMessage);

            /*******************Notification for promotional message
             * Intent intent = new Intent(mContext, MyOpenableActivity.class);
             intent.putExtra("key", "value");
             PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);

             NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
             Notification notif = new Notification.Builder(mContext)
             .setContentIntent(pendingIntent)
             .setContentTitle(title)
             .setContentText(message)
             .setSmallIcon(R.mipmap.ic_launcher)
             .setLargeIcon(result)
             .setStyle(new Notification.BigPictureStyle().bigPicture(result))
             .build();
             notif.flags |= Notification.FLAG_AUTO_CANCEL;
             notificationManager.notify(1, notif);*/
        }
    }

    private void showImageNotification(Bitmap image, RemoteMessage remoteMessage) {


        /*****************sending message notification ***************/
        Intent resultIntent = new Intent();

        SessionManager sessionManager=new SessionManager(FirebaseMessagingService.this);
        if (!sessionManager.isLoggedIn()){
            return;
        }
        Log.e("sdkjfhskdfjfsd","this is notification data "+remoteMessage.getData());

        String notificationGroup="";

        String notificationContent="";

        if (remoteMessage.getData().get("notification_type")
                .equalsIgnoreCase("BookingOrderInfo")){

            resultIntent=new Intent(FirebaseMessagingService.this, JobRealTimeTrackingScreen.class);
            resultIntent.putExtra("booking_order_number",remoteMessage.getData().get("booking_order_number"));
            resultIntent.putExtra("booking_order_id",remoteMessage.getData().get("ses_booking_id"));
            resultIntent.putExtra("customer_lat",remoteMessage.getData().get("user_lat"));
            resultIntent.putExtra("customer_long",remoteMessage.getData().get("user_long"));
            notificationContent=remoteMessage.getData().get("order_info");
        }else if (remoteMessage.getData().get("notification_type")
                .equalsIgnoreCase("OrderAccepted")){

            resultIntent=new Intent(FirebaseMessagingService.this, JobRealTimeTrackingScreen.class);
            resultIntent.putExtra("booking_order_number",remoteMessage.getData().get("booking_order_number"));
            resultIntent.putExtra("booking_order_id",remoteMessage.getData().get("ses_booking_id"));
            resultIntent.putExtra("customer_lat",remoteMessage.getData().get("user_lat"));
            resultIntent.putExtra("customer_long",remoteMessage.getData().get("user_long"));
            notificationContent=remoteMessage.getData().get("message");
        }else if (remoteMessage.getData().get("notification_type").equalsIgnoreCase("PaymentRequest")){
            resultIntent=new Intent(FirebaseMessagingService.this, DoPaymentScreen.class);
            resultIntent.putExtra("billing_amount",remoteMessage.getData().get("billing_amount"));
            notificationContent=remoteMessage.getData().get("message");
        }
        else {
            notificationContent=remoteMessage.getData().get("message");
            resultIntent=new Intent(FirebaseMessagingService.this, PneckMapLocation.class);
        }


        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setAction(Long.toString(System.currentTimeMillis()));


        Integer userId=generateRandom();

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        userId,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );



        NotificationCompat.Builder mBuilder;


        Uri alarmSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Log.e("gfdsgdgdsgsdgds","setting notification sound ");
        mBuilder=new NotificationCompat.Builder(this,
                getResources().getString(R.string.default_notification_channel_id));

        mBuilder.setSmallIcon(R.drawable.pneck_logo)
                .setWhen(0).setAutoCancel(true)
                .setGroup(notificationGroup)
                .setSound(alarmSound)
                .setContentIntent(resultPendingIntent)
                .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.pneck_logo)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationContent))
                .setContentText(notificationContent)
                .setLargeIcon(image)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder.setChannelId(getResources().getString(R.string.default_notification_channel_id));
        }

        notificationManager.notify(userId, mBuilder.build());

    }



    private void sendNotification(RemoteMessage remoteMessage) {

    /*****************sending message notification ***************/
        Intent resultIntent = new Intent();

        SessionManager sessionManager=new SessionManager(FirebaseMessagingService.this);
        if (!sessionManager.isLoggedIn()){
            return;
        }

        String notificationGroup="";

        resultIntent=new Intent(FirebaseMessagingService.this, PneckMapLocation.class);

        resultIntent.setAction(Long.toString(System.currentTimeMillis()));

        Integer userId=generateRandom();

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this,
                        userId,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder;


        Uri alarmSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Log.e("gfdsgdgdsgsdgds","setting notification sound ");
        mBuilder=new NotificationCompat.Builder(this,
                getResources().getString(R.string.default_notification_channel_id));

        mBuilder.setSmallIcon(R.drawable.pneck_logo)
                .setWhen(0).setAutoCancel(true)
                .setGroup(notificationGroup)
                .setSound(alarmSound)
                .setContentIntent(resultPendingIntent)
                .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.pneck_logo)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder.setChannelId(getResources().getString(R.string.default_notification_channel_id));
        }

/*****************sending message notification ***************/


        notificationManager.notify(userId, mBuilder.build());

    }


    private NotificationManager notificationManager;

    private NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private static String EnableSoundNotification="StrangersChat.enable_sound_notification";
    //private static String DisableSoundNotification="StrangersChat.disable_sound_notification";
    private void createChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Enable sound
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                    EnableSoundNotification, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            getManager().createNotificationChannel(notificationChannel);

        }

    }

    private void UpdateTokenToServer(String token) {

        SessionManager sessionManager=new SessionManager(FirebaseMessagingService.this);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "userDevicetoken";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id", sessionManager.getUserid());
        dataParams.put("device_token",token);

        Log.e("klsddsfdsf", "this is we sending " + dataParams.toString());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                RegistrationSuccess(),
                RegistrationError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(FirebaseMessagingService.this).add(dataParamsJsonReq);

    }


    private Response.Listener<JSONObject> RegistrationSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("klsddsfdsf", "this is complete response " + response);
                    if (response.getBoolean("success")) {
                    }

                } catch (Exception e) {
                    Log.v("klsddsfdsf", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener RegistrationError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }



    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }




}