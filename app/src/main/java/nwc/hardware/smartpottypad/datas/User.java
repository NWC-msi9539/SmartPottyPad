package nwc.hardware.smartpottypad.datas;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String ID = "";
    private String PIN = "";
    private int departFloor = -1;
    private int roomCount = 0;
    private List<Room> rooms = new ArrayList<>();

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public int getDepartFloor() {
        return departFloor;
    }

    public void setDepartFloor(int departFloor) {
        this.departFloor = departFloor;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}
