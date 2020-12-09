package com.callpneck.taxi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.taxi.activity.DestinationPickerActivity;

import android.location.Address;
import android.widget.TextView;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyHolder> {
    Context context;
    List<Address> addressList;

    public AddressAdapter(Context context, List<Address> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String addressMain=addressList.get(position).getLocality();
        String addressLoc=addressList.get(position).getAddressLine(0);

        holder.addressTv.setText(addressMain);
        holder.addressTv2.setText(addressLoc);

        holder.addressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("addr",addressList.get(position).getAddressLine(0));
                data.putExtra("lat",addressList.get(position).getLatitude());
                data.putExtra("lot",addressList.get(position).getLongitude());

            }


        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView addressTv;
        TextView addressTv2;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            addressTv = itemView.findViewById(R.id.tv_address);
            addressTv2 = itemView.findViewById(R.id.tv_address2);
        }
    }
}
