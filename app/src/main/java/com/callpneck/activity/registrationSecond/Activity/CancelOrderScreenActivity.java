package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.callpneck.R;

public class CancelOrderScreenActivity extends AppCompatActivity {

    String order_id, res_id;
    String message="";
    RadioButton oneRb, twoRb, threeRb, fourRb, fiveRb;
    Button btnCnl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order_screen);

        if (getIntent()!=null){
            order_id = getIntent().getStringExtra("order_id");
            res_id = getIntent().getStringExtra("res_id");
        }
        oneRb = findViewById(R.id.oneRb);
        twoRb = findViewById(R.id.twoRb);
        threeRb = findViewById(R.id.threeRb);
        fourRb = findViewById(R.id.fourRb);
        fiveRb = findViewById(R.id.fiveRb);
        btnCnl = findViewById(R.id.btnCnl);
        clickListener();
    }

    private void clickListener() {
        oneRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = oneRb.getText().toString();
            }
        });
        twoRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = twoRb.getText().toString();
            }
        });
        threeRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = threeRb.getText().toString();
            }
        });
        fourRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = fourRb.getText().toString();
            }
        });
        fiveRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = fiveRb.getText().toString();
            }
        });
        btnCnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CancelOrderScreenActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}