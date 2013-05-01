package jing.app.hey;

import jing.app.hey.ui.HostServiceSetupFragment;
import jing.app.hey.ui.PeerIPSetupFragment;
import jing.app.hey.ui.WelcomeFragment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class KuaiChuanSetupActivity extends Activity 
        implements PeerIPSetupFragment.Callback, HostServiceSetupFragment.Callback {
    private WelcomeFragment mWelcomeFragment;
    private PeerIPSetupFragment mPeerIPSetupFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kuai_chuan_setup_activity);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        
        mWelcomeFragment = new WelcomeFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.setup_fragment_container, mWelcomeFragment);
        ft.commit();
        
        new LaunchSetupTask().execute();
    }

    private class LaunchSetupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            mPeerIPSetupFragment = new PeerIPSetupFragment();
            ft.replace(R.id.setup_fragment_container, mPeerIPSetupFragment);
            ft.commit();
        }
        
    }

    @Override
    public void onNext() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        HostServiceSetupFragment fragment = new HostServiceSetupFragment();
        ft.replace(R.id.setup_fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void onSetupDone() {
        Intent intent = new Intent(this, Welcome.class);
        startActivity(intent);
        finish();
    }
}
