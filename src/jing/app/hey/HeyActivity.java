package jing.app.hey;

import jing.app.hey.service.SocketService;
import jing.app.hey.ui.BucketDetailFragment;
import jing.app.hey.ui.BucketListFragment;
import jing.app.hey.ui.ReceivedListFragment;
import jing.app.hey.ui.SettingsFragment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HeyActivity extends Activity 
        implements BucketListFragment.Callback {
    BucketListFragment mBucketListFragment;
    BucketDetailFragment mBucketDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab = actionBar.newTab()
                .setText("My photos")
                .setTabListener(new TabListener<BucketListFragment>(
                        this, "bucket-list", BucketListFragment.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
                .setText("Received photos")
                .setTabListener(new TabListener<ReceivedListFragment>(
                        this, "received-list", ReceivedListFragment.class));
        actionBar.addTab(tab);
        
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
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
        case R.id.menu_settings:
            openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
        
    }

    private void openSettings() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBucketSelected(String bucketName) {
        openBucket(bucketName);
    }

    private void openBucket(String bucketName) {
        Intent intent = new Intent(getApplicationContext(), BucketDetailActivity.class);
        intent.putExtra("bucket-name", bucketName);
        startActivity(intent);
    }

    private class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }
        
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            
        }
        
    }

}
