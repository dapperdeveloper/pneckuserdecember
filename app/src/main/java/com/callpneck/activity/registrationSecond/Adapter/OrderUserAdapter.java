package com.callpneck.activity.registrationSecond.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.SideMenuScreens.HelpScreen;
import com.callpneck.activity.TrackOrder.TrackOrderActivity;
import com.callpneck.activity.registrationSecond.Activity.ReceiptOrderActivity;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUserList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderUserAdapter extends RecyclerView.Adapter<OrderUserAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(OrderUserList item);
    }
    Activity context;
    List<OrderUserList> itemList;
    private final OnItemClickListener listener;

    public OrderUserAdapter(Activity context, List<OrderUserList> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_restaurent_order_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(itemList.get(position), listener);
        holder.viewDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReceiptActivity(itemList.get(position).getId()+"", itemList.get(position).getType());
            }
        });
        holder.helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpActivity();
            }
        });
    }

    private void openHelpActivity() {
        Intent intent = new Intent(context, HelpScreen.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openReceiptActivity(String oid, String type) {
        Intent intent = new Intent(context, ReceiptOrderActivity.class);
        intent.putExtra("status", oid);
        intent.putExtra("type", type);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView shopNameTv, orderStatusTv, orderType, orderIdTv,
                totalAmountTv, addressTv,  dateOfOrderTv;
        CircleImageView productIv;
        Button trackOrderBtn, viewDetailBtn, helpBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            trackOrderBtn = itemView.findViewById(R.id.button2);
            productIv = itemView.findViewById(R.id.productIv);
            shopNameTv = itemView.findViewById(R.id.textView3);
            orderStatusTv = itemView.findViewById(R.id.textView5);
            orderType = itemView.findViewById(R.id.textView6);
            orderIdTv = itemView.findViewById(R.id.textView7);
            totalAmountTv = itemView.findViewById(R.id.textView8);
            addressTv = itemView.findViewById(R.id.addressTv);
            dateOfOrderTv = itemView.findViewById(R.id.textView9);
            viewDetailBtn = itemView.findViewById(R.id.viewDetailBtn);
            helpBtn = itemView.findViewById(R.id.button3);

        }

        public void bind(final OrderUserList item, OnItemClickListener listener){
            orderIdTv.setText("#"+item.getOrderNumber());
            shopNameTv.setText(item.getResName());
            dateOfOrderTv.setText(item.getDates());
            addressTv.setText(item.getResAddress());
            String status = item.getStatus();
            if (status.equalsIgnoreCase("Cancelled")){
                orderStatusTv.setTextColor(context.getResources().getColor(R.color.white));
                orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.red));
                viewDetailBtn.setVisibility(View.VISIBLE);
                trackOrderBtn.setVisibility(View.GONE);
            }
            else if (status.equalsIgnoreCase("Delivered")){
                orderStatusTv.setTextColor(context.getResources().getColor(R.color.white));
                orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.blue1));
                viewDetailBtn.setVisibility(View.VISIBLE);
                trackOrderBtn.setVisibility(View.GONE);
            }
            else {
                orderStatusTv.setTextColor(context.getResources().getColor(R.color.white));
                orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.light_green));
                viewDetailBtn.setVisibility(View.GONE);
                trackOrderBtn.setVisibility(View.VISIBLE);
            }
            orderStatusTv.setText(status);
            totalAmountTv.setText(new StringBuilder("Rs.").append(item.getTotalAmount()));
            Glide.with(context).load(item.getResImage()).placeholder(R.drawable.pneck_logo).into(productIv);
            trackOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });


        }
    }
}
