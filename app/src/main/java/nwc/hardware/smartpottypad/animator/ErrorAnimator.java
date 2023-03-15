package nwc.hardware.smartpottypad.animator;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.BounceInterpolator;

import nwc.hardware.smartpottypad.listeners.OnAnimationEndListener;
import nwc.hardware.smartpottypad.listeners.OnAnimationStartListener;

public class ErrorAnimator {
    private View target;
    private Context mContext;
    private FloatEvaluator evaluator = new FloatEvaluator();
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private OnAnimationEndListener endListener;
    private OnAnimationStartListener startListener;

    private ValueAnimator.AnimatorUpdateListener ErrorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Float fraction = animation.getAnimatedFraction();
            float value = evaluator.evaluate(fraction, 0f, 22f);
            int color_value = (int) argbEvaluator.evaluate(fraction, Color.TRANSPARENT, Color.parseColor("#FFD32F2F"));
            target.setTranslationX(value);
            target.setBackgroundColor(color_value);
        }
    };

    private ValueAnimator valueAnimator;

    public ErrorAnimator(View v, Context mContext){
        this.mContext = mContext;
        target = v;
        valueAnimator = TimeAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(500);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(startListener != null){
                    startListener.onAnimationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(endListener != null){
                    endListener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.addUpdateListener(ErrorListener);
    }

    public ErrorAnimator addOnAnimationEnd(OnAnimationEndListener listener){
        this.endListener = listener;
        return this;
    }
    public ErrorAnimator addOnAnimationStart(OnAnimationStartListener listener){
        this.startListener = listener;
        return this;
    }


    public void start(){
        Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        valueAnimator.start();
    }
}
