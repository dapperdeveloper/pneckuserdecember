package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Model.paymentHistory.PaymentList;

import java.util.List;

public class MoneyInAdapter extends RecyclerView.Adapter<MoneyInAdapter.MyViewHolder> {

    Context context;
    List<PaymentList> transactionList;

    SessionManager sessionManager;
    public MoneyInAdapter(Context context, List<PaymentList> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_transactions_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PaymentList transaction = transactionList.get(position);
        if(transaction.getStatus().equals("received")){
            holder.constraintLayout3.setVisibility(View.VISIBLE);
            holder.card_view.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.dateTv.setVisibility(View.VISIBLE);
            holder.moneyTv.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.userNameTv.setVisibility(View.VISIBLE);
            holder.recImgView.setVisibility(View.VISIBLE);
            holder.moneyTv.setText(new StringBuilder("₹").append(transaction.getAmount()));
            holder.dateTv.setText(transaction.getDates());
            holder.recImgView.setImageResource(R.drawable.ic_receive_money);
            holder.userNameTv.setText("Received Money From "+transaction.getSenderName() +" By Wallet");
            holder.divider.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
        }else {
            holder.constraintLayout3.setVisibility(View.GONE);
            holder.card_view.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.GONE);
            holder.dateTv.setVisibility(View.GONE);
            holder.moneyTv.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            holder.userNameTv.setVisibility(View.GONE);
            holder.recImgView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout3;
        LinearLayout linearLayout;
        CardView card_view;
        ImageView recImgView;
        TextView userNameTv,dateTv,moneyTv;
        View divider;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recImgView = itemView.findViewById(R.id.recImgView);
            userNameTv = itemView.findViewById(R.id.userNameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            moneyTv = itemView.findViewById(R.id.moneyTv);
            divider = itemView.findViewById(R.id.divider);
            constraintLayout3 = itemView.findViewById(R.id.constraintLayout3);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            card_view = itemView.findViewById(R.id.card_view);


        }
    }
}
