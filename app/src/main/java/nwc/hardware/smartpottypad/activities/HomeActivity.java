package nwc.hardware.smartpottypad.activities;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.User;
import nwc.hardware.smartpottypad.fragments.home.HomeFragment;
import nwc.hardware.smartpottypad.fragments.home.SettingFragment;
import nwc.hardware.smartpottypad.listeners.OnBackKeyDownListener;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    public final static int FRAGMENT_SETTING= 0;
    public final static int FRAGMENT_INFO = 1;

    private HomeFragment homeFragment;
    private SettingFragment settingFragment;
    private OnBackKeyDownListener onBackKeyDownListener;

    private User info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeFragment = new HomeFragment(this);
        settingFragment = new SettingFragment(this);

        changeFragment(FRAGMENT_SETTING);
    }

    public void changeFragment(int idx){
        if(idx == FRAGMENT_SETTING){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, settingFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_INFO){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, homeFragment).commitAllowingStateLoss();
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

    public void setInfo(User info){
        this.info = info;
    }

    public User getInfo(){
        return info;
    }

}