package com.myproject.testing;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Context;

import com.myproject.logic.MqttService;
import com.myproject.ui.activities.HomeActivity;

import java.lang.reflect.Method;

public class TestMqttService {

    private Context context;

    MqttService testMqttService;


   @Before
    public void createMqttClient() throws MqttException {
        testMqttService = new MqttService(context.getApplicationContext());
   }


}
