package nwc.hardware.smartpottypad.animator;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.BounceInterpolator;

public class ErrorAnimator {
    private View target;
    private FloatEvaluator evaluator = new FloatEvaluator();
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
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

    public ErrorAnimator(View v){
        target = v;
        valueAnimator = TimeAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(500);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(ErrorListener);
    }

    public void start(){
        valueAnimator.start();
    }
}
