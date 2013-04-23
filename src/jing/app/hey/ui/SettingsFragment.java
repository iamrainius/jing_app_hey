package jing.app.hey.ui;

import jing.app.hey.R;
import jing.app.hey.SettingsActivity;
import jing.app.hey.utils.NetworkUtils;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

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

}
