package com.callpneck.ViewHolders;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.model.UserOrderModel;

public class UserOrderViewHolder extends RecyclerView.ViewHolder {

    private TextView orderId;
    private TextView orderStatus;
    private TextView orderAcceptedOn;
    private TextView orderDeliveredOn;
    private TextView orderSubTotal;
    private TextView orderBookingCharge;
    private TextView orderGrandTotal;
    private TextView bookingCompletedAt;

    public UserOrderViewHolder(View itemView) {
        super(itemView);
        orderId = (TextView)itemView.findViewById( R.id.order_id );
        orderStatus = (TextView)itemView.findViewById( R.id.order_status );
        orderAcceptedOn = (TextView)itemView.findViewById( R.id.order_accepted_on );
        orderDeliveredOn = (TextView)itemView.findViewById( R.id.order_delivered_on );
        orderSubTotal = (TextView)itemView.findViewById( R.id.order_sub_total );
        orderBookingCharge = (TextView)itemView.findViewById( R.id.order_booking_charge );
        orderGrandTotal = (TextView)itemView.findViewById( R.id.order_grand_total );
        bookingCompletedAt = (TextView)itemView.findViewById( R.id.booking_completed_at );
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUpCardView(final UserOrderViewHolder holder, final Activity context,
                              final UserOrderModel data, final int position){

        orderId.setText(data.getOrder_number());

        String currentBookingStatus =data.getOrder_status();
        if (currentBookingStatus.equals("awaited")){
            currentBookingStatus="Order Not Yet Accepted";
        }else if (currentBookingStatus.equals("accepted")){
            currentBookingStatus="Order Accepted";
        }else if (currentBookingStatus.equals("accepted_otp_confirmed")){
            currentBookingStatus="OTP Confirmed";
        }else if (currentBookingStatus.equals("order_info_provided")){
            currentBookingStatus="Order Information Added";
        }else if (currentBookingStatus.equals("delivery_otp_confirmed")){
            currentBookingStatus="Delivery OTP Confirmed";
        }else if (currentBookingStatus.equals("order_request_payment")){
            currentBookingStatus="Order Payment Request";
        }else if (currentBookingStatus.equals("order_completed")){
            currentBookingStatus="Order Completed";
        }else if (currentBookingStatus.equals("rejected")){
            currentBookingStatus="Order Canceled";
        }

        orderStatus.setText(currentBookingStatus);


        if (data.getAccept_otp_confirm_at()!=null&&data.getAccept_otp_confirm_at().length()>1
                &&!data.getAccept_otp_confirm_at().equals("null")&&!data.getAccept_otp_confirm_at().equals("0.00")){
            orderAcceptedOn.setText("Order Accepted On : "+data.getAccept_otp_confirm_at());
        }else {
            orderAcceptedOn.setVisibility(View.GONE);
        }

        if (data.getDelivery_confirm_at()!=null&&data.getDelivery_confirm_at().length()>1
                &&!data.getDelivery_confirm_at().equals("null")&&!data.getDelivery_confirm_at().equals("0.00")){
            orderDeliveredOn.setText("Order Delivered At : "+data.getDelivery_confirm_at());
        }else {
            orderDeliveredOn.setVisibility(View.GONE);
        }

        if (data.getOrder_subtotal()!=null&&data.getOrder_subtotal().length()>0
                &&!data.getOrder_subtotal().equals("null")&&!data.getOrder_subtotal().equals("0.00")){
            orderSubTotal.setText("Order Subtotal : "+data.getOrder_subtotal());
        }else {
            orderSubTotal.setVisibility(View.GONE);
        }

        if (data.getBooking_charge()!=null&&data.getBooking_charge().length()>1
                &&!data.getBooking_charge().equals("null")&&!data.getBooking_charge().equals("0.00")){
            orderBookingCharge.setText("Order Booking Charge : "+data.getBooking_charge());
        }else {
            orderBookingCharge.setVisibility(View.GONE);
        }

        if (data.getGrand_total()!=null&&data.getGrand_total().length()>1
                &&!data.getGrand_total().equals("null")&&!data.getGrand_total().equals("0.00")){
            orderGrandTotal.setText("Order Grand Total : "+data.getGrand_total());
        }else {
            orderGrandTotal.setVisibility(View.GONE);
        }

        if (data.getBooking_complete_at()!=null&&data.getBooking_complete_at().length()>1
                &&!data.getBooking_complete_at().equals("null")&&!data.getBooking_complete_at().equals("0.00")){
            bookingCompletedAt.setText("Order Completed On : "+data.getBooking_complete_at());
        }else {
            bookingCompletedAt.setVisibility(View.GONE);
        }


        //trackOrderBtn.setVisibility(View.VISIBLE);

        /*String currentBookingStatus =data.getOrder_status();
        if (currentBookingStatus.equals("awaited")){
            currentBookingStatus="Order Not Yet Accepted";
            trackOrderBtn.setVisibility(View.VISIBLE);
        }else if (currentBookingStatus.equals("accepted")){
            currentBookingStatus="Order Accepted";
            trackOrderBtn.setVisibility(View.VISIBLE);
        }else if (currentBookingStatus.equals("accepted_otp_confirmed")){
            currentBookingStatus="OTP Confirmed";
            trackOrderBtn.setVisibility(View.VISIBLE);
        }else if (currentBookingStatus.equals("order_info_provided")){
            currentBookingStatus="Order Information Added";
            trackOrderBtn.setVisibility(View.VISIBLE);
        }else if (currentBookingStatus.equals("delivery_otp_confirmed")){
            currentBookingStatus="Delivery OTP Confirmed";
            trackOrderBtn.setVisibility(View.VISIBLE);
        }else if (currentBookingStatus.equals("order_request_payment")){
            currentBookingStatus="Order Payment Request";
            trackOrderBtn.setVisibility(View.VISIBLE);
        }else {
            trackOrderBtn.setVisibility(View.GONE);
        }



        //orderStatus.setText(data.getOrder_status());
        if (data.getOrder_status().equalsIgnoreCase("rejected")){
            trackOrderBtn.setVisibility(View.GONE);
        }else {
            trackOrderBtn.setVisibility(View.VISIBLE);
        }
        trackOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!data.getOrder_status().equalsIgnoreCase("rejected")){
                    Bundle bundle=new Bundle();
                    bundle.putString("order_ses_id",data.getId());
                    LaunchActivityClass.LaunchBookingCompleted(context,bundle);
                }
                else {
                    Toast.makeText(context,"This order has been canceled by you.",Toast.LENGTH_SHORT).show();
                }
            }
        });
*/
    }

}