package com.callpneck.activity.Vendor;

import android.content.Context;
import android.os.Handler;

import com.callpneck.Language.ThemeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.callpneck.Const;
import com.callpneck.PublicMethods;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.adapter.VendorAdapters;
import com.callpneck.model.VendorModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VendorMainPage extends AppCompatActivity {

    private String userLatitude,userLongitude;
    private SessionManager sessionManager;

    private ArrayList<String> serviceList=new ArrayList<>();
    private ArrayList<String> subserviceList=new ArrayList<>();
    private ArrayList<String> subservicecatalogList=new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ArrayList<VendorModel> vendorList =new ArrayList<>();
    private VendorAdapters vendorListAdapters;
    private ImageView goBack;
    private ImageView closeFilter;
    private RadioGroup radioGroup;
    private RadioButton staticVendor;
    private RadioButton dynamicVendor;
    private Spinner selectProfession;
    private Spinner selectSubCategory;
    private Spinner selectCatalogue;
    private MaterialRippleLayout applyFilter;
    private FloatingActionButton filterList;
    private RelativeLayout filterLayout;
    private View filterBackGround;
    private RelativeLayout FilterWorkLayout;

    private ArrayList<String> selectedDaysList=new ArrayList<>();
    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thursday;
    private CheckBox friday;
    private CheckBox saturday;
    private CheckBox sunday;
    private String[] DayList={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

    private HashMap<String, String> primeCategoryHash=new HashMap<>();
    private HashMap<String, String> subCategoryHash=new HashMap<>();
    private HashMap<String, String> catalogHash=new HashMap<>();
/*
    private HashMap<String, String> stateHash=new HashMap<>();
    private HashMap<String, String> cityHash=new HashMap<>();*/


    private TextView vendorTitle;
    private RelativeLayout searchToolbar;
    private ImageView goBackSearch;
    private EditText SearchItemEdit;
    private LinearLayout SearchServiceAction;
    private RelativeLayout normalToolbar;
    private ImageView openSearchView;
    private ImageView actualSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_vendor_main_page);

       userLatitude=getIntent().getStringExtra("userLat");
        userLongitude=getIntent().getStringExtra("userLong");


        findViews();
        clickListeners();
        editTextActions();
        daysClickListeners();
        sessionManager=new SessionManager(VendorMainPage.this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        vendorListAdapters = new VendorAdapters(VendorMainPage.this, vendorList,userLatitude,userLongitude);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(VendorMainPage.this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(vendorListAdapters);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getDataList();
        getPneckService();
        spinnerChangeListeners();
    }

    private void editTextActions() {
        SearchItemEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH
                        ||actionId==EditorInfo.IME_ACTION_DONE){
                    Log.e("sdhfskdfsf","calling edit action ");
                    if (SearchItemEdit.getText().toString().length()>0){
                        vendorTitle.setText(SearchItemEdit.getText().toString());
                        progressBar.setVisibility(View.VISIBLE);
                        vendorList.clear();
                        vendorListAdapters.notifyDataSetChanged();
                        getFilteredData();
                    }else {
                        searchToolbar.setVisibility(View.GONE);
                        normalToolbar.setVisibility(View.VISIBLE);
                    }
                    PublicMethods.hideKeyboard(VendorMainPage.this);
                }
                return false;
            }
        });
    }

    private void spinnerChangeListeners() {
        selectProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectProfession.getSelectedItemPosition()>0){
                    getSubService();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectSubCategory.getSelectedItemPosition()>0){
                    getCatalogList();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    private void findViews() {

        vendorTitle=findViewById(R.id.title);
        openSearchView=findViewById(R.id.open_search_view);
        actualSearchView=findViewById(R.id.actual_search_btn);

        searchToolbar = (RelativeLayout)findViewById( R.id.search_toolbar );
        goBackSearch = (ImageView)findViewById( R.id.go_back_search );
        SearchItemEdit = (EditText)findViewById( R.id.Search_item_edit );
        SearchServiceAction = (LinearLayout)findViewById( R.id.Search_service_action );
        normalToolbar = (RelativeLayout)findViewById( R.id.normal_toolbar );

        goBack=findViewById(R.id.go_back);
        mRecyclerView=findViewById(R.id.recycler_view);
        progressBar=findViewById(R.id.progress_bar);

        FilterWorkLayout=findViewById(R.id.filter_work_layout);
        closeFilter = (ImageView)findViewById( R.id.close_filter );
        radioGroup = (RadioGroup)findViewById( R.id.radio_group );
        staticVendor = (RadioButton)findViewById( R.id.static_vendor );
        dynamicVendor = (RadioButton)findViewById( R.id.dynamic_vendor );
        selectProfession = (Spinner)findViewById( R.id.select_profession );
        selectSubCategory = (Spinner)findViewById( R.id.select_sub_category );
        selectCatalogue = (Spinner)findViewById( R.id.select_catalogue );
        applyFilter = (MaterialRippleLayout)findViewById( R.id.apply_filter );
        filterList = (FloatingActionButton)findViewById( R.id.filter_list );
        filterLayout = (RelativeLayout)findViewById( R.id.filter_layout );
        filterBackGround = (View)findViewById( R.id.filter_back_ground );

        monday = (CheckBox)findViewById( R.id.monday );
        tuesday = (CheckBox)findViewById( R.id.tuesday );
        wednesday = (CheckBox)findViewById( R.id.wednesday );
        thursday = (CheckBox)findViewById( R.id.thursday );
        friday = (CheckBox)findViewById( R.id.friday );
        saturday = (CheckBox)findViewById( R.id.saturday );
        sunday = (CheckBox)findViewById( R.id.sunday );
    }

    private void daysClickListeners() {
        monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!selectedDaysList.contains(DayList[0])){
                        selectedDaysList.add(DayList[0]);
                    }
                }else {
                    if (selectedDaysList.contains(DayList[0])){
                        selectedDaysList.remove(DayList[0]);
                    }
                }
            }
        });


        tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!selectedDaysList.contains(DayList[1])){
                        selectedDaysList.add(DayList[1]);
                    }
                }else {
                    if (selectedDaysList.contains(DayList[1])){
                        selectedDaysList.remove(DayList[1]);
                    }
                }
            }
        });

        wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!selectedDaysList.contains(DayList[2])){
                        selectedDaysList.add(DayList[2]);
                    }
                }else {
                    if (selectedDaysList.contains(DayList[2])){
                        selectedDaysList.remove(DayList[2]);
                    }
                }
            }
        });


        thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!selectedDaysList.contains(DayList[3])){
                        selectedDaysList.add(DayList[3]);
                    }
                }else {
                    if (selectedDaysList.contains(DayList[3])){
                        selectedDaysList.remove(DayList[3]);
                    }
                }
            }
        });

        friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!selectedDaysList.contains(DayList[4])){
                        selectedDaysList.add(DayList[4]);
                    }
                }else {
                    if (selectedDaysList.contains(DayList[4])){
                        selectedDaysList.remove(DayList[4]);
                    }
                }
            }
        });

        saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!selectedDaysList.contains(DayList[5])){
                        selectedDaysList.add(DayList[5]);
                    }
                }else {
                    if (selectedDaysList.contains(DayList[5])){
                        selectedDaysList.remove(DayList[5]);
                    }
                }
            }
        });

        sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!selectedDaysList.contains(DayList[6])){
                        selectedDaysList.add(DayList[6]);
                    }
                }else {
                    if (selectedDaysList.contains(DayList[6])){
                        selectedDaysList.remove(DayList[6]);
                    }
                }
            }
        });


    }


    private void getSubService() {

        progressBar.setVisibility(View.VISIBLE);

        String ServerURL;
        ServerURL= getResources().getString(R.string.pneck_app_url) + "/Vendor/subcat_load";

        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("service_id",""+primeCategoryHash.get(serviceList.get(selectProfession.getSelectedItemPosition())));


        Log.e("fetch_error", "this is url " +ServerURL);

        Log.e("fetch_error", "this is we sending " + dataParams.toString());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                subCategorySuccess(),
                ServiceError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(VendorMainPage.this).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> subCategorySuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("fetch_error", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {

                        subCategoryHash.clear();
                        subserviceList.clear();
                        progressBar.setVisibility(View.GONE);
                        JSONObject obj=innerResponse.getJSONObject("data");
                        JSONArray list=obj.getJSONArray("subcategory_list");

                        if (subserviceList.size()==0){
                            subserviceList.add("Select Sub Category");
                        }

                        for (int i=0;i<list.length();i++){
                            JSONObject object = list.getJSONObject(i);
                            if (!subserviceList.contains(object.getString("name"))) {
                                subserviceList.add(object.getString("name"));
                                subCategoryHash.put(object.getString("name"),object.getString("id"));
                            }
                        }


                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(VendorMainPage.this, R.layout.support_simple_spinner_dropdown_item,
                                subserviceList);

                        selectSubCategory.setAdapter(arrayAdapter);

                    }

                } catch (JSONException e) {
                    Log.v("fetch_error", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private void getCatalogList() {

        progressBar.setVisibility(View.VISIBLE);

        String ServerURL;
        ServerURL= getResources().getString(R.string.pneck_app_url) + "/Vendor/catalogue_load";

        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("subcat_id",""+subCategoryHash.get(subserviceList.get(selectSubCategory.getSelectedItemPosition())));
        //dataParams.put("subcat_id",""+selectSubCategory.getSelectedItemPosition());

        Log.e("otp_verfication", "this is url " +ServerURL);

        Log.e("otp_verfication", "this is we sending " + dataParams.toString());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                catalogSuccess(),
                ServiceError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(VendorMainPage.this).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> catalogSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("otp_verfication", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {

                        catalogHash.clear();
                        subservicecatalogList.clear();
                        progressBar.setVisibility(View.GONE);
                        JSONObject obj=innerResponse.getJSONObject("data");
                        JSONArray list=obj.getJSONArray("catalogue");

                        if (subservicecatalogList.size()==0){
                            subservicecatalogList.add("Select Catalogue");
                        }

                        for (int i=0;i<list.length();i++){
                            JSONObject object = list.getJSONObject(i);
                            if (!subservicecatalogList.contains(object.getString("name"))) {
                                subservicecatalogList.add(object.getString("name"));
                                catalogHash.put(object.getString("name"),object.getString("id"));
                            }
                        }


                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(VendorMainPage.this, R.layout.support_simple_spinner_dropdown_item,
                                subservicecatalogList);

                        selectCatalogue.setAdapter(arrayAdapter);

                    }

                } catch (Exception e) {
                    Log.v("otp_verfication", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }


    private void getPneckService() {

        progressBar.setVisibility(View.VISIBLE);

        String ServerURL;
        ServerURL= getResources().getString(R.string.pneck_app_url) + "/Vendor/coreServices";

        HashMap<String, String> dataParams = new HashMap<String, String>();


        Log.e("get_service", "this is url " +ServerURL);

        Log.e("get_service", "this is we sending " + dataParams.toString());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.GET,
                ServerURL,
                dataParams,
                ServiceSuccess(),
                ServiceError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(VendorMainPage.this).add(dataParamsJsonReq);
    }


    private Response.Listener<JSONObject> ServiceSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("get_service", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        primeCategoryHash.clear();
                        progressBar.setVisibility(View.GONE);
                        JSONObject obj=innerResponse.getJSONObject("data");
                        JSONArray list=obj.getJSONArray("list");

                        serviceList.add("Select Service");
                        for (int i=0;i<list.length();i++){
                            JSONObject object = list.getJSONObject(i);
                            if (!serviceList.contains(object.getString("title"))) {
                                serviceList.add(object.getString("title"));
                                primeCategoryHash.put(object.getString("title"),object.getString("id"));
                            }
                        }


                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(VendorMainPage.this, R.layout.support_simple_spinner_dropdown_item,
                                serviceList);

                        selectProfession.setAdapter(arrayAdapter);

                    }

                } catch (Exception e) {
                    Log.v("get_service", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener ServiceError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                Log.v("fetch_error", "inside error block  " + error.getMessage());
            }
        };
    }




    private void clickListeners() {

        actualSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SearchItemEdit.getText().toString().length()>0){
                    vendorTitle.setText(SearchItemEdit.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    vendorList.clear();
                    vendorListAdapters.notifyDataSetChanged();
                    getFilteredData();
                }else {
                    searchToolbar.setVisibility(View.GONE);
                    normalToolbar.setVisibility(View.VISIBLE);
                }
                PublicMethods.hideKeyboard(VendorMainPage.this);
            }
        });

        goBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchToolbar.setVisibility(View.GONE);
                normalToolbar.setVisibility(View.VISIBLE);
                PublicMethods.hideKeyboard(VendorMainPage.this);
            }
        });
        openSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchToolbar.setVisibility(View.VISIBLE);
                normalToolbar.setVisibility(View.GONE);
                SearchItemEdit.setFocusable(true);
                SearchItemEdit.setSelection(0);
                SearchItemEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(SearchItemEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        FilterWorkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        filterBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("kjdsfhjksfs","filter background is clicked ");
                hideExtraMenu();
            }
        });
        filterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("kjdsfhjksfs","filter filterList is clicked ");
                showExtraMenu();
            }
        });
        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("kjdsfhjksfs","filter closeFilter is clicked ");
                hideExtraMenu();
            }
        });
        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                progressBar.setVisibility(View.VISIBLE);
                vendorList.clear();
                vendorListAdapters.notifyDataSetChanged();
                getFilteredData();
            }
        });
    }

    private void getFilteredData() {

        progressBar.setVisibility(View.VISIBLE);
        String ServerURL;
        ServerURL= getResources().getString(R.string.pneck_app_url) + "/userNearByVendorList";

        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id",""+sessionManager.getUserid());
        dataParams.put("ep_token",""+sessionManager.getUserToken());
        dataParams.put("curr_lat",""+userLatitude);
        dataParams.put("curr_long",""+userLongitude);
        dataParams.put("vendor_search",""+SearchItemEdit.getText().toString());
        /*if (!staticVendor.isChecked()&&!dynamicVendor.isChecked()){
            Toast.makeText(VendorMainPage.this,getString(R.string.PLEASE_SELECT_VENDOR_TYPE),Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (staticVendor.isChecked()){
            dataParams.put("type",""+"static");
        }

        if (dynamicVendor.isChecked()){
            dataParams.put("type",""+"dynamic");
        }

        if (selectProfession.getSelectedItemPosition()>0){
            dataParams.put("service_id",""+primeCategoryHash.get(serviceList.get(selectProfession.getSelectedItemPosition())));
        }
        if (selectSubCategory.getSelectedItemPosition()>0){
            dataParams.put("subcat_id",""+subCategoryHash.get(subserviceList.get(selectSubCategory.getSelectedItemPosition())));
        }
        if (selectCatalogue.getSelectedItemPosition()>0){
            dataParams.put("catalogue_id",""+catalogHash.get(subservicecatalogList.get(selectCatalogue.getSelectedItemPosition())));
        }
        if (selectedDaysList.size()>0){
            String DaysString="";
            for(int i=0;i<selectedDaysList.size();i++){
                if (i!=0){
                    DaysString+=",";
                }
                DaysString+=selectedDaysList.get(i);
            }
            dataParams.put("days",DaysString);
        }

        Log.e("fetch_error", "this is url " +ServerURL);

        Log.e("fetch_error", "this is we sending " + dataParams.toString());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                ShowSuccess(),
                ServiceError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(VendorMainPage.this).add(dataParamsJsonReq);
    }

    @Override
    public void onBackPressed() {
        if (isExtraOpen){
            hideExtraMenu();
        }else {
            super.onBackPressed();
        }

    }

    private boolean isExtraOpen=false;
    private void showExtraMenu(){
        if (!isExtraOpen){
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
            anim.reset();
            //InStockOptionMainLayout.setVisibility(View.VISIBLE);
            filterLayout.setVisibility(View.VISIBLE);
            filterLayout.clearAnimation();
            filterLayout.startAnimation(anim);
            isExtraOpen=true;
        }

        //extraOptionLayout.setVisibility(View.VISIBLE);
    }
    private void hideExtraMenu(){
        if (isExtraOpen){
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
            anim.reset();

            filterLayout.clearAnimation();
            filterLayout.startAnimation(anim);
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable(){
                public void run(){
                    //InStockOptionMainLayout.setVisibility(View.GONE);
                    isExtraOpen=false;
                    filterLayout.setVisibility(View.GONE);
                }
            }, 198);
        }
    }

    private void getDataList() {

        progressBar.setVisibility(View.VISIBLE);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userNearByVendorList";
        HashMap<String, String> dataParams = new HashMap<String, String>();


        dataParams.put("user_id",sessionManager.getUserid());
        dataParams.put("ep_token",sessionManager.getUserToken());
        dataParams.put("curr_lat",userLatitude);
        dataParams.put("curr_long",userLongitude);
        dataParams.put("curr_address","empty");

        Log.e("user_near_by_vendor", "this is url " +ServerURL);

        Log.e("user_near_by_vendor", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                ShowSuccess(),
                ShowError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(VendorMainPage.this).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> ShowSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("user_near_by_vendor", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject data=innerResponse.getJSONObject("data");
                        JSONArray vendorsList=data.getJSONArray("vendors");
                        for (int i=0;i<vendorsList.length();i++){
                            JSONObject object=vendorsList.getJSONObject(i);
                            vendorList.add(new VendorModel(object.getString("vendor_id"),
                                    object.getString("rating"),
                                    !object.getString("shop_title").equalsIgnoreCase("null")?object.getString("shop_title"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    !object.getString("first_name").equalsIgnoreCase("null")?object.getString("first_name"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    !object.getString("type").equalsIgnoreCase("null")?object.getString("type"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    !object.getString("opening_time").equalsIgnoreCase("null")?object.getString("opening_time"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    !object.getString("closing_time").equalsIgnoreCase("null")?object.getString("closing_time"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    object.getString("phone"),
                                    object.getString("image"),
                                    !object.getString("professional_service").equalsIgnoreCase("null")?object.getString("professional_service"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    !object.getString("category").equalsIgnoreCase("null")?object.getString("category"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    !object.getString("catalogues").equalsIgnoreCase("null")?object.getString("catalogues"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    !object.getString("distance_km").equalsIgnoreCase("null")?object.getString("distance_km"):getResources().getString(R.string.DATA_NOT_FOUND),
                                    object.getString("curr_latitude"),
                                    object.getString("curr_longitude"),
                                    object.getString("curr_loc_address")));
                        }
                        Log.v("user_near_by_vendor", "updating the vendors list size is " + vendorList.size());
                        vendorListAdapters.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.v("user_near_by_vendor", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener ShowError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG+error.getMessage(), Toast.LENGTH_LONG).show();
                Log.v("user_near_by_vendor", "inside error block  " + error.getMessage());
            }
        };
    }


}
