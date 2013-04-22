package jing.app.hey.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import jing.app.bitmap.BitmapLoader;
import jing.app.hey.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PeerListFragment extends Fragment {
	public static final String TAG = null;

	private String mUriString;
	
	private ImageView mImageView;
	
	public PeerListFragment(String urlString) {
		mUriString = urlString;
	}

	@Override
	public void onStart() {
		super.onStart();
		
		if (!TextUtils.isEmpty(mUriString)) {
			BitmapLoader.getInstance(getActivity()).loadBitmapFromUri(mUriString, mImageView, false);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.peer_list_fragment, container, false);
		mImageView = (ImageView) view.findViewById(R.id.selected_image);
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
	
	private class SendToPeerTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Socket socket = new Socket("192.168.1.11", 8765);
				OutputStream os = socket.getOutputStream();
				os.write(mUriString.getBytes());
				os.flush();
				os.close();
			} catch (UnknownHostException e) {
				Log.d(TAG, "Cannot connect to the host.");
			} catch (IOException e) {
				Log.d(TAG, e.getMessage());
			}
			return null;
		}
		
	}
	
}
