package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Adapter.PneckUserListAdapter;
import com.callpneck.activity.registrationSecond.Model.responseAddMoney.SendMoneyResponse;
import com.callpneck.activity.registrationSecond.Model.sendMoneyResponse.CheckUserForMoney;
import com.callpneck.activity.registrationSecond.Model.userList.PneckList;
import com.callpneck.activity.registrationSecond.Model.userList.PneckUserList;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferMoneyActivity extends AppCompatActivity {

    Button nextBtn;
    EditText mailEt;

    String userMailOrNumber, user_id, money, receiverId;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    RecyclerView pneckUserRv;
    PneckUserListAdapter adapter;
    List<PneckList> pneckLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_transfer_money);

        mailEt = findViewById(R.id.mailEt);
        nextBtn = findViewById(R.id.nextBtn);
        pneckUserRv = findViewById(R.id.pneckUserRv);

        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        user_id = sessionManager.getUserid();

        pneckLists = new ArrayList<>();

        mailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable != null && !editable.toString().trim().isEmpty()){
                    getResult(editable.toString());
                }
                else {
                    nextBtn.setEnabled(false);
                    mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mailEt.getText().toString().trim().equals(sessionManager.getUserMail()) || mailEt.getText().toString().equals(sessionManager.getUserMobile())){
                    showSnackBar(TransferMoneyActivity.this, "You can not send money to yourself");
                }else {
                    moneyDialog();
                }
            }
        });

//        getPneckUserList();

        getContactList();

        adapter = new PneckUserListAdapter(getApplicationContext(), pneckLists, new PneckUserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PneckList item) {
                String mobile = item.getMobile();
                mailEt.setText(mobile);
            }
        });
        pneckUserRv.setAdapter(adapter);
    }

    private void getPneckUserList() {
        Call<PneckUserList> call = APIClient.getInstance().getPneckUserList();
        call.enqueue(new Callback<PneckUserList>() {
            @Override
            public void onResponse(Call<PneckUserList> call, Response<PneckUserList> response) {
                try {
                    PneckUserList model = response.body();
                    if(model != null && model.getStatus()){
                        pneckLists.clear();
                        pneckLists = model.getPneckList();
                    }
                    else if (model != null && !model.getStatus()){
                        showSnackBar(TransferMoneyActivity.this,model.getMessage());
                    }
                    else {
                        showSnackBar(TransferMoneyActivity.this, "Server Error");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PneckUserList> call, Throwable t) {
                showSnackBar(TransferMoneyActivity.this, t.getMessage());
            }
        });
    }

    private void moneyDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.botttom_dialog_send_money,null);
        bottomSheetDialog.setContentView(view);
        SessionManager sessionManager = new SessionManager(this);

        EditText moneyEt =  view.findViewById(R.id.moneyEt);
        Button  sendMoneyBtn = view.findViewById(R.id.sendMoneyBtn);

        bottomSheetDialog.show();
        sendMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = moneyEt.getText().toString().trim();
                receiverId = sessionManager.getReceiverId();
                user_id = sessionManager.getUserid();
                if (TextUtils.isEmpty(money)){
                    Toast.makeText(TransferMoneyActivity.this, "Enter money", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(receiverId)){
                    Toast.makeText(TransferMoneyActivity.this, "Receiver id required", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMoney(user_id,money,receiverId);
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void sendMoney(String user_id,String money, String receiverId) {
        progressDialog.show();
        Call<SendMoneyResponse> call = APIClient.getInstance().sendMoneyToUser(user_id, user_id, receiverId, money);
        call.enqueue(new Callback<SendMoneyResponse>() {
            @Override
            public void onResponse(Call<SendMoneyResponse> call, Response<SendMoneyResponse> response) {
                try {
                    SendMoneyResponse model = response.body();
                    if (model != null && model.getStatus()){
                       showSnackBar(TransferMoneyActivity.this,model.getMessage());
                        progressDialog.dismiss();
                        onBackPressed();
                    }
                    else if(model != null && !model.getStatus()){
                        showSnackBar(TransferMoneyActivity.this,model.getMessage());
                        progressDialog.dismiss();
                    }
                    else {
                        showSnackBar(TransferMoneyActivity.this,"Server Error");
                        progressDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SendMoneyResponse> call, Throwable t) {
                progressDialog.dismiss();
                showSnackBar(TransferMoneyActivity.this,t.getMessage());
            }
        });
    }

    private void getResult(String input) {
        Call<CheckUserForMoney> call = APIClient.getInstance().checkUserForMoney(input);
        call.enqueue(new Callback<CheckUserForMoney>() {
            @Override
            public void onResponse(Call<CheckUserForMoney> call, Response<CheckUserForMoney> response) {
                CheckUserForMoney model = response.body();
                try {
                    if (model != null && model.getStatus()){
                       mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark, 0);
                        sessionManager.setReceiverId(model.getUserData().getId()+"");
                        nextBtn.setEnabled(true);
                    }
                    else if(model != null && !model.getStatus()){
                        nextBtn.setEnabled(false);
                        mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    else {
                        nextBtn.setEnabled(false);
                        mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        showSnackBar(TransferMoneyActivity.this, "Server Error");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CheckUserForMoney> call, Throwable t) {
                mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                nextBtn.setEnabled(false);
                showSnackBar(TransferMoneyActivity.this, t.getMessage());
            }
        });
    }
    public static void showSnackBar(Activity activity, String snackTitle) {
        View Parentview=activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(Parentview, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    private boolean validation(){
        boolean valid = true;
        userMailOrNumber = mailEt.getText().toString();
        if(TextUtils.isEmpty(userMailOrNumber)){
            Toast.makeText(this, "Enter mail or mobile", Toast.LENGTH_SHORT).show();
            return  false;
        }

        return valid;
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
    String name, phoneNo;
    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                         phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("TAG", "Name: " + name);
                        Log.i("TAG", "Phone Number: " + phoneNo);

                        pneckLists.add(new PneckList(name,phoneNo));


                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }

}