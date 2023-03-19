package nwc.hardware.smartpottypad.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.adapters.HomeRoomAdapter;
import nwc.hardware.smartpottypad.callback.SwipeToDeleteCallback;
import nwc.hardware.smartpottypad.datas.Room;
import nwc.hardware.smartpottypad.datas.User;
import nwc.hardware.smartpottypad.services.UpdateService;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";

    public static List<Room> rooms = new ArrayList<>();
    public static User info;

    private TextView departFloorTXT;
    private ImageButton HomedeleteBTN;

    private RecyclerView roomRCC;
    private ImageButton addBTN;
    private HomeRoomAdapter adapter;

    private HomeActivity parent;
    private Intent intent;
    private FrameLayout HomeDetailFrame;

    private ItemTouchHelper itemTouchHelper;
    private SwipeToDeleteCallback callback;

    public HomeFragment(HomeActivity activity){
        parent = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        HomeDetailFrame = v.findViewById(R.id.HomeDetailFrame);

        intent = new Intent(getContext(), UpdateService.class);
        info = parent.getInfo();
        rooms = info.getRooms();

        Log.d(TAG, "Info Rooms Size --> " + info.getRooms().size());
        adapter = new HomeRoomAdapter(rooms, getContext(), info.getDepartFloor(), parent);

        departFloorTXT = v.findViewById(R.id.departFloorTXT);
        departFloorTXT.setText(info.getDepartFloor() + "병동");

        roomRCC = v.findViewById(R.id.home_roomRCC);
        roomRCC.setHasFixedSize(true);
        roomRCC.setLayoutManager(new LinearLayoutManager(getContext()));
        roomRCC.setAdapter(adapter);

        callback = new SwipeToDeleteCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(roomRCC);

        HomedeleteBTN = v.findViewById(R.id.HomedeleteBTN);
        HomedeleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!adapter.isAnim()) {
                    addBTN.animate()
                            .setDuration(250)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .rotation(-45f)
                            .start();
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                    vibrator.vibrate(250);
                    callback.setEnabled(true);
                    adapter.onAnim();
                }else{
                    addBTN.animate()
                            .setDuration(250)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .rotation(0f)
                            .start();
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                    vibrator.vibrate(250);
                    callback.setEnabled(false);
                    adapter.offAnim();
                }
            }
        });

        addBTN = v.findViewById(R.id.home_addRoomBTN);
        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!adapter.isAnim()){
                    Room room = new Room(adapter.getItemCount() + 1);
                    info.setRoomCount(info.getRoomCount() + 1);
                    Log.d(TAG, "Click adapter! Add Index --> " + (adapter.getItemCount() + 1));
                    adapter.addRoom(room);
                }else{
                    addBTN.animate()
                            .setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .rotation(0f)
                            .start();
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                    vibrator.vibrate(250);
                    callback.setEnabled(false);
                    adapter.offAnim();
                }
            }
        });

        parent.startService(intent);

        return v;
    }

    public void repaintAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(intent != null){
            parent.stopService(intent);
            intent = null;
        }
    }
}