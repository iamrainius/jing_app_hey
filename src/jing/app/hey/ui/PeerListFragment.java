package jing.app.hey.ui;

import jing.app.bitmap.BitmapLoader;
import jing.app.hey.R;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PeerListFragment extends Fragment {
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
	
}
