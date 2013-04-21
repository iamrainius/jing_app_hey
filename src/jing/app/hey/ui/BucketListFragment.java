package jing.app.hey.ui;


import jing.app.bitmap.BitmapLoader;
import jing.app.hey.R;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BucketListFragment extends ListFragment {
	
	public interface Callback {
		public void onBucketSelected(String mBucketName);
	}
	
	private static final int LOADER_ID = 0;
	private static final String IMAGE_COUNT = "image_count";

	protected static final String[] BUCKET_PROJECTION = {
		MediaStore.Images.ImageColumns._ID,
		MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
		MediaStore.Images.ImageColumns.DATA,
		"count(" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + ") AS " + IMAGE_COUNT
	};

	private BucketAdapter mAdapter;
	private Activity mActivity;
	private BitmapLoader mBitmapLoader;
	private Callback mCallback;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mAdapter = new BucketAdapter(mActivity);
	    setListAdapter(mAdapter);
	    
	    mBitmapLoader = BitmapLoader.getInstance(mActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.bucket_list_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		LoaderManager lm = getLoaderManager();
		lm.initLoader(LOADER_ID, null, LOADER_CALLBACKS);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity != null) {
			mActivity = activity;
			mCallback = (Callback) activity;
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

	private final LoaderCallbacks<Cursor> LOADER_CALLBACKS = new LoaderCallbacks<Cursor>() {
		
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			mAdapter.swapCursor(null);
		}
		
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mAdapter.swapCursor(data);
		}
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String selection = "0 == 0) GROUP BY (" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
			CursorLoader loader = new CursorLoader(mActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BUCKET_PROJECTION, selection, null, null);
			return loader;
		}
	};



	private class BucketAdapter extends CursorAdapter {
		public static final int COLUMN_ID = 0;
		public static final int COLUMN_BUCKET_NAME = 1;
		public static final int COLUMN_DATA = 2;
		public static final int COLUMN_COUNT = 3;
		
		private LayoutInflater mInflater;
		
		public BucketAdapter(Context context) {
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
			String bucketName = cursor.getString(COLUMN_BUCKET_NAME);
			String uriString = cursor.getString(COLUMN_DATA);
			int count = cursor.getInt(COLUMN_COUNT);
			
			ImageView coverView = (ImageView) item.findViewById(R.id.cover);
			TextView nameView = (TextView) item.findViewById(R.id.bucket_name);
			TextView countView = (TextView) item.findViewById(R.id.count);
			
			nameView.setText(bucketName);
			countView.setText(String.valueOf(count));
			
			item.mBucketName = bucketName;
			
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
	
}
