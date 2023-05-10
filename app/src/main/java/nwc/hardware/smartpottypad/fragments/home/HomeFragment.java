package nwc.hardware.smartpottypad.fragments.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.activities.IntroActivity;
import nwc.hardware.smartpottypad.adapters.HomeRoomAdapter;
import nwc.hardware.smartpottypad.callback.SwipeToDeleteCallback;
import nwc.hardware.smartpottypad.datas.Bed;
import nwc.hardware.smartpottypad.datas.Room;
import nwc.hardware.smartpottypad.datas.User;
import nwc.hardware.smartpottypad.services.UpdateService;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";

    public static List<Room> rooms = new ArrayList<>();
    public static User info;

    private TextView departFloorTXT;
    private TextView uidTXT;
    private Button HomeLogoutBTN;
    private ImageButton HomedeleteBTN;
    private ImageButton HomeSettingBTN;
    private ImageButton HomeSettingCloseBTN;

    private RecyclerView roomRCC;
    private ImageButton addBTN;
    private HomeRoomAdapter adapter;

    private HomeActivity parent;
    private Intent intent;
    private FrameLayout HomeDetailFrame;
    private FrameLayout HomeSettingFrame;

    private LinearLayout total_alertContainer;

    private ItemTouchHelper itemTouchHelper;
    private SwipeToDeleteCallback callback;

    private ViewPropertyAnimator closeSettingFrameVisibleAnimator;
    private ViewPropertyAnimator closeSettingFrameGoneAnimator;

    Map<String, Chip> addChipList = new HashMap<>();

    public HomeFragment(HomeActivity activity){
        parent = activity;
    }

    public boolean closeSettingFrameVisible(){
        if(HomeSettingFrame.getVisibility() != View.GONE){
            if(closeSettingFrameGoneAnimator == null){
                closeSettingFrameGoneAnimator = HomeSettingFrame.animate()
                        .setDuration(1000)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .translationX(2000f)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                if(HomeSettingFrame.getVisibility() == View.VISIBLE){
                                    HomeSettingFrame.setVisibility(View.GONE);
                                    closeSettingFrameGoneAnimator = null;
                                }
                            }
                        });
                closeSettingFrameGoneAnimator.start();
            }

            return true;
        }else{
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        HomeDetailFrame = v.findViewById(R.id.HomeDetailFrame);
        HomeSettingFrame = v.findViewById(R.id.HomeSettingFrame);
        HomeSettingFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        total_alertContainer = v.findViewById(R.id.total_alertContainer);

        intent = new Intent(getContext(), UpdateService.class);
        info = parent.getInfo();
        rooms = info.getRooms();

        roomRCC = v.findViewById(R.id.home_roomRCC);

        Log.d(TAG, "Info Rooms Size --> " + info.getRooms().size());
        adapter = new HomeRoomAdapter(rooms, getContext(), info.getDepartFloor(), parent, roomRCC);

        departFloorTXT = v.findViewById(R.id.departFloorTXT);
        departFloorTXT.setText(info.getDepartFloor() + "병동");


        roomRCC.setHasFixedSize(true);
        roomRCC.setLayoutManager(new LinearLayoutManager(getContext()));
        roomRCC.setAdapter(adapter);

        callback = new SwipeToDeleteCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(roomRCC);

        uidTXT = v.findViewById(R.id.uidTXT);
        uidTXT.setText(SetPreferences.databaseKey);

        DatabaseReference roomInfoRef = FirebaseDatabase.getInstance().getReference("users/" + SetPreferences.databaseKey + "/rooms");
        roomInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot roomSnapshot: snapshot.getChildren()) {
                    // 이제 각각의 방을 순회합니다.
                    for(DataSnapshot bedSnapshot : roomSnapshot.child("beds").getChildren()) {
                        // 각각의 침대를 순회합니다.
                        Bed bed = bedSnapshot.getValue(Bed.class);
                        if(bed != null && (bed.getAlertType() == 1 || bed.getAlertType() == 2)) {
                            String key = (info.getDepartFloor() * 100 + (bed.getDepartRoom() + 1) + " - " + (bed.getIndex()));
                            Log.w(TAG, "TEST : " + bed.getName());
                            if(!addChipList.containsKey(key)){
                                Chip chip = new Chip(getContext());

                                chip.setText(key);
                                chip.setCheckable(false); // 필요에 따라 설정

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                int margin = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        4,
                                        getResources().getDisplayMetrics()
                                );
                                layoutParams.setMargins(margin, 0, margin, 0);

                                chip.setLayoutParams(layoutParams);
                                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.jamsil_2_light);

                                chip.setTypeface(typeface);

                                ColorStateList colorStateList;
                                if (bed.getAlertType() == 1) {
                                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FFFEB300"));
                                } else {
                                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FFE1574C"));
                                }
                                chip.setChipBackgroundColor(colorStateList);

                                chip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        adapter.scrollAlertElement(bed.getDepartRoom(), bed.getIndex() - 1);
                                    }
                                });

                                addChipList.put(key, chip);
                                total_alertContainer.addView(chip);
                            } else {
                                Chip chip = addChipList.get(key);

                                ColorStateList colorStateList;
                                if (bed.getAlertType() == 1) {
                                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FFFEB300"));
                                } else {
                                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FFE1574C"));
                                }
                                chip.setChipBackgroundColor(colorStateList);

                                addChipList.put(key, chip);
                            }
                        } else {
                            if(bed == null){
                                return;
                            }
                            String key = (info.getDepartFloor() * 100 + (bed.getDepartRoom() + 1) + " - " + (bed.getIndex()));
                            if(addChipList.containsKey(key)){
                                total_alertContainer.removeView(addChipList.get(key));
                                addChipList.remove(key);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 오류 처리를 여기에 작성합니다.
            }
        });

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

        HomeSettingBTN = v.findViewById(R.id.home_settingBTN);
        HomeSettingBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HomeSettingFrame.getVisibility() == View.GONE){
                    HomeSettingFrame.setVisibility(View.VISIBLE);
                    closeSettingFrameVisibleAnimator = HomeSettingFrame.animate()
                            .setDuration(1000)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    closeSettingFrameVisibleAnimator = null;
                                }
                            })
                            .translationX(0f);

                    closeSettingFrameVisibleAnimator.start();
                }
            }
        });

        HomeSettingCloseBTN = v.findViewById(R.id.home_setting_closeBTN);
        HomeSettingCloseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HomeSettingFrame.getVisibility() == View.VISIBLE){
                    if(closeSettingFrameGoneAnimator == null){
                        closeSettingFrameGoneAnimator = HomeSettingFrame.animate()
                                .setDuration(1000)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .translationX(2000f)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(HomeSettingFrame.getVisibility() == View.VISIBLE){
                                            HomeSettingFrame.setVisibility(View.GONE);
                                            closeSettingFrameGoneAnimator = null;
                                        }
                                    }
                                });
                    }
                    closeSettingFrameGoneAnimator.start();
                }
            }
        });

        HomeLogoutBTN = v.findViewById(R.id.home_logoutBTN);
        HomeLogoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPreferences setPreferences = SetPreferences.getInstance();
                setPreferences.reset();
                Intent intent = new Intent(getContext(), IntroActivity.class);
                startActivity(intent);
                parent.finish();
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