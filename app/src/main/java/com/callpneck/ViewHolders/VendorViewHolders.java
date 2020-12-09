package com.callpneck.ViewHolders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.model.VendorModel;

public class VendorViewHolders extends RecyclerView.ViewHolder {

    private ImageView userImage;
    private TextView vendorName;
    private TextView vendorRating;
    private TextView primeService;
    private TextView category;
    private TextView catalog;
    private TextView distanceAway;
    private TextView workingTime;
    private LinearLayout phoneLayout;
    private TextView userPhoneNo;
    private LinearLayout cancelOrder;
    private TextView vendorType;



    public VendorViewHolders(View itemView) {
        super(itemView);
        userImage = (ImageView)itemView.findViewById( R.id.user_image );
        vendorName = (TextView)itemView.findViewById( R.id.vendor_name );
        vendorRating = (TextView)itemView.findViewById( R.id.vendor_rating );
        primeService = (TextView)itemView.findViewById( R.id.prime_service );
        category = (TextView)itemView.findViewById( R.id.category );
        catalog = (TextView)itemView.findViewById( R.id.catalog );
        distanceAway = (TextView)itemView.findViewById( R.id.distance_away );
        workingTime = (TextView)itemView.findViewById( R.id.working_time );
        phoneLayout = (LinearLayout)itemView.findViewById( R.id.phone_layout );
        userPhoneNo = (TextView)itemView.findViewById( R.id.user_phone_no );
        cancelOrder = (LinearLayout)itemView.findViewById( R.id.cancel_order );
        vendorType = (TextView)itemView.findViewById( R.id.vendor_type );
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUpCardView(final VendorViewHolders holder, final Activity context,
                              final VendorModel data, final int position,String userLat,String userLong){

        Glide.with(context)
                .load(data.getUserImage())
                .placeholder(R.drawable.ic_account_user)
                .error(R.drawable.ic_account_user)
                .dontAnimate().into(userImage);
        vendorName.setText(data.getShopName());
        workingTime.setText("Working Time : "+data.getOpenTime()+" to "+data.getCloseTime());
        vendorRating.setText(data.getVendorRating());
        primeService.setText("Service : "+data.getPrimeService());
        category.setText("Category : "+data.getCategory());
        catalog.setText("Catalog : "+data.getCatalogue());
        distanceAway.setText("Distance : "+data.getDistanc_km()+" away");
        if (data.getUserType().equalsIgnoreCase("static")){
            vendorType.setText(context.getResources().getString(R.string.STATIC));
        }else {
            vendorType.setText(context.getResources().getString(R.string.DYNAMIC));
        }

        holder.phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", data.getUserPhone(), null));
                        context.startActivity(intent);
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("user_lat",userLat);
                bundle.putString("user_long",userLong);
                bundle.putString("vendor_id",data.getVendorId());
                LaunchActivityClass.LaunchCompleteVendorDisplay(context,bundle);
            }
        });
    }

}