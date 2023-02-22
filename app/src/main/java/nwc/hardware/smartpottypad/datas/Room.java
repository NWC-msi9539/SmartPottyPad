package nwc.hardware.smartpottypad.datas;

public class Room {
    /**
     * departFloor = 소속 층
     * index = 병실 호수
     * level = (0, 6인실) / (1, 4인실) / (2, 2인실) / (3, 1인실) / (4, 중환자실)
     */
    private int departFloor = 0;
    private int index = 1;
    private int level = 0;
    private int bedCount = 0;
}
