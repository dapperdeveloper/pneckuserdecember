package com.callpneck.activity.Vendor.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.callpneck.R;

public class ImageHolder extends Fragment {



    private ProgressBar mProgressBar;
    String imageListPath;
    private ImageView imageHolder;
    @SuppressLint("ValidFragment")
    public ImageHolder(Activity activity, String path){
        imageListPath=path;
    }

    public ImageHolder(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.image_holder_fragment, null);

        mProgressBar=view.findViewById(R.id.progress_bar);
        imageHolder=view.findViewById(R.id.image_load);

        Log.e("kjdhsfkjssf","this is image i got "+imageListPath);
        Glide.with(getActivity())
                .load(imageListPath)
                .placeholder(R.color.grey_40)
                .placeholder(R.color.grey_40)
                .dontAnimate().into(imageHolder);


        return view;
    }


}
