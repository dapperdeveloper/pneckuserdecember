package com.callpneck.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.callpneck.R;
import com.callpneck.ViewHolders.VendorViewHolders;
import com.callpneck.model.VendorModel;

import java.util.ArrayList;

public class VendorAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_VIEW_BASIC_TYPE = 1;

    private ArrayList<VendorModel> itemsList;
    private Activity mContext;
    private boolean value;

    private String userLat,userLong;

    public VendorAdapters(Activity context, ArrayList<VendorModel> itemsList,String lat,String longitude) {
        this.itemsList = itemsList;
        this.mContext = context;
        userLat=lat;
        userLong=longitude;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder vh;
        if (i == ITEM_VIEW_BASIC_TYPE) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.individual_vendor_layout, viewGroup, false);
            vh = new VendorViewHolders(v);
        }
        else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);
            vh = new UserOrderAdapters.ProgressViewHolder(v);
        }

        return vh;

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        if (rholder instanceof VendorViewHolders){
            final VendorViewHolders holder;
            holder = (VendorViewHolders) rholder;
            holder.setUpCardView(holder, mContext, itemsList.get(i),i,userLat,userLong);
        }
        else if (rholder instanceof UserOrderAdapters.ProgressViewHolder){
            /*if (!value) {
                ((ProgressViewHolder) rholder).progressBar.setVisibility(View.VISIBLE);
                CustomProgressLoading.StartOnlyProgress(((ProgressViewHolder) rholder).progressBar);
            } else ((ProgressViewHolder) rholder).progressBar.setVisibility(View.GONE);*/
        }


    }


    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_BASIC_TYPE;
    }


    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public interface ClickAdapterListener {


        void onRowLongClicked(int position);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ImageView progressBar;

        public ProgressViewHolder(View v) {
            super(v);
           /* progressBar = (ImageView) v.findViewById(R.id.loading_progress_bar);
            CustomProgressLoading.StartOnlyProgress(progressBar);*/
        }
    }

}