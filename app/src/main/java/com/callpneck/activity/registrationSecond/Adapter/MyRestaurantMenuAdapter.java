package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Activity.ShopDetailActivity;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.productListResponse.ProductList;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyRestaurantMenuAdapter extends RecyclerView.Adapter<MyRestaurantMenuAdapter.ViewHolder> {

    Context context;
    List<ProductList> itemList;

    List<MainData> dataList = new ArrayList<>();

    RoomDB database;

    public MyRestaurantMenuAdapter(Context context, List<ProductList> itemList) {
        this.context = context;
        this.itemList = itemList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_retaurant_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProductList item = itemList.get(position);
        holder.productTitleTv.setText(item.getName());
        Glide.with(context).load(item.getImage()).into(holder.productIv);

        //holder.productPriceTv.setText(new StringBuilder("Rs.").append(item.getPrice()));

        holder.descriptionTv.setText(item.getDescription());
        holder.ingredientsTv.setText("Ingredients: "+item.getIngredients());
        //Initialize database
        database = RoomDB.getInstance(context);

        holder.discountPriceTv.setText("Rs."+item.getOffer_price());
        holder.productPriceTv.setText("Rs."+item.getPrice());
        String discountAvailable = item.getDiscount_available();
        if (discountAvailable.equals("true")){
            holder.discountPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.offerLabelTv.setText(item.getDiscount()+"%");
            holder.productPriceTv.setPaintFlags(holder.productPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            holder.discountPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
            holder.offerLabelTv.setText(item.getDiscount()+"%");
            holder.productPriceTv.setPaintFlags(0);
        }

        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuantityDialog(item);
            }
        });


    }

    private double cost =0;
    private double finalCost =0;
    private int quantity = 0;
    private void showQuantityDialog(ProductList modelProduct) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.bs_dialog_quantity,null);
        bottomSheetDialog.setContentView(view);

        ImageView productIv;
        final TextView titleTv, pCategoryTv, descriptionTv, discountNoteTv, originalPriceTv, finalPriceTv
                ,quantityTv;
        ImageButton decrementBtn, incrementBtn;
        Button continueBtn;

        productIv = view.findViewById(R.id.productIv);
        titleTv = view.findViewById(R.id.titleTv);
        pCategoryTv = view.findViewById(R.id.pCategoryTv);
        descriptionTv = view.findViewById(R.id.descriptionTv);
        discountNoteTv = view.findViewById(R.id.discountNoteTv);
        originalPriceTv = view.findViewById(R.id.originalPriceTv);
        finalPriceTv = view.findViewById(R.id.finalPriceTv);
        quantityTv = view.findViewById(R.id.quantityTv);
        decrementBtn = view.findViewById(R.id.decrementBtn);
        incrementBtn = view.findViewById(R.id.incrementBtn);
        continueBtn = view.findViewById(R.id.continueBtn);

        //get data
        final String productId = modelProduct.getId()+"";
        String title = modelProduct.getName();
        String productCategory = modelProduct.getCategory();
        String description = modelProduct.getDescription();
        String discount = modelProduct.getDiscount();
        String image = modelProduct.getImage();


        final String price;
        price = modelProduct.getOffer_price();

        cost = Double.parseDouble(price.replaceAll("Rs.",""));
        finalCost = Double.parseDouble(price.replaceAll("Rs.",""));
        quantity =1;

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_grey).into(productIv);

        }catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_grey);
        }

        titleTv.setText(""+title);
        pCategoryTv.setText(""+productCategory);
        descriptionTv.setText(""+description);
        discountNoteTv.setText(""+discount);

        quantityTv.setText(""+quantity);
        originalPriceTv.setText("Rs."+modelProduct.getOffer_price());
        finalPriceTv.setText("Rs."+finalCost);
        bottomSheetDialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost+ cost;
                quantity++;

                finalPriceTv.setText("Rs."+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(quantity>1){
                    finalCost = finalCost - cost;
                    quantity--;
                    finalPriceTv.setText("Rs."+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("Rs.","");
                String quantity = quantityTv.getText().toString().trim();
                addToCart(title,priceEach, image,totalPrice,quantity);

                bottomSheetDialog.dismiss();

            }
        });

    }

    private void addToCart(String title, String priceEach, String image, String price, String quantity) {
        MainData data = new MainData();
        data.setName(title);
        data.setImage(image);
        data.setCost(price);
        data.setPrice(priceEach);
        data.setQuantity(quantity);
        //Insert text in database
        database.mainDao().insert(data);
        dataList.clear();
        ((ShopDetailActivity)context).cartCount();
        dataList.addAll(database.mainDao().getAll());
        Toast.makeText(context, "Added  Succefully", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        CircleImageView productIv;
        ImageView  addToCartBtn;
        TextView productTitleTv,productPriceTv, descriptionTv, ingredientsTv, offerLabelTv, discountPriceTv;

        ConstraintLayout discountNoteTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productIv = itemView.findViewById(R.id.productIv);
            productTitleTv = itemView.findViewById(R.id.productTitleTv);
            productPriceTv = itemView.findViewById(R.id.productPriceTv);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            ingredientsTv = itemView.findViewById(R.id.ingredientsTv);
            offerLabelTv = itemView.findViewById(R.id.offerLabelTv);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);
            discountPriceTv = itemView.findViewById(R.id.discountPriceTv);
        }


    }
}
