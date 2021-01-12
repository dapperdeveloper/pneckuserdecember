package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.BookingResponse.MyOrder;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    Context context;
    List<MyOrder> orderList;

    public BookingAdapter(Context context, List<MyOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.layout_booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyOrder data = orderList.get(position);
        holder.orderIdTv.setText(data.getOrderNumber());
        String status = data.getOrderStatus();
        if (status.equalsIgnoreCase("order_completed")){
            holder.timeLayout.setVisibility(View.VISIBLE);
            holder.viewBtn.setVisibility(View.VISIBLE);
        }else {
            holder.timeLayout.setVisibility(View.GONE);
            holder.viewBtn.setVisibility(View.GONE);
        }
        holder.statusBookingTv.setText(data.getOrderStatus()+"");
        holder.dateTv.setText(data.getBookingCompleteAt()+"");
        holder.grand_totalTv.setText(data.getGrandTotal()+"");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class ViewHolder  extends RecyclerView.ViewHolder{

        TextView orderIdTv, dateTv, statusBookingTv, grand_totalTv;
        LinearLayout orderLayout, timeLayout;
        Button viewBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            statusBookingTv = itemView.findViewById(R.id.statusBookingTv);
            grand_totalTv = itemView.findViewById(R.id.grand_totalTv);
            orderLayout = itemView.findViewById(R.id.orderLayout);
            timeLayout = itemView.findViewById(R.id.timeLayout);
            viewBtn = itemView.findViewById(R.id.viewBtn);
        }
    }
}
