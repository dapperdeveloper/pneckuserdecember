package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.InterFace.IRecyclerItemSelectedListener;
import com.callpneck.activity.registrationSecond.Model.SubCategory;

import java.util.ArrayList;
import java.util.List;

public class MySubCategoryAdapter extends RecyclerView.Adapter<MySubCategoryAdapter.MyViewHolder> {

    Context context;
    List<SubCategory> categoryList;
    List<CardView> cardViewList;

    public MySubCategoryAdapter(Context context, List<SubCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_subcategory_item,parent,false);
        return new MyViewHolder(view);
    }

    boolean flag = false;
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {

                if (flag){
                    holder.addCategory.setImageResource(R.drawable.ic_baseline_check_category);
                    flag = false;
                }else {
                    holder.addCategory.setImageResource(R.drawable.ic_add_categoty_choose);
                    flag = true;
                }

                //Todo code to enable next button in Subcategory
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_shopping_item,addCategory;
        TextView subCategoryTv;
        CardView cardContainer;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;
        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subCategoryTv = itemView.findViewById(R.id.subCategoryTv);
            img_shopping_item = itemView.findViewById(R.id.img_shopping_item);
            addCategory = itemView.findViewById(R.id.addCategory);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            cardContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
