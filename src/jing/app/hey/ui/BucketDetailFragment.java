package jing.app.hey.ui;

import jing.app.bitmap.BitmapLoader;
import jing.app.hey.R;
import jing.app.hey.ui.BucketListFragment.Callback;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class BucketDetailFragment extends Fragment implements OnItemClickListener {
	public interface Callback {
		public void onImageSelected(String urlString);
	}
	
	private static final int LOADER_ID = 1;
	
	private GridView mGridView;
	private Activity mActivity;
	private ImageAdapter mAdapter;
	private BitmapLoader mBitmapLoader;

	private String mBucketName;
	private Callback mCallback;
	
	static final String[] IMAGE_PROJECTION = {
		MediaStore.Images.ImageColumns._ID,
		MediaStore.Images.ImageColumns.DATA
	};
	
	public BucketDetailFragment(String bucketName) {
		mBucketName = bucketName;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActivity = getActivity();
		
		mBitmapLoader = BitmapLoader.getInstance(mActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.bucket_detail_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mGridView = (GridView) mActivity.findViewById(R.id.image_grid);
		mAdapter = new ImageAdapter(mActivity);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		
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
			
			String selection = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "='" + mBucketName + '\'';
			CursorLoader loader = new CursorLoader(mActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
					IMAGE_PROJECTION, selection, null, null);
			return loader;
		}
	};

	private class ImageAdapter extends CursorAdapter {
		public static final int COLUMN_ID = 0;
		public static final int COLUMN_DATA = 1;
		
		LayoutInflater mInflater;

		public ImageAdapter(Context context) {
			super(context, null, true);
			mInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(R.layout.image_grid_item, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageGridItem item = (ImageGridItem) view;
			ImageView imageView = (ImageView) item.findViewById(R.id.image_item);
			
			String uriString = cursor.getString(COLUMN_DATA);
			item.mUri = uriString;
			loadThumbnail(imageView, uriString);
		}
		
	}

	public void loadThumbnail(ImageView imageView, String uriString) {
		mBitmapLoader.loadBitmapFromUri(uriString, imageView, true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ImageGridItem item = (ImageGridItem) view;
		if (!TextUtils.isEmpty(item.mUri)) {
			openImage(item.mUri);
		}
	}

	private void openImage(String uriString) {
		if (mCallback != null) {
			mCallback.onImageSelected(uriString);
		}
	}
}
