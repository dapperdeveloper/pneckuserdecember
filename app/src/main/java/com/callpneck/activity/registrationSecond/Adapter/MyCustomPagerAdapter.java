package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.deliveryboy.DeliveryMainActivity;
import com.callpneck.activity.deliveryboy.TrackOrderDeliveryActivity;
import com.callpneck.activity.registrationSecond.Activity.MoreActivity;
import com.callpneck.activity.registrationSecond.Activity.MyWalletActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderDetailActivity;
import com.callpneck.activity.registrationSecond.Activity.ShopHomeActivity;
import com.callpneck.model.dashboard.BannerSliderImage;
import com.callpneck.taxi.TaxiMainActivity;

import java.util.List;

public class MyCustomPagerAdapter extends PagerAdapter {
    Context context;
    List<BannerSliderImage> bannerDatumList;
    LayoutInflater layoutInflater;

    SessionManager sessionManager;
    public MyCustomPagerAdapter(Context context, List<BannerSliderImage> bannerDatumList) {
        this.context = context;
        this.bannerDatumList = bannerDatumList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(context);
    }
    @Override
    public int getCount() {
        return bannerDatumList.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==  object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.layout_banner_look, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image_look_book);
        ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        try {

            RequestOptions requestOptions= new RequestOptions();
            Glide.with(context)
                    .load(bannerDatumList.get(position).getImage())
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
            //Glide.with(context).load(bannerDatumList.get(position).getImage()).placeholder(R.drawable.pneck_banner).into(imageView);
        }catch (Exception e){

        }

        container.addView(itemView);

        BannerSliderImage item = bannerDatumList.get(position);
        if (bannerDatumList.get(position).getImage()!=null)
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (item.getCate_type().equalsIgnoreCase("restaurant")) {
                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null){
                            Intent intent = new Intent(context, ShopHomeActivity.class);
                            intent.putExtra("categoryName", item.getTitle());
                            context.startActivity(intent);
                        }


                    } else if(item.getCate_type().equalsIgnoreCase("cab")){
                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                            Intent intent = new Intent(context, TaxiMainActivity.class);
                            intent.putExtra("categoryName", item.getTitle());
                            context.startActivity(intent);
                        }
                    }
                    else if(item.getCate_type().equalsIgnoreCase("provider")){
                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                            Intent intent = new Intent(context, ProviderDetailActivity.class);
                            intent.putExtra("categoryName", item.getTitle());
                            context.startActivity(intent);
                        }
                    }
                    else if(item.getCate_type().equalsIgnoreCase("shop")){
                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                            Intent intent = new Intent(context, ProviderActivity.class);
                            intent.putExtra("categoryName", item.getTitle());
                            context.startActivity(intent);
                        }
                    }
                    else if(item.getCate_type().equalsIgnoreCase("delivery")){
                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {

                            if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                    sessionManager.getCurrentDeliveryOrderId().length()>0){
                                Intent intent=new Intent(context, TrackOrderDeliveryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(context, DeliveryMainActivity.class);
                                intent.putExtra("categoryName", item.getTitle());
                                context.startActivity(intent);
                            }

                        }
                    }
                    else if(item.getCate_type().equalsIgnoreCase("wallet")){
                        Intent intent = new Intent(context, MyWalletActivity.class);
                        context.startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "Location not set", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
