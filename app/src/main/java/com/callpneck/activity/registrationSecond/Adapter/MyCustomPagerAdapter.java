package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
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
import com.callpneck.activity.registrationSecond.InterFace.Adapter_Click_Listener;
import com.callpneck.model.dashboard.BannerSliderImage;
import com.callpneck.taxi.TaxiMainActivity;

import java.util.List;

public class MyCustomPagerAdapter extends PagerAdapter {
    Context context;
    List<BannerSliderImage> bannerDatumList;
    LayoutInflater layoutInflater;

    SessionManager sessionManager;

    Adapter_Click_Listener adapter_click_listener;

    public MyCustomPagerAdapter(Context context, List<BannerSliderImage> bannerDatumList, Adapter_Click_Listener adapter_click_listener) {
        this.context = context;
        this.bannerDatumList = bannerDatumList;
        this.adapter_click_listener = adapter_click_listener;
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

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = layoutInflater.inflate(R.layout.layout_banner_look, container, false);


        ImageView imageView = (ImageView) itemView.findViewById(R.id.image_look_book);
        ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        final CardView slider_rlt = itemView.findViewById(R.id.slider_rlt);



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

        slider_rlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter_click_listener.onItemClick(v,position,bannerDatumList.get(position));
            }
        });
        container.addView(itemView, 0);

        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
