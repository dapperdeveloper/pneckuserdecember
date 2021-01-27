package com.callpneck.activity.deliveryboy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.deliveryboy.Adapter.BoyListAdapter;
import com.callpneck.activity.deliveryboy.model.DUser;
import com.callpneck.activity.deliveryboy.model.DriverList;
import com.callpneck.activity.deliveryboy.model.DriverList_;
import com.callpneck.activity.deliveryboy.model.OrderSubmit;
import com.callpneck.activity.registrationSecond.Activity.OrderPlacedActivity;
import com.callpneck.activity.registrationSecond.MainScreenActivity;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.api.ApiClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import net.bohush.geometricprogressview.GeometricProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryBoyListActivity extends AppCompatActivity {

    BoyListAdapter adapter;
    RecyclerView recyclerView;
    List<DriverList_> dUserList;
    ProgressBar progressbar;
    Toolbar toolbar;
    SwipeRefreshLayout swipeLayout;
    TextView tvAlert;
    String pickupAddress, dropAddress, UserLatitude, UserLongitude;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_list);

        if (getIntent()!= null){
            pickupAddress = getIntent().getStringExtra("pickupAddress");
            dropAddress = getIntent().getStringExtra("dropAddress");
            UserLatitude = getIntent().getStringExtra("UserLatitude");
            UserLongitude = getIntent().getStringExtra("UserLongitude");

        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delivery Boy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeLayout = findViewById(R.id.swipeLayout);
        progressbar = findViewById(R.id.progressBar);
        tvAlert = findViewById(R.id.tvAlert);

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recycleview);
        if (UserLongitude!= null&& UserLatitude!=null)
        getDeliveryBoyData();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeliveryBoyData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    String deliveryFee="";
    private void getDeliveryBoyData() {
        dUserList = new ArrayList<>();
        Call<DriverList> call = APIClient.getInstance().getDeliveryBoyData(UserLatitude, UserLongitude);
        call.enqueue(new Callback<DriverList>() {
            @Override
            public void onResponse(Call<DriverList> call, Response<DriverList> response) {
                if (response.isSuccessful()){
                    progressbar.setVisibility(View.GONE);
                    try {

                        DriverList model = response.body();
                        if (model!= null && model.getSuccess()){
                            dUserList.clear();
                            dUserList = model.getDriverList();
                            adapter = new BoyListAdapter(dUserList, DeliveryBoyListActivity.this, new BoyListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(DriverList_ item) {
                                    String distance = item.getDistance()+"";
                                    String km = distance.substring(0,3);

                                    int rateKm = Integer.parseInt(distance.substring(0,1));
                                    if (rateKm==0){
                                        deliveryFee= "30";
                                    }
                                    else if (rateKm == 1){
                                        deliveryFee= "30";
                                    }
                                    else if (rateKm == 2){
                                        deliveryFee = "40";
                                    }
                                    else if (rateKm == 3){
                                        deliveryFee = "50";
                                    }
                                    else if(rateKm == 4){
                                        deliveryFee = "60";
                                    }
                                    else {
                                        deliveryFee = "100";
                                    }
                                    showMAkeOrderDialog(item.getId()+"", item.getFirstName()+" "+item.getLastName()+"", item.getEmpAddress()+"", deliveryFee);
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }

                    }catch (Exception e){
                        e.toString();
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverList> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
            }
        });

    }


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermission;
    private String[] storagePermission;

    ImageView orderIV;
    EditText orderEt;
    BottomSheetDialog bottomSheet;
    private void showMAkeOrderDialog(String emp_id, String emp_name, String emp_address, String deliveryFee) {
        final View view = getLayoutInflater().inflate(R.layout.layout_make_order_screen, null);
         bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setContentView(view);
        bottomSheet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheet.setCancelable(false);
        orderEt = view.findViewById(R.id.orderEt);
        RelativeLayout addPhotoBtn = view.findViewById(R.id.addPhotoBtn);
        orderIV = view.findViewById(R.id.orderIV);
        Button submitBtn = view.findViewById(R.id.submitBtn);
        ImageView closeBtn = view.findViewById(R.id.closeBtn);

        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image_uri!=null){
                    final View view = getLayoutInflater().inflate(R.layout.process_order_sheet_layout, null);
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DeliveryBoyListActivity.this);
                    bottomSheetDialog.setContentView(view);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.show();
                    orderWithImage(bottomSheetDialog, emp_id, emp_name, emp_address, deliveryFee);
                }else {
                    orderWithoutImage();
                }
            }
        });

        bottomSheet.show();

    }
    File file;
    private void orderWithImage(BottomSheetDialog bottomSheetDialog, String emp_id, String emp_name, String emp_address, String deliveryFee) {
        RequestBody orde_list  = RequestBody.create(MediaType.parse("multipart/form-data"), orderEt.getText().toString()+"");
        RequestBody start_address  = RequestBody.create(MediaType.parse("multipart/form-data"), pickupAddress);
        RequestBody drop_address  = RequestBody.create(MediaType.parse("multipart/form-data"), dropAddress);
        RequestBody user_id  = RequestBody.create(MediaType.parse("multipart/form-data"), sessionManager.getUserid()+"");
        RequestBody user_name  = RequestBody.create(MediaType.parse("multipart/form-data"), sessionManager.getUserName()+"");
        RequestBody user_mobile  = RequestBody.create(MediaType.parse("multipart/form-data"), sessionManager.getUserMobile()+"");
        RequestBody empId  = RequestBody.create(MediaType.parse("multipart/form-data"), emp_id);
        RequestBody empName  = RequestBody.create(MediaType.parse("multipart/form-data"), emp_name);
        RequestBody empAddress  = RequestBody.create(MediaType.parse("multipart/form-data"), emp_address);
        RequestBody empFee  = RequestBody.create(MediaType.parse("multipart/form-data"), deliveryFee);


        MultipartBody.Part requestImage = null;
        if (file == null)
        {
            file = new File(getRealPathFromURI(image_uri));

        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        requestImage = MultipartBody.Part.createFormData("order_image", file.getName(), requestFile);

        Call<OrderSubmit> call = APIClient.getInstance().createOrderByUser(orde_list, start_address, drop_address, user_id, user_name, user_mobile, empId, empName, empAddress, empFee, requestImage);



        call.enqueue(new Callback<OrderSubmit>() {
            @Override
            public void onResponse(Call<OrderSubmit> call, Response<OrderSubmit> response) {
                if (response.isSuccessful()){
                    try {
                        OrderSubmit orderSubmit = response.body();
                        if (orderSubmit.getSuccess()){
                            bottomSheetDialog.dismiss();
                            bottomSheet.dismiss();
                            sessionManager.saveCurrentOrderDeliveryId(orderSubmit.getId()+"");
                            Log.e("ORDER_ID", orderSubmit.getId()+"");
                            if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                    sessionManager.getCurrentDeliveryOrderId().length()>0){
                                LaunchActivityClass.LaunchTrackingDeliveryScreen(DeliveryBoyListActivity.this);
                            }

                        }
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onFailure(Call<OrderSubmit> call, Throwable t) {
                bottomSheetDialog.dismiss();
                bottomSheet.dismiss();
            }
        });



    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        androidx.loader.content.CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    private void orderWithoutImage() {
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

    Uri image_uri=null;
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
                orderIV.setImageURI( image_uri);
                orderIV.setVisibility(View.VISIBLE);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                orderIV.setImageURI( image_uri);
                orderIV.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
    }
}