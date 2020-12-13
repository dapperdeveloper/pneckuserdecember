package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Activity.ServiceDetailActivity;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductFood;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.Vendor;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProviderAdapter extends RecyclerView.Adapter<MyProviderAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Vendor item);
    }
    Context context;
    List<Vendor> providerList;
    SessionManager sessionManager;
    private final OnItemClickListener listener;
    public MyProviderAdapter(Context context, List<Vendor> providerList, OnItemClickListener listener) {
        this.context = context;
        this.providerList = providerList;
        this.listener = listener;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_provider_list, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.bind(providerList.get(position), listener);



    }

    private void openMap(String sourceLatitude, String sourceLongitude, String destinationLatitude, String destinationLongitude) {
        String address = "https://maps.google.com/maps?saddr=" +sourceLatitude +","+sourceLongitude
                + "&daddr=" + destinationLatitude +"," +destinationLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        context.startActivity(intent);
    }
    private void setCallCustomer(String providerNum){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", providerNum, null));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView shopIV;
        TextView shopNameTv, shopDistanceTv, moreBtn;
        RatingBar ratingBar;
        ImageView phoneBtn, gpsBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shopIV = itemView.findViewById(R.id.shopIV);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            shopDistanceTv = itemView.findViewById(R.id.shopDistanceTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            phoneBtn = itemView.findViewById(R.id.imageView3);
            gpsBtn = itemView.findViewById(R.id.imageView2);

        }
        public void bind(final Vendor item, OnItemClickListener listener){

            String providerNum = item.getPhone()+"";
            String destinationLatitude = item.getCurrLatitude();
            String destinationLongitude = item.getCurrLongitude();
            String sourceLatitude = sessionManager.getUserLatitude();
            String sourceLongitude = sessionManager.getUserLongitude();
           shopNameTv.setText(item.getShopTitle()+"");
            shopDistanceTv.setText(item.getDistanceKm()+" Km Away");

            try {
                float rating = item.getRating();
                ratingBar.setRating(rating);
            }catch (Exception e){
                ratingBar.setRating(0f);
            }


            Glide.with(context).load(item.getImage()).placeholder(R.drawable.ic_user_replace).into(shopIV);

            phoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCallCustomer(providerNum);
                }
            });
            gpsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sourceLatitude!= null)
                        openMap(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });


        }
    }

}
