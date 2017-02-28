package com.example.administrator.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class HeaderDemo extends PTRHeader {

    public HeaderDemo(Context context) {
        this(context, null);
    }

    public HeaderDemo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private TextView tv;

    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.header, this);
        tv = (TextView) view.findViewById(R.id.state);
    }

    @Override
    public void onPullDown() {
        tv.setText("继续下拉");
    }

    @Override
    public void onRelease() {
        tv.setText("松开刷新");
    }

    @Override
    public void onRefresh() {
        tv.setText("刷新中……");
    }

    @Override
    public void onComplete() {
        tv.setText("刷新完成");
    }


}
