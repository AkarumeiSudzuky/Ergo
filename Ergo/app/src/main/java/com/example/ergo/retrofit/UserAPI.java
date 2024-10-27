package com.example.ergo.retrofit;

import com.example.ergo.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserAPI {

    //=================GET====================
    @GET("/user/get-one")
    Call<User> getUserById(Long userId);

    @GET("/user/get-one-username")
    Call<User> getUserByUsername(@Query("username") String username);

    //================POST=====================
    @POST("/user/save")
    Call<User> save(@Body User user);

}
