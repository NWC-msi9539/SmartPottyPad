package nwc.hardware.smartpottypad.datas;

import androidx.annotation.Nullable;

public class WifiProfile {
    private String ssid = "";
    private String password = "";

    public WifiProfile(){}

    public WifiProfile(String ssid, String password) {
        this.ssid = ssid;
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WifiProfile wifiProfile = (WifiProfile) obj;
        return ssid.equals(wifiProfile.ssid) &&
                password.equals(wifiProfile.password);
    }
}
