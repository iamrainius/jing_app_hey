package jing.app.hey.ui;

import jing.app.hey.R;
import jing.app.hey.utils.NetworkUtils;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HostServiceSetupFragment extends Fragment implements OnClickListener {
    
    private Button mStart;
    private Button mSkip;
    
    private Callback mCallback;
    
    public interface Callback {
        public void onSetupDone();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_setup_fragment, container, false);
        mStart = (Button) view.findViewById(R.id.btn_start_service);
        mStart.setOnClickListener(this);
        mSkip = (Button) view.findViewById(R.id.btn_skip_service);
        mSkip.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_start_service:
            onStartService();
            break;
        case R.id.btn_skip_service:
            onSkip();
            break;
        }
        
    }

    private void onSkip() {
        setServicePreference(false);
        if (mCallback != null) {
            mCallback.onSetupDone();
        }
    }

    private void onStartService() {
        setServicePreference(true);
        if (mCallback != null) {
            mCallback.onSetupDone();
        }
    }
    
    private void setServicePreference(boolean start) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Editor editor = sharedPref.edit();
        editor.putBoolean("start-service", start);
        editor.commit();
    }

}
