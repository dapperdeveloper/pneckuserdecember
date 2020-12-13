package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.callpneck.R;

import java.util.List;

public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    private final List<String> Imageid;

    public ImageAdapter(Context mContext, List<String> imageid) {
        this.mContext = mContext;
        Imageid = imageid;
    }


    @Override
    public int getCount()
    {
        return Imageid.size();
    }
    @Override
    public Object getItem(int position)
    {
        return position;
    }
    @Override
    public long getItemId(int position)
    {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup
            parent)
    {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null)
        {
            gridView = new View(mContext);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.grid_layout, null);
            // set image based on selected text
            ImageView imageView = (ImageView)
                    gridView.findViewById(R.id.grid_item_image);
            Glide.with(mContext).load(Imageid.get(position)).into(imageView);
        }
        else
        {
            gridView = (View) convertView;
        }
        return gridView;
    }
}
