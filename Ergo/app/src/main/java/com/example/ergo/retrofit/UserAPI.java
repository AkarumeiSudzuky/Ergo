package com.example.ergo.retrofit;

import com.example.ergo.model.User;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @GET("/user/get-friends")
    Call<List<User>> getAllFriends(@Query("userId") Long userId);


    //================POST=====================
    @POST("/user/save")
    Call<User> saveUser(@Body User user);

    @POST("/user/add-friend")
    Call<User> addFriend(@Query("userId") Long userId, @Query("friendId") Long friendId);



    //=================DELETE=====================
    @DELETE("/user/remove-friend")
    Call<Void> removeFriend(@Query("userId") Long userId, @Query("friendId") Long friendId);
}