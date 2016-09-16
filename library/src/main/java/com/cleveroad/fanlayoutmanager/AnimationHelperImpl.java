package com.cleveroad.fanlayoutmanager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Collection;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
class AnimationHelperImpl implements AnimationHelper {

    public static final float ANIMATION_VIEW_SCALE_FACTOR = 1.5f;
    private static final int ANIMATION_SINGLE_OPEN_DURATION = 300;
    private static final int ANIMATION_SINGLE_CLOSE_DURATION = 300;
    private static final int ANIMATION_SHIFT_VIEWS_DURATION = 200;
    private static final int ANIMATION_SHIFT_VIEWS_DELAY_THRESHOLD = 50;
    private static final float ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD = 0.4F;

    @Override
    public void openItem(@NonNull final View view, int delay, Animator.AnimatorListener animatorListener) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1F, ANIMATION_VIEW_SCALE_FACTOR + ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();

                if (value < 1F + ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD / 2) {
                    value = Math.abs(value - 2F);
                } else {
                    value -= ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD;
                }

                scaleView(view, value);

            }
        });

        valueAnimator.setStartDelay(delay);
        valueAnimator.setDuration(ANIMATION_SINGLE_OPEN_DURATION);
        valueAnimator.addListener(animatorListener);
        valueAnimator.start();

    }

    @Override
    public void closeItem(final @NonNull View view, int delay, Animator.AnimatorListener animatorListener) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(ANIMATION_VIEW_SCALE_FACTOR, 1F);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                scaleView(view, value);
            }
        });
        valueAnimator.setStartDelay(delay);
        valueAnimator.setDuration(ANIMATION_SINGLE_CLOSE_DURATION);
        valueAnimator.addListener(animatorListener);
        valueAnimator.start();
    }

    private void scaleView(final View view, float value) {
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight());
        view.setScaleX(value);
        view.setScaleY(value);
    }

    @Override
    public void shiftSideViews(@NonNull final Collection<ViewAnimationInfo> views,
                               int delay,
                               @NonNull final RecyclerView.LayoutManager layoutManager,
                               @Nullable final Animator.AnimatorListener animatorListener,
                               @Nullable final ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        ValueAnimator bounceAnimator = ValueAnimator.ofFloat(0F, 1F);
        bounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                for (ViewAnimationInfo info : views) {
                    int left = (int) (info.startLeft + value * (info.finishLeft - info.startLeft));
                    int right = (int) (info.startRight + value * (info.finishRight - info.startRight));
                    layoutManager.layoutDecorated(info.view, left, info.top, right, info.bottom);
                }
                if (animatorUpdateListener != null) {
                    animatorUpdateListener.onAnimationUpdate(valueAnimator);
                }
//                if (layoutManager instanceof FanLayoutManager) {
//                    ((FanLayoutManager) layoutManager).updateArcViewPositions();
//                }
            }
        });

        bounceAnimator.setDuration(ANIMATION_SHIFT_VIEWS_DURATION);
        bounceAnimator.setStartDelay(delay + ANIMATION_SHIFT_VIEWS_DELAY_THRESHOLD);
        if (animatorListener != null) {
            bounceAnimator.addListener(animatorListener);
        }
        bounceAnimator.start();
    }

    @Override
    public float getViewScaleFactor() {
        return ANIMATION_VIEW_SCALE_FACTOR;
    }

    @Override
    public void straightenView(View view, float radius, @Nullable Animator.AnimatorListener listener) {
        if (view != null) {
            ObjectAnimator viewObjectAnimator = ObjectAnimator.ofFloat(view,
                    "rotation", radius, 0f);
            viewObjectAnimator.setDuration(150);
            viewObjectAnimator.setInterpolator(new DecelerateInterpolator());
            if (listener != null) {
                viewObjectAnimator.addListener(listener);
            }
            viewObjectAnimator.start();
        }

    }


}
