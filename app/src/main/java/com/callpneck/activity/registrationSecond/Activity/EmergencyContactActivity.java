package com.callpneck.activity.registrationSecond.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import com.callpneck.activity.registrationSecond.Adapter.MyEmergencyContactAdapter;
import com.callpneck.activity.registrationSecond.Model.EmergencyContact;

import java.util.List;

public class EmergencyContactActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 1;

    private RecyclerView contactListRv;
    private LinearLayout posterLayout;
    MyEmergencyContactAdapter adapter;
    List<EmergencyContact>  contactList;
    EmergencyContact emergencyContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_emergency_contact);

        contactListRv = findViewById(R.id.contactListRv);
        posterLayout = findViewById(R.id.posterLayout);




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

            Toast.makeText(this, ""+nameUser+" \n"+ phoneNo, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}