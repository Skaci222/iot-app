package com.myproject.ui.activities;

import static android.graphics.Typeface.BOLD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myproject.R;
import com.myproject.logic.MqttService;
import com.myproject.provisioning.EspMainActivity;
import com.myproject.retrofit.Client;
import com.myproject.retrofit.EmqxApi;
import com.myproject.retrofit.ResponseObject;
import com.myproject.retrofit.RetrofitService;
import com.myproject.room.DeviceViewModel;
import com.myproject.room.Message;
import com.myproject.room.MessageViewModel;
import com.myproject.ui.adapters.DeviceListAdapter;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements DeviceFragment.SetTemperatureValueListener, DeviceListAdapter.MyViewHolder.OnPopupMenuItemSelectedListener {

    public static final String TAG = "SmartFurnace";
    public static final String TEMP_SET_STATUS = "TEMP_SET_STATUS";
    public static final String RELAY_STATUS = "RELAY_STATUS";
    public static final String REQUEST_ALL = "REQUEST_ALL";
    public static final String RESET_DEVICE = "RESET_DEVICE";
    public static final String TEMP_SET_VALUE = "TEMP_SET_VALUE";
    public static final String TEMP_VALUE = "TEMP_VALUE";
    public static final String TEMP_EVT_TOPIC = "iot-2/evt/relay_device";
    public static final String TEMP_CMD_TOPIC = "iot-2/cmd/relay_device";
    private ExtendedFloatingActionButton fabMenu;
    private FloatingActionButton fabTempData, fabProvisionDevice;
    private TextView fabTvTempData, fabTvProvisionDevice, tvTempValue, tvSetTempValue, tvIsBLowerOn;
    private boolean isFabExtended;
    private MqttService mqttService;
    private ScheduledExecutorService executor1;
    private String deviceId, deviceName;

    private Random random;
    private ImageButton btnTempUp, btnTempDown;

    private String degreeString;
    private int degrees;

    private SharedPreferences preferences;

    private MessageViewModel messageViewModel;

    private DeviceViewModel deviceViewModel;

    private String tempSetStatus, tempActualStatus, relayStatus;

    private ArrayList<Integer> tempValues = new ArrayList<>();
    private List<Client> clientList = new ArrayList<>();

    private RetrofitService retrofitService;
    private EmqxApi emqxApi;

    private RecyclerView recyclerView;
    private DeviceListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DeviceListAdapter.OnItemClickListener listener;

    private DeviceFragment deviceFragment;

    private TextView tvTitle, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i(TAG, "onCreate called");

        fabMenu = findViewById(R.id.fabMenu);
        fabTempData = findViewById(R.id.fabTempData);
        fabTvTempData = findViewById(R.id.fabTvTempData);
        fabProvisionDevice = findViewById(R.id.fabProvisionDevice);
        fabTvProvisionDevice = findViewById(R.id.fabTvProvisionDevice);
        tvTempValue = findViewById(R.id.tvTempValue);
        btnTempUp = findViewById(R.id.btnTempUp);
        btnTempDown = findViewById(R.id.btnTempDown);
        tvSetTempValue = findViewById(R.id.tvSetTempValue);
        degreeString = tvSetTempValue.getText().toString();
        tvIsBLowerOn = findViewById(R.id.tvIsBlowerOn);
        recyclerView = findViewById(R.id.rvConnectedDevices);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);

        /**
         * connect to MQTT broker
         */

        try {
            mqttService = new MqttService(this);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        fabTempData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putIntegerArrayList("tempValues", tempValues);
                Intent i = new Intent(HomeActivity.this, GraphActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        fabProvisionDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, EspMainActivity.class);
                Bundle b = new Bundle();
                b.putString("name", "deviceName");
                b.putString("type", "deviceType");
                i.putExtras(b);
                startActivity(i);
            }
        });
       /* btnProv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, EspMainActivity.class);
                Bundle b = new Bundle();
                b.putString("name", "deviceName");
                b.putString("type", "deviceType");
                i.putExtras(b);
                startActivity(i);
            }
        });*/
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabExtended) {
                    tvTempValue.setAlpha(0.1f);
                    tvIsBLowerOn.setAlpha(0.1f);
                    fabTempData.show();
                    fabTvTempData.setVisibility(View.VISIBLE);
                    fabProvisionDevice.show();
                    fabTvProvisionDevice.setVisibility(View.VISIBLE);
                    isFabExtended = true;

                } else {
                    tvTempValue.setAlpha(1f);
                    tvIsBLowerOn.setAlpha(1f);
                    fabTempData.hide();
                    fabTvTempData.setVisibility(View.GONE);
                    isFabExtended = false;
                    fabProvisionDevice.hide();
                    fabTvProvisionDevice.setVisibility(View.GONE);
                }
            }
        });

        /**
         * device Id is supposed to be sent via provisioning dataExchange() in ProvisionActivity, manually inputting for now
         */
       // deviceId = "69813C";
        deviceId = "DDB11C";
        //deviceId = "E089FC";

        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);


       mqttService.setCallBackListener(new MqttService.CallBackListener() {
            @Override
            public void messageReceived(String topic, MqttMessage message) throws JSONException {
                JSONObject object = new JSONObject(new String(message.getPayload()));
                Log.i(TAG, "message from HomeActivity:  " + object +  " from topic: " + topic);

                tempSetStatus = object.getString(TEMP_SET_VALUE);
                tvSetTempValue.setText(tempSetStatus);
                degrees = Integer.parseInt(tempSetStatus);

                tempActualStatus = object.getString(TEMP_VALUE);
                relayStatus = object.getString(RELAY_STATUS);

                deviceFragment = DeviceFragment.newInstance(tempActualStatus, tempSetStatus, relayStatus);
                getSupportFragmentManager().beginTransaction().replace(R.id.deviceFragContainer, deviceFragment)
                                .addToBackStack("device_frag")
                                        .commit();

                Log.i(TAG, "tempSetStatus is: " + tempSetStatus + " tempActualStatus is: "
                        + tempActualStatus + " relaySetStatus is: " + relayStatus);
                tvTempValue.setText(tempActualStatus.substring(0,4));
                if(relayStatus.equals("50")){
                    Log.i(TAG, "blower is off");
                    tvIsBLowerOn.setText("Blower is off");
                } else if(relayStatus.equals("100")){
                    Log.i(TAG, "blower is on");
                    tvIsBLowerOn.setText("Blower is on");
                }
                if(tempSetStatus.equals(tvSetTempValue.getText())){
                    tvSetTempValue.setTypeface(tvSetTempValue.getTypeface(), BOLD);
                    Log.i(TAG, "set temp matches setTempStatus");
                }

                Message message1 = new Message(topic, TEMP_SET_STATUS, tempSetStatus, new Date());
                Message message2 = new Message(topic, TEMP_VALUE, tempActualStatus, new Date());
                Message message3 = new Message(topic, RELAY_STATUS, relayStatus, new Date());

                messageViewModel.insert(message1);
                messageViewModel.insert(message2);
                messageViewModel.insert(message3);

                Log.i(TAG, "messages: " + messageViewModel.getAllMessages());
            }


            @Override
            public void onConnect() throws MqttException, JSONException {

            }
        });

        initRetrofit();

        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            throw new RuntimeException();
        }

        layoutManager = new LinearLayoutManager(this);
        adapter = new DeviceListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setClickListener(listener);

        fetchClients();

        adapter.setClickListener(new DeviceListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int pos) {
                deviceFragment = new DeviceFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.deviceFragContainer, deviceFragment)
                        .addToBackStack("device_frag")
                        .commit();
                recyclerView.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                Runnable tempRequest = () -> {
                    try {
                        mqttService.publishToTemp(TEMP_CMD_TOPIC, deviceId, REQUEST_ALL, TEMP_SET_VALUE, RESET_DEVICE, 1, degrees, 0);
                        Log.i(TAG, "resuming executor 1");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                };
                executor1 = Executors.newScheduledThreadPool(1);
                executor1.scheduleAtFixedRate(tempRequest, 0, 10, TimeUnit.SECONDS);
                mqttService.subscribe(TEMP_EVT_TOPIC, deviceId);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportFragmentManager().popBackStack("device_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        recyclerView.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        if(executor1 != null){
            Log.i(TAG, "shutting down executor 1");
            executor1.shutdown();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called");
        if (executor1 != null) {
            Log.i(TAG, "shutting down executor 1");
            executor1.shutdown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called");
        /**
         * Resume requesting temperature status to display in textView
         */
        if(deviceFragment != null && deviceFragment.isAdded()) {
            Runnable tempRequest = new Runnable() {
                @Override
                public void run() {
                    try {
                        mqttService.publishToTemp(TEMP_CMD_TOPIC, deviceId, REQUEST_ALL, TEMP_SET_STATUS, RELAY_STATUS, 1, degrees, 0);
                        Log.i(TAG, "resuming executor 1");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            executor1 = Executors.newScheduledThreadPool(1);
            executor1.scheduleAtFixedRate(tempRequest, 0, 5, TimeUnit.SECONDS);
        }
    }

    public void initRetrofit(){
        retrofitService = new RetrofitService();
        emqxApi = retrofitService.getRetrofit().create(EmqxApi.class);
    }

    public void fetchClients(){
        emqxApi.getClients().enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                ResponseObject responseObject = response.body();
                List<Client> clients = new ArrayList<>(responseObject.getData());
                for(Client client : clients){
                    Log.i("RETROFITSERVICE", "client username: " + client.getUsername() + ", clientID: "
                            + client.getClientId());
                    adapter.setClients(clients);
                    Log.i("RETROFIT-SERVICE", "size of RecyclerViewList: " + adapter.getItemCount());
                }
            }
            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Log.i("RETROFIT", "onFailure " +t.getMessage());
            }
        });
    }

    @Override
    public void increaseTemp() {
        degrees = degrees + 1;
        Log.i(TAG, "degrees is: " + degrees);

        try {
            mqttService.publishToTemp(TEMP_CMD_TOPIC, deviceId, REQUEST_ALL, TEMP_SET_VALUE, RESET_DEVICE, 1, degrees, 0);
            tvSetTempValue.setText(degreeString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void decreaseTemp() {
        degrees = degrees - 1;
        Log.i(TAG, "degrees is: " + degrees);
        try {
            mqttService.publishToTemp(TEMP_CMD_TOPIC, deviceId, REQUEST_ALL, TEMP_SET_VALUE, RESET_DEVICE, 1, degrees, 0);
            tvSetTempValue.setText(degreeString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void showRenameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.rename_device_dialog, null);
        builder.setView(v).setTitle("Change device name").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }

    @Override
    public void showDeleteDialog() {

    }
}