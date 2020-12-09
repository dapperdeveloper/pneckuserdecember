package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.search.SearchListModel;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.latout_search_items,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String category=categoryList.get(position).getTitle();
        holder.bind(categoryList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(SearchListModel item);
    }

    Context context;
    List<SearchListModel> categoryList;
    private final OnItemClickListener listener;

    public AdapterSearch(Context context, List<SearchListModel> categoryList, OnItemClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_category_item;
        TextView txt_name_shopping_item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            img_category_item = itemView.findViewById(R.id.img_shopping_item);
            txt_name_shopping_item = itemView.findViewById(R.id.txt_name_shopping_item);
        }

        public void bind(final SearchListModel item, final OnItemClickListener listener) {
            Glide.with(context).load(item.getCategory_images()).placeholder(R.drawable.pneck_logo).into(img_category_item);
            txt_name_shopping_item.setText(item.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
