package nwc.hardware.smartpottypad.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.Bed;
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
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, settingFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_INFO){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, homeFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_DETAIL_WIFI){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.HomeDetailFrame, home_checkWifiFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_DETAIL_SCAN_WIFI){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.HomeDetailFrame, searchingWifiFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_DETAIL_SCAN_BLUETOOTH){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.HomeDetailFrame, searchingBluetoothFragment).commitAllowingStateLoss();
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

    public DeviceProfile getProfile() {
        return profile;
    }

    public void setProfile(DeviceProfile profile) {
        this.profile = profile;
    }

    public void addAttachReference(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                .child(profile.getUserkey())
                .child("rooms")
                .child(profile.getRoomindex())
                .child("beds")
                .child(profile.getBedindex());

        ValueEventListener degreelistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Degree Changed!");
                double degree = (double) snapshot.getValue();

                Bed bed = info.getRooms().get(Integer.parseInt(profile.getRoomindex()))
                        .getBeds().get(Integer.parseInt(profile.getBedindex()));
                bed.setDegree((float)degree);

                if(bed.getAlertType() == Bed.TYPE_DISCONNECTION){
                    bed.setAlertType(Bed.TYPE_NORMAL);
                    reference.setValue(bed);
                    homeFragment.repaintAdapter();
                }else if(bed.getAlertType() == Bed.TYPE_NORMAL){
                    Log.d(TAG, "Degree is NORMAL!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.child("attach").setValue(true);
        ValueEventListener attachlistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Attach Changed!");
                boolean isAttach = (boolean)snapshot.getValue();
                Bed bed = info.getRooms().get(Integer.parseInt(profile.getRoomindex()))
                        .getBeds().get(Integer.parseInt(profile.getBedindex()));
                bed.setAttach(isAttach);
                if(!isAttach){
                    reference.child("degree").removeEventListener(degreeAttaches.get(reference));
                    reference.child("attach").removeEventListener(Attaches.get(reference));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        reference.child("degree").addValueEventListener(degreelistener);
        reference.child("attach").addValueEventListener(attachlistener);

        degreeAttaches.put(reference, degreelistener);
        Attaches.put(reference, attachlistener);
    }
}