package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
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
import com.callpneck.R;
import com.callpneck.model.dashboard.BannerSliderImage;

import java.util.List;

public class MyCustomPagerAdapter extends PagerAdapter {
    Context context;
    List<BannerSliderImage> bannerDatumList;
    LayoutInflater layoutInflater;

    public MyCustomPagerAdapter(Context context, List<BannerSliderImage> bannerDatumList) {
        this.context = context;
        this.bannerDatumList = bannerDatumList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        if (bannerDatumList.get(position).getImage()!=null)
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, ""+bannerDatumList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
