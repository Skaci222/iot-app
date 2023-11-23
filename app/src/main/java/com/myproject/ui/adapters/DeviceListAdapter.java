package com.myproject.ui.adapters;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.R;
import com.myproject.logic.RenameDialog;
import com.myproject.retrofit.Client;
import com.myproject.room.Device;
import com.myproject.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder> {

    List<Client> clientList = new ArrayList<>();

    String newName;
    private Context context;
    public OnItemClickListener clickListener;



    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public DeviceListAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item, parent, false);
        return new MyViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListAdapter.MyViewHolder holder, int position) {
        Client currentItem = clientList.get(position);
        String deviceName = currentItem.getUsername();
        holder.tvEspName.setText(deviceName);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.showPopupMenu(view);
            }
        });



    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public void setClients(List<Client> clients) {
        clientList = clients;
        notifyDataSetChanged();
    }

    public Client getClientAt(int position){
        return clientList.get(position);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        public TextView tvEspName;
        private ImageButton imageButton;

        public OnPopupMenuItemSelectedListener popupListener;

        public interface OnPopupMenuItemSelectedListener {
            void showRenameDialog();
            void showDeleteDialog();
        }

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvEspName = itemView.findViewById(R.id.tvEspName);
            imageButton = itemView.findViewById(R.id.imgBtnMenu);

          /*  imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(view);
                }
            });*/

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });


        }

        public void showPopupMenu(View v){
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch(item.getItemId()){
                case R.id.actionRenameDevice:
                    popupListener.showRenameDialog();
                    return true;

                case R.id.actionUnProv:
                    popupListener.showDeleteDialog();
                    return true;

                default:
                    return false;
            }

        }
    }


}
