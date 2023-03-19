package nwc.hardware.smartpottypad.fragments.home.detail;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.util.Set;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.DeviceProfile;
import nwc.hardware.smartpottypad.datas.WifiProfile;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class CheckWifiFragment extends Fragment {
    public static final int TYPE_TEXT_DISCONNECT = 0;
    public static final int TYPE_TEXT_DISABLE = 1;
    public static final int TYPE_TEXT_CONNECT = 2;
    public static final int TYPE_TEXT_SAVED = 3;

    public static WifiProfile wifiProfile;

    private LottieAnimationView Home_wifiLTV;
    private TextView Home_DetailinfoTXT;
    private TextView Home_FindAnotherWifiTXT;
    private Button Home_wifiYesBTN;
    private EditText Home_WifiPassTXT;
    private String infoText = "";
    private int infoType = -1;

    private HomeActivity parent;
    private String ssid;
    private String pass;

    public CheckWifiFragment(HomeActivity activity){
        parent = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_check_wifi, container, false);

        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!(wifiManager != null && wifiManager.isWifiEnabled())) { wifiManager.setWifiEnabled(true); }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        SetPreferences preferences = SetPreferences.getInstance();
        Set<String> ssids = preferences.getWifiSSID();
        Set<String> passes = preferences.getWifiPASS();
        if(ssids.size() > 0){
            wifiProfile = new WifiProfile(ssids.iterator().next(), passes.iterator().next());
        }

        if (wifiInfo != null) {
            if(wifiProfile == null){
                wifiProfile = new WifiProfile(wifiInfo.getSSID().replaceAll("\"", ""), "");
            }
            ssid = wifiProfile.getSsid();
            Log.d("WIFI_INFO", "SSID: " + ssid);
            if(!ssid.contains("unknown")){
                if(wifiProfile.getPassword() != null){
                    infoText =  "저장된 와이파이\n\"" + ssid + "\"\n위 와이파이의 정보를 사용할까요?";
                    infoType = TYPE_TEXT_SAVED;
                }else{
                    infoText =  "현재연결 와이파이\n\"" + ssid + "\"\n위 와이파이의 비밀번호를 적어주세요!";
                    infoType = TYPE_TEXT_CONNECT;
                }
            }else{
                parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_SCAN_WIFI);
            }
        } else {
            parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_SCAN_WIFI);
            Log.d("WIFI_INFO", "Not connected to a Wi-Fi network");
        }


        Home_wifiLTV = v.findViewById(R.id.Home_wifiLTV);
        Home_wifiLTV.setAnimation(R.raw.wifi_disconnected);
        Home_wifiLTV.setRepeatMode(LottieDrawable.RESTART);
        Home_wifiLTV.setRepeatCount(LottieDrawable.INFINITE);
        Home_wifiLTV.playAnimation();

        Home_DetailinfoTXT = v.findViewById(R.id.Home_DetailinfoTXT);
        Home_DetailinfoTXT.setText(infoText);

        Home_WifiPassTXT = v.findViewById(R.id.Home_WifiPassTXT);
        Home_WifiPassTXT.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Home_WifiPassTXT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){

                    if(Home_WifiPassTXT.getText().toString().isEmpty()){
                        pass = "";
                    }else{
                        pass = Home_WifiPassTXT.getText().toString();
                    }

                    wifiProfile.setPassword(pass);

                    Set<String> ssids = new ArraySet<>();
                    Set<String> passes = new ArraySet<>();

                    ssids.add(ssid);
                    passes.add(pass);

                    preferences.setWifiSSID(ssids);
                    preferences.setWifiPASS(passes);
                    preferences.save();

                    DeviceProfile profile = parent.getProfile();
                    profile.setWifiProfile(wifiProfile);

                    wifiProfile = null;

                    parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_SCAN_BLUETOOTH);
                }
                return false;
            }
        });

        Home_wifiYesBTN = v.findViewById(R.id.Home_wifiYesBTN);
        Home_wifiYesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceProfile profile = parent.getProfile();
                profile.setWifiProfile(wifiProfile);

                parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_SCAN_BLUETOOTH);

                wifiProfile = null;
            }
        });

        Home_FindAnotherWifiTXT = v.findViewById(R.id.Home_FindAnotherWifiTXT);
        Home_FindAnotherWifiTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_SCAN_WIFI);
            }
        });

        if(infoType == TYPE_TEXT_CONNECT){
            Log.d(TAG,"IN Connect Type !");
            Home_FindAnotherWifiTXT.setVisibility(View.VISIBLE);
            Home_wifiYesBTN.setVisibility(View.GONE);

            InputMethodManager imm = getContext().getSystemService(InputMethodManager.class);
            Home_WifiPassTXT.setVisibility(View.VISIBLE);
            Home_WifiPassTXT.requestFocus();
            imm.showSoftInput(Home_WifiPassTXT, InputMethodManager.SHOW_FORCED);
        }else if(infoType == TYPE_TEXT_SAVED){
            Log.d(TAG,"IN SAVED Type !");

            Home_FindAnotherWifiTXT.setVisibility(View.VISIBLE);
            Home_WifiPassTXT.setVisibility(View.GONE);
            Home_wifiYesBTN.setVisibility(View.VISIBLE);
        }else{
            Log.d(TAG,"IN ELSE Type !");
            Home_wifiYesBTN.setVisibility(View.GONE);
            Home_WifiPassTXT.setVisibility(View.GONE);
            Home_FindAnotherWifiTXT.setVisibility(View.GONE);
        }
        
        return v;
    }

    @Override
    public void onDestroy() {
        wifiProfile = null;
        super.onDestroy();
    }
}