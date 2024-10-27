package com.example.ergo.retrofit;

import com.example.ergo.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserAPI {
    @GET("/user/get-one")
    Call<User> getUser(Long userId);

    @POST("/user/save")
    Call<User> save(@Body User user);

}
