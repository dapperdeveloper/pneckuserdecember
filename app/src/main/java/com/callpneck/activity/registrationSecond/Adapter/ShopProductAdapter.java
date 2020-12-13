package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Model.Banner;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ViewHolder> {

    Context context;
    List<Banner> bannerList;

    public ShopProductAdapter(Context context, List<Banner> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_shop_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int resource = bannerList.get(position).getImage();
        String name = bannerList.get(position).getName();
        Picasso.get().load(resource).into(holder.img_icon);
        String inStock = bannerList.get(position).getInStock();
        if (inStock.equals("inStock")){
            holder.lvl_outofstock.setVisibility(View.GONE);
        }
        else {
            holder.lvl_outofstock.setVisibility(View.VISIBLE);
        }

        holder.txtTitle.setText(name);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_icon;
        LinearLayout lvl_cardbg, lvl_offer, lvl_outofstock;
        TextView txtTitle, price, priceoofer, txt_offer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_icon = itemView.findViewById(R.id.img_icon);
            lvl_cardbg = itemView.findViewById(R.id.lvl_cardbg);
            lvl_offer = itemView.findViewById(R.id.lvl_offer);
            lvl_outofstock = itemView.findViewById(R.id.lvl_outofstock);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            price = itemView.findViewById(R.id.price);
            priceoofer = itemView.findViewById(R.id.priceoofer);
            txt_offer = itemView.findViewById(R.id.txt_offer);
        }
    }
}
