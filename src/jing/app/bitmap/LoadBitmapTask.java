package jing.app.bitmap;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LoadBitmapTask extends AsyncTask<Uri, Void, Void> {
	private ImageView mImageView;
	private int mWidth;
	private int mHeight;
	private String mType;
	private Bitmap mBitmap;
	private Context mContext;
	private Uri mUri;
	private boolean mSquare;
	
	public LoadBitmapTask(Context context, ImageView view, boolean square) {
		mContext = context;
		mImageView = view;
		mSquare = square;
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
		if (mImageView != null) {
			mImageView.setImageBitmap(mBitmap);
			BitmapLoader.getInstance(mContext).add(mUri.toString(), mBitmap);
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