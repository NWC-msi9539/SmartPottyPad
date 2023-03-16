package nwc.hardware.smartpottypad.services;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.smartpottypad.datas.Bed;
import nwc.hardware.smartpottypad.datas.Room;
import nwc.hardware.smartpottypad.datas.User;
import nwc.hardware.smartpottypad.fragments.home.HomeFragment;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class UpdateService extends Service {
    private DatabaseReference infos = FirebaseDatabase.getInstance().getReference("users").child(SetPreferences.databaseKey);
    private DatabaseReference dataBaseRooms = FirebaseDatabase.getInstance().getReference("users").child(SetPreferences.databaseKey).child("rooms");

    private Timer timer;
    private TimerTask timerTask;

    private User LastSaveinfo = new User();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        return START_STICKY;
    }

    public void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(!LastSaveinfo.equals(HomeFragment.info)){
                    Log.d(TAG, "Updating DB!!");
                    infos.setValue(HomeFragment.info);
                    copyInfo();
                }
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void copyInfo(){
        List<Room> rooms = new ArrayList<>();
        List<Room> Origin = HomeFragment.info.getRooms();
        for(Room r : Origin){
            List<Bed> beds = new ArrayList<>();
            List<Bed> Originbeds = r.getBeds();

            for(Bed b : Originbeds){
                beds.add(b);
            }

            Room room = new Room();
            room.setIndex(r.getIndex());
            room.setBeds(beds);
            rooms.add(room);
        }
        LastSaveinfo.setID(HomeFragment.info.getID());
        LastSaveinfo.setPIN(HomeFragment.info.getPIN());
        LastSaveinfo.setDepartFloor(HomeFragment.info.getDepartFloor());
        LastSaveinfo.setRooms(rooms);
        LastSaveinfo.setRoomCount(HomeFragment.info.getRoomCount());
    }

    public void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}