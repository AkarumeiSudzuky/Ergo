package com.example.ergo.retrofit;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;
    public RetrofitService(){
        initializedRetrofit();
    }

    private void initializedRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://87.246.223.50:8080")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

}