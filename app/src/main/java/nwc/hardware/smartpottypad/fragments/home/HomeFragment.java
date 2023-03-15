package nwc.hardware.smartpottypad.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.adapters.HomeRoomAdapter;
import nwc.hardware.smartpottypad.datas.Room;
import nwc.hardware.smartpottypad.datas.User;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";

    private List<Room> rooms = new ArrayList<>();

    private TextView departFloorTXT;

    private RecyclerView roomRCC;
    private ImageButton addBTN;
    private HomeRoomAdapter adapter;

    private HomeActivity parent;
    private User info;

    private DatabaseReference infos = FirebaseDatabase.getInstance().getReference("users").child(SetPreferences.databaseKey);

    private Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            infos.child("roomCount").setValue(info.getRoomCount());

        }
    };

    public HomeFragment(HomeActivity activity){
        parent = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        info = parent.getInfo();

        adapter = new HomeRoomAdapter(rooms, getContext(), info.getDepartFloor());

        departFloorTXT = v.findViewById(R.id.departFloorTXT);
        departFloorTXT.setText(info.getDepartFloor() + "병동");

        roomRCC = v.findViewById(R.id.home_roomRCC);
        roomRCC.setHasFixedSize(true);
        roomRCC.setLayoutManager(new LinearLayoutManager(getContext()));
        roomRCC.setAdapter(adapter);

        addBTN = v.findViewById(R.id.home_addRoomBTN);
        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setRoomCount(info.getRoomCount() + 1);
                adapter.addRoom(new Room(adapter.getItemCount() + 1));
                Log.d(TAG, "Click adapter! Size --> " + adapter.getItemCount());
            }
        });


        timer.schedule(timerTask, 0, 2000);

        return v;
    }
}