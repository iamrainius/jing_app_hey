package jing.app.hey;

import jing.app.hey.ui.BucketDetailFragment;
import jing.app.hey.ui.PeerListFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class BucketDetailActivity extends Activity implements BucketDetailFragment.Callback {

	BucketDetailFragment mBucketDetailFragment;
	PeerListFragment mPeerListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hey_activity);
		
		Intent intent = getIntent();
		String bucketName = intent.getStringExtra("bucket-name");
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mBucketDetailFragment = new BucketDetailFragment(bucketName);
		ft.add(R.id.fragment_container, mBucketDetailFragment);
		ft.commit();
	}

	@Override
	public void onImageSelected(String urlString) {
		mPeerListFragment = new PeerListFragment(urlString);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_container, mPeerListFragment);
		ft.addToBackStack(null);
		ft.commit();
	}
	
}
