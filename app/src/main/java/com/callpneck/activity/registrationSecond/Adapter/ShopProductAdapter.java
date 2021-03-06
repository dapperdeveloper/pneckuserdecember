package com.callpneck.activity.registrationSecond.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Activity.ServiceDetailActivity;
import com.callpneck.activity.registrationSecond.Activity.TransferMoneyActivity;
import com.callpneck.activity.registrationSecond.Model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.muddzdev.styleabletoast.StyleableToast;


import java.util.ArrayList;
import java.util.List;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ViewHolder> {

    Context context;
    List<Product> bannerList;
    List<MainData> dataList = new ArrayList<>();

    RoomDB database;
    public ShopProductAdapter(Context context, List<Product> bannerList) {
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

        Product item = bannerList.get(position);

        holder.productImagesViewPager.setAdapter(new ProductImagesAdapter(item.getImage()));
        holder.viewPagerIndicator.setupWithViewPager(holder.productImagesViewPager);

        //Glide.with(context).load(bannerList.get(position).getImage().get(0).toString()).placeholder(R.drawable.ic_user_replace).into(holder.img_icon);
        if (item.getStock().equals("1")){
            holder.lvl_outofstock.setVisibility(View.GONE);
        }else {
            holder.lvl_outofstock.setVisibility(View.VISIBLE);
        }
        holder.txtTitle.setText(item.getName());
        holder.priceoofer.setText(new StringBuilder("M.R.P ₹").append(item.getMrp()));
        holder.price.setText(new StringBuilder("Price ₹").append(item.getSelling_price()));
        holder.priceoofer.setPaintFlags(holder.priceoofer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        float v = Float.parseFloat(item.getMrp()); //500
        float n = Float.parseFloat(item.getSelling_price()); //250

        float percent = n * 100f / v;
        float result = 100f - percent;
        //discount
        holder.txt_offer.setText(new StringBuilder(String.valueOf(result).substring(0,2)).append("% Off"));


        database = RoomDB.getInstance(context);
        holder.lvl_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getStock().equals("1")){
                    showQuantityDialog(item, holder.txt_offer.getText().toString());
                }
                else {
                    StyleableToast.makeText(context, "currently out of stock...!", Toast.LENGTH_LONG, R.style.mytoast).show();

                }
            }
        });
        holder.lvl_cardbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getStock().equals("1")){
                    showQuantityDialog(item, holder.txt_offer.getText().toString());
                }
                else {
                    StyleableToast.makeText(context, "currently out of stock...!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }


            }
        });


    }
    private double cost =0;
    private double finalCost =0;
    private int quantity = 0;

    private void showQuantityDialog(Product item, String discount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_shop_item,null);
        ImageView productIv,closeTv ;
        final TextView titleTv, pCategoryTv, descriptionTv, originalPriceTv, finalPriceTv
                ,quantityTv, discountNoteTv, itemPriceQuantity;
        ImageButton decrementBtn, incrementBtn;
        Button continueBtn;

        productIv = view.findViewById(R.id.productIv);
        titleTv = view.findViewById(R.id.titleTv);
        pCategoryTv = view.findViewById(R.id.pCategoryTv);
        descriptionTv = view.findViewById(R.id.descriptionTv);
        closeTv = view.findViewById(R.id.Goback);
        originalPriceTv = view.findViewById(R.id.originalPriceTv);
        finalPriceTv = view.findViewById(R.id.finalPriceTv);
        quantityTv = view.findViewById(R.id.quantityTv);
        decrementBtn = view.findViewById(R.id.decrementBtn);
        incrementBtn = view.findViewById(R.id.incrementBtn);
        continueBtn = view.findViewById(R.id.continueBtn);
        discountNoteTv = view.findViewById(R.id.discountNoteTv);
        itemPriceQuantity = view.findViewById(R.id.itemPriceQuantity);
        //set view
        builder.setView(view);
        AlertDialog dialog = builder.create();

        dialog.show();

        //get data
        final String productId = item.getId()+"";
        String title = item.getName();
        String description = item.getDetails();

        String category = item.getCategoryname()+"";


        final String price;
        price = item.getSelling_price();
        cost = Double.parseDouble(price.replaceAll("Rs.",""));
        finalCost = Double.parseDouble(price.replaceAll("Rs.",""));
        quantity =1;

        Glide.with(context).load(item.getImage().get(0).toString()).placeholder(R.drawable.ic_user_replace).into(productIv);


        discountNoteTv.setText(discount);
        titleTv.setText(""+title);
        descriptionTv.setText(""+description);

        quantityTv.setText(""+quantity);
        originalPriceTv.setText("Rs."+item.getSelling_price());
        finalPriceTv.setText("Rs."+finalCost);
        itemPriceQuantity.setText("x "+quantity+ " items");

        pCategoryTv.setText(category);
        String image;
        try {
            image= item.getImage().get(0).toString();
        }
        catch (Exception e){
            image = "http://pneck.com/front_theme/images/logo-white.png";
        }
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost+ cost;
                quantity++;
                finalPriceTv.setText("Rs."+finalCost);
                quantityTv.setText(""+quantity);
                itemPriceQuantity.setText("x "+quantity+ " items");
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
                    itemPriceQuantity.setText("x "+quantity+ " items");
                }
            }
        });
        String finalImage = image;
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("Rs.","");
                String quantity = quantityTv.getText().toString().trim();
                addToCart(title,priceEach, finalImage,totalPrice,quantity);
                dialog.dismiss();

            }
        });
        closeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
        ((ServiceDetailActivity)context).cartCount();
        dataList.addAll(database.mainDao().getAll());
        Toast.makeText(context, "Added  Succefully", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return bannerList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView img_icon;
        LinearLayout lvl_cardbg, lvl_offer,lvl_click,  lvl_outofstock;
        TextView txtTitle, price, priceoofer, txt_offer;
        //////////product image layout
        ViewPager productImagesViewPager;
        TabLayout viewPagerIndicator;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagesViewPager = itemView.findViewById(R.id.product_images_viewpager);
            viewPagerIndicator = itemView.findViewById(R.id.viewpager_indicator);
            lvl_cardbg = itemView.findViewById(R.id.lvl_cardbg);
            lvl_offer = itemView.findViewById(R.id.lvl_offer);
            lvl_outofstock = itemView.findViewById(R.id.lvl_outofstock);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            price = itemView.findViewById(R.id.price);
            priceoofer = itemView.findViewById(R.id.priceoofer);
            txt_offer = itemView.findViewById(R.id.txt_offer);
            lvl_click = itemView.findViewById(R.id.lvl_click);

        }
    }


}
