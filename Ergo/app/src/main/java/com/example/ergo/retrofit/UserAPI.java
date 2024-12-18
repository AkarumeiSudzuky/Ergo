package com.example.ergo.retrofit;

import com.example.ergo.model.JwtAuthenticationResponse;
import com.example.ergo.model.SignInRequest;
import com.example.ergo.model.SignUpRequest;
import com.example.ergo.model.User;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserAPI {

    //=================GET====================
    @GET("/user/get-one")
    Call<User> getUserById(Long userId);

    @GET("/user/get-one-username")
    Call<User> getUserByUsername(@Query("username") String username);

    @GET("/user/current")
    Call<User> getCurrentUser(@Header("Authorization") String token);

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @GET("/user/get-friends")
    Call<List<User>> getAllFriends(@Query("userId") Long userId);


    //================POST=====================
    @POST("/user/save")
    Call<User> saveUser(@Body User user);

    @POST("/user/signup")
    Call<JwtAuthenticationResponse> signup(@Body SignUpRequest request);

    @POST("/user/signin")
    Call<JwtAuthenticationResponse> signin(@Body SignInRequest request);

    @POST("/user/add-friend")
    Call<Void> addFriend(@Query("userId") Long userId, @Query("friendId") Long friendId);



    //=================DELETE=====================
    @DELETE("/user/remove-friend")
    Call<Void> removeFriend(@Query("userId") Long userId, @Query("friendId") Long friendId);
}