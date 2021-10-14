package com.example.longtouch.floatTest;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.example.longtouch.MainActivity;


/**
 * author:YY on 2020/7/31
 * content:
 */
public class AnimUtil {

    public static void upAnim(Point start, Point end, final View view, WindowManager windowManager) {
        int lx = end.x - start.x;
        int ly = end.y - start.y;
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
            int x = layoutParams.x;
            int y = layoutParams.y;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //YLog.e("upAnim:", start + "_" + width + "_" + height + "_" + value + "");
                Log.d("TAG", "onAnimationUpdate: "+value);
                view.setBackgroundColor((Integer) evaluate(value,0X000000FF,0XFF0000FF));
                layoutParams.x = x+(int)(lx*value);
                layoutParams.y = y+(int)(ly*value);
                windowManager.updateViewLayout(view, layoutParams);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public static void downAnim(Point start, Point end, final View view, WindowManager windowManager) {
        int lx = end.x - start.x;
        int ly = end.y - start.y;

        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
            int x = layoutParams.x;
            int y = layoutParams.y;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //YLog.e("upAnim:", start + "_" + width + "_" + height + "_" + value + "");
                Log.d("TAG", "onAnimationUpdate: "+value);

                view.setBackgroundColor((Integer) evaluate(value,0XFF0000FF,0X000000FF));
                layoutParams.x = x+(int)(lx*value);
                layoutParams.y = y+(int)(ly*value);
                windowManager.updateViewLayout(view, layoutParams);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public static Object evaluate(float fraction, Object startValue, Object endValue) {
        if(fraction>1)return endValue;
        int startInt = (Integer) startValue;
        float startA = ((startInt >> 24) & 0xff) / 255.0f;
        float startR = ((startInt >> 16) & 0xff) / 255.0f;
        float startG = ((startInt >>  8) & 0xff) / 255.0f;
        float startB = ( startInt        & 0xff) / 255.0f;

        int endInt = (Integer) endValue;
        float endA = ((endInt >> 24) & 0xff) / 255.0f;
        float endR = ((endInt >> 16) & 0xff) / 255.0f;
        float endG = ((endInt >>  8) & 0xff) / 255.0f;
        float endB = ( endInt        & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }

}

