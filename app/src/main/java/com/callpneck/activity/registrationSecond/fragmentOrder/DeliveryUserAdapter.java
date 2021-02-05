package com.callpneck.activity.registrationSecond.fragmentOrder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery.DeliveryData;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeliveryUserAdapter  extends RecyclerView.Adapter<DeliveryUserAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(DeliveryData item);
    }
    Activity context;
    List<DeliveryData> itemList;
    private final OnItemClickListener listener;

    public DeliveryUserAdapter(Activity context, List<DeliveryData> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_delivery_order_item,parent,false);
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

        TextView deliveryBoyName, orderStatusTv, orderType, orderIdTv,
                totalAmountTv, addressTv,  dateOfOrderTv;
        CircleImageView productIv;
        Button trackOrderBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            trackOrderBtn = itemView.findViewById(R.id.button3);
            productIv = itemView.findViewById(R.id.deliveryBoyAvatar);
            deliveryBoyName = itemView.findViewById(R.id.textView3);
            orderStatusTv = itemView.findViewById(R.id.textView5);
            orderType = itemView.findViewById(R.id.textView6);
            orderIdTv = itemView.findViewById(R.id.textView7);
            totalAmountTv = itemView.findViewById(R.id.textView8);
            addressTv = itemView.findViewById(R.id.addressTv);
            dateOfOrderTv = itemView.findViewById(R.id.textView9);
        }

        public void bind(final DeliveryData item, OnItemClickListener listener){
            orderIdTv.setText("#"+item.getOrderNumber());
            deliveryBoyName.setText(item.getEmpName());
            dateOfOrderTv.setText(item.getDates());
            addressTv.setText(item.getEmpAddress());
            String status = item.getStatus();
            if (status.equalsIgnoreCase("Cancelled")){
                orderStatusTv.setTextColor(context.getResources().getColor(R.color.white));
                orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
            else if (status.equalsIgnoreCase("Delivered")){
                orderStatusTv.setTextColor(context.getResources().getColor(R.color.white));
                orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.blue1));
            }
            else {
                orderStatusTv.setTextColor(context.getResources().getColor(R.color.white));
                orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.light_green));
            }
            orderStatusTv.setText(status);
            double totalAmount = Integer.parseInt(item.getTotalAmount());
            double dFee =  Integer.parseInt(item.getDeliveryCharge());
            double amount = totalAmount+dFee;
            totalAmountTv.setText(new StringBuilder("Rs.").append(amount));
            Glide.with(context).load(item.getEmpImage()+"").placeholder(R.drawable.ic_account_user).into(productIv);
            trackOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });


        }
    }
}
