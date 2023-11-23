package com.myproject.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "device_table")
public class Device {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String type;
    private String deviceId;

    public Device(String type, String deviceId){
        this.type = type;
        this.deviceId = deviceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String mac) {
        this.deviceId = mac;
    }
}
