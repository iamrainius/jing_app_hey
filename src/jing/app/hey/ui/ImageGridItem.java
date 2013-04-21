package jing.app.hey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ImageGridItem extends RelativeLayout {
	String mUri;
	
	public ImageGridItem(Context context) {
		super(context);
	}

	public ImageGridItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageGridItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), 
				getDefaultSize(0, widthMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		
		int hSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		int wSpec = hSpec;
		super.onMeasure(wSpec, hSpec);
	}

}
