package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class SuperPTRLayout extends ViewGroup {

    private RelativeLayout header;

    private View content;

    private RelativeLayout footer;

    private int headerHeight;
    private int footerHeight;

    private int refreshHeight;

    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private SuperPTRHelper ptrHelper;

    private float mDownX;  //第一次按下的x坐标
    private float mDownY;  //第一次按下的y坐标
    private float mLastY;  //最后一次移动的Y坐标
    private int mLastScrollerY;
    private boolean verticalScrollFlag = false;   //是否允许垂直滚动

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private int state = STATE_IDLE;
    private static final int STATE_IDLE = 11;
    private static final int STATE_MOVE = 12;
    private static final int STATE_FLING = 13;
    private static final int STATE_BACK = 14;
    private static final int STATE_REFRESH = 15;

    private static final float MOVE_RATIO = 2.0f;

    public SuperPTRLayout(Context context) {
        this(context, null);
    }

    public SuperPTRLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperPTRLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScroller = new Scroller(context);
        ptrHelper = new SuperPTRHelper();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        // 添加头
        header = new RelativeLayout(getContext());
        LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (screenHeight / MOVE_RATIO));
        header.setLayoutParams(lp1);
        header.setBackgroundColor(Color.YELLOW);
        addView(header);
        // 内容
        content = getChildAt(0);
        // 添加脚
        footer = new RelativeLayout(getContext());
        LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (screenHeight / MOVE_RATIO));
        footer.setLayoutParams(lp2);
        footer.setBackgroundColor(Color.BLUE);
        addView(footer);
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(header, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
        measureContentView(content, widthMeasureSpec, heightMeasureSpec);
        measureChildWithMargins(footer, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
        // 得到header和footer的高度
        headerHeight = header.getMeasuredHeight();
        footerHeight = footer.getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + headerHeight + footerHeight, MeasureSpec.EXACTLY));
    }

    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 布局
        int scrollY = getScrollY();

        if (header != null) {
            final int left = 0;
            final int top = 0 - headerHeight + scrollY;
            final int right = left + header.getMeasuredWidth();
            final int bottom = top + header.getMeasuredHeight();
            header.layout(left, top, right, bottom);
        }

        if (content != null) {
            final int left = 0;
            final int top = scrollY;
            final int right = left + content.getMeasuredWidth();
            final int bottom = top + content.getMeasuredHeight();
            content.layout(left, top, right, bottom);
        }

        if (content != null && footer != null) {
            final int left = 0;
            final int top = content.getMeasuredHeight() + scrollY;
            final int right = left + footer.getMeasuredWidth();
            final int bottom = top + footer.getMeasuredHeight();
            footer.layout(left, top, right, bottom);
        }
    }

    public void setHeader() {
        HeaderDemo headerDemo = new HeaderDemo(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        headerDemo.setLayoutParams(lp);
        header.addView(headerDemo);
        refreshHeight = headerDemo.getRealHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentX = ev.getX();                   //当前手指相对于当前view的X坐标
        float currentY = ev.getY();                   //当前手指相对于当前view的Y坐标
        float shiftX = Math.abs(currentX - mDownX);   //当前触摸位置与第一次按下位置的X偏移量
        float shiftY = Math.abs(currentY - mDownY);   //当前触摸位置与第一次按下位置的Y偏移量
        float deltaY;                                 //滑动的偏移量，即连续两次进入Move的偏移量
        obtainVelocityTracker(ev);                    //初始化速度追踪器
        switch (ev.getAction()) {
            //Down事件主要初始化变量
            case MotionEvent.ACTION_DOWN:
                verticalScrollFlag = false;
                mDownX = currentX;
                mDownY = currentY;
                mLastY = currentY;
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                deltaY = mLastY - currentY; //连续两次进入move的偏移
                mLastY = currentY;
                if (shiftX > mTouchSlop && shiftX > shiftY) {
                    //水平滑动
                    verticalScrollFlag = false;
                } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                    //垂直滑动
                    verticalScrollFlag = true;
                }
                if (verticalScrollFlag) {
                    move(deltaY);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (verticalScrollFlag) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity); //1000表示单位，每1000毫秒允许滑过的最大距离是mMaximumVelocity
                    float yVelocity = mVelocityTracker.getYVelocity();  //获取当前的滑动速度
                    //根据当前的速度和初始化参数，将滑动的惯性初始化到当前View，至于是否滑动当前View，取决于computeScroll中计算的值
                    //这里不判断最小速度，确保computeScroll一定至少执行一次
                    if (Math.abs(yVelocity) >= mMinimumVelocity) {
                        fling(yVelocity);
                    } else {
                        springBack();
                    }
                    //阻止快读滑动的时候点击事件的发生，滑动的时候，将Up事件改为Cancel就不会发生点击了
                    if ((shiftX > mTouchSlop || shiftY > mTouchSlop)) {
                        int action = ev.getAction();
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        boolean dd = super.dispatchTouchEvent(ev);
                        ev.setAction(action);
                        return dd;
                    }
                } else {
                    springBack();
                }
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        //手动将事件传递给子View，让子View自己去处理事件
        super.dispatchTouchEvent(ev);
        //消费事件，返回True表示当前View需要消费事件，就是事件的TargetView
        return true;
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private boolean canNestedScrollUp() {
        return ptrHelper.getScrollUpRange() != 0;
    }

    private boolean canNestedScrollDown() {
        return ptrHelper.getScrollDownRange() != 0;
    }

    private boolean headerIsShow() {
        return getScrollY() < 0;
    }

    private boolean footerIsShow() {
        return getScrollY() > 0;
    }

    private int getMinY() {
        return -ptrHelper.getScrollDownRange() - headerHeight;
    }

    private int getMaxY() {
        return ptrHelper.getScrollUpRange() + footerHeight;
    }

    private void move(float deltaY) {
        state = STATE_MOVE;
        onMove(deltaY);
    }

    private void fling(float yVelocity) {
        if (yVelocity == 0) return;
        mScroller.abortAnimation();
        mScroller.forceFinished(true);
        mLastScrollerY = getScrollY();
        mScroller.fling(0, getScrollY(), 0, -(int) yVelocity, 0, 0, getMinY(), getMaxY());
        state = STATE_FLING;
        invalidate();
    }

    private void springBack() {
        int s = getScrollY();
        if (state == STATE_MOVE && s < 0 && s < -refreshHeight) {
            beginRefresh();
        } else if (state == STATE_MOVE && s > 0 && s > refreshHeight){

        }else {
            closeHeader();
        }
    }

    private void beginRefresh() {
        mScroller.abortAnimation();
        mScroller.forceFinished(true);
        mLastScrollerY = getScrollY();
        state = STATE_REFRESH;
        mScroller.startScroll(0, mLastScrollerY, 0, -mLastScrollerY - refreshHeight, Math.abs(-mLastScrollerY - refreshHeight) + 1);
        invalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                closeHeader();
            }
        }, 2000);
    }

    public void closeHeader() {
        if (getScrollY() == 0) return;
        mScroller.abortAnimation();
        mScroller.forceFinished(true);
        mLastScrollerY = getScrollY();
        state = STATE_BACK;
        mScroller.startScroll(0, mLastScrollerY, 0, -mLastScrollerY, Math.abs(mLastScrollerY) + 1);
        invalidate();
    }

    @Override
    public void computeScroll() {
        switch (state) {
            case STATE_FLING:
                onFling();
                break;
            case STATE_BACK:
            case STATE_REFRESH:
                onBack();
                break;
        }
    }

    private void onMove(float deltaY) {
        if (deltaY > 0) {
            if (headerIsShow()) {
                scrollBy(0, getHideHeight(deltaY));
            } else if (canNestedScrollUp()) {
                ptrHelper.scrollBy((int) deltaY);
            } else {
                scrollBy(0, (int) (deltaY / MOVE_RATIO));
            }
        } else if (deltaY < 0) {
            if (footerIsShow()) {
                scrollBy(0, getHideHeight(deltaY));
            } else if (canNestedScrollDown()) {
                ptrHelper.scrollBy((int) deltaY);
            } else {
                scrollBy(0, (int) (deltaY / MOVE_RATIO));
            }
        }
    }

    private int getHideHeight(float delta) {
        float all = delta / MOVE_RATIO;
        if (all < 0) {
            return (int) Math.max(all, -getScrollY());
        } else if (all > 0) {
            return (int) Math.min(all, -getScrollY());
        } else {
            return 0;
        }
    }

    private void onFling() {
        if (mScroller.computeScrollOffset()) {
            final int currY = mScroller.getCurrY();
            final int deltaY = currY - mLastScrollerY;
            onMove(deltaY);
            invalidate();
            mLastScrollerY = currY;
        } else {
            springBack();
        }
    }

    private void onBack() {
        if (mScroller.computeScrollOffset()) {
            final int currY = mScroller.getCurrY();
            final int deltaY = currY - mLastScrollerY;
            if (deltaY != 0) scrollBy(0, deltaY);
            invalidate();
            mLastScrollerY = currY;
        } else {
            reset();
        }
    }

    private void reset() {
        mScroller.abortAnimation();
        mScroller.forceFinished(true);
        state = STATE_IDLE;
    }

    @Override
    public void scrollBy(int x, int y) {
        int scrollY = getScrollY();
        int toY = scrollY + y;
        if (toY >= headerHeight) {
            toY = headerHeight;
        } else if (toY <= -footerHeight) {
            toY = -footerHeight;
        }
        y = toY - scrollY;
        super.scrollBy(x, y);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public void setCurrentScrollableContainer(SuperPTRHelper.ScrollableContainer scrollableContainer) {
        ptrHelper.setCurrentScrollableContainer(scrollableContainer);
    }
}
