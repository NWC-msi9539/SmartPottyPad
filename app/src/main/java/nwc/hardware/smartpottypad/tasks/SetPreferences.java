package nwc.hardware.smartpottypad.tasks;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

import nwc.hardware.smartpottypad.listeners.OnSavedEndListener;

public class SetPreferences {
    public static final byte[] ivBytes = { -11, -127, 33, 87, 99, 105, -85, -77, 12, -2, 99, 127, -123, 91, -65, 53 };
    public static String databaseKey = "";
    private static final String preferencesKey = "NWCPref_Potty";
    private static SetPreferences preferences;

    private boolean autoLogin;
    private String key;
    private String LoginInfo;
    private Set<String> wifiSSID;
    private Set<String> wifiPASS;
    private Context applicationContext;


    private OnSavedEndListener listener;

    private SetPreferences(Context applicationContext){
        this.applicationContext = applicationContext;
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        autoLogin = sharedPreferences.getBoolean("autoLogin", false);
        key = sharedPreferences.getString("key", "");
        wifiSSID = sharedPreferences.getStringSet("wifiSSID", new ArraySet<>());
        wifiPASS = sharedPreferences.getStringSet("wifiPASS", new ArraySet<>());
        LoginInfo = sharedPreferences.getString("LoginInfo", "");
    }

    public static SetPreferences getInstance(Context applicationContext){
        if(preferences == null){
            preferences = new SetPreferences(applicationContext);
        }
        return preferences;
    }

    public static SetPreferences getInstance(){
        return preferences;
    }

    public SetPreferences setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
        return this;
    }

    public SetPreferences setKey(String key) {
        this.key = key;
        return this;
    }

    public SetPreferences setLoginInfo(String loginInfo) {
        LoginInfo = loginInfo;
        return this;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public String getKey() {
        return key;
    }

    public String getLoginInfo() {
        return LoginInfo;
    }

    public Set<String> getWifiSSID() {
        return wifiSSID;
    }

    public void setWifiSSID(Set<String> wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    public Set<String> getWifiPASS() {
        return wifiPASS;
    }

    public void setWifiPASS(Set<String> wifiPASS) {
        this.wifiPASS = wifiPASS;
    }

    public void save(){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("wifiSSID", wifiSSID);
        editor.putStringSet("wifiPASS", wifiPASS);
        editor.putBoolean("autoLogin", autoLogin);
        editor.putString("key", key);
        editor.putString("LoginInfo", LoginInfo);
        editor.apply();
        read();
        if(listener != null){
            listener.onSaved();
        }
        if(wifiSSID.size() != 0) {
            Iterator<String> ssid_iterator = wifiSSID.iterator();
            Iterator<String> pass_iterator = wifiPASS.iterator();
            String ssid = ssid_iterator.next();
            String pass = pass_iterator.next();
            Log.d(TAG, "save! ssid --> " + ssid);
            Log.d(TAG, "save! pass --> " + pass);
        }
    }

    public void reset(){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("wifiSSID", new ArraySet<>());
        editor.putStringSet("wifiPASS", new ArraySet<>());
        editor.putBoolean("autoLogin", false);
        editor.putString("key", "");
        editor.putString("LoginInfo", "");
        editor.apply();
        read();
    }

    private void read(){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        autoLogin = sharedPreferences.getBoolean("autoLogin", false);
        key = sharedPreferences.getString("key", "");
        LoginInfo = sharedPreferences.getString("LoginInfo", "");
        wifiSSID = sharedPreferences.getStringSet("wifiSSID", new ArraySet<>());
        wifiPASS = sharedPreferences.getStringSet("wifiPASS", new ArraySet<>());
    }

    public SetPreferences addEndTaskListener(OnSavedEndListener listener){
        this.listener = listener;
        return this;
    }
}
