package com.callpneck.activity.registrationSecond.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Activity.OrderListActivity;
import com.callpneck.activity.registrationSecond.Model.OrderTracker;

import java.util.ArrayList;


public class OrderTrackerListFragment extends Fragment {

    RecyclerView recyclerView;
    TextView nodata;
    ProgressBar progressbar;
    private ArrayList<OrderTracker> orderTrackerArrayList;
    int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_tracker_list, container, false);

        pos = getArguments().getInt("pos");
        progressbar = v.findViewById(R.id.progressbar);
        recyclerView = v.findViewById(R.id.recycleview);
        nodata = (TextView) v.findViewById(R.id.nodata);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        switch (pos) {
            case 0:
                orderTrackerArrayList = OrderListActivity.orderTrackerslist;
                break;
            case 1:
                orderTrackerArrayList = OrderListActivity.processedlist;
                break;
            case 2:
                orderTrackerArrayList = OrderListActivity.shippedlist;
                break;
            case 3:
                orderTrackerArrayList = OrderListActivity.deliveredlist;
                break;
            case 4:
                orderTrackerArrayList = OrderListActivity.cancelledlist;
                break;
            case 5:
                orderTrackerArrayList = OrderListActivity.returnedList;
                break;
        }

//        if (orderTrackerArrayList.size() == 0)
            nodata.setVisibility(View.VISIBLE);

//        recyclerView.setAdapter(new TrackerAdapter(getActivity(), orderTrackerArrayList));
        progressbar.setVisibility(View.GONE);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
