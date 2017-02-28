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

public class HeaderDemo extends RelativeLayout {

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

    public void onPullDown() {
        tv.setText("继续下拉");
    }

    public void onRelease() {
        tv.setText("松开刷新");
    }

    public void onRefresh() {
        tv.setText("刷新中……");
    }

    public void onComplete() {
        tv.setText("刷新完成");
    }


}
