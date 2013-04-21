package jing.app.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
			LoadBitmapTask task = new LoadBitmapTask(mContext, view, square);
			task.execute(Uri.parse("file://" + uriString));
		}
		
	}
}
