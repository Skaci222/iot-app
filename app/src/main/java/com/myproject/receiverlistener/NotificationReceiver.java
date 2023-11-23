package com.myproject.receiverlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.myproject.R;
import com.myproject.logic.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.addWoodNotification("Time To Add Wood",
                "add wood to continue being toasty");

        notificationHelper.getManager().notify(6, nb.build());

        Intent broadcastIntent = new Intent("add_wood_notification");
        context.sendBroadcast(broadcastIntent);
    }
}
