package com.callpneck.activity.SideMenuScreens;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.Const;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.adapter.UserOrderAdapters;
import com.callpneck.model.UserOrderModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MyUserOrderList extends AppCompatActivity {

    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private RecyclerView mRecyclerView;
    private ArrayList<UserOrderModel> orderList =new ArrayList<>();
    private ArrayList<String> addedOrderList=new ArrayList<>();
    private UserOrderAdapters userOrderAdapters;
    private ImageView goBack;
    private RelativeLayout emptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_my_user_order_list);

        goBack=findViewById(R.id.go_back);
        mRecyclerView=findViewById(R.id.recycler_view);
        progressBar=findViewById(R.id.progress_bar);
        emptyView=findViewById(R.id.empty_view);
        sessionManager=new SessionManager(MyUserOrderList.this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        userOrderAdapters = new UserOrderAdapters(MyUserOrderList.this, orderList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyUserOrderList.this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(userOrderAdapters);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        checkINDatabase();


    }

    private void checkINDatabase() {

        progressBar.setVisibility(View.VISIBLE);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userMyOrders";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id",sessionManager.getUserid());
        dataParams.put("ep_token",sessionManager.getUserToken());

        Log.e("user_order_list", "this is url " +ServerURL);

        Log.e("user_order_list", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                RegistrationSuccess(),
                RegistrationError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(MyUserOrderList.this).add(dataParamsJsonReq);
    }


    private Response.Listener<JSONObject> RegistrationSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.v("user_order_list", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");

                    if (innerResponse.getBoolean("success")) {

                        String msg=innerResponse.getString("message");

                        if (msg.trim().equalsIgnoreCase("Info-No data!")){
                            emptyView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }else {
                            JSONObject data=innerResponse.getJSONObject("data");

                            JSONArray orders=data.getJSONArray("my_orders");

                            for (int i=0;i<orders.length();i++){
                                JSONObject object=orders.getJSONObject(i);
                                if (!addedOrderList.contains(object.getString("order_number"))){
                                    orderList.add(new UserOrderModel(object.getString("id"),
                                            object.getString("order_number"),object.getString("order_status"),
                                            object.getString("accept_otp_confirm_at"),object.getString("delivery_confirm_at"),
                                            object.getString("order_subtotal"),object.getString("booking_charge"),
                                            object.getString("grand_total"),object.getString("booking_complete_at")));
                                }

                            }

                            userOrderAdapters.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                    if (orderList.size()==0){
                        emptyView.setVisibility(View.VISIBLE);
                    }else {
                        emptyView.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);

                } catch (Exception e) {
                    Log.v("user_order_list", "inside catch block  " + e.getMessage());
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                Log.v("user_order_list", "inside error block  " + error.getMessage());
            }
        };
    }
}
