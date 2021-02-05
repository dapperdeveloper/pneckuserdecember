package com.callpneck.activity.registrationSecond.api;


import com.callpneck.activity.TrackOrder.Model.TrackOrderModel;
import com.callpneck.activity.deliveryboy.model.DriverList;
import com.callpneck.activity.deliveryboy.model.OrderSubmit;
import com.callpneck.activity.deliveryboy.model.TrackMyOrder;
import com.callpneck.activity.registrationSecond.Model.BookingResponse.BookingResponse;
import com.callpneck.activity.registrationSecond.Model.GalleryResponse.ServiceGalleyResponse;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.Model.ProductModel;
import com.callpneck.activity.registrationSecond.Model.ReviewModel.ShopReviewRes;
import com.callpneck.activity.registrationSecond.Model.VenderDetailModel.VendorDetail;
import com.callpneck.activity.registrationSecond.Model.addContact.AddEmegencyContact;
import com.callpneck.activity.registrationSecond.Model.addContact.DeleteContact;
import com.callpneck.activity.registrationSecond.Model.addressResponse.AddAddressResponse;
import com.callpneck.activity.registrationSecond.Model.bannerData.BannerDataResponse;
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
import com.callpneck.activity.registrationSecond.Model.showContact.ShowEmegencyContact;
import com.callpneck.activity.registrationSecond.Model.userList.PneckUserList;
import com.callpneck.activity.registrationSecond.Model.walletOrder.WalletOrder;
import com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery.CustumerOrderDetail;
import com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery.DeliveryOrder;
import com.callpneck.model.dashboard.MainDashboard;
import com.squareup.okhttp.ResponseBody;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIRequests {


    @GET("getcategory")
    Call<ResponseFoodHome> getCategoryData();


    @GET("vendorHome")
    Call<MainDashboard> getDashData();
    @GET("vendorHomefull")
    Call<MainDashboard> getDashFullData();

    @FormUrlEncoded
    @POST("UserOrderStatusShow")
    Call<TrackMyOrder> userOrderStatusShow(@Field("order_id") String order_id);



    @FormUrlEncoded
    @POST("getproducts")
    Call<ProductResponse> getProductData(@Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("ResBanner")
    Call<BannerDataResponse> getBanner(@Field("latitude") String latitude, @Field("longitude") String longitude);


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
    @POST("order_list_user_delivery")
    Call<DeliveryOrder> getUserOrderDelivery(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("order_list_user_shop")
    Call<OrderUser> getUserOrderShop(@Field("user_id") String user_id);
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
    @POST("getorder_shop")
    Call<ResponseOrderSubmit> orderShopSubmit(@Field("res_id") String res_id,
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
    @POST("getorder_cod_shop")
    Call<ResponseOrderSubmit> orderShopSubmitCod(@Field("res_id") String res_id,
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

    @FormUrlEncoded
    @POST("getorder_wallet_shop")
    Call<WalletOrder> orderShopSubmitWallet(@Field("res_id") String res_id,
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

    @FormUrlEncoded
    @POST("userNearByVendorList")
    Call<ModelProvider> vendorList(@Field("user_id") String user_id,
                                   @Field("ep_token") String ep_token,
                                   @Field("curr_lat") String curr_lat,
                                   @Field("curr_long") String curr_long,
                                   @Field("category") String category);


    @FormUrlEncoded
    @POST("pneck_user_list")
    Call<PneckUserList> getPneckUserList(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("show_services_gallery")
    Call<ServiceGalleyResponse> getGallery(@Field("vendor_id") String vendor_id);

    @FormUrlEncoded
    @POST("productList")
    Call<ProductModel> getProduct(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("emergency_contact")
    Call<AddEmegencyContact> addEmergencyNumber(@Field("user_id") String user_id,
                                        @Field("name")String name,
                                        @Field("number") String number);

    @FormUrlEncoded
    @POST("emergency_contact_list")
    Call<ShowEmegencyContact> showEmergencyNumber(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("emergency_contact_delete")
    Call<DeleteContact> deleteContact(@Field("id") String id);

    @FormUrlEncoded
    @POST("VendorDetail")
    Call<VendorDetail> vendorDetail(@Field("vendor_id") String vendor_id);

    @FormUrlEncoded
    @POST("UserOrderListDetails")
    Call<TrackOrderModel> UserOrderListDetails(@Field("order_id") String vendor_id);


    @FormUrlEncoded
    @POST("CancelOrder")
    Call<DeleteContact> CancelOrder(@Field("order_id") String order_id,
                                    @Field("user_id") String user_id,
                                    @Field("res_id") String res_id,
                                    @Field("message") String message);


    @FormUrlEncoded
    @POST("userMyOrders")
    Call<BookingResponse> getBookingList(@Field("user_id") String vendor_id,
                                         @Field("ep_token") String ep_token);

    @FormUrlEncoded
    @POST("rating")
    Call<DeleteContact> ratingToVendor(@Field("user_id") String user_id,
                                       @Field("order_id") String order_id,
                                       @Field("res_id") String res_id,
                                       @Field("msg") String msg,
                                       @Field("rating") String rating
                                       );



    @FormUrlEncoded
    @POST("driver_list")
    Call<DriverList> getDeliveryBoyData(@Field("latitude") String latitude, @Field("longitude") String longitude);


    @Multipart
    @POST("orderlistuser")
    Call<OrderSubmit> createOrderByUser(@Part("orde_list") RequestBody orde_list,
                                       @Part("start_address") RequestBody start_address,
                                       @Part("drop_address") RequestBody drop_address,
                                       @Part("user_id") RequestBody user_id,
                                       @Part("user_name") RequestBody user_name,
                                       @Part("user_mobile") RequestBody user_mobile,
                                        @Part("emp_id") RequestBody emp_id,
                                        @Part("emp_name") RequestBody emp_name,
                                        @Part("emp_address") RequestBody emp_address,
                                        @Part("delivery_charge") RequestBody empFee,
                                       @Part MultipartBody.Part order_image


    );

    @FormUrlEncoded
    @POST("orderlistuser")
    Call<OrderSubmit> createOrderByUserWithoutImage(@Field("orde_list") String orde_list,
                                        @Field("start_address") String start_address,
                                        @Field("drop_address") String drop_address,
                                        @Field("user_id") String user_id,
                                        @Field("user_name") String user_name,
                                        @Field("user_mobile") String user_mobile,
                                        @Field("emp_id") String emp_id,
                                        @Field("emp_name") String emp_name,
                                        @Field("emp_address") String emp_address,
                                        @Field("delivery_charge") String empFee,
                                        @Field("order_image") String order_image


    );

    @FormUrlEncoded
    @POST("DeliveryBoyOrderList")
    Call<CustumerOrderDetail> deliveryBoyOrderList(@Field("order_id") String order_id
    );

    @FormUrlEncoded
    @POST("ShopRating")
    Call<ShopReviewRes> reviewData(@Field("res_id") String order_id
    );

}
