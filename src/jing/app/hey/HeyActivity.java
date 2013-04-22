package jing.app.hey;

import jing.app.hey.service.SocketService;
import jing.app.hey.ui.BucketDetailFragment;
import jing.app.hey.ui.BucketListFragment;
import jing.app.hey.ui.PeerListFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class HeyActivity extends Activity 
		implements BucketListFragment.Callback, BucketDetailFragment.Callback {
	BucketListFragment mBucketListFragment;
	BucketDetailFragment mBucketDetailFragment;
	private PeerListFragment mPeerListFragment;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hey_activity);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mBucketListFragment = new BucketListFragment();
		ft.add(R.id.fragment_container, mBucketListFragment);
		ft.commit();
		
		Intent i = new Intent(getApplicationContext(), SocketService.class);
		startService(i);
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hey_activity, menu);
		return true;
	}

	@Override
	public void onBucketSelected(String bucketName) {
		openBucket(bucketName);
	}

	private void openBucket(String bucketName) {
		mBucketDetailFragment = new BucketDetailFragment(bucketName);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_container, mBucketDetailFragment);
		ft.addToBackStack(null);
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
