package nwc.hardware.smartpottypad.fragments.home.detail;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.WifiProfile;

public class Home_checkWifiFragment extends Fragment {
    public static final int TYPE_TEXT_DISCONNECT = 0;
    public static final int TYPE_TEXT_DISABLE = 1;
    public static final int TYPE_TEXT_CONNECT = 2;

    private LottieAnimationView Home_wifiLTV;
    private TextView Home_DetailinfoTXT;
    private EditText Home_WifiPassTXT;
    private String infoText = "";
    private int infoType = -1;

    private HomeActivity parent;
    private String ssid;
    private String pass;

    public Home_checkWifiFragment(HomeActivity activity){
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
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID();
            Log.d("WIFI_INFO", "SSID: " + ssid);

            infoText =  "현재연결 와이파이\n" + ssid + "\n위 와이파이의 비밀번호를 적어주세요!";
            infoType = TYPE_TEXT_CONNECT;
        } else {
            infoText = "연결 된 와이파이가 없어요.\n와이파이부터 연결해주세요!";
            infoType = TYPE_TEXT_DISCONNECT;
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
                    parent.setWifiProfile(new WifiProfile(ssid, pass));
                }
                return false;
            }
        });

        if(infoType == TYPE_TEXT_CONNECT){
            InputMethodManager imm = getContext().getSystemService(InputMethodManager.class);
            Home_WifiPassTXT.setVisibility(View.VISIBLE);
            Home_WifiPassTXT.requestFocus();
            imm.showSoftInput(Home_WifiPassTXT, InputMethodManager.SHOW_IMPLICIT);
        }else{
            Home_WifiPassTXT.setVisibility(View.GONE);
        }

        return v;
    }
}