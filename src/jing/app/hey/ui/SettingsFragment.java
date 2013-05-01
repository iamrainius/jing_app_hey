package jing.app.hey.ui;

import jing.app.hey.R;
import jing.app.hey.SettingsActivity;
import jing.app.hey.service.SocketService;
import jing.app.hey.utils.NetworkUtils;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        
        String ip = NetworkUtils.getWiFiIpAddress(getActivity());
        Preference myPref = findPreference(SettingsActivity.PREF_MY_HOST);
        myPref.setSummary(ip);
        // Only directly show IP, no need to change it
        myPref.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if ("start-service".equals(key)) {
            boolean start = sharedPreferences.getBoolean(key, false);
            Intent intent = new Intent(getActivity(), SocketService.class);
            if (start) {
                getActivity().startService(intent);
            } else {
                getActivity().stopService(intent);
            }
        }
    }
    
}
