package com.callpneck.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.callpneck.R;

public class VendorFragment extends Fragment {

    View view;

    public VendorFragment() {

    }

    public static VendorFragment newInstance() {

        VendorFragment fragment = new VendorFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.vendor_fragment, container, false);
        return view;
    }
}
