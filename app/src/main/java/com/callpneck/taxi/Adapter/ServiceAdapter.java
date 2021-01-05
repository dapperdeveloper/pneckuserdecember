package com.callpneck.taxi.Adapter;

import android.app.Service;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.taxi.model.CarType;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 08-05-2018.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    Context context;
    List<CarType> carTypes;

    public ServiceAdapter(Context context, List<CarType> carTypes) {
        this.context = context;
        this.carTypes = carTypes;
    }
    public List<CarType> getCarTypeList()
    {
        return  carTypes;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_service, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.serviceName.setText(carTypes.get(position).getCarName());
        Glide.with(context).load(carTypes.get(position).getImageUrl()).into(holder.image);
        if(carTypes.get(position).getSelected()==1)
        {
            holder.imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue1));
        }
        else
        {
            holder.imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return carTypes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout itemView;
        private RelativeLayout imageView;
        private TextView serviceName, price;
        private ImageView image;

        MyViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.service_name);
            price = view.findViewById(R.id.price);
            price.setVisibility(View.GONE);
            imageView = view.findViewById(R.id.imageView);
            image = view.findViewById(R.id.image);
            itemView = view.findViewById(R.id.item_view);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0;i<carTypes.size();i++){
                        if(carTypes.get(i).getSelected()==1)
                        {
                            carTypes.set(i,new CarType(carTypes.get(i).getId(),carTypes.get(i).getCarName(),carTypes.get(i).getImageUrl(),0));
                        }
                    }
                    carTypes.set(getAdapterPosition(),new CarType(carTypes.get(getAdapterPosition()).getId(),carTypes.get(getAdapterPosition()).getCarName(),carTypes.get(getAdapterPosition()).getImageUrl(),1));
                    notifyDataSetChanged();
                }
            });
        }


    }



}
