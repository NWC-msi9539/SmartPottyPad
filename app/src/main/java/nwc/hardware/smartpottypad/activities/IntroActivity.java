package nwc.hardware.smartpottypad.activities;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.Display;
import nwc.hardware.smartpottypad.fragments.IntroFragment;
import nwc.hardware.smartpottypad.fragments.RegisterFragment;
import nwc.hardware.smartpottypad.listeners.OnBackKeyDownListener;

public class IntroActivity extends AppCompatActivity {
    public final static int FRAGMENT_INTRO = 0;
    public final static int FRAGMENT_REGISTER = 1;

    private final String TAG = "IntroActivity";
    private IntroFragment introFragment;
    private RegisterFragment registerFragment;
    private OnBackKeyDownListener onBackKeyDownListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introFragment = new IntroFragment(this);
        registerFragment = new RegisterFragment(this);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();

        Display.createInfo(getApplicationContext());
    }

    public void changeFragment(int idx){
        if(idx == FRAGMENT_INTRO){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_REGISTER){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, registerFragment).commitAllowingStateLoss();
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