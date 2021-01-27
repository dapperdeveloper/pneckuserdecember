package com.callpneck.activity.registrationSecond.fragmentOrder;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.TrackOrder.TrackOrderActivity;
import com.callpneck.activity.registrationSecond.Adapter.OrderUserAdapter;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUser;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUserList;
import com.callpneck.activity.registrationSecond.api.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ShopList extends Fragment {


    public ShopList() {
        // Required empty public constructor
    }
    private TextView noOrderTv;
    SwipeRefreshLayout swipeLayoutSecond;
    private RecyclerView orderRv;
    String user_id;
    private SessionManager sessionManager;

    private List<OrderUserList> orderUserList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_list, container, false);
        init(view);

        sessionManager=new SessionManager(getActivity());
        user_id = sessionManager.getUserid();

        if (AppController.isConnected(getActivity())){
            getUserOrderList();
        }


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
        return view;
    }

    private void getUserOrderList() {
        orderUserList = new ArrayList<>();
        Call<OrderUser> call = APIClient.getInstance().getUserOrderShop(user_id);
        call.enqueue(new Callback<OrderUser>() {
            @Override
            public void onResponse(Call<OrderUser> call, retrofit2.Response<OrderUser> response) {
                if(response.isSuccessful()){
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
                        noOrderTv.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onFailure(Call<OrderUser> call, Throwable t) {
                noOrderTv.setVisibility(View.VISIBLE);
            }
        });

    }
    private void init(View view) {
        swipeLayoutSecond = view.findViewById(R.id.swipeLayoutSecond);
        orderRv = view.findViewById(R.id.orderRv);
        noOrderTv = view.findViewById(R.id.noOrderTv);
    }
}