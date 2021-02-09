package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.DemoModel.CatResModel;

import java.util.List;
import java.util.Random;

public class CatRestaurantNearItem  extends RecyclerView.Adapter<CatRestaurantNearItem.ItemRowHolder> {

    private List<CatResModel> dataList;
    private Context mContext;
    private int rowLayout;
    private int selectedPosition=0;

    private CatRestaurantNearItem.OnItemClickListener listener;

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {

        final CatResModel singleItem = dataList.get(position);

        holder.text.setText(singleItem.getNama_kategori());

        if(position==selectedPosition)
        {
            holder.text.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.background.setBackground(mContext.getResources().getDrawable(R.drawable.btn_rect));
        }
        else
        {
            holder.text.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.background.setBackground(mContext.getResources().getDrawable(R.drawable.btn_bordered));
            Random rand = new Random();
            int i = rand.nextInt(4);


            switch (i) {
                case 1:
                    holder.background.getBackground().setColorFilter(mContext.getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                    holder.text.setTextColor(mContext.getResources().getColor(R.color.gray));
                    break;
                default:
                    break;
            }
        }
        holder.bind(singleItem,listener);
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public interface OnItemClickListener {
        void onItemClick(CatResModel item);
    }

    public CatRestaurantNearItem(List<CatResModel> dataList, Context mContext, int rowLayout, OnItemClickListener listener) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.rowLayout = rowLayout;
        this.listener = listener;
    }



    public class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text;
        LinearLayout background;

        public ItemRowHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            background = itemView.findViewById(R.id.background);
        }
        public void bind(final CatResModel item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                    selectedPosition=getAdapterPosition();
                    notifyDataSetChanged();
                }
            });

        }
    }

}
