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

public class PeerIPSetupFragment extends Fragment implements OnClickListener {
    
    private EditText mPeerIp;
    private TextView mYourIp;
    private Button mOk;
    private Button mSkip;
    
    private Callback mCallback;
    
    public interface Callback {
        public void onNext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.peer_ip_setup_fragment, container, false);
        
        mPeerIp = (EditText) view.findViewById(R.id.edit_ip);
        
        mYourIp = (TextView) view.findViewById(R.id.show_your_ip);
        StringBuilder sb = new StringBuilder(getActivity().getString(R.string.your_ip));
        sb.append(NetworkUtils.getWiFiIpAddress(getActivity()));
        mYourIp.setText(sb.toString());
        
        mOk = (Button) view.findViewById(R.id.btn_ok);
        mOk.setOnClickListener(this);
        mSkip = (Button) view.findViewById(R.id.btn_skip);
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
        case R.id.btn_ok:
            onDone();
            break;
        case R.id.btn_skip:
            onSkip();
            break;
        }
        
    }

    private void onSkip() {
        if (mCallback != null) {
            mCallback.onNext();
        }
    }

    private void onDone() {
        // Store the IP address to the shared preference
        String ip = mPeerIp.getText().toString();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Editor editor = sharedPref.edit();
        editor.putString("peer-host", ip);
        editor.commit();
        
        if (mCallback != null) {
            mCallback.onNext();
        }
    }

}
