package com.myproject.retrofit;

import android.util.Log;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit retrofit;

    public RetrofitService(){
        initializeRetrofit();

    }

    private void initializeRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("mf5dca7a", "b806a5ee51ffca78"))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://l1716957.ala.us-east-1.emqxsl.com:8443/api/v5/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okHttpClient)
                .build();
        Log.i("RETROFITSERVICE", "initialized retrofit");
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
