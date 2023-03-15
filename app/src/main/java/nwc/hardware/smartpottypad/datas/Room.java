package nwc.hardware.smartpottypad.datas;

import java.util.ArrayList;
import java.util.List;

public class Room {
    /**
     * departFloor = 소속 층
     * index = 병실 호수
     */
    private int index = -1;
    private List<Bed> beds = new ArrayList<>();

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
}
