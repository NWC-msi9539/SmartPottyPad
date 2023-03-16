package nwc.hardware.smartpottypad.fragments.intro;

import android.animation.FloatEvaluator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.activities.IntroActivity;
import nwc.hardware.smartpottypad.animator.ErrorAnimator;
import nwc.hardware.smartpottypad.listeners.OnAnimationEndListener;
import nwc.hardware.smartpottypad.tasks.SetPreferences;
import nwc.hardware.smartpottypad.utils.CryptoUtil;

public class LoginFragment extends Fragment {
    private final String TAG = "LoginFragment";

    private IntroActivity parent;

    private EditText IDTXT;
    private EditText PASSTXT;
    private Button LoginBTN;
    private TextView stepTXT;
    private TextView infoTXT;
    private CheckBox login_autoCheck;

    private int step = 0;

    private Drawable EditBACK;

    private InputMethodManager imm;

    private ErrorAnimator errorAnimator;

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
                stepTXT.setVisibility(View.GONE);
                login_autoCheck.setVisibility(View.VISIBLE);
                LoginBTN.setVisibility(View.VISIBLE);
            }else{
                stepTXT.setVisibility(View.VISIBLE);
                login_autoCheck.setVisibility(View.GONE);
                LoginBTN.setVisibility(View.GONE);
            }
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

    private InputFilter[] filters = { inputBodyFilter, new InputFilter.LengthFilter(16) };


    public LoginFragment(IntroActivity parent){
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);



        imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);

        EditBACK = getContext().getDrawable(com.google.android.gms.base.R.drawable.common_google_signin_btn_text_light_normal_background);

        login_autoCheck = v.findViewById(R.id.login_autoCheck);

        IDTXT = v.findViewById(R.id.login_IDTXT);
        IDTXT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG , "actionId --> " + actionId + ", ORIGIN --> " + EditorInfo.IME_ACTION_NEXT);
                if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION){
                    nextStep();
                }
                return false;
            }
        });
        IDTXT.setFilters(filters);
        IDTXT.requestFocus();

        infoTXT = v.findViewById(R.id.login_infoTXT);

        PASSTXT = v.findViewById(R.id.login_passTXT);
        PASSTXT.addTextChangedListener(passWatcher);
        PASSTXT.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (PASSTXT.getText().toString().length() == 0) {
                            preStep();
                        }
                    }
                }
                return false;
            }
        });
        stepTXT = v.findViewById(R.id.login_stepTXT);

        stepTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.changeFragment(IntroActivity.FRAGMENT_REGISTER);
            }
        });
        LoginBTN = v.findViewById(R.id.login_BTN);
        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm.showSoftInput(IDTXT, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);

        return v;
    }

    private void nextStep(){

        switch (step){
            case 0:
                step = 1;
                PASSTXT.setVisibility(View.VISIBLE);
                PASSTXT.requestFocus();
                IDTXT.setBackground(EditBACK);
                IDTXT.setEnabled(false);
                infoTXT.setText("PIN번호를 입력해주세요.");
                break;
            case 1:
                step = 2;
                infoTXT.setText("로그인 중입니다!");
                PASSTXT.setBackground(EditBACK);
                PASSTXT.setEnabled(false);
                imm.hideSoftInputFromWindow(PASSTXT.getWindowToken(), 0);
                ValueAnimator animator = TimeAnimator.ofFloat(0f, 1f);
                FloatEvaluator evaluator = new FloatEvaluator();
                animator.setDuration(1000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float fraction = animation.getAnimatedFraction();
                        float value = evaluator.evaluate(fraction, 0f, 1f);
                        infoTXT.setAlpha(value);
                    }
                });
                animator.start();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
                        String ID = IDTXT.getText().toString();
                        String PIN = PASSTXT.getText().toString();
                        users.orderByChild("id").equalTo(ID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                Map<String, Map<String, String>> value = (Map<String, Map<String, String>>) task.getResult().getValue();

                                if(value != null){
                                    Log.d(TAG, "EXIST --> " + value);
                                    SetPreferences.databaseKey = (String)(((Map<?, ?>) task.getResult().getValue()).keySet().iterator().next());
                                    Iterator<Map<String, String>> iterator = value.values().iterator();
                                    Map<String, String> userData = iterator.next();
                                    Log.d(TAG, "USERDATA --> ID : " + userData.get("id") + ", PIN : " + userData.get("pin"));
                                    if(userData.get("pin").equals(PIN)){
                                        if(login_autoCheck.isChecked()){
                                            String id = IDTXT.getText().toString();
                                            String pass = PASSTXT.getText().toString();
                                            String mergedText = id + " " + pass;

                                            CryptoUtil.EncryptText(mergedText.getBytes());
                                        }
                                        Intent intent = new Intent(getContext(), HomeActivity.class);
                                        startActivity(intent);
                                        parent.finish();
                                    }else{
                                        animator.cancel();
                                        errorAnimator = new ErrorAnimator(PASSTXT, getContext());
                                        Toast.makeText(getContext(), "PIN 번호가 맞지 않아요", Toast.LENGTH_SHORT).show();
                                        preStep();
                                    }
                                }else{
                                    animator.cancel();
                                    errorAnimator = new ErrorAnimator(IDTXT, getContext());
                                    errorAnimator.addOnAnimationEnd(new OnAnimationEndListener() {
                                        @Override
                                        public void onAnimationEnd() {
                                            IDTXT.setBackground(EditBACK);
                                        }
                                    });
                                    Toast.makeText(getContext(), "찾을 수 없는 ID에요.", Toast.LENGTH_SHORT).show();
                                    preStep();
                                }
                            }
                        });
                    }
                }, 3000);
                break;
        }
    }

    private void preStep(){
        switch (step){
            case 1:
                step = 0;
                infoTXT.setText("ID를 입력해주세요.");
                PASSTXT.setVisibility(View.GONE);
                IDTXT.setBackground(null);
                IDTXT.setEnabled(true);
                IDTXT.requestFocus();
                break;
            case 2:
                step = 1;
                infoTXT.setText("PIN번호를 입력해주세요.");
                PASSTXT.setBackground(null);
                PASSTXT.setEnabled(true);
                PASSTXT.requestFocus();
                imm.showSoftInput(PASSTXT, InputMethodManager.SHOW_IMPLICIT);

                errorAnimator.start();

                break;
        }
    }

}