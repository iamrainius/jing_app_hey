package jing.app.hey.ui;


import jing.app.bitmap.BitmapLoader;
import jing.app.hey.R;
import jing.app.hey.provider.HeyProvider;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReceivedListFragment extends ListFragment {

    public interface Callback {
        public void onBucketSelected(String mBucketName);
    }
    
    private static final int LOADER_ID = 0;

    private ReceivedAdapter mAdapter;
    private Activity mActivity;
    private BitmapLoader mBitmapLoader;
    private Callback mCallback;

    private LoadReceivedTask mLoadReceivedTask;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mAdapter = new ReceivedAdapter(mActivity);
        setListAdapter(mAdapter);
        
        mBitmapLoader = BitmapLoader.getInstance(mActivity);
        mLoadReceivedTask = new LoadReceivedTask();
        mLoadReceivedTask.execute();
    }
    
    @Override
    public void onDestroy() {
        if (mLoadReceivedTask != null && !mLoadReceivedTask.isCancelled()) {
            mLoadReceivedTask.cancel(true);
        }
        
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bucket_list_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            
            item.mBucketName = title;
            
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
