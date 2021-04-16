package com.callpneck.activity.deliveryboy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.LaunchActivityClass;
import com.callpneck.MultimediaUpload.AndroidMultiPartEntity;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.SideMenuScreens.EditProfileScreen;
import com.callpneck.activity.deliveryboy.model.OrderSubmit;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hendraanggrian.appcompat.widget.SocialEditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    SocialEditText description_edit;
    TextView aditional_details_text_count;
    int counter = -1;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermission;
    private String[] storagePermission;


    ProgressDialog dialog;
    private ImageView pImageIv;

    Uri image_uri=null;
    Bitmap bitmap;

    String postCategory, emp_id, emp_name, emp_address, deliveryFee, dropAddress,pickupAddress ;
//    Spinner spinner;
    Button submitBtn;
    SessionManager sessionManager;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_create_order);

        if (getIntent()!= null){
            emp_id = getIntent().getStringExtra("emp_id");
            emp_name = getIntent().getStringExtra("emp_name");
            emp_address = getIntent().getStringExtra("emp_address");
            deliveryFee = getIntent().getStringExtra("deliveryFee");
            pickupAddress = getIntent().getStringExtra("pickupAddress");
            dropAddress = getIntent().getStringExtra("dropAddress");
        }

        initView();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order To " + emp_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        sessionManager = new SessionManager(this);
        //spinner.setOnItemSelectedListener(this);
        dialog = new ProgressDialog(this);

        dialog.setTitle("Please wait");
        dialog.setMessage("Ordering.....");
        dialog.setCanceledOnTouchOutside(false);

        setSpinnerAdapter();


        aditional_details_text_count.setText("0" + "/" + Constants.Char_limit);
        description_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.Char_limit)});

        description_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                aditional_details_text_count.setText(description_edit.getText().length() + "/" + Constants.Char_limit);
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        pImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (image_uri!=null && bitmap!=null){
//
//                    orderWithImage(emp_id, emp_name, emp_address, deliveryFee);
//                }else {
                if (TextUtils.isEmpty(description_edit.getText().toString())){
                        Toast.makeText(CreateOrderActivity.this, "Make a order......", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    orderWithoutImage( emp_id, emp_name, emp_address, deliveryFee);
                }
//            }
        });


    }

    private void orderWithImage(String emp_id, String emp_name, String emp_address, String deliveryFee) {
        dialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, APIClient.BASE_URL+"orderlistuser",
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try
                        {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if (obj.getBoolean("success")){
                                String orderId = obj.getInt("order_id")+"";
                                if (emp_id!=null && !emp_id.equals(""))
                                {
                                    prepareNotificationMessage(emp_id, orderId);
                                }

                            }
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("orde_list",description_edit.getText().toString()+"");
                params.put("start_address",pickupAddress);
                params.put("drop_address", dropAddress);
                params.put("user_id", sessionManager.getUserid()+"");
                params.put("user_name",sessionManager.getUserName()+"");
                params.put("user_mobile",sessionManager.getUserMobile()+"");
                params.put("emp_id", emp_id);
                params.put("emp_name", emp_name);
                params.put("emp_address", emp_address);
                params.put("delivery_charge", deliveryFee);
                return params;
            }
            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("order_image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

        };
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void orderWithoutImage( String emp_id, String emp_name, String emp_address, String deliveryFee) {

        dialog.show();
        Call<OrderSubmit> call = APIClient.getInstance().createOrderByUserWithoutImage(description_edit.getText().toString()+"", pickupAddress, dropAddress, sessionManager.getUserid()+"", sessionManager.getUserName()+"", sessionManager.getUserMobile()+"", emp_id, emp_name, emp_address, deliveryFee, "");
        call.enqueue(new Callback<OrderSubmit>() {
            @Override
            public void onResponse(Call<OrderSubmit> call, Response<OrderSubmit> response) {
                if (response.isSuccessful()){
                    try {
                        OrderSubmit orderSubmit = response.body();
                        if (orderSubmit.getSuccess()){

                            if (emp_id!=null && !emp_id.equals(""))
                            {
                                prepareNotificationMessage(emp_id, orderSubmit.getId()+"");
                            }
                        }
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onFailure(Call<OrderSubmit> call, Throwable t) {
               dialog.dismiss();
            }
        });

    }



    private void initView() {
        description_edit=findViewById(R.id.description_edit);
        aditional_details_text_count = findViewById(R.id.aditional_details_text_count);
        pImageIv = findViewById(R.id.pImageIv);
        //spinner = findViewById(R.id.spinner);
        submitBtn = findViewById(R.id.submitBtn);

    }

    private void setSpinnerAdapter() {
        List<String> categories = new ArrayList<>();
        categories.add("Choose one");
        categories.add("Attach Photo");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        postCategory = adapterView.getItemAtPosition(i).toString();

        if (postCategory.equals("Attach Photo"))
        {
            pImageIv.setVisibility(View.VISIBLE);

        }
        else if (postCategory.equals("Choose one")) {
            pImageIv.setVisibility(View.GONE);
            pImageIv.setImageURI(null);
            pImageIv.setImageBitmap(null);
            image_uri = null;
            bitmap = null;
        }
        //Toast.makeText(this, "Selected:"+postCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void showImagePickDialog() {
        String[] options = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    //camera
                    if(checkCameraPermission()){

                        //camera permission allowed
                        pickFromCamera();
                    }else {
                        //not allowed
                        requestCameraPermission();
                    }
                }else {
                    //gallery
                    if(checkStoragePermission()){

                        pickFromGallery();
                    }else {

                        requestStoragePermission();
                    }
                }
            }
        }).show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues contentValues= new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result && result2;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(this, "Camera permission is necessary.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length >0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(this, "Storage permission is necessary.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE){

                image_uri = data.getData();

                try {
                    //getting bitmap object from uri
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);

                    //displaying selected image to imageview
                    pImageIv.setImageBitmap(bitmap);
                    pImageIv.setVisibility(View.VISIBLE);
                    //calling the method uploadBitmap to upload image
                    //uploadBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                try {
                    //getting bitmap object from uri
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);

                    //displaying selected image to imageview
                    pImageIv.setImageBitmap(bitmap);
                    pImageIv.setVisibility(View.VISIBLE);
                    //calling the method uploadBitmap to upload image
                    //uploadBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
    }


    private void prepareNotificationMessage(String emp_id, String orderId){
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order" + orderId;
        String NOTIFICATION_MESSAGE = "Congratulation....! You have new order From Customer";
        String NOTIFICATION_TYPE = "CustomerOrder";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",sessionManager.getUserid()); //current user is user so uid is buyer id
            notificationBodyJo.put("sellerUid",emp_id);
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){

            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo, orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, final String orderId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        sessionManager.saveCurrentOrderDeliveryId(orderId);

                        if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                sessionManager.getCurrentDeliveryOrderId().length()>0){
                            LaunchActivityClass.LaunchTrackingDeliveryScreen(CreateOrderActivity.this);
                        }
                        Log.d("FCM_RESPONSE", response.toString());
                        dialog.dismiss();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        sessionManager.saveCurrentOrderDeliveryId(orderId);
                        if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                sessionManager.getCurrentDeliveryOrderId().length()>0){
                            LaunchActivityClass.LaunchTrackingDeliveryScreen(CreateOrderActivity.this);
                        }
                        dialog.dismiss();
                        //error occur
                        Log.d("FCM_ERROR", ""+error.getMessage());
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization","key="+Constants.FCM_KEY);
                return headers;
            }
        };

        //enqueue the Volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

}