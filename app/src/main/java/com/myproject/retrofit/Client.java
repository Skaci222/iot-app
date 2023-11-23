package com.myproject.retrofit;

import com.google.gson.annotations.SerializedName;

public class Client {

    @SerializedName("username")
    private String username;

    @SerializedName("clientid")
    private String clientId;

    @SerializedName("topic")
    private String topic;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
