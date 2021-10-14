package com.example.longtouch.floatTest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FrameView extends FrameLayout {
    public FrameView(@NonNull Context context) {
        super(context);
    }

    public FrameView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
