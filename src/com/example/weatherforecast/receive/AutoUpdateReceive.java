package com.example.weatherforecast.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.weatherforecast.service.AutoUpdateService;

/**
 * Created by misaki on 2016/7/4.
 */
public class AutoUpdateReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
