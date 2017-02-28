package com.example.administrator.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class FooterDemo extends RelativeLayout {

    public FooterDemo(Context context) {
        this(context, null);
    }

    public FooterDemo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private RelativeLayout rootView;
    private TextView tv;

    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.header, this);
        rootView = (RelativeLayout) view.findViewById(R.id.root_view);
        tv = (TextView) view.findViewById(R.id.state);
    }

    public int getRealHeight(){
        return rootView.getLayoutParams().height;
    }

    public void onPullUp() {
        tv.setVisibility(VISIBLE);
        tv.setText("继续上拉");
    }

    public void onRelease() {
        tv.setVisibility(VISIBLE);
        tv.setText("松开加载");
    }

    public void onLoadMore() {
        tv.setVisibility(VISIBLE);
        tv.setText("加载中……");
    }

    public void onComplete() {
        tv.setVisibility(VISIBLE);
        tv.setText("加载完成");
    }

    public void onIdle() {
        tv.setVisibility(INVISIBLE);
    }

}
