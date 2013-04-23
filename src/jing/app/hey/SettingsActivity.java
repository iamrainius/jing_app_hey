package jing.app.hey;

import jing.app.hey.ui.SettingsFragment;
import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	public static final String PREF_MY_HOST = "my-host";
    public static final String PREF_KEY = "peer-host";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
