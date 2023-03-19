package nwc.hardware.smartpottypad.datas;

public class DeviceProfile {
    private WifiProfile wifiProfile = new WifiProfile();
    private String userkey = "";
    private String roomindex = "";
    private String bedindex = "";

    public WifiProfile getWifiProfile() {
        return wifiProfile;
    }

    public void setWifiProfile(WifiProfile wifiProfile) {
        this.wifiProfile = wifiProfile;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getRoomindex() {
        return roomindex;
    }

    public void setRoomindex(String roomindex) {
        this.roomindex = roomindex;
    }

    public String getBedindex() {
        return bedindex;
    }

    public void setBedindex(String bedindex) {
        this.bedindex = bedindex;
    }
}
