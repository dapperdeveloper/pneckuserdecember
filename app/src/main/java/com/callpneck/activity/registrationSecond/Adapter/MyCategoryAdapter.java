package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Activity.ProviderActivity;
import com.callpneck.activity.registrationSecond.Activity.ShopHomeActivity;
import com.callpneck.model.dashboard.SubcategoryList;
import com.callpneck.taxi.TaxiMainActivity;

import java.util.List;

public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(SubcategoryList item);
    }
    Context context;
    List<SubcategoryList> categoryList;
    private final OnItemClickListener listener;

    public MyCategoryAdapter(Context context, List<SubcategoryList> categoryList, OnItemClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.latout_category_items,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String category=categoryList.get(position).getTitle();

        holder.bind(categoryList.get(position), listener);
    
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (category.equalsIgnoreCase("Restaurant")){
//                    Intent intent = new Intent(context, ShopHomeActivity.class);
//                    intent.putExtra("categoryName", category);
//                    context.startActivity(intent);
//                }
//
//
//                else if (categoryList.get(position).getTitle().equalsIgnoreCase("Cab Driver")){
//                    Intent intent = new Intent(context, TaxiMainActivity.class);
//                    context.startActivity(intent);
//                }
//                else {
//                    Intent intent = new Intent(context, ProviderActivity.class);
//                    context.startActivity(intent);
//                }
//
//            }
//        });


 
    }




    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_category_item;
        TextView txt_name_shopping_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category_item = itemView.findViewById(R.id.img_shopping_item);
            txt_name_shopping_item = itemView.findViewById(R.id.txt_name_shopping_item);
        }

        public void bind(final SubcategoryList item, final OnItemClickListener listener) {
            Glide.with(context).load(item.getCategoryImages()).placeholder(R.drawable.pneck_logo).into(img_category_item);
            txt_name_shopping_item.setText(item.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }


    }
}
