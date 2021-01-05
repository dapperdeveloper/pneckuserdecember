package com.callpneck.taxi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.taxi.TaxiMainActivity;
import com.callpneck.taxi.activity.DriverListActivity;
import com.callpneck.taxi.activity.RealTimeActivity;
import com.callpneck.taxi.model.AgreeDriverData;
import com.callpneck.taxi.model.driver.Datum;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.MyHolder> {
    Context context;
    List<AgreeDriverData> data;
    String bookingId;
    SessionManager sessionManager;

    public DriverAdapter(Context context, List<AgreeDriverData> data) {
        this.context = context;
        this.data = data;
        this.sessionManager = new SessionManager(context);;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_driver_list,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final int rate = new Random().nextInt(6)+5;
        //holder.carType.setText(data.get(position).getBookingId());
        holder.cashOffer.setText("INR "+data.get(position).getEmpCashOffered());
        holder.driverName.setText(data.get(position).getEmployeeName());
        holder.arrivalTime.setText(data.get(position).getEmployeeTimeToReach());
        holder.ratings.setText("Ratings("+rate+")");
        holder.distance.setText(data.get(position).getEmployeeDistanceToReach());
        bookingId=data.get(position).getBookibgId();


        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerSelectedDriver(data.get(position).getEmployeeId().toString(),data.get(position).getBookibgId().toString());
            }
        });


    }

    private void customerSelectedDriver(String emp_id,String bookind_id){

        String ServerURL = context.getResources().getString(R.string.pneck_app_url) + "/changeAvailableEmpStatus";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("booking_id",bookind_id);
        dataParams.put("employee_id",emp_id);
        dataParams.put("status","accept");

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                StatusChangeResponce(bookind_id),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> StatusChangeResponce(String b_id) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Serajc","accept responce recieved");
                    Log.d("Serajc", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        sessionManager.setSesBookingId(b_id);
                        Log.d("Serajcr","status changed");
                        ((DriverListActivity)context).finishActivity();
                        Intent intent=new Intent(context, RealTimeActivity.class);
                        context.startActivity(intent);


                    }else {
                        Log.d("Seraj","change to fail status");
                    }

                } catch (Exception e) {
                    Log.d("Seraj", "error cash inside catch block  " + e.getMessage());
                   // e.printStackTrace();

                }
            }
        };
    }
    private Response.ErrorListener ErrorListener() {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("Seraj", "error occured" + error.getMessage());
            }
        };
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView driverImg;
        TextView carType,driverName,cashOffer,arrivalTime,ratings,distance;
        Button declineBtn,acceptBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            carType=itemView.findViewById(R.id.car_name);
            cashOffer=itemView.findViewById(R.id.cash_offer);
            ratings=itemView.findViewById(R.id.driver_rating);
            driverName=itemView.findViewById(R.id.driver_name);
            arrivalTime=itemView.findViewById(R.id.arrive_time);
            distance=itemView.findViewById(R.id.away_from_distance);
            acceptBtn=itemView.findViewById(R.id.accept_btn);

        }
    }
}
