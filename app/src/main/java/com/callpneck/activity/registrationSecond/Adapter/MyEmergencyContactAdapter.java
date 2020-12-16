package com.callpneck.activity.registrationSecond.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((EmergencyContactActivity) context).deleteImage(contactList.get(getAdapterPosition()).getId());

                }
            });
        }
    }
}
