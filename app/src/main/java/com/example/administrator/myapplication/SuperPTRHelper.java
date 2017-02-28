package com.example.administrator.myapplication;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class SuperPTRHelper {

    private ScrollableContainer mCurrentScrollableContainer;

    /** 包含有 ScrollView ListView RecyclerView 的组件 */
    public interface ScrollableContainer {

        /** @return ScrollView ListView RecyclerView 或者其他的布局的实例 */
        RecyclerView getScrollableView();
    }

    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer) {
        this.mCurrentScrollableContainer = scrollableContainer;
    }

    private RecyclerView getScrollableView() {
        if (mCurrentScrollableContainer == null) return null;
        return mCurrentScrollableContainer.getScrollableView();
    }

    public int getScrollUpRange(){
        RecyclerView scrollableView = getScrollableView();
        if (scrollableView != null){
            return scrollableView.computeVerticalScrollRange()-scrollableView.computeVerticalScrollExtent() - scrollableView.computeVerticalScrollOffset();
        }
        return 0;
    }

    public int getScrollDownRange(){
        RecyclerView scrollableView = getScrollableView();
        if (scrollableView != null){
            return scrollableView.computeVerticalScrollOffset();
        }
        return 0;
    }

    public boolean onError(){
        RecyclerView scrollableView = getScrollableView();
        return scrollableView == null || scrollableView.getVisibility() == View.INVISIBLE || scrollableView.getVisibility() == View.GONE;
    }

    /**
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     */
    public boolean isTop() {
        RecyclerView scrollableView = getScrollableView();
        return scrollableView == null || isRecyclerViewTop(scrollableView);
    }

    private boolean isRecyclerViewTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                if (childAt == null || (firstVisibleItemPosition == 0 && childAt.getTop() == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将特定的view按照初始条件滚动
     *
     * @param velocityY 初始滚动速度
     */
    @SuppressLint("NewApi")
    public void smoothScrollBy(int velocityY) {
        RecyclerView scrollableView = getScrollableView();
        if (scrollableView != null) scrollableView.fling(0, velocityY);
    }

    public void scrollBy(int deltaY) {
        RecyclerView scrollableView = getScrollableView();
        if (scrollableView != null) scrollableView.scrollBy(0, deltaY);
    }
}
