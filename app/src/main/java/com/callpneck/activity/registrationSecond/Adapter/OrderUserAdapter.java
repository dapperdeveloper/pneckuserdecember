package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductFood;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUserList;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderUserAdapter extends RecyclerView.Adapter<OrderUserAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(OrderUserList item);
    }
    Context context;
    List<OrderUserList> itemList;
    private final OnItemClickListener listener;

    public OrderUserAdapter(Context context, List<OrderUserList> itemList, OnItemClickListener listener) {
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

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView shopNameTv, orderStatusTv, orderType, orderIdTv,
                totalAmountTv, addressTv,  dateOfOrderTv;
        CircleImageView productIv;
        Button vieDetailBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vieDetailBtn = itemView.findViewById(R.id.button2);
            productIv = itemView.findViewById(R.id.productIv);
            shopNameTv = itemView.findViewById(R.id.textView3);
            orderStatusTv = itemView.findViewById(R.id.textView5);
            orderType = itemView.findViewById(R.id.textView6);
            orderIdTv = itemView.findViewById(R.id.textView7);
            totalAmountTv = itemView.findViewById(R.id.textView8);
            addressTv = itemView.findViewById(R.id.addressTv);
            dateOfOrderTv = itemView.findViewById(R.id.textView9);

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
            }
            else {
                orderStatusTv.setTextColor(context.getResources().getColor(R.color.white));
                orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.light_green));
            }
            orderStatusTv.setText(status);
            totalAmountTv.setText(new StringBuilder("Rs.").append(item.getTotalAmount()));
            Glide.with(context).load(item.getResImage()).placeholder(R.drawable.pneck_logo).into(productIv);
            vieDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
