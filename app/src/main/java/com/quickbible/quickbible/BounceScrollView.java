package com.quickbible.quickbible;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;
public class BounceScrollView extends ScrollView {
    public int MAX_Y_OVERSCROLL_DISTANCE = 200;
    private Context mContext;
    private int mMaxYOverscrollDistance;
    public void setMAX_Y_OVERSCROLL_DISTANCE(int i) {
        this.MAX_Y_OVERSCROLL_DISTANCE = i;
        initBounceScrollView();
    }
    public BounceScrollView(Context context) {
        super(context);
        mContext = context;
        initBounceScrollView();
    }
    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBounceScrollView();
    }
    public BounceScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceScrollView();
    }
    private void initBounceScrollView() {
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }
}