package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.userList.PneckList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PneckUserListAdapter extends RecyclerView.Adapter<PneckUserListAdapter.ViewHolder>{


    public interface  OnItemClickListener{
        void onItemClick(PneckList item);
    }

    Context context;
    List<PneckList> pneckLists;
    private final OnItemClickListener listener;

    public PneckUserListAdapter(Context context, List<PneckList> pneckLists, OnItemClickListener listener) {
        this.context = context;
        this.pneckLists = pneckLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_pneck_user, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(pneckLists.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return pneckLists.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userAvatar;
        TextView userNameTv, phoneTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.circleImageView2);
            userNameTv = itemView.findViewById(R.id.textView10);
            phoneTv = itemView.findViewById(R.id.phoneTv);
        }

        public void bind(PneckList item, OnItemClickListener listener){
           userAvatar.setImageResource(R.drawable.ic_baseline_account_circle_24);
           userNameTv.setText(item.getName()+"");
           phoneTv.setText(item.getMobile());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
