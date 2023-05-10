package nwc.hardware.smartpottypad.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.DeviceProfile;
import nwc.hardware.smartpottypad.datas.User;
import nwc.hardware.smartpottypad.fragments.home.HomeFragment;
import nwc.hardware.smartpottypad.fragments.home.SettingFragment;
import nwc.hardware.smartpottypad.fragments.home.detail.CheckWifiFragment;
import nwc.hardware.smartpottypad.fragments.home.detail.SearchingBluetoothFragment;
import nwc.hardware.smartpottypad.fragments.home.detail.SearchingWifiFragment;
import nwc.hardware.smartpottypad.listeners.OnBackKeyDownListener;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";
    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_BACK_TO_EXIT_INTERVAL = 2000;

    public final static int FRAGMENT_SETTING= 0;
    public final static int FRAGMENT_INFO = 1;
    public final static int FRAGMENT_DETAIL_WIFI= 2;
    public final static int FRAGMENT_DETAIL_SCAN_WIFI= 3;
    public final static int FRAGMENT_DETAIL_SCAN_BLUETOOTH= 4;

    private HomeFragment homeFragment;
    private SettingFragment settingFragment;
    private CheckWifiFragment home_checkWifiFragment;
    private SearchingWifiFragment searchingWifiFragment;
    private SearchingBluetoothFragment searchingBluetoothFragment;
    private OnBackKeyDownListener onBackKeyDownListener;

    private Map<DatabaseReference, ValueEventListener> degreeAttaches = new HashMap<>();
    private Map<DatabaseReference, ValueEventListener> Attaches = new HashMap<>();

    private User info;
    private DeviceProfile profile = new DeviceProfile();

    private int nowFragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeFragment = new HomeFragment(this);
        settingFragment = new SettingFragment(this);
        home_checkWifiFragment = new CheckWifiFragment(this);
        searchingWifiFragment = new SearchingWifiFragment(this);
        searchingBluetoothFragment = new SearchingBluetoothFragment(this);
        profile.setUserkey(SetPreferences.databaseKey);
        changeFragment(FRAGMENT_SETTING);
    }

    public void changeFragment(int idx){
        if(idx == FRAGMENT_SETTING){
            nowFragmentIndex = idx;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, settingFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_INFO){
            nowFragmentIndex = idx;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, homeFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_DETAIL_WIFI){
            nowFragmentIndex = idx;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.HomeDetailFrame, home_checkWifiFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_DETAIL_SCAN_WIFI){
            nowFragmentIndex = idx;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.HomeDetailFrame, searchingWifiFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_DETAIL_SCAN_BLUETOOTH){
            nowFragmentIndex = idx;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.HomeDetailFrame, searchingBluetoothFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(nowFragmentIndex == FRAGMENT_INFO){
                if(homeFragment.closeSettingFrameVisible()){
                    return false;
                }
            }
            if (doubleBackToExitPressedOnce) {
                // 두 번째 클릭 간격이 2초 이내인 경우 앱을 종료합니다.
                super.onBackPressed();
                return true;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, DOUBLE_BACK_TO_EXIT_INTERVAL);
            return true;
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

    public DeviceProfile getProfile() {
        return profile;
    }

    public void setProfile(DeviceProfile profile) {
        this.profile = profile;
    }
}