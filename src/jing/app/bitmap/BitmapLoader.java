package jing.app.bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

public class BitmapLoader {
    private static final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final int MEMORY_CACHE_SIZE = MAX_MEMORY / 8;
    
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
        return mMemoryCache.get(key);
    }
    
    public void loadBitmapFromUri(String uriString, ImageView view, boolean square) {
        final String key = uriString;
        
        final Bitmap bitmap = get(key);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(uriString, view, square)) {
                final LoadBitmapTask task = new LoadBitmapTask(mContext, view, square);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), null, task);
                view.setImageDrawable(asyncDrawable);
                task.execute(Uri.parse("file://" + uriString));
            }
        }
        
    }
    
    private boolean cancelPotentialWork(String uriString, ImageView imageView, boolean square) {
        final LoadBitmapTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Uri bitmapData = bitmapWorkerTask.mUri;
            if (bitmapData != null && !bitmapData.toString().equalsIgnoreCase(uriString)) {
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
    
    private class LoadBitmapTask extends AsyncTask<Uri, Void, Void> {
        private ImageView mImageView;
        private final WeakReference<ImageView> mImageViewReference;
        private int mWidth;
        private int mHeight;
        private String mType;
        private Bitmap mBitmap;
        private Context mContext;
        Uri mUri;
        private boolean mSquare;
        
        public LoadBitmapTask(Context context, ImageView view, boolean square) {
            mContext = context;
            mImageView = view;
            mSquare = square;
            mImageViewReference = new WeakReference<ImageView>(mImageView);
        }
        
        @Override
        protected Void doInBackground(Uri... uris) {
            if (mImageView != null) {
                mWidth = mImageView.getWidth();
                if (mSquare) {
                    mHeight = mWidth;
                } else {
                    mHeight = mImageView.getHeight();
                }
                
                try {
                    mBitmap = decodeSampledBitmapFromUri(uris[0], mWidth, mHeight);
                    mUri = uris[0];
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
                    BitmapLoader.getInstance(mContext).add(mUri.toString(), mBitmap);
                }
                
            }
        }
        
        private int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
        
            if (height > reqHeight || width > reqWidth) {
        
                // Calculate ratios of height and width to requested height and width
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
        
                // Choose the smallest ratio as inSampleSize value, this will guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
        
            return inSampleSize;
        }
        
        private Bitmap decodeSampledBitmapFromUri(Uri uri,
                int reqWidth, int reqHeight) throws IOException {
                // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream is = mContext.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
                // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            is = mContext.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is, null, options);
        }

    }
}
