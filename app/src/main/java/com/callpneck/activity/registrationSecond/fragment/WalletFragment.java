package com.callpneck.activity.registrationSecond.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.registrationSecond.Activity.AddMoneyActivity;
import com.callpneck.activity.registrationSecond.Activity.TransactionsActivity;
import com.callpneck.activity.registrationSecond.Activity.TransferMoneyActivity;
import com.callpneck.activity.registrationSecond.Adapter.MyTransactionAdapter;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.Model.paymentHistory.PaymentList;
import com.callpneck.activity.registrationSecond.Model.paymentHistory.PaymentListResponse;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.api.ApiInterface;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletFragment extends Fragment {


    public WalletFragment() {
        // Required empty public constructor
    }



    RecyclerView transactionRv;
    MyTransactionAdapter adapter;
    List<PaymentList> transactionList;
    CardView addMoneyBtn, transferMoneyBtn, transactionBtn;
    TextView walletBlncTv;
    private SessionManager sessionManager;
    String user_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_wallet, container, false);

        transactionRv = view.findViewById(R.id.transactionRv);
        addMoneyBtn = view.findViewById(R.id.addMoneyBtn);
        transferMoneyBtn = view.findViewById(R.id.transferMoneyBtn);
        transactionBtn = view.findViewById(R.id.transactionBtn);
        walletBlncTv = view.findViewById(R.id.walletBlncTv);

        sessionManager=new SessionManager(getActivity());
        user_id = sessionManager.getUserid();
        transactionList = new ArrayList<>();

        if (AppController.isConnected(getActivity()))
        getTransactionHistory(user_id);



        if (AppController.isConnected(getActivity()))
        getWalletBalance();

        clicks();
        return view;

    }

    private void getTransactionHistory(String user_id) {
        ApiInterface apiInterface = ApiClient.getInstance(getContext()).getApi();
        Call<PaymentListResponse> call = apiInterface.getPaymentList(user_id);
        call.enqueue(new Callback<PaymentListResponse>() {
            @Override
            public void onResponse(Call<PaymentListResponse> call, Response<PaymentListResponse> response) {
                try {

                    PaymentListResponse model = response.body();
                    if (model != null && model.getStatus()){
                        transactionList.clear();
                        transactionList = model.getPaymentList();
                        adapter = new MyTransactionAdapter(getContext(),transactionList);
                        transactionRv.setAdapter(adapter);
                    }
                    else if(model != null && !model.getStatus()){
                    }
                    else {

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<PaymentListResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getWalletBalance();
    }

    private void getWalletBalance() {
        ApiInterface apiInterface = ApiClient.getInstance(getContext()).getApi();
        Call<GetWallet> call = apiInterface.getWallet(user_id);
        call.enqueue(new Callback<GetWallet>() {
            @Override
            public void onResponse(Call<GetWallet> call, Response<GetWallet> response) {
                try {
                    GetWallet getWallet = response.body();
                    if (getWallet != null && getWallet.getStatus()){
                        walletBlncTv.setText("₹"+getWallet.getAmount()+"");
                    }
                    else {

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<GetWallet> call, Throwable t) {
                Log.d("WalletBalance", t.getMessage());
            }
        });
    }

    private void clicks() {
        addMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMoneyActivity();
            }
        });
        transferMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTransferMoneyActivity();
            }
        });
        transactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTransactionActivty();
            }
        });
    }

    private void openTransactionActivty() {
        Intent intent = new Intent(getActivity(), TransactionsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openTransferMoneyActivity() {
        Intent intent = new Intent(getActivity(), TransferMoneyActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void openAddMoneyActivity() {

        Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

    }
}