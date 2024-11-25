package com.example.ergo.retrofit;

import com.example.ergo.model.Team;
import com.example.ergo.model.User;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TeamApi {
    //=====================GET=======================
    @GET("/team/get-last")
    Call<Long> getLastTeamId();

    @GET("/team/get-all-forUser")
    Call<List<Team>> getAllTeamsForUser(@Query("userId")Long userId);


    //==================POST=========================
    @POST("/team/save")
    Call<Void> saveTeam(@Body Team team);

    @POST("/team/add-users")
    Call<Void> addUsersToTeam(@Query("teamId") Long teamId, @Body Set<User> users);


}