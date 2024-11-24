package com.example.ergo.retrofit;

import com.example.ergo.model.Task;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface TaskAPI {

    //==============GET===========================
    @GET("/task/get-tasks-for-user")
    Call<List<Task>> getTasksForUser(@Query("userId") Long userId);


    @GET("/task/get-tasks-for-team")
    Call<List<Task>> getTasksForTeam(@Query("teamId") int teamId);


    @GET("/task/get-task-by-id") // Update this path as per your API design
    Call<Task> getTaskById(@Query("taskId") int taskId);

    //=============POST==========================
    @POST("/task/save")
    Call<Void>saveTask(@Body Task task);


    //=============PUT============================
    @PUT("/task/update-task-status")
    Call<Void> updateStatus(@Query("taskId") int taskId, @Query("status") int status);


    //===========Delete============================
    @DELETE("/task/delete")
    Call<Void> deleteTask(@Query("taskId") int taskId);
}