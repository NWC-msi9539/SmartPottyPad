package nwc.hardware.smartpottypad.datas;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Room {
    /**
     * departFloor = 소속 층
     * index = 병실 호수
     */
    private int index = -1;
    private List<Bed> beds = new ArrayList<>();

    public Room(){}

    public Room(int index){
        setIndex(index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Bed> getBeds() {
        return beds;
    }

    public void setBeds(List<Bed> beds) {
        this.beds = beds;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Room room = (Room) obj;

        return index == room.getIndex() &&
                beds.equals(room.getBeds());
    }
}
