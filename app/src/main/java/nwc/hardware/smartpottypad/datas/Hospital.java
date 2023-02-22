package nwc.hardware.smartpottypad.datas;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

public class Hospital {
    private String[] names = { "김민성" , "김상제" , "김경민", "김선중" , "김나현" , "강호철" };
    private String[] disease = { "바보", "멍청이" , "똥바보" , "말미잘" , "해삼" , "킹받음" };
    private static final String TAG = "datas.Hospital";

    public static final int DB_ERROR_UNKNOWN = -1;
    public static final int DB_SUCCESS = 1;

    /**
     * name = 필수
     * address = 선택
     * totalFloorCount = 필수
     */
    String name = "unknown";
    String address = "unknown";
    int totalFloorCount = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalFloorCount() {
        return totalFloorCount;
    }

    public void setTotalFloorCount(int totalFloorCount) {
        this.totalFloorCount = totalFloorCount;
    }

    public int putDB(){
        if(name.equals("unknown")){
            return -1;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("address", address);
        data.put("totalfloorcount", totalFloorCount);

        DataControl dataControl = DataControl.getInstance();
        dataControl.putDB("hospital", data, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });

        return 1;
    }
}
