package com.callpneck.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.SessionManager;

public class SettingFragment extends Fragment implements View.OnClickListener {

    View view;
    SessionManager sessionManager;
    LinearLayout ll_profile;
    TextView text_user_pnone;

    public SettingFragment() {

    }

    public static SettingFragment newInstance() {
        SettingFragment settingFragment = new SettingFragment();
        return settingFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.setting_fragment, container, false);
        text_user_pnone = (TextView) view.findViewById(R.id.text_user_pnone);
        sessionManager = new SessionManager(getActivity());
        String mobile = sessionManager.getUserMobile();
        text_user_pnone.setText(mobile);
        init();

        ll_profile.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_profile:
                profile();
                break;

        }
    }

    public void init() {

        ll_profile = view.findViewById(R.id.ll_profile);

    }

    public void profile() {
        /*Fragment fragment = new ProfileFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();*/
    }


}
