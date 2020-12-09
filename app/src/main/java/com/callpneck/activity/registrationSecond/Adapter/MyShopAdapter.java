package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.Category;
import com.callpneck.activity.registrationSecond.Model.FoodShop;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductFood;
import com.callpneck.activity.registrationSecond.api.ApiClient;

import java.util.List;

public class MyShopAdapter extends RecyclerView.Adapter<MyShopAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ProductFood item);
    }
    Context context;
    List<ProductFood> itemList;
    private final OnItemClickListener listener;

    public MyShopAdapter(Context context, List<ProductFood> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_restaurant_name_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(itemList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout offerLayout;
        ImageView shopIv;
        TextView shopStatusTv, shopNameTv, addressTv, dTimeTv, dFeeTv, offerLabelTv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            offerLayout = itemView.findViewById(R.id.offerLayout);


            shopIv = itemView.findViewById(R.id.shopIv);

            shopStatusTv = itemView.findViewById(R.id.shopStatusTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            dTimeTv = itemView.findViewById(R.id.dTimeTv);
            dFeeTv = itemView.findViewById(R.id.dFeeTv);
            offerLabelTv = itemView.findViewById(R.id.offerLabelTv);

        }

        public void bind(final ProductFood item, OnItemClickListener listener){


            if (item.getIsopen().equalsIgnoreCase("1")){
                shopStatusTv.setText("Open");
            }
            else {
                shopStatusTv.setText("Close");
            }

            shopNameTv.setText(item.getName());
            dTimeTv.setText(item.getDeliveryTime()+" Mins");
            Glide.with(context).load(item.getImage()).placeholder(R.drawable.pneck_logo).into(shopIv);

            if (item.getDiscount().equalsIgnoreCase("0")){
                offerLayout.setVisibility(View.GONE);
            }else {
                offerLayout.setVisibility(View.VISIBLE);
                offerLabelTv.setText("Get "+item.getDiscount() + "% off on order above Rs."+item.getDiscountMin());
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
