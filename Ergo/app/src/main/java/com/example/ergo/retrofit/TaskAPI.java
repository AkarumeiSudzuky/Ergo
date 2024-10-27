package com.example.ergo.retrofit;

import com.example.ergo.model.Task;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TaskAPI {
    //==============GET===========================

    @GET("/task/get-tasks-for-user")
    Call<List<Task>> getTasksForUser(@Query("userId") Long userId);



    //=============POST==========================
    @POST("/task/save")
    Call<Task>saveTask(@Body Task task);
}
