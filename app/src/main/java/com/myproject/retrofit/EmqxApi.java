package com.myproject.retrofit;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface EmqxApi {

    @GET("clients")
    Call<ResponseObject> getClients();


}
