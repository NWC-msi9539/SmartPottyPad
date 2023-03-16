package nwc.hardware.smartpottypad.datas;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class User {
    private String ID = "";
    private String PIN = "";
    private int departFloor = -1;
    private int roomCount = 0;
    private List<Room> rooms = new ArrayList<Room>();

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User info = (User) obj;
        List<Room> CompareRooms = info.getRooms();

        int cnt = 0;
        int Bedcnt = 0;
        for(Room r : CompareRooms){
            try {
                if (r.equals(rooms.get(cnt))) {
                    List<Bed> beds = r.getBeds();
                    List<Bed> Originbeds = rooms.get(cnt++).getBeds();

                    if (beds.equals(Originbeds)) {
                        for (Bed b : beds) {
                            if (!b.equals(Originbeds.get(Bedcnt++))) {
                                return false;
                            }
                        }
                        Bedcnt = 0;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
        return ID.equals(info.getID()) &&
                PIN.equals(info.getPIN()) &&
                departFloor == info.getDepartFloor() &&
                roomCount == info.getRoomCount() &&
                rooms.equals(info.getRooms());
    }
}
