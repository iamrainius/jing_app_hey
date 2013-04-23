package jing.app.hey.ui;


import java.io.File;
import java.io.FileNotFoundException;

import jing.app.bitmap.BitmapLoader;
import jing.app.hey.R;
import jing.app.hey.provider.HeyProvider;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReceivedListFragment extends ListFragment implements OnItemLongClickListener {

    public interface Callback {
        public void onBucketSelected(String mBucketName);
    }
    
    private static final int LOADER_ID = 0;

    private ReceivedAdapter mAdapter;
    private Activity mActivity;
    private BitmapLoader mBitmapLoader;
    private Callback mCallback;
    private ActionMode mActionMode;

    private LoadReceivedTask mLoadReceivedTask;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mAdapter = new ReceivedAdapter(mActivity);
        setListAdapter(mAdapter);

        mBitmapLoader = BitmapLoader.getInstance(mActivity);
    }
    
    
    @Override
	public void onResume() {
    	mLoadReceivedTask = new LoadReceivedTask();
        mLoadReceivedTask.execute();
		super.onResume();
	}
    
    @Override
	public void onPause() {
    	if (mLoadReceivedTask != null && !mLoadReceivedTask.isCancelled()) {
            mLoadReceivedTask.cancel(true);
        }
    	
		super.onPause();
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bucket_list_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        listView.setOnItemLongClickListener(this);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            mActivity = activity;
            //mCallback = (Callback) activity;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BucketListItem item = (BucketListItem) v;
        openBucket(item.mBucketName);
    }

    private void openBucket(String mBucketName) {
        if (mCallback != null) {
            mCallback.onBucketSelected(mBucketName);
        }
    }
    
    private class LoadReceivedTask extends AsyncTask<Void, Void, Void> {
        Cursor mCursor;
        
        @Override
        protected Void doInBackground(Void... params) {
            mCursor = getActivity().getContentResolver().query(HeyProvider.CONTENT_URI, null, null, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mAdapter != null) {
                mAdapter.swapCursor(mCursor);
            }
        }
       
    }

    private class ReceivedAdapter extends CursorAdapter {
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_DATA = 1;
        public static final int COLUMN_TITLE = 2;
        public static final int COLUMN_TIME = 3;
        
        private LayoutInflater mInflater;
        private ActionMode mActionMode;
        
        public ReceivedAdapter(Context context) {
            super(context, null, true);
            mInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mInflater.inflate(R.layout.bucket_list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //BucketListItem item = (BucketListItem) view;
            BucketListItem item = (BucketListItem) view;
            
            long _id = cursor.getLong(COLUMN_ID);
            String title = cursor.getString(COLUMN_TITLE);
            String uriString = cursor.getString(COLUMN_DATA);
            long time = cursor.getLong(COLUMN_TIME);
            
            ImageView coverView = (ImageView) item.findViewById(R.id.cover);
            TextView titleView = (TextView) item.findViewById(R.id.bucket_name);
            TextView timeView = (TextView) item.findViewById(R.id.count);
            
            titleView.setText(title);
            timeView.setText(String.valueOf(time));
            
            item.mBucketName = uriString;
            
            loadCover(coverView, uriString);
        }
        
    }

    private void loadCover(ImageView coverView, String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            return;
        }
        
        if (mBitmapLoader != null) {
            mBitmapLoader.loadBitmapFromUri(uriString, coverView, true);
        }
    }

    String mSelectedItem = null;
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mActionMode != null) {
			return false;
		}
		
		mActionMode = mActivity.startActionMode(mActionModeCallback);
		mActionMode.setTitle(mActivity.getString(R.string.context_menu_title));
		view.setSelected(true);
		BucketListItem item = (BucketListItem) view;
		mSelectedItem = item.mBucketName;
		return true;
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mActivity.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);
			return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_save:
				doSaveItem();
				mode.finish();
				return true;
			case R.id.menu_del:
				doDeleteItem();
				mode.finish();
				return true;
			default:
				return false;
			}
			
		}

		private void doSaveItem() {
			String uriString = new String(mSelectedItem);
			mSelectedItem = null;
			SaveReceivedTask task = new SaveReceivedTask(uriString);
			task.execute();
		}

		private void doDeleteItem() {
			String uriString = new String(mSelectedItem);
			mSelectedItem = null;
			DeleteReceivedTask task = new DeleteReceivedTask(uriString);
			task.execute();
		}
	};
	private class SaveReceivedTask extends AsyncTask<Void, Void, String> {
		String mUriString;
		
		public SaveReceivedTask(String uriString) {
			mUriString = uriString;
		}

		@Override
		protected String doInBackground(Void... params) {
			String url = null;
			try {
				url = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), mUriString, null, "");
				
				MediaScannerConnection.scanFile(mActivity, new String[] { url },
	                    null, null);
			} catch (FileNotFoundException e) {
			}
			
			return url;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Toast.makeText(mActivity, "Saved to " + result, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mActivity, "Failed to save", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
		
	}
	
	private class DeleteReceivedTask extends AsyncTask<Void, Void, Void> {
		
		String mUriString;
		
		public DeleteReceivedTask(String uriString) {
			mUriString = uriString;
		}

		@Override
		protected Void doInBackground(Void... params) {
			File file = new File(mUriString);
			if (file.exists()) {
				file.delete();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mLoadReceivedTask != null && !mLoadReceivedTask.isCancelled()) {
	            mLoadReceivedTask.cancel(true);
	        }
			
			mLoadReceivedTask = new LoadReceivedTask();
			mLoadReceivedTask.execute();
			
			super.onPostExecute(result);
		}
		
	}
    
}
