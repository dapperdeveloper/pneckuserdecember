package com.callpneck.activity.SideMenuScreens;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

import com.callpneck.Language.ThemeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.callpneck.R;

public class WalletScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_wallet_screen);
        findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLevelSetUpDialog(WalletScreen.this);
            }
        });
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void showLevelSetUpDialog(Activity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View mView = context.getLayoutInflater().inflate(R.layout.payment_options_dialog, null);

        LinearLayout payUMoney;
        LinearLayout creditDebitCard;
        LinearLayout redeemCode;
        FloatingActionButton cancel;

        payUMoney = (LinearLayout)mView.findViewById( R.id.pay_u_money );
        creditDebitCard = (LinearLayout)mView.findViewById( R.id.credit_debit_card );
        redeemCode = (LinearLayout)mView.findViewById( R.id.redeem_code );
        cancel = (FloatingActionButton)mView.findViewById( R.id.cancel );


        builder.setView(mView);
        final AlertDialog gameOver_dialog = builder.create();
        gameOver_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        creditDebitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameOver_dialog.dismiss();
            }
        });

        payUMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameOver_dialog.dismiss();
            }
        });

        redeemCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameOver_dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameOver_dialog.dismiss();
            }
        });


        gameOver_dialog.show();
    }

}
