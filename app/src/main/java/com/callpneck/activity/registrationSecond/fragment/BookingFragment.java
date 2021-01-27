package com.callpneck.activity.registrationSecond.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.TrackOrder.TrackOrderActivity;
import com.callpneck.activity.registrationSecond.Activity.ReceiptOrderActivity;
import com.callpneck.activity.registrationSecond.Adapter.BookingAdapter;
import com.callpneck.activity.registrationSecond.Adapter.OrderUserAdapter;
import com.callpneck.activity.registrationSecond.Model.BookingResponse.BookingResponse;
import com.callpneck.activity.registrationSecond.Model.BookingResponse.MyOrder;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUser;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUserList;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.adapter.UserOrderAdapters;
import com.callpneck.model.UserOrderModel;
import com.callpneck.utils.CustPrograssbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

public class BookingFragment extends Fragment {
    TextView tabBookingTv, tabOrdersTv;
    private RecyclerView  orderRv;
    private List<OrderUserList> orderUserList;
    private List<MyOrder> bookingResponseList;
    private RelativeLayout bookingRl, orderRl;
    private SessionManager sessionManager;
    private RecyclerView bookingRv;
    private ArrayList<UserOrderModel> orderList =new ArrayList<>();
    private ArrayList<String> addedOrderList=new ArrayList<>();
    private UserOrderAdapters userOrderAdapters;
    private TextView emptyView, noOrderTv, noBookingTv;
    CustPrograssbar custPrograssbar;
    String user_id;
    SwipeRefreshLayout swipeLayout, swipeLayoutSecond;


    public BookingFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_booking, container, false);
        custPrograssbar = new CustPrograssbar();
        init(view);

        sessionManager=new SessionManager(getActivity());
        user_id = sessionManager.getUserid();

        showBookingUI();

        bookingRv.setHasFixedSize(true);
        bookingRv.setItemViewCacheSize(20);
        userOrderAdapters = new UserOrderAdapters(getActivity(), orderList);
        bookingRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));



        tabBookingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load booking
                if (AppController.isConnected(getActivity()))
                showBookingUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.isConnected(getActivity()))
                    getUserOrderList();

                //load orders
                showOrdersUI();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBookingList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        swipeLayoutSecond.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserOrderList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayoutSecond.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        if (AppController.isConnected(getActivity()))
            getBookingList();

        return view;
    }

    private void getBookingList() {
        custPrograssbar.PrograssCreate(getContext());
        bookingResponseList = new ArrayList<>();
        Call<BookingResponse> call = APIClient.getInstance().getBookingList(sessionManager.getUserid(),sessionManager.getUserToken());
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, retrofit2.Response<BookingResponse> response) {
                if(response.isSuccessful()){
                    custPrograssbar.ClosePrograssBar();
                    try {
                        BookingResponse model = response.body();
                        if (model.getResponse()!= null && model.getResponse().getSuccess()){

                            if (model.getResponse().getData().getMyOrders().size()>0){
                                emptyView.setVisibility(View.GONE);
                                bookingResponseList.clear();
                                bookingResponseList = model.getResponse().getData().getMyOrders();
                                bookingRv.setAdapter(new BookingAdapter(getContext(), bookingResponseList));
                            }
                        }

                    }catch (Exception e){
                        e.toString();
                        custPrograssbar.ClosePrograssBar();
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                custPrograssbar.ClosePrograssBar();
            }
        });
    }

    private void getUserOrderList() {
        custPrograssbar.PrograssCreate(getContext());
        orderUserList = new ArrayList<>();
        Call<OrderUser> call = APIClient.getInstance().getUserOrderList(user_id);
        call.enqueue(new Callback<OrderUser>() {
            @Override
            public void onResponse(Call<OrderUser> call, retrofit2.Response<OrderUser> response) {
                if(response.isSuccessful()){
                    custPrograssbar.ClosePrograssBar();
                    try {
                        OrderUser orderUser = response.body();
                        if (orderUser != null && orderUser.getErrorCode()==0){
                            if (orderUser.getData().size()==0){
                                noOrderTv.setVisibility(View.VISIBLE);
                            }
                            else {
                                orderUserList.clear();
                                orderUserList = orderUser.getData();
                                orderRv.setAdapter(new OrderUserAdapter(getActivity(), orderUserList, new OrderUserAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(OrderUserList item) {
                                        Intent intent = new Intent(getActivity(), TrackOrderActivity.class);
                                        intent.putExtra("status",item.getStatus()+"");
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                    }
                                }));
                                noOrderTv.setVisibility(View.GONE);
                            }

                        }
                    }
                    catch (Exception e){
                        e.toString();
                        custPrograssbar.ClosePrograssBar();
                    }
                }


            }

            @Override
            public void onFailure(Call<OrderUser> call, Throwable t) {
                custPrograssbar.ClosePrograssBar();
            }
        });

    }


    private void init(View view) {
        swipeLayout = view.findViewById(R.id.swipeLayout);
        swipeLayoutSecond = view.findViewById(R.id.swipeLayoutSecond);
        tabOrdersTv = view.findViewById(R.id.tabOrdersTv);
        tabBookingTv = view.findViewById(R.id.tabBookingTv);
        bookingRl = view.findViewById(R.id.bookingRl);
        orderRl = view.findViewById(R.id.orderRl);
        orderRv = view.findViewById(R.id.orderRv);
        bookingRv = view.findViewById(R.id.bookingRv);
        emptyView=view.findViewById(R.id.noBookingTv);
        noOrderTv = view.findViewById(R.id.noOrderTv);


    }

    private void showBookingUI() {
        //show product ui  and hide orders ui
        bookingRl.setVisibility(View.VISIBLE);
        orderRl.setVisibility(View.GONE);
        tabBookingTv.setTextColor(getResources().getColor(R.color.white));
        tabBookingTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }
    private void showOrdersUI() {
        //show orders ui and hide product ui
        bookingRl.setVisibility(View.GONE);
        orderRl.setVisibility(View.VISIBLE);

        tabBookingTv.setTextColor(getResources().getColor(R.color.black));
        tabBookingTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);
    }
}