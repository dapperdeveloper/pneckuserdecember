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
import com.callpneck.activity.registrationSecond.Model.Transaction;

import java.util.List;

public class MyTransactionAdapter extends RecyclerView.Adapter<MyTransactionAdapter.MyViewHolder> {

    Context context;
    List<Transaction> transactionList;

    public MyTransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_transactions_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Transaction transaction = transactionList.get(position);
        holder.moneyTv.setText(transaction.getMoney());
        holder.userNameTv.setText(transaction.getUserName());
        holder.dateTv.setText(transaction.getDate());
        if (transaction.getType().equals("send")){
            holder.recImgView.setImageResource(R.drawable.ic_send_money);
            holder.divider.setBackgroundColor(context.getResources().getColor(R.color.redcolor));
        }
        else if(transaction.getType().equals("receive")){
            holder.recImgView.setImageResource(R.drawable.ic_receive_money);
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
