package nwc.hardware.smartpottypad.tasks;

import android.content.Context;
import android.content.SharedPreferences;

import nwc.hardware.smartpottypad.listeners.OnSavedEndListener;

public class SetPreferences {
    public static final byte[] ivBytes = { -11, -127, 33, 87, 99, 105, -85, -77, 12, -2, 99, 127, -123, 91, -65, 53 };
    public static String databaseKey = "";
    private static final String preferencesKey = "NWCPref_Potty";
    private static SetPreferences preferences;

    private boolean autoLogin;
    private String key;
    private String LoginInfo;
    private Context applicationContext;

    private OnSavedEndListener listener;

    private SetPreferences(Context applicationContext){
        this.applicationContext = applicationContext;
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        autoLogin = sharedPreferences.getBoolean("autoLogin", false);
        key = sharedPreferences.getString("key", "");
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

    public void save(){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoLogin", autoLogin);
        editor.putString("key", key);
        editor.putString("LoginInfo", LoginInfo);
        editor.apply();
        read();
        if(listener != null){
            listener.onSaved();
        }
    }

    public void reset(){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
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
    }

    public SetPreferences addEndTaskListener(OnSavedEndListener listener){
        this.listener = listener;
        return this;
    }
}
