package com.myproject.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseObject {

    @SerializedName("data")
    private List<Client> data;

    public List<Client> getData() {
        return data;
    }
    public void setData(List<Client> data) {
        this.data = data;
    }
}
