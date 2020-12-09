package com.callpneck.activity.registrationSecond.api;


import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.Model.addressResponse.AddAddressResponse;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductResponse;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ResponseFoodHome;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ResponseOrderSubmit.ResponseOrderSubmit;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.productListResponse.ShopDataList;
import com.callpneck.activity.registrationSecond.Model.getAddress.ResponseAddress;
import com.callpneck.activity.registrationSecond.Model.paymentHistory.PaymentListResponse;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.ModelProvider;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUser;
import com.callpneck.activity.registrationSecond.Model.responseAddMoney.AddMoneyResponse;
import com.callpneck.activity.registrationSecond.Model.responseAddMoney.SendMoneyResponse;
import com.callpneck.activity.registrationSecond.Model.sendMoneyResponse.CheckUserForMoney;
import com.callpneck.activity.registrationSecond.Model.walletOrder.WalletOrder;
import com.squareup.okhttp.ResponseBody;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {


    @GET("getcategory")
    Call<ResponseFoodHome> getCategoryData();


    @FormUrlEncoded
    @POST("getproducts")
    Call<ProductResponse> getProductData(@Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("getopenproductsbycosthightolow")
    Call<ProductResponse> getOpenProductsByCostHighToLow(@Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("getopenproductsbyoffer")
    Call<ProductResponse> getOpenProductsByOffer(@Field("latitude") String latitude, @Field("longitude") String longitude);



    @FormUrlEncoded
    @POST("getopenproductsbycostlowtohigh")
    Call<ProductResponse> getOpenProductsByCostLowToHigh(@Field("latitude") String latitude, @Field("longitude") String longitude);


    @FormUrlEncoded
    @POST("getopenproductsbydeliverytime")
    Call<ProductResponse> getOpenProductsByDeliveryTime(@Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("getopenproductsbyrating")
    Call<ProductResponse> getOpenProductsByrRating(@Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("getopenproductsbyrelevance")
    Call<ProductResponse> getOpenProductsByRelevance(@Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("getopenproductsbycategory")
    Call<ProductResponse> getOpenProductsByCategory(@Field("id") String id);

    @Multipart
    @POST("getclosedproductsbycategory")
    Call<ProductResponse> getClosedProductsByCategory(@Part("id") RequestBody id);

    @FormUrlEncoded
    @POST("getdishlist")
    Call<ShopDataList> getProductList(@Field("res_id") String res_id);

    @FormUrlEncoded
    @POST("order_list_user")
    Call<OrderUser> getUserOrderList(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_user_address")
    Call<ResponseAddress> getHomeAddress(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_user_address_work")
    Call<ResponseAddress> getWorkAddress(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("wallet_list")
    Call<GetWallet> getWallet(@Field("user_id") String user_id);
    @FormUrlEncoded
    @POST("payment_list")
    Call<PaymentListResponse> getPaymentList(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("money_send_check_user")
    Call<CheckUserForMoney> checkUserForMoney(@Field("email_mobile") String email_mobile);

    @FormUrlEncoded
    @POST("money_send")
    Call<SendMoneyResponse> sendMoneyToUser(@Field("user_id") String user_id ,
                                            @Field("sender_id") String sender_id ,
                                            @Field("recevied_id") String recevied_id,
                                            @Field("send_amount") String send_amount);

    @FormUrlEncoded
    @POST("add_money")
    Call<AddMoneyResponse> addMoney(
                                    @Field("user_id") String user_id,
                                    @Field("name") String name ,
                                    @Field("email") String email  ,
                                    @Field("mobile") String mobile,
                                    @Field("amount") String amount,
                                    @Field("payment_id") String payment_id
                                   );
    @FormUrlEncoded
    @POST("user_address")
    Call<AddAddressResponse> addUserHomeAddress(@Field("address") String address,
                                                @Field("lati") String lati,
                                                @Field("longi") String longi,
                                                @Field("status") String status,
                                                @Field("user_id")String user_id );
    @FormUrlEncoded
    @POST("user_address_work")
    Call<AddAddressResponse> addUserWorkAddress(@Field("address") String address,
                                                @Field("lati") String lati,
                                                @Field("longi") String longi,
                                                @Field("status") String status,
                                                @Field("user_id")String user_id );

    @Multipart
    @POST("getproductssearch")
    Call<ProductResponse> restaurantSearchByName(@Part("name") RequestBody name);


    @FormUrlEncoded
    @POST("getorder")
    Call<ResponseOrderSubmit> orderSubmit(@Field("res_id") String res_id,
                                          @Field("user_id") String user_id,
                                          @Field("lati") String lati,
                                          @Field("longi") String longi,
                                          @Field("item_count") String item_count,
                                          @Field("total_amount") String total_amount,
                                          @Field("datas") String datas,
                                          @Field("name") String name,
                                          @Field("mobile") String mobile,
                                          @Field("usr_address") String usr_address,
                                          @Field("email") String email,
                                          @Field("payment_method") String payment_method,
                                          @Field("razorPayId") String razorPayId,
                                          @Field("success") String success
    );

    @FormUrlEncoded
    @POST("getorder_cod")
    Call<ResponseOrderSubmit> orderSubmitCod(@Field("res_id") String res_id,
                                             @Field("user_id") String user_id,
                                             @Field("lati") String lati,
                                             @Field("longi") String longi,
                                             @Field("item_count") String item_count,
                                             @Field("total_amount") String total_amount,
                                             @Field("datas") String datas,
                                             @Field("name") String name,
                                             @Field("mobile") String mobile,
                                             @Field("usr_address") String usr_address,
                                             @Field("email") String email
    );

    @FormUrlEncoded
    @POST("getorder_wallet")
    Call<WalletOrder> orderSubmitWallet(@Field("res_id") String res_id,
                                        @Field("user_id") String user_id,
                                        @Field("lati") String lati,
                                        @Field("longi") String longi,
                                        @Field("item_count") String item_count,
                                        @Field("total_amount") String total_amount,
                                        @Field("datas") String datas,
                                        @Field("name") String name,
                                        @Field("mobile") String mobile,
                                        @Field("usr_address") String usr_address,
                                        @Field("email") String email,
                                        @Field("amount") String amount
    );

    @Multipart
    @POST("userNearByVendorList")
    Call<ModelProvider> vendorList(@Part("user_id") RequestBody user_id,
                                   @Part("ep_token") RequestBody ep_token,
                                   @Part("curr_lat") RequestBody curr_lat,
                                   @Part("curr_long") RequestBody curr_long,
                                   @Part("category") RequestBody category);


}
