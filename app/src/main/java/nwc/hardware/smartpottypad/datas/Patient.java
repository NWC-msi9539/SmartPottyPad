package nwc.hardware.smartpottypad.datas;

import java.util.HashMap;
import java.util.Map;

public class Patient {
    private String name = "unknown";
    private String ID = "";
    private long firstUseTime = 0;
    private long lastChangeTime = 0;
    private int totalPooCount = 0;
    private int totalChangeCount = 0;
    private Map<String, Object> log = new HashMap<>();

}
