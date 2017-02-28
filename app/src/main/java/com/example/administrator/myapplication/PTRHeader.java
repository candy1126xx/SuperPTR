package com.example.administrator.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public abstract class PTRHeader extends RelativeLayout {

    public PTRHeader(Context context) {
        this(context, null);
    }

    public PTRHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PTRHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void onPullDown();
    public abstract void onRelease();
    public abstract void onRefresh();
    public abstract void onComplete();
}
