package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.ModelReview;
import com.callpneck.activity.registrationSecond.Model.ReviewModel.ReviewData;

import java.util.ArrayList;
import java.util.List;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{

    private Context context;
    private List<ReviewData> reviewArrayList;

    public AdapterReview(Context context, List<ReviewData> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review,parent,false);

        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {

        ReviewData item = reviewArrayList.get(position);
        holder.nameTv.setText(reviewArrayList.get(position).getName());
        holder.dateTv.setText(item.getDates()+"");
        holder.ratingBar.setRating(Float.parseFloat(item.getRating()));
        holder.reviewTv.setText(item.getMsg()+"");
        try{
            Glide.with(context).load(item.getProfileImage()).into(holder.profileIv);
        }catch (Exception e){

        }

    }


    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder{

        private ImageView profileIv;
        private TextView nameTv, dateTv, reviewTv;
        private RatingBar ratingBar;
        public HolderReview(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
