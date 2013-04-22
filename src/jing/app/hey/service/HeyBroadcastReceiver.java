package jing.app.hey.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HeyBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "HeyBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "The system is booted");
		Intent i = new Intent(context, SocketService.class);
		context.startService(i);
	}

}
