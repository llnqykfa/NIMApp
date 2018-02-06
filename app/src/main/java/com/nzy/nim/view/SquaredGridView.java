package com.nzy.nim.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/11/28.
 */
public class SquaredGridView extends GridView {

    public SquaredGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredGridView(Context context) {
        super(context);
    }

    public SquaredGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}