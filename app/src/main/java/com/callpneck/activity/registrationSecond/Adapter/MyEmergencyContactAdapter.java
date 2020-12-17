package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Activity.EmergencyContactActivity;
import com.callpneck.activity.registrationSecond.Model.EmergencyContact;
import com.callpneck.activity.registrationSecond.Model.showContact.ShowContactList;

import java.util.List;

public class MyEmergencyContactAdapter extends RecyclerView.Adapter<MyEmergencyContactAdapter.MyViewHolder> {

    Context context;
    List<ShowContactList> contactList;

    public MyEmergencyContactAdapter(Context context, List<ShowContactList> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ShowContactList emergencyContact = contactList.get(position);

        holder.nameTv.setText(emergencyContact.getName());
        holder.phoneTv.setText(emergencyContact.getNumber());
        holder.call_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCallCustomer(emergencyContact.getNumber()+"");
            }
        });
    }

    private void setCallCustomer(String providerNum){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", providerNum, null));
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public List<ShowContactList> getCurrentList()
    {
        return contactList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv, phoneTv;
        ImageView deleteBtn;
        LinearLayout call_layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            call_layout = itemView.findViewById(R.id.call_layout);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((EmergencyContactActivity) context).deleteImage(contactList.get(getAdapterPosition()).getId());

                }
            });
        }
    }
}
