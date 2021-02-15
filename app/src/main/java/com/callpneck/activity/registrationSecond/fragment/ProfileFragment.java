package com.callpneck.activity.registrationSecond.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.callpneck.Const;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.activity.SideMenuScreens.EditProfileScreen;
import com.callpneck.activity.SideMenuScreens.HelpScreen;
import com.callpneck.activity.SideMenuScreens.UserFeedbackScreen;
import com.callpneck.activity.registrationSecond.Activity.AddMoneyActivity;
import com.callpneck.activity.registrationSecond.Activity.EmergencyContactActivity;
import com.callpneck.activity.registrationSecond.Activity.FavouriteProviderActivity;
import com.callpneck.activity.registrationSecond.Activity.HomeMapActivity;
import com.callpneck.activity.registrationSecond.Activity.InviteFriendActivity;
import com.callpneck.Language.LanguageSettingActivity;
import com.callpneck.activity.registrationSecond.Activity.MyBookingActivity;
import com.callpneck.activity.registrationSecond.Activity.MyWalletActivity;
import com.callpneck.activity.registrationSecond.Activity.NotificationsActivity;
import com.callpneck.activity.registrationSecond.Activity.SettingActivity;
import com.callpneck.activity.registrationSecond.Activity.TransferMoneyActivity;
import com.callpneck.activity.registrationSecond.Activity.WorkMapActivity;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.utils.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileFragment extends Fragment {
    ImageButton editProfileBtn;

    RelativeLayout logoutLayout,  notificationsBtn,   emergencyContactBtn,  changeLanguageBtn,
            addHomeBtn, addWorkBtn,
            aboutUsBtn, privacyPolicyBtn, termNConditionBtn, feedbackBtn, contactUsBtn, settingBtn;
    LinearLayout inviteAndEarnBtn, topUpBtn, walletBtn, bookingBtn;
    View view;
    private SessionManager sessionManager;

    String myMobile, myName;
    TextView user_name, user_mobile;
    CircleImageView circleImageView;

    public TextView walletBlncTv;
    public ProfileFragment() {
        // Required empty public constructor
    }
    Activity activity;
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity= getActivity();
        view=inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);

        sessionManager = new SessionManager(getContext());
        myMobile = sessionManager.getUserMobile();
        myName = sessionManager.getUserName();
        if (myName!=null)
        user_name.setText(myName);
        if (user_mobile!=null)
        user_mobile.setText("+91 "+myMobile);

        getWalletBalance();

        try {
            Glide.with(activity).load(sessionManager.getUserImage()).placeholder(R.drawable.ic_profile).into(circleImageView);
        }catch (Exception e){
            e.printStackTrace();
        }

        clickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myName = sessionManager.getUserName();
        user_name.setText(myName);
    }

    private void getWalletBalance() {
        Call<GetWallet> call = APIClient.getInstance().getWallet(sessionManager.getUserid());
        call.enqueue(new Callback<GetWallet>() {
            @Override
            public void onResponse(Call<GetWallet> call, retrofit2.Response<GetWallet> response) {
                GetWallet getWallet = response.body();
                if (getWallet != null && getWallet.getStatus()){
                    walletBlncTv.setText("₹"+getWallet.getAmount()+"");
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<GetWallet> call, Throwable t) {
                Log.d("WalletBalance", t.getMessage());
            }
        });
    }
    private void clickListeners() {
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        });
        addHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomeMapActivity();
            }
        });
        addWorkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWorkMapActivity();
            }
        });


        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFeedbackActivity();
            }
        });
        contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelpActivity();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditProfileActivity();
            }
        });
        termNConditionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("url", Constants.tremAndCondition);
                bundle.putBoolean("is_privacy",false);
                LaunchActivityClass.LaunchWebScreen(getActivity(),bundle);
                getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
            }
        });
        privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("url",Constants.privacyPolicy);
                bundle.putBoolean("is_privacy",true);
                LaunchActivityClass.LaunchWebScreen(getActivity(),bundle);
                getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
            }
        });
        aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivityClass.LaunchAboutUs(getActivity());
                getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
            }
        });




        changeLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLanguageActivity();
            }
        });
        emergencyContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmergencyContactActivity();
            }
        });


        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNotificationActivity();
            }
        });


        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyBookingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
            }
        });
        walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWalletActivity();

            }
        });
        topUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMoneyActivity();
            }
        });
        inviteAndEarnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInviteFriendActivity();
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutToServer();
            }
        });

    }

    private void openLanguageActivity() {
        Intent intent = new Intent(getActivity(), LanguageSettingActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }


    private void openHomeMapActivity() {
        Intent intent = new Intent(getActivity(), HomeMapActivity.class);
        intent.putExtra("home","homeAddress");
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }


    private void openWorkMapActivity() {
        Intent intent = new Intent(getActivity(), WorkMapActivity.class);
        intent.putExtra("work","workAddress");
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openFeedbackActivity() {
        Intent intent = new Intent(getActivity(), UserFeedbackScreen.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openHelpActivity() {
        Intent intent = new Intent(getActivity(), HelpScreen.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(getActivity(), EditProfileScreen.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openWalletActivity() {
        Intent intent = new Intent(getActivity(), MyWalletActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openEmergencyContactActivity() {

        Intent intent = new Intent(getActivity(), EmergencyContactActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openNotificationActivity() {

        Intent intent = new Intent(getActivity(), NotificationsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openInviteFriendActivity() {
        Intent intent = new Intent(getActivity(), InviteFriendActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openAddMoneyActivity() {

        Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

    }

    private void logoutToServer() {

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userLogout";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id", sessionManager.getUserid());
        dataParams.put("ep_token", sessionManager.getUserToken());

        Log.e("logout_user", " that's we are sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                userLogoutSuccess(),
                userLogoutError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(dataParamsJsonReq);

    }

    private Response.Listener<JSONObject> userLogoutSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.v("logout_user", "this is complete response " + response);
                    if(sessionManager.getLoginType().equals("2"))
                    {
                        //  LoginManager.getInstance().logOut();

                    }

                    sessionManager.clearSession();
                    LaunchActivityClass.LaunchLoginScreen(getActivity());

                } catch (Exception e) {
                    Log.e("logout_user", "this is error exception " + e.getMessage());
                }
            }
        };
    }

    private Response.ErrorListener userLogoutError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), R.string.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                Log.v("logout_user", "inside error block  " + error.getMessage());
            }
        };
    }

    private void initView(View view) {
        walletBlncTv = view.findViewById(R.id.walletBlncTv);
        user_name = (TextView) view.findViewById(R.id.textView2);
        user_mobile = (TextView) view.findViewById(R.id.mobile_no);
        circleImageView = view.findViewById(R.id.circleImageView);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        logoutLayout=view.findViewById(R.id.logout_rel_layout);
        inviteAndEarnBtn = view.findViewById(R.id.inviteAndEarnBtn);
        topUpBtn = view.findViewById(R.id.topUpBtn);
        walletBtn = view.findViewById(R.id.walletBtn);
        bookingBtn = view.findViewById(R.id.bookingBtn);
        notificationsBtn = view.findViewById(R.id.notificationsBtn);
        emergencyContactBtn = view.findViewById(R.id.emergencyContactBtn);
       // changeCurrencyBtn = view.findViewById(R.id.changeCurrencyBtn);
        changeLanguageBtn = view.findViewById(R.id.changeLanguageBtn);
        settingBtn = view.findViewById(R.id.settingBtn);


        aboutUsBtn = view.findViewById(R.id.aboutUsBtn);
        privacyPolicyBtn = view.findViewById(R.id.privacyPolicyBtn);
        termNConditionBtn = view.findViewById(R.id.termNConditionBtn);
        contactUsBtn = view.findViewById(R.id.contactUsBtn);
        feedbackBtn = view.findViewById(R.id.feedbackBtn);

        addHomeBtn = view.findViewById(R.id.addHomeBtn);
        addWorkBtn = view.findViewById(R.id.addWorkBtn);
    }
}