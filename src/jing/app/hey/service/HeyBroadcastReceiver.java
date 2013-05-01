package jing.app.hey.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class HeyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "HeyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean start = sharedPrefs.getBoolean("start-service", false);
        if (start) {
            Intent i = new Intent(context, SocketService.class);
            context.startService(i);
        }
    }

}
