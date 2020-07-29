package com.mad.p03.np2020.routine.Focus.Model;

import android.content.Context;
import android.util.AttributeSet;


public class ResizeableButton extends androidx.appcompat.widget.AppCompatButton {
    public ResizeableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }
}
