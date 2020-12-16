package com.callpneck.activity.registrationSecond.Adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.Category;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.Cuisines;
import com.callpneck.activity.registrationSecond.api.ApiClient;

import java.util.List;

public class MyRestaurantListAdapter extends RecyclerView.Adapter<MyRestaurantListAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Cuisines item);
    }
    Context context;
    List<Cuisines> items;
    private final OnItemClickListener listener;

    public MyRestaurantListAdapter(Context context, List<Cuisines> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.latout_restrurent_items,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    boolean flag= false;
    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_category_item;
        TextView txt_name_shopping_item;
        View view;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category_item = itemView.findViewById(R.id.img_shopping_item);
            txt_name_shopping_item = itemView.findViewById(R.id.txt_name_shopping_item);
            view = itemView.findViewById(R.id.view);
            view.setVisibility(View.GONE);


        }
            public void bind(final Cuisines item, final OnItemClickListener listener) {
                txt_name_shopping_item.setText(item.getName());
                Glide.with(itemView.getContext()).load(item.getImage()).placeholder(R.drawable.pneck_logo).into(img_category_item);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
                final ObjectAnimator animation = ObjectAnimator.ofFloat(img_category_item, "rotationY", 0.0f, 360f);  // HERE 360 IS THE ANGLE OF ROTATE, YOU CAN USE 90, 180 IN PLACE OF IT,  ACCORDING TO YOURS REQUIREMENT
                animation.setDuration(1000); // HERE 500 IS THE DURATION OF THE ANIMATION, YOU CAN INCREASE OR DECREASE ACCORDING TO YOURS REQUIREMENT
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();

            }

    }
}
