package jing.app.hey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Welcome extends Activity {
    private static final String PREF_FIRST_RUN = "first-run";
	
    private boolean mIsFirstRun = true;
    SharedPreferences mSharedPref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mIsFirstRun = mSharedPref.getBoolean(PREF_FIRST_RUN, true);
        
        if (isFirstRun()) {
            Intent intent = new Intent(this, KuaiChuanSetupActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, HeyActivity.class);
            startActivity(intent);
            finish();
        }
    }

    boolean isFirstRun() {
        if (!mIsFirstRun) {
            return false;
        }
        
        mIsFirstRun = false;
        Editor editor = mSharedPref.edit();
        editor.putBoolean(PREF_FIRST_RUN, mIsFirstRun);
        editor.commit();
        return true ;
    }

}
