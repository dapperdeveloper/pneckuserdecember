package com.callpneck.activity.registrationSecond.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Adapter.MyEmergencyContactAdapter;
import com.callpneck.activity.registrationSecond.Model.addContact.AddEmegencyContact;
import com.callpneck.activity.registrationSecond.Model.addContact.DeleteContact;
import com.callpneck.activity.registrationSecond.Model.showContact.ShowContactList;
import com.callpneck.activity.registrationSecond.Model.showContact.ShowEmegencyContact;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyContactActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 1;

    private RecyclerView contactListRv;
    private LinearLayout posterLayout;
    MyEmergencyContactAdapter adapter;
    List<ShowContactList>  contactList;
    SessionManager sessionManager;
    String user_id;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_emergency_contact);

        contactListRv = findViewById(R.id.contactListRv);
        posterLayout = findViewById(R.id.posterLayout);
        sessionManager = new SessionManager(this);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please Wait....");
        dialog.setCanceledOnTouchOutside(false);
        user_id = sessionManager.getUserid();


        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.addContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickContact();
            }
        });

        contactList = new ArrayList<>();
        contactListRv.setHasFixedSize(true);
        adapter = new MyEmergencyContactAdapter(this,contactList);
        contactListRv.setAdapter(adapter);
        getContactList();

    }

    public void deleteImage(int id)
    {
        getProductDelete(id);
    }

    private void getProductDelete(int id) {
        dialog.setMessage("Deleting...");
        dialog.show();
        Call<DeleteContact> call = APIClient.getInstance().deleteContact(id+"");
        call.enqueue(new Callback<DeleteContact>() {
            @Override
            public void onResponse(Call<DeleteContact> call, Response<DeleteContact> response) {
                DeleteContact model = response.body();
                dialog.dismiss();
                if(model.getSuccess())
                {
                    contactList = adapter.getCurrentList();
                    for(int i=0;i<contactList.size();i++)
                    {
                        if(contactList.get(i).getId()==id)
                        {
                            Toast.makeText(EmergencyContactActivity.this,   model.getMessage(), Toast.LENGTH_SHORT).show();
                            contactList.remove(i);
                            adapter.notifyItemRemoved(i);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                else
                {
                    onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<DeleteContact> call, Throwable t) {

            }
        });
    }

    private void getContactList() {
        dialog.show();
        Call<ShowEmegencyContact> call = APIClient.getInstance().showEmergencyNumber(user_id);
        call.enqueue(new Callback<ShowEmegencyContact>() {
            @Override
            public void onResponse(Call<ShowEmegencyContact> call, Response<ShowEmegencyContact> response) {
                try {
                    ShowEmegencyContact model = response.body();
                    if (model != null && model.getSuccess() && model.getData().size()>0){
                        contactList.addAll(model.getData());
                        adapter.notifyDataSetChanged();
                        posterLayout.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                    else if (model !=null && !model.getSuccess()){
                        posterLayout.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                    else {
                        dialog.dismiss();
                        posterLayout.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e){
                    dialog.dismiss();
                    posterLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ShowEmegencyContact> call, Throwable t) {
                posterLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent,RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode){
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        }else {
            Toast.makeText(this, "No contact selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String nameUser = null;
            Uri uri= data.getData();
            cursor = getContentResolver().query(uri,null,null,null,null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIndex);
            nameUser = cursor.getString(name);

            submitEmergencyContact(phoneNo+"",nameUser+"");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void submitEmergencyContact(String phoneNumber, String name) {
        dialog.setMessage("Contact is adding.....");
        Call<AddEmegencyContact> call = APIClient.getInstance().addEmergencyNumber(user_id,name, phoneNumber);
        call.enqueue(new Callback<AddEmegencyContact>() {
            @Override
            public void onResponse(Call<AddEmegencyContact> call, Response<AddEmegencyContact> response) {
                try {
                    AddEmegencyContact model = response.body();
                    if (model != null && model.getSuccess()){
                        Toast.makeText(EmergencyContactActivity.this, ""+model.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent refresh = new Intent(EmergencyContactActivity.this, EmergencyContactActivity.class);
                        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Set this flag
                        startActivity(refresh);
                        dialog.dismiss();
                    }
                    else if (model != null && !model.getSuccess()){
                        Toast.makeText(EmergencyContactActivity.this, ""+model.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    else {
                        dialog.dismiss();
                        Toast.makeText(EmergencyContactActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<AddEmegencyContact> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(EmergencyContactActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}