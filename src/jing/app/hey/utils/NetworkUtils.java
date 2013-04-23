package jing.app.hey.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {
	private static String longToIP(long longIp) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.valueOf((longIp & 0x000000FF)))
          .append(".")
          .append(String.valueOf((longIp & 0x0000FFFF) >>> 8))
          .append(".")
          .append(String.valueOf((longIp & 0x00FFFFFF) >>> 16))
          .append(".")
          .append(String.valueOf((longIp >>> 24)));

        return sb.toString();
    }
    
    public static String getWiFiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int address = wifiInfo.getIpAddress();
        //InetAddress inetAddress = new 
        return longToIP(address);
    }
}
