package com.callpneck.activity.registrationSecond.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Activity.EditCartActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    //Initialize variable
    private List<MainData> dataList;
    private Activity context;
    private RoomDB database;

    public MyCartAdapter(List<MainData> dataList, Activity context) {
        this.dataList = dataList;
        this.context = context;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Initialize main data
        MainData data = dataList.get(position);
        //Initialize database
        database = RoomDB.getInstance(context);

        //set text on textView
        holder.txt_cart_name.setText(data.getName());

        final String cost = data.getCost();
        Picasso.get().load(data.getImage()).into(holder.cart_img);
        holder.txt_cart_quantity.setText(data.getQuantity());
        holder.txt_cart_price.setText(data.getCost());
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initialize main data
                MainData d = dataList.get(holder.getAdapterPosition());
                //Delete text from database
                database.mainDao().delete(d);
                //notify when data is deleted
                int position = holder.getAdapterPosition();
                dataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,dataList.size());
                if (dataList.size() == 0){
                    ((EditCartActivity)context).checkoutBtn.setVisibility(View.GONE);
                }else {
                    ((EditCartActivity)context).checkoutBtn.setVisibility(View.VISIBLE);
                }
                //adjust the subtotal after product remove
                double subTotalWithoutDiscount = ((EditCartActivity)context).allTotalPrice;
                double subTotalAfterProductRemove = subTotalWithoutDiscount - Double.parseDouble(cost.replace("Rs.",""));
                ((EditCartActivity)context).allTotalPrice = subTotalAfterProductRemove;
                ((EditCartActivity)context).txt_total_price.setText("Rs." +((EditCartActivity)context).allTotalPrice);


            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView cart_img, img_descrease,img_increase, btDelete ;
        TextView txt_cart_name, txt_cart_price, txt_cart_quantity;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cart_img = itemView.findViewById(R.id.cart_img);

            img_descrease = itemView.findViewById(R.id.img_descrease);
            img_increase = itemView.findViewById(R.id.img_increase);
            txt_cart_name = itemView.findViewById(R.id.txt_cart_name);
            txt_cart_price = itemView.findViewById(R.id.txt_cart_price);
            btDelete = itemView.findViewById(R.id.bt_delete);
            txt_cart_quantity = itemView.findViewById(R.id.txt_cart_quantity);
        }
    }
}
