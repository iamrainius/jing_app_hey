package jing.app.hey.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import jing.app.bitmap.BitmapLoader;
import jing.app.hey.R;
import jing.app.hey.SettingsActivity;

import org.apache.commons.io.IOUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PeerListFragment extends Fragment {
    public static final String TAG = null;

    private String mUriString;
    
    private ImageView mImageView;
    private ActionBar mActionBar;
    
    public PeerListFragment(String urlString) {
        mUriString = urlString;
    }

    @Override
    public void onResume() {
        super.onResume();
        
        if (!TextUtils.isEmpty(mUriString)) {
            int w = mLayout.getWidth();
            int h = mLayout.getHeight();
            BitmapLoader.getInstance(getActivity()).loadBitmapFromUri(mUriString, mImageView, w, h, false);
        }
    }

    RelativeLayout mLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.peer_list_fragment, container, false);
        mLayout = (RelativeLayout) view.findViewById(R.id.peer_list_layout);
        mImageView = (ImageView) view.findViewById(R.id.selected_image);
        
        // Set titile
        File file = new File(mUriString);
        String title = file == null ? "" : file.getName();
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(title);
        actionBar.setSubtitle(null);
        return view;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.peer_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_send:
            sendToPeer();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void sendToPeer() {
        SendToPeerTask task = new SendToPeerTask();
        task.execute();
    }
    
    private class SendToPeerTask extends AsyncTask<Void, Void, Boolean> {

        private static final int NOTIFICATION_ID = 0;
        private NotificationManager mNotifyManager;
        private Builder mBuilder;
        private final Activity mActivity = getActivity();
        
        @Override
        protected void onPreExecute() {
            mNotifyManager =
                    (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(getActivity());
            Uri soundUri = RingtoneManager.getActualDefaultRingtoneUri(mActivity, RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setContentTitle(mActivity.getString(R.string.notification_title))
                .setContentText(mActivity.getString(R.string.send_in_progress))
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_menu_send_normal_holo_light);
            
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final String host = readPrefHost();
            mBuilder.setProgress(0, 0, true);
            mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
            boolean result = false;
            try {
                Socket socket = new Socket(host, 8765);
                Uri uri = Uri.parse("file://" + mUriString);
                InputStream is = getActivity().getContentResolver().openInputStream(uri);
                OutputStream os = socket.getOutputStream();
                IOUtils.copy(is, os);
                os.flush();
                os.close();
                is.close();
                result = true;
            } catch (UnknownHostException e) {
                Log.d(TAG, "Cannot connect to the host.");
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
            return result;
        }

        private String readPrefHost() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
            return sharedPref.getString(SettingsActivity.PREF_KEY, "");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String notifString = null;
            if (result) {
                notifString = mActivity.getString(R.string.send_successfully);
                Toast.makeText(getActivity(), 
                       R.string.send_successfully, Toast.LENGTH_SHORT).show();
            } else {
                notifString = mActivity.getString(R.string.send_failed);
                Toast.makeText(getActivity(), 
                        R.string.send_failed, Toast.LENGTH_SHORT).show();
            }
            
            mBuilder.setContentText(notifString)
            // Removes the progress bar
                    .setProgress(0,0,false);
            mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
            super.onPostExecute(result);
        }
    }
}
