package jing.app.hey.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import jing.app.hey.utils.Utils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SocketService extends Service implements Runnable{
	private final static String TAG = "SocketService";
	
	private static volatile boolean sStartingUp = false;
    private static volatile boolean sStop = false;
    private static final Object sSyncLock = new Object();

	private static SocketService INSTANCE = null;
	private static Thread sServiceThread = null;
	private static ServerSocket sServerSocket = null;
	
	public final static int PORT = 8765;
	
	@Override
	public void onCreate() {
		Utils.runAsync(new Runnable() {
			
			@Override
			public void run() {
				if (sStartingUp) return;
				
				synchronized (sSyncLock) {
					Log.d(TAG, "SocketService is created");
					
					if (sStop) {
						return;
					}
				}
				
			}
			
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "!!! SocketService: startCommand, startingUp = " + sStartingUp);
		if (!sStartingUp) {
			sStartingUp = true;
			Utils.runAsync(new Runnable() {

				@Override
				public void run() {
					synchronized (sSyncLock) {
						startSocketServiceThread();
						sStartingUp = false;
					}
				}
				
			});
		}
		return Service.START_STICKY;
	}
	
	protected void startSocketServiceThread() {
		if (sServiceThread == null || !sServiceThread.isAlive()) {
			Log.d(TAG, sServiceThread == null ? "Starting thread..." : "Restarting thread...");
			sServiceThread = new Thread(this, "SocketService");
            INSTANCE  = this;
            try {
				sServerSocket = new ServerSocket(PORT);
			} catch (IOException e) {
				Log.e(TAG, "Failed to initiate the server socket: " + e.getMessage());
			}
            sServiceThread.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Utils.runAsync(new Runnable() {
			
			@Override
			public void run() {
				synchronized(sSyncLock) {
		            // Stop the sync manager thread and return
		            if (sServiceThread != null) {
		                sStop = true;
		            }
				}
		        
				if (sServerSocket != null) {
		    		SocketAddress host = sServerSocket.getLocalSocketAddress();
		    		try {
		    			sServerSocket.close();
		    			Log.d(TAG, "Server " + host + ":" + PORT + " is stopped.");
		    		} catch (IOException e) {
		    		}
		    	}
		        
			}
		});
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void run() {
		sStop = false;
		Log.d(TAG, "SocketService is running");
		
		while (!sStop) {
			synchronized (sSyncLock) {
				if (sServerSocket == null) {
					sStop = true;
					continue;
				}
			}
				
			try {
				Socket socket = sServerSocket.accept();
				InputStream is = socket.getInputStream();
				String result = "no result";
				if (is != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					result = reader.readLine();
					reader.close();
					is.close();
				}
				
				Log.d(TAG, "Read: " + result);
				
			} catch (IOException e) {
			}
		}
	}

}
