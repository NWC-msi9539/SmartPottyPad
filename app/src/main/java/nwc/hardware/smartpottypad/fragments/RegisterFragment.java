package nwc.hardware.smartpottypad.fragments;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.regex.Pattern;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.IntroActivity;
import nwc.hardware.smartpottypad.animator.ErrorAnimator;
import nwc.hardware.smartpottypad.datas.Display;
import nwc.hardware.smartpottypad.listeners.OnBackKeyDownListener;

public class RegisterFragment extends Fragment {
    private final String TAG = "registerFragment";

    private IntroActivity parent;

    private float startValue = -24f;
    private float endValue= 24f;

    private ImageView fadeTool;
    private ImageView loadingIMG;

    private TextView stepInfo;
    private TextView statusTXT;
    private EditText IDTXT;
    private EditText PassTXT;
    private EditText PassCheckTXT;
    private Button stepBTN;

    private InputMethodManager imm;

    private Drawable EditBACK;
    private Drawable emptyBACK = new ColorDrawable(Color.TRANSPARENT);

    private LinearLayout RegLayout;

    private TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length() == 6){
                nextStepDraw();
            }
        }
    };

    private View.OnKeyListener passListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(v.equals(PassTXT)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (PassTXT.getText().toString().length() == 0) {
                            preStepDraw();
                        }
                    }
                }
            }else if(v.equals(PassCheckTXT)){
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (PassCheckTXT.getText().toString().length() == 0) {
                            preStepDraw();
                        }
                    }
                }
            }
            return false;
        }
    };

    protected InputFilter inputBodyFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9a-zA-Z]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    private InputFilter[] filters = { inputBodyFilter };

    private int step = 0;

    public RegisterFragment(IntroActivity activity){
        parent = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        parent.setOnKeyDownListener(new OnBackKeyDownListener() {
            @Override
            public void onBackKeyDown() {
                preStepDraw();
            }
        });

        imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        EditBACK = getContext().getDrawable(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal_background);

        RegLayout = v.findViewById(R.id.RegLayout);
        fadeTool = v.findViewById(R.id.Reg_fadeTool);
        stepInfo = v.findViewById(R.id.stepInfoTXT);
        statusTXT = v.findViewById(R.id.Reg_statusTXT);
        loadingIMG = v.findViewById(R.id.Reg_loadingIMG);
        loadingIMG.setTranslationY(-1 * Display.height_Float / 8f);

        IDTXT = v.findViewById(R.id.Reg_idEDIT);
        IDTXT.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    stepBTN.callOnClick();
                }
                return false;
            }
        });
        IDTXT.setFilters(filters);
        IDTXT.setCursorVisible(false);

        PassTXT = v.findViewById(R.id.Reg_passEDIT);
        PassTXT.addTextChangedListener(passWatcher);
        PassTXT.setOnKeyListener(passListener);
        PassTXT.setCursorVisible(false);

        PassCheckTXT = v.findViewById(R.id.Reg_passcheckEDIT);
        PassCheckTXT.addTextChangedListener(passWatcher);
        PassCheckTXT.setOnKeyListener(passListener);
        PassCheckTXT.setCursorVisible(false);

        stepBTN = v.findViewById(R.id.nextStepBTN);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeTool.animate()
                        .setInterpolator(new AccelerateInterpolator())
                        .setDuration(1000)
                        .translationX(Display.width)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                fadeTool.animate()
                                        .setInterpolator(new AccelerateInterpolator())
                                        .setStartDelay(1000)
                                        .setDuration(1000)
                                        .translationX(0f)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                stepInfo.setText("사용하실 ID를 적어주세요!\n(영어, 숫자 혼용 16자 제한)");
                                                fadeTool.animate()
                                                        .setInterpolator(new AccelerateInterpolator())
                                                        .setStartDelay(0)
                                                        .setDuration(1000)
                                                        .translationX(Display.width)
                                                        .withEndAction(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                IDTXT.setVisibility(View.VISIBLE);
                                                                IDTXT.requestFocus();
                                                                stepBTN.setVisibility(View.VISIBLE);
                                                                imm.showSoftInput(IDTXT, 0);
                                                            }
                                                        })
                                                        .start();
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();
            }
        }, 500);

        stepBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IDTXT.getText().toString().isEmpty()){
                    ErrorAnimator errorAnimator = new ErrorAnimator(IDTXT);
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                    errorAnimator.start();
                    return;
                }
                nextStepDraw();
            }
        });

        return v;
    }


    /**
     * step 0 - ID 입력
     * step 1 - PIN 입력
     * step 2 - PIN 확인
     * step 3 - ID등록
     */
    public void nextStepDraw(){
        switch (step){
            case 0:
                step = 1;
                IDTXT.setBackground(EditBACK);
                IDTXT.setEnabled(false);
                fadeTool.animate()
                        .setInterpolator(new AccelerateInterpolator())
                        .setDuration(1000)
                        .translationX(0f)
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                stepBTN.animate()
                                        .setInterpolator(new AccelerateInterpolator())
                                        .setDuration(1000)
                                        .alpha(0f)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                stepBTN.setEnabled(false);
                                            }
                                        })
                                        .start();
                            }
                        })
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                stepInfo.setText("사용하실 PIN번호를 적어주세요!\n(6자리 숫자에요!)");
                                fadeTool.animate()
                                        .setInterpolator(new AccelerateInterpolator())
                                        .setDuration(1000)
                                        .translationX(Display.width)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                PassTXT.setVisibility(View.VISIBLE);
                                                PassTXT.requestFocus();
                                                imm.showSoftInput(PassTXT, 0);
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();
                break;
            case 1:
                step = 2;
                PassCheckTXT.setVisibility(View.VISIBLE);
                PassCheckTXT.requestFocus();
                PassTXT.setBackground(EditBACK);
                PassTXT.setEnabled(false);
                stepInfo.setText("다시 한번 적어주세요!");
                imm.showSoftInput(PassCheckTXT, 0);
                break;
            case 2:
                step = 3;
                PassCheckTXT.setBackground(EditBACK);
                PassCheckTXT.setEnabled(false);
                stepBTN.setVisibility(View.INVISIBLE);
                stepInfo.animate()
                        .setInterpolator(new AccelerateInterpolator())
                        .setDuration(1000)
                        .translationY(-1 * getResources().getDimension(R.dimen.padding_96))
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                statusTXT.animate()
                                        .setInterpolator(new AccelerateInterpolator())
                                        .setDuration(1000)
                                        .alpha(1f)
                                        .withStartAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingIMG.animate()
                                                        .setInterpolator(new DecelerateInterpolator())
                                                        .setDuration(1000)
                                                        .withEndAction(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                FloatEvaluator evaluator = new FloatEvaluator();
                                                                ValueAnimator animator = TimeAnimator.ofFloat(startValue, endValue);
                                                                animator.setDuration(1500);
                                                                animator.setRepeatCount(ValueAnimator.INFINITE);
                                                                animator.setRepeatMode(ValueAnimator.REVERSE);
                                                                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                                                animator.addListener(new Animator.AnimatorListener() {
                                                                    @Override
                                                                    public void onAnimationStart(Animator animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationCancel(Animator animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationRepeat(Animator animation) {

                                                                    }
                                                                });
                                                                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                                    @Override
                                                                    public void onAnimationUpdate(ValueAnimator animation) {
                                                                        Float fraction = animation.getAnimatedFraction();
                                                                        float value = evaluator.evaluate(fraction, startValue, endValue);
                                                                        float alpha_value = evaluator.evaluate(fraction, 1f, 0f);
                                                                        loadingIMG.setRotationX(value);
                                                                        statusTXT.setAlpha(alpha_value);
                                                                    }
                                                                });
                                                                animator.start();
                                                            }
                                                        })
                                                        .translationY(getResources().getDimension(R.dimen.padding_24))
                                                        .start();
                                            }
                                        })
                                        .start();
                            }
                        })
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                RegLayout.animate()
                                        .setInterpolator(new AccelerateInterpolator())
                                        .setDuration(1000)
                                        .translationY(getResources().getDimension(R.dimen.padding_96))
                                        .start();
                            }
                        })
                        .start();
                break;
            default:
                break;
        }
    }

    public void preStepDraw(){
        switch (step){
            case 0:
                parent.changeFragment(IntroActivity.FRAGMENT_INTRO);
                break;
            case 1:
                step = 0;
                IDTXT.setBackground(emptyBACK);
                IDTXT.setEnabled(true);
                IDTXT.requestFocus();
                stepInfo.setText("사용하실 ID를 적어주세요!\n(영어, 숫자 혼용 16자 제한)");
                PassTXT.setVisibility(View.GONE);
                PassTXT.setText("");
                stepBTN.animate()
                        .setInterpolator(new DecelerateInterpolator())
                        .setDuration(1000)
                        .alpha(1f)
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                stepBTN.setEnabled(true);
                                stepBTN.setAlpha(0f);
                            }
                        })
                        .start();
                imm.showSoftInput(IDTXT, 0);
                break;
            case 2:
                step = 1;
                PassTXT.setBackground(emptyBACK);
                PassTXT.setEnabled(true);
                PassTXT.requestFocus();
                stepInfo.setText("사용하실 PIN번호를 적어주세요!\n(6자리 숫자에요!)");
                PassCheckTXT.setVisibility(View.GONE);
                PassCheckTXT.setText("");
                imm.showSoftInput(PassTXT, 0);
                break;
            case 3:
                break;
            default:
                break;
        }
    }

}