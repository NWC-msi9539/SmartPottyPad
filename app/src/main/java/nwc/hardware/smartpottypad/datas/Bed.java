package nwc.hardware.smartpottypad.datas;

import com.google.firebase.database.annotations.Nullable;

import nwc.hardware.smartpottypad.R;

public class Bed {
    public final static int TYPE_ADD = -2;
    public final static int TYPE_DISCONNECTION = -1;
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_NEED = 1;
    public final static int TYPE_MUST = 2;

    /**
     * alertType = (-1, 디바이스 미연결) , (0, 이상없음) , (1, 교체 필요) , (2, 교체 시급)
     */
    private String name = "unknown";
    private int departRoom = -1;
    private int index = 1;
    private long firstUseTime = 0;
    private long lastChangeTime = 0;
    private long enCountTime = 0;
    private int pooCount = 0;
    private int changeCount = 0;
    private float degree = -999.9f;
    private int alertType = TYPE_DISCONNECTION;
    private boolean isAttach = false;

    public Bed(){}

    public Bed(int index, int departRoom){
        setAlertType(Bed.TYPE_DISCONNECTION);
        setChangeCount(0);
        setFirstUseTime(-1);
        setLastChangeTime(-1);
        setEnCountTime(-1);
        setIndex(index);
        setName(getIndex() + "번침대");
        setPooCount(0);
        setDepartRoom(departRoom);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFirstUseTime() {
        return firstUseTime;
    }

    public void setFirstUseTime(long firstUseTime) {
        this.firstUseTime = firstUseTime;
    }

    public long getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(long lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public int getPooCount() {
        return pooCount;
    }

    public void setPooCount(int pooCount) {
        this.pooCount = pooCount;
    }

    public int getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
    }

    public int getAlertType() {
        return alertType;
    }

    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAttach() {
        return isAttach;
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public long getEnCountTime() {
        return enCountTime;
    }

    public void setEnCountTime(long enCountTime) {
        this.enCountTime = enCountTime;
    }

    public String getTypeToString(int type){
        String value = "";
        switch (type){
            case TYPE_DISCONNECTION:
                value = "연결 안 됌";
                break;
            case TYPE_NORMAL:
                value = "이상 없음";
                break;
            case TYPE_NEED:
                value = "교체 필요";
                break;
            case TYPE_MUST:
                value = "교체 시급";
                break;
        }
        return value;
    }

    public int getTypeToDrawbleID(int type){
        int value = -1;
        switch (type){
            case TYPE_ADD:
                value = R.drawable.add;
                break;
            case TYPE_DISCONNECTION:
                value = R.drawable.unlink;
                break;
            case TYPE_NORMAL:
                value = R.drawable.happy_face;
                break;
            case TYPE_NEED:
                value = R.drawable.caution;
                break;
            case TYPE_MUST:
                value = R.drawable.exclamation_mark;
                break;
        }
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bed bed = (Bed) obj;

        return java.util.Objects.equals(name, bed.getName()) &&
                departRoom == bed.getDepartRoom() &&
                index == bed.getIndex() &&
                firstUseTime == bed.getFirstUseTime() &&
                lastChangeTime == bed.getLastChangeTime() &&
                enCountTime == bed.getEnCountTime() &&
                pooCount == bed.getPooCount() &&
                changeCount == bed.getChangeCount() &&
                alertType == bed.getAlertType() &&
                Float.compare(bed.getDegree(), degree) == 0 &&
                isAttach == bed.isAttach();
    }

    public int getDepartRoom() {
        return departRoom;
    }

    public void setDepartRoom(int departRoom) {
        this.departRoom = departRoom;
    }
}
