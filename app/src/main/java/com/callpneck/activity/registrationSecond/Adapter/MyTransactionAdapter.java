package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Model.Transaction;
import com.callpneck.activity.registrationSecond.Model.paymentHistory.PaymentList;

import java.util.List;

public class MyTransactionAdapter extends RecyclerView.Adapter<MyTransactionAdapter.MyViewHolder> {

    Context context;
    List<PaymentList> transactionList;

    SessionManager sessionManager;
    public MyTransactionAdapter(Context context, List<PaymentList> transactionList) {
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
        holder.moneyTv.setText(new StringBuilder("₹").append(transaction.getAmount()));


        holder.dateTv.setText(transaction.getDates());
        if (transaction.getStatus().equals("Send")){
            holder.recImgView.setImageResource(R.drawable.ic_send_money);
            holder.divider.setBackgroundColor(context.getResources().getColor(R.color.redcolor));
            holder.userNameTv.setText("Transferred Money to "+transaction.getReceveName()+" By Wallet");
        }
        else if(transaction.getStatus().equals("received")){
            holder.recImgView.setImageResource(R.drawable.ic_receive_money);
            holder.userNameTv.setText("Received Money From "+transaction.getSenderName() +" By Wallet");
            holder.divider.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

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

        }
    }
}
