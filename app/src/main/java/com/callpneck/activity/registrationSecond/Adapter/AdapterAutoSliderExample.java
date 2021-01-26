package com.callpneck.activity.registrationSecond.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Activity.ShopDetailActivity;
import com.callpneck.activity.registrationSecond.Activity.ShopHomeActivity;
import com.callpneck.activity.registrationSecond.Model.bannerData.Datum;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.Cuisines;
import com.muddzdev.styleabletoast.StyleableToast;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class AdapterAutoSliderExample  extends SliderViewAdapter<AdapterAutoSliderExample.SliderAdapterVH> {

    private Activity context;
    private List<Datum> mSliderItems;

    public AdapterAutoSliderExample(Activity context, List<Datum> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }
    public void renewItems(List<Datum> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Datum sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        Datum sliderItem = mSliderItems.get(position);

//        viewHolder.textViewDescription.setText(sliderItem.getDescription());
//        viewHolder.textViewDescription.setTextSize(16);
//        viewHolder.textViewDescription.setTextColor(Color.WHITE);
        String resource = sliderItem.getBanner()+"";
        try{
            Glide.with(viewHolder.itemView)
                    .load(resource)
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        }catch (Exception e){
            e.toString();
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sliderItem.getIsopen().equalsIgnoreCase("1")){
                    Intent intent = new Intent(context, ShopDetailActivity.class);
                    intent.putExtra("shopName",sliderItem.getName());
                    intent.putExtra("shopAvatar",sliderItem.getImage()+"");
                    intent.putExtra("shopAddress",sliderItem.getAddress());
                    intent.putExtra("ratings", sliderItem.getRating());
                    intent.putExtra("res_id", sliderItem.getId()+"");
                    intent.putExtra("dTime", sliderItem.getDeliveryTime());
                    intent.putExtra("discount", sliderItem.getDiscount());
                    intent.putExtra("discountMin", sliderItem.getDiscountMin());
                    intent.putExtra("description", sliderItem.getDescription());
                    intent.putExtra("phoneOne", sliderItem.getPhone());
                    intent.putExtra("phoneTwo", sliderItem.getMobile());
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                }
                else {
                    StyleableToast.makeText(context, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                }
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground =itemView.findViewById(R.id.banner_image);
            this.itemView = itemView;
        }
    }

}
