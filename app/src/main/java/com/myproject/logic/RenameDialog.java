package com.myproject.logic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.myproject.R;
import com.myproject.ui.activities.HomeActivity;

public class RenameDialog extends DialogFragment {

    private EditText etDeviceName;

    public interface InputListener {
        void sendInput(String name);
    }

    private InputListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.delete_device_dialog, null);

        builder.setView(v).setTitle("New Device Name").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener = (InputListener) getActivity();
                String name = etDeviceName.getText().toString();
                Bundle b = new Bundle();
                b.putString("deviceName", name);
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtras(b);
                startActivity(intent);
                listener.sendInput(name);
                dismiss();
            }
        });

        return builder.create();
    }
}
