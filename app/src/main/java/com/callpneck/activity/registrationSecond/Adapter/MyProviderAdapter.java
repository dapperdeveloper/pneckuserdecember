package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Activity.ServiceDetailActivity;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.Vendor;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProviderAdapter extends RecyclerView.Adapter<MyProviderAdapter.MyViewHolder> {

    Context context;
    List<Vendor> providerList;

    public MyProviderAdapter(Context context, List<Vendor> providerList) {
        this.context = context;
        this.providerList = providerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_provider_list, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.shopNameTv.setText(providerList.get(position).getFirstName()+"");
        holder.shopDistanceTv.setText(providerList.get(position).getDistanceKm());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ServiceDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView shopIV;
        TextView shopNameTv, shopDistanceTv, moreBtn;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shopIV = itemView.findViewById(R.id.shopIV);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            shopDistanceTv = itemView.findViewById(R.id.shopDistanceTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}
