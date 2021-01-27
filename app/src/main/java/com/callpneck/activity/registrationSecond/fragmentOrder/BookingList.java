package com.callpneck.activity.registrationSecond.fragmentOrder;

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
import com.callpneck.activity.registrationSecond.Adapter.BookingAdapter;
import com.callpneck.activity.registrationSecond.Model.BookingResponse.BookingResponse;
import com.callpneck.activity.registrationSecond.Model.BookingResponse.MyOrder;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.utils.CustPrograssbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class BookingList extends Fragment {


    public BookingList() {
        // Required empty public constructor
    }
    private List<MyOrder> bookingResponseList;
    TextView emptyView;
    SwipeRefreshLayout swipeLayout;
    String user_id;
    private RecyclerView bookingRv;

    private SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

        init(view);
        sessionManager=new SessionManager(getActivity());
        user_id = sessionManager.getUserid();

        if (AppController.isConnected(getActivity()))
            getBookingList();


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
        return view;
    }

    private void init(View view) {
        swipeLayout = view.findViewById(R.id.swipeLayout);
        bookingRv = view.findViewById(R.id.bookingRv);
        emptyView=view.findViewById(R.id.noBookingTv);
    }

    private void getBookingList() {
        bookingResponseList = new ArrayList<>();
        Call<BookingResponse> call = APIClient.getInstance().getBookingList(sessionManager.getUserid(),sessionManager.getUserToken());
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, retrofit2.Response<BookingResponse> response) {
                if(response.isSuccessful()){
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
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
            }
        });
    }
}
