package nwc.hardware.smartpottypad.activities;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.Display;
import nwc.hardware.smartpottypad.fragments.intro.IntroFragment;
import nwc.hardware.smartpottypad.fragments.intro.LoginFragment;
import nwc.hardware.smartpottypad.fragments.intro.RegisterFragment;
import nwc.hardware.smartpottypad.listeners.OnBackKeyDownListener;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class IntroActivity extends AppCompatActivity {
    private final String TAG = "IntroActivity";

    public final static int FRAGMENT_INTRO = 0;
    public final static int FRAGMENT_REGISTER = 1;
    public final static int FRAGMENT_LOGIN = 2;

    private IntroFragment introFragment;
    private RegisterFragment registerFragment;
    private LoginFragment loginFragment;
    private OnBackKeyDownListener onBackKeyDownListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introFragment = new IntroFragment(this);
        registerFragment = new RegisterFragment(this);
        loginFragment = new LoginFragment(this);

        Display.createInfo(getApplicationContext());
        SetPreferences.getInstance(getApplicationContext());

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
    }

    public void changeFragment(int idx){
        if(idx == FRAGMENT_INTRO){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_REGISTER){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, registerFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_LOGIN){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, loginFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(onBackKeyDownListener != null){
                onBackKeyDownListener.onBackKeyDown();
            }
        }
        return false;
    }

    public void setOnKeyDownListener(OnBackKeyDownListener listener){
        this.onBackKeyDownListener = listener;
    }

}