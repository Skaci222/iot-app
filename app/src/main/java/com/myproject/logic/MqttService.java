package com.myproject.logic;


import static com.myproject.ui.activities.HomeActivity.RELAY_STATUS;
import static com.myproject.ui.activities.HomeActivity.TEMP_VALUE;
import static com.myproject.ui.activities.HomeActivity.TEMP_SET_STATUS;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//import org.eclipse.paho.android.service.MqttAndroidClient;

import com.myproject.room.Device;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MqttService implements Parcelable{

    public static final String TAG = "MqttService";
    //public static final String  HIVE_BROKER = "ssl://e7ea538cb0564a42b068269a96574848.s1.eu.hivemq.cloud:8883";

    public static final String EDMX_BROKER = "ssl://l1716957.ala.us-east-1.emqxsl.com";
    public MqttAndroidClient client;
    private Context context;

    private List<String> keys = new ArrayList<>();


    public interface CallBackListener{
        void messageReceived(String topic, MqttMessage message) throws JSONException;
        void onConnect() throws MqttException, JSONException;

    }

    public CallBackListener listener;

    public MqttService(Context context) throws MqttException {
        this.context = context.getApplicationContext();
        this.client = new MqttAndroidClient(context, EDMX_BROKER, UUID.randomUUID().toString(), Ack.AUTO_ACK);
        connectMqtt();

    }

    protected MqttService(Parcel in) {
    }

    public static final Creator<MqttService> CREATOR = new Creator<MqttService>() {
        @Override
        public MqttService createFromParcel(Parcel in) {
            return new MqttService(in);
        }

        @Override
        public MqttService[] newArray(int size) {
            return new MqttService[size];
        }
    };

    public void setCallBackListener(CallBackListener listener){
        this.listener = listener;
    }

    public MqttConnectOptions getMqttOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
       // options.setPassword("Password1".toCharArray());
        //options.setUserName("samKa");
        options.setPassword("password".toCharArray());
        options.setUserName("Android_2");
        options.setCleanSession(true);
        options.setKeepAliveInterval(60000000);
        return options;
    }

    public void connectMqtt() throws MqttException {
        IMqttToken token = client.connect(getMqttOptions());
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.i(TAG, "onSuccess");
                if(listener != null){
                    try {
                        listener.onConnect();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                client.setCallback(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean reconnect, String serverURI) {
                        Log.i(TAG, "connect complete");
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.i(TAG, "connection lost, reconnecting...");
                        try {
                            connectMqtt();
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String key1 = TEMP_SET_STATUS;
                        String key2 = RELAY_STATUS;
                        String key3 = TEMP_VALUE;

                        if(listener != null) {
                            listener.messageReceived(topic, message);
                        } else {
                            Log.i(TAG, "listener is null");
                        }

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.i(TAG, "did not connect :( " + exception.getMessage());
            }
        });
    }

    public void publishMessage(String topic, String deviceId, String key, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        topic = topic +"/"+deviceId + "/json";
        MqttMessage message = new MqttMessage(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        client.publish(topic, message);
        Log.i(TAG, "published " + message + " to " + topic);
    }

    public void publishToTemp(String topic, String deviceId, String keyRequestAll, String keySetTemp, String keyReset, int valRequestAll, int valSetTemp, int valReset) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(keyRequestAll, valRequestAll);
        jsonObject.put(keySetTemp, valSetTemp);
        jsonObject.put(keyReset, valReset);
        topic = topic +"/"+ deviceId + "/json";
        MqttMessage message = new MqttMessage(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        client.publish(topic, message);
        Log.i(TAG, "published " + message + " to " + topic);

    }


    public void subscribe(String topic, String deviceId){
        topic = topic + "/" + deviceId + "/json" ; //ADD THE "N" BACK TO "JSON"
        client.subscribe(topic, 0);
        Log.i(TAG, "subscribed to " + topic);
    }

    public void unsubscribe(String topic, String deviceId){
        client.unsubscribe(topic);
        Log.i(TAG, "unsubscribed from: " + topic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //parcel.writeParcelable((Parcelable) MqttService.this, 0);
    }

}
