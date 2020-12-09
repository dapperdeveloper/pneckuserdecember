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

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.ModelReview;

import java.util.ArrayList;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{

    private Context context;
    private ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
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

        holder.nameTv.setText(reviewArrayList.get(position).getName());

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
