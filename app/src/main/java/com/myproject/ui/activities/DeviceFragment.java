package com.myproject.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myproject.R;
import com.myproject.logic.MqttService;

public class DeviceFragment extends Fragment {

    private EditText etDeviceName;

    private String deviceName;
    private TextView tvTempVal, isBlowerOn, tvSetTempVal;
    ImageButton buttonTempUp, buttonTempDown;

    private SetTemperatureValueListener setTemperatureValueListener;
    private DeviceNameChangeListener deviceNameChangeListener;

    public interface SetTemperatureValueListener {
        void increaseTemp();
        void decreaseTemp();
    }

    public interface DeviceNameChangeListener {
        void setDeviceName(String name);
    }


    public static DeviceFragment newInstance(String tempActual, String tempSet, String relayStatus){
        Bundle args = new Bundle();
        DeviceFragment deviceFragment = new DeviceFragment();
        //args.putString("name", name);
        args.putString("tempActual", tempActual);
        args.putString("tempSet", tempSet);
        args.putString("relayStatus", relayStatus);
        deviceFragment.setArguments(args);
        return deviceFragment;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.device_fragment, container, false);
        tvTempVal = v.findViewById(R.id.tvTempVal);
        tvSetTempVal = v.findViewById(R.id.tvSetTempVal);
        isBlowerOn = v.findViewById(R.id.isBlowerOn);
        buttonTempUp = v.findViewById(R.id.buttonTempUp);
        buttonTempDown = v.findViewById(R.id.buttonTempDown);
        etDeviceName = v.findViewById(R.id.etDeviceName);
        etDeviceName.setCursorVisible(false);

        if(getArguments() != null){
            tvSetTempVal.setText(getArguments().getString("tempSet"));
            tvTempVal.setText(getArguments().getString("tempActual").substring(0,4));
            if(getArguments().getString("relayStatus").equals("100")){
                isBlowerOn.setText("Blower is ON");
            } else if(getArguments().getString("relayStatus").equals("50")){
                isBlowerOn.setText("Blower is OFF");
            } else{
                isBlowerOn.setText("unknown error");
            }

        }

        buttonTempUp.setOnClickListener(view -> {
            setTemperatureValueListener = (SetTemperatureValueListener) getActivity();
            setTemperatureValueListener.increaseTemp();
        });

        buttonTempDown.setOnClickListener(view -> {
            setTemperatureValueListener = (SetTemperatureValueListener) getActivity();
            setTemperatureValueListener.decreaseTemp();
        });
        deviceNameChangeListener = (DeviceNameChangeListener) getActivity();

        etDeviceName = v.findViewById(R.id.etDeviceName);
        etDeviceName.setOnClickListener(view -> {
            if(view.getId() == etDeviceName.getId()) {
                etDeviceName.setCursorVisible(true);
            }
        });

        etDeviceName.setOnEditorActionListener((textView, i, keyEvent) -> {
            etDeviceName.setFocusableInTouchMode(false);
            etDeviceName.clearFocus();
            etDeviceName.setFocusableInTouchMode(true);
            if(keyEvent != null && i == EditorInfo.IME_ACTION_DONE){
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(etDeviceName.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                deviceName = etDeviceName.getText().toString();
                deviceNameChangeListener.setDeviceName(deviceName);
            }
            return false;
        });


        return v;
    }
}
