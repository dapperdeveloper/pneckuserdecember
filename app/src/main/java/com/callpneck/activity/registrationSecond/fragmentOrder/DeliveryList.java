package com.callpneck.activity.registrationSecond.fragmentOrder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery.DeliveryData;
import com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery.DeliveryOrder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DeliveryList extends Fragment {


    public DeliveryList() {
        // Required empty public constructor
    }
    private TextView noOrderTv;
    SwipeRefreshLayout swipeLayoutSecond;
    private RecyclerView orderRv;
    String user_id;
    private SessionManager sessionManager;

    private List<DeliveryData> orderUserList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_delivery_list, container, false);
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
        Call<DeliveryOrder> call = APIClient.getInstance().getUserOrderDelivery(user_id);
        call.enqueue(new Callback<DeliveryOrder>() {
            @Override
            public void onResponse(Call<DeliveryOrder> call, retrofit2.Response<DeliveryOrder> response) {
                if(response.isSuccessful()){
                    try {
                        DeliveryOrder orderUser = response.body();
                        if (orderUser != null && orderUser.getErrorCode()==0){
                            if (orderUser.getData().size()==0){
                                noOrderTv.setVisibility(View.VISIBLE);
                            }
                            else {
                                orderUserList.clear();
                                orderUserList = orderUser.getData();
                                orderRv.setAdapter(new DeliveryUserAdapter(getActivity(), orderUserList, new DeliveryUserAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DeliveryData item) {
                                        Intent intent = new Intent(getContext(), CustomerOrderDetailActivity.class);
                                        intent.putExtra("oid",item.getId()+"");
                                        intent.putExtra("type", item.getType()+"");
                                        startActivity(intent);
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
            public void onFailure(Call<DeliveryOrder> call, Throwable t) {
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