package com.cocosoft.ecart.network;

/**
 * Created by.dmin on 5/3/2017.
 */

import com.cocosoft.ecart.loginmodule.LoginCredentials;
import com.cocosoft.ecart.loginmodule.User;
import com.cocosoft.ecart.orderHistory.OrderMaster;
import com.cocosoft.ecart.scanlistmodule.Product;
import com.cocosoft.ecart.wishlistmodule.WishList;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIInterface {


    @POST("user/register")
    Call<User> registerUser(@Body User user);

    @POST("secure/user/update")
    Call<User> updateUser(@Body User user);

    @POST("user/login")
    Call<String> loginUser(@Body LoginCredentials loginCredentials);

    @GET("user/getproduct/{product_id}/")
    Call<Product> getProduct(@Path(value = "product_id", encoded = true) int productid);

    @POST("secure/user/addwishlist")
    Call<WishList> addWishList(@Body WishList wishlist, @Header("Authorization") String authHeader);

    @POST("secure/user/allwishlist")
    Call<List<WishList>> allWishList(@Header("Authorization") String authHeader);

    @POST("secure/user/addorder")
    Call<OrderMaster> addOrder(@Body OrderMaster ordermaster, @Header("Authorization") String authHeader);

    @GET("secure/user/allorder")
    Call<List<OrderMaster>> allOrder(@Header("Authorization") String authHeader);
}