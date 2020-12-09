package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.ModelServices;

import java.util.List;

public class MyServicesAdapter extends RecyclerView.Adapter<MyServicesAdapter.MyViewHolder> {

    Context context;
    List<ModelServices> servicesList;

    public MyServicesAdapter(Context context, List<ModelServices> servicesList) {
        this.context = context;
        this.servicesList = servicesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_service_item, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


      holder.serviceTitleTv.setText(servicesList.get(position).getTitle());
      holder.servicePriceTv.setText(servicesList.get(position).getPrice());
        holder.bookNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView serviceTitleTv, serviceDescriptionTv, servicePriceTv, bookNowBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitleTv = itemView.findViewById(R.id.serviceTitleTv);
            serviceDescriptionTv = itemView.findViewById(R.id.serviceDescriptionTv);
            servicePriceTv = itemView.findViewById(R.id.servicePriceTv);
            bookNowBtn = itemView.findViewById(R.id.bookNowBtn);

        }
    }
}
