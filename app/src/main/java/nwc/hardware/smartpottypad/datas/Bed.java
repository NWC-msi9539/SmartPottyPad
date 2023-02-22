package nwc.hardware.smartpottypad.datas;

public class Bed {
    public final static int TYPE_DISCONNECTION = -1;
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_NEED = 1;
    public final static int TYPE_MUST = 2;

    /**
     * alertType = (-1, 디바이스 미연결) , (0, 이상없음) , (1, 교체 필요) , (2, 교체 시급)
     */
    private String name = "unknown";
    private String ID = "";
    private long firstUseTime = 0;
    private long lastChangeTime = 0;
    private int pooCount = 0;
    private int changeCount = 0;
    private int alertType = TYPE_DISCONNECTION;
}
