package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
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
        try {
            Glide.with(context).load(bannerDatumList.get(position).getImage()).placeholder(R.drawable.pneck_icon).into(imageView);
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
