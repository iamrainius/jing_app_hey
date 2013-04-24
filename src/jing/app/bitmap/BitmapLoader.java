package jing.app.bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

public class BitmapLoader {
    private static final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final int MEMORY_CACHE_SIZE = MAX_MEMORY / 8;
	public static final String TAG = "BitmapLoader";
    
    private LruCache<String, Bitmap> mMemoryCache;
    private Context mContext;
    
    private static BitmapLoader sInstance;
    
    private BitmapLoader(Context context) {
        mContext = context;
        mMemoryCache = new LruCache<String, Bitmap>(MEMORY_CACHE_SIZE) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

        };
    }

    public static BitmapLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BitmapLoader(context);
        }
        
        return sInstance;
    }
    
    public void add(String key, Bitmap bitmap) {
        if (get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    
    public Bitmap get(String key) {
        return null;//mMemoryCache.get(key);
    }
    
    public void loadBitmapFromUri(String uriString, ImageView view, int width, int height, boolean square) {
        final String key = uriString;
        
        final Bitmap bitmap = get(key);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(uriString, view)) {
                final LoadBitmapTask task = new LoadBitmapTask(mContext, view, width, height, square);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), null, task);
                view.setImageDrawable(asyncDrawable);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uriString);
            }
        }
        
    }
    
    private boolean cancelPotentialWork(String uriString, ImageView imageView) {
        final LoadBitmapTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.mUri;
            if (bitmapData != null && !bitmapData.equalsIgnoreCase(uriString)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    
    private static LoadBitmapTask getBitmapWorkerTask(ImageView imageView) {
           if (imageView != null) {
               final Drawable drawable = imageView.getDrawable();
               if (drawable instanceof AsyncDrawable) {
                   final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                   return asyncDrawable.getBitmapWorkerTask();
               }
            }
            return null;
        }
    
    class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<LoadBitmapTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                LoadBitmapTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<LoadBitmapTask>(bitmapWorkerTask);
        }

        public LoadBitmapTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    
    private class LoadBitmapTask extends AsyncTask<String, Void, Void> {
        private ImageView mImageView;
        private final WeakReference<ImageView> mImageViewReference;
        private int mWidth;
        private int mHeight;
        private Bitmap mBitmap;
        String mUri;
        private boolean mSquare;
        
        public LoadBitmapTask(Context context, ImageView view,int width,int height, boolean square) {
            mContext = context;
            mImageView = view;
            mSquare = square;
            mImageViewReference = new WeakReference<ImageView>(mImageView);
            mWidth = width;
            mHeight = height;
        }
        
        @Override
        protected Void doInBackground(String... paths) {
            
            if (mImageView != null) {
                
                if (mSquare) {
                    mHeight = mWidth = 100;
                }
                
                try {
                    mBitmap = decodeSampledBitmapFromUri(paths[0], mWidth, mHeight);
                    mUri = paths[0];
                } catch (IOException e) {
                }
            }
            
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (isCancelled()) {
                mBitmap = null;
            }
            
            if (mImageViewReference != null && mBitmap != null) {
                final ImageView imageView = mImageViewReference.get();
                final LoadBitmapTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(mBitmap);
                    //BitmapLoader.getInstance(mContext).add(mUri.toString(), mBitmap);
                }
                
            }
        }
        
        private int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            
            if (reqWidth == 0 || reqHeight == 0) {
                return inSampleSize;
            }
        
            if (height > reqHeight || width > reqWidth) {
        
                // Calculate ratios of height and width to requested height and width
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
        
                // Choose the smallest ratio as inSampleSize value, this will guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
                Log.d(TAG, "reqWidth = " + reqWidth + ", width = " + width);
                Log.d(TAG, "reqHeight = " + reqHeight + ", height = " + height);
            }
        
            return inSampleSize;
        }
        
        private Bitmap decodeSampledBitmapFromUri(String path,
                int reqWidth, int reqHeight) throws IOException {
                // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
                // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            Log.d(TAG, "Option: inSampleSize = " + options.inSampleSize);
                // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        }

    }
}
