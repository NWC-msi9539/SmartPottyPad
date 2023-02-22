package nwc.hardware.smartpottypad.datas;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class DataControl {
    private static DataControl dataControl;
    private FirebaseFirestore DB;

    private DataControl(){
        DB = FirebaseFirestore.getInstance();
    }

    public static DataControl getInstance(){
        if(dataControl == null){
            dataControl = new DataControl();
        }
        return dataControl;
    }

    public void putDB(String Collection, String Document, Map<String, Object> data, OnSuccessListener successListener, OnFailureListener failureListener, boolean isMerge) {
        if(isMerge){
            DB.collection(Collection)
                    .document(Document)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener);
        }else{
            DB.collection(Collection)
                    .document(Document)
                    .set(data)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener);
        }
    }

    public void putDB(String Collection, Map<String, Object> data, OnSuccessListener successListener, OnFailureListener failureListener) {
        DB.collection(Collection)
                .add(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public Map<String, Object> getDB(){
        return null;
    }
}
