package com.callpneck.activity.deliveryboy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.deliveryboy.model.DUser;
import com.callpneck.activity.deliveryboy.model.DriverList_;
import com.callpneck.activity.registrationSecond.Adapter.MyShopAdapter;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductFood;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoyListAdapter  extends RecyclerView.Adapter<BoyListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DriverList_ item);
    }
    List<DriverList_> dUserList;
    Context context;

    SessionManager sessionManager;
    private final OnItemClickListener listener;

    public BoyListAdapter(List<DriverList_> dUserList, Context context, OnItemClickListener listener) {
        this.dUserList = dUserList;
        this.context = context;
        this.listener = listener;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_delivery_boy_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(dUserList.get(position), listener);

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
        return dUserList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTv, deliveryFeeTv, distanceTv;
        Button makeOrderBtn;
        CircleImageView avatarIv;
        ImageView gpsBtn, callBtn;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.textView4);
            makeOrderBtn = itemView.findViewById(R.id.button4);
            avatarIv = itemView.findViewById(R.id.img_shopping_item);
            deliveryFeeTv = itemView.findViewById(R.id.textView13);
            ratingBar = itemView.findViewById(R.id.ratingBar2);
            distanceTv = itemView.findViewById(R.id.distanceTv);
            gpsBtn = itemView.findViewById(R.id.gpsBtn);
            callBtn = itemView.findViewById(R.id.callBtn);
        }
        public void bind(final DriverList_ item, OnItemClickListener listener){

            String sourceLatitude = sessionManager.getUserLatitude();
            String sourceLongitude = sessionManager.getUserLongitude();
            gpsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sourceLatitude!= null)
                        openMap(sourceLatitude, sourceLongitude, item.getCurrLatitude()+"", item.getCurrLongitude()+"");
                }
            });
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCallCustomer(item.getMobile()+"");
                }
            });
            nameTv.setText(item.getFirstName()+" "+item.getLastName());
            String distance = item.getDistance()+"";

            String km = distance.substring(0,3);

            distanceTv.setText(km +" Km away");
            int rateKm = Integer.parseInt(distance.substring(0,1));
            if (rateKm==0){
                deliveryFeeTv.setText("Delivery Fee: Rs.30");
            }
            else if (rateKm == 1){
                deliveryFeeTv.setText("Delivery Fee: Rs.30");
            }
            else if (rateKm == 2){
                deliveryFeeTv.setText("Delivery Fee: Rs.40");
            }
            else if (rateKm == 3){
                deliveryFeeTv.setText("Delivery Fee: Rs.50");
            }
            else if(rateKm == 4){
                deliveryFeeTv.setText("Delivery Fee: Rs.60");
            }
            else {
                deliveryFeeTv.setText("Delivery Fee: Rs.100");
            }

            try{
                Glide.with(context).load(item.getVehicleImage()).into(avatarIv);
            }catch (Exception e){
                e.toString();
            }

            try{
                float rating = (float) item.getRating();
                ratingBar.setRating(rating);
            }catch (Exception e){

            }



            makeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
