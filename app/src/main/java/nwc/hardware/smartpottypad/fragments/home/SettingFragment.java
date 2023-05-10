package nwc.hardware.smartpottypad.fragments.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.Bed;
import nwc.hardware.smartpottypad.datas.Room;
import nwc.hardware.smartpottypad.datas.User;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class SettingFragment extends Fragment {

    private LottieAnimationView HomeIntroLTV;
    private TextView settingDepartStepTXT;
    private EditText HomeDepartFloorTXT;

    private FrameLayout settingFrame;
    private LinearLayout setting1;
    private LinearLayout setting2;

    private HomeActivity parent;

    public SettingFragment(HomeActivity activity){
        parent = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        settingFrame = v.findViewById(R.id.settingFrame);

        HomeIntroLTV = v.findViewById(R.id.HomeIntroLTV);
        HomeIntroLTV.setAnimation(R.raw.check_yes_search);
        HomeIntroLTV.setRepeatMode(LottieDrawable.RESTART);
        HomeIntroLTV.setRepeatCount(LottieDrawable.INFINITE);
        HomeIntroLTV.playAnimation();

        settingDepartStepTXT = v.findViewById(R.id.settingDepartStepTXT);
        HomeDepartFloorTXT = v.findViewById(R.id.HomeDepartFloorTXT);
        HomeDepartFloorTXT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                User info = parent.getInfo();
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    settingDepartStepTXT.setText("유저 정보를 갱신할께요!");
                    DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference("users").child(SetPreferences.databaseKey);
                    info.setDepartFloor(Integer.parseInt(HomeDepartFloorTXT.getText().toString()));
                    userinfo.setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            settingDepartStepTXT.setText("저장이 성공적으로 완료 되었습니다!");
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    settingDepartStepTXT.setText("홈 화면으로 이동할께요!");
                                }
                            }, 1000);
                            handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    parent.changeFragment(HomeActivity.FRAGMENT_INFO);
                                }
                            }, 3000);

                            parent.setInfo(info);
                        }
                    });
                }
                return false;
            }
        });

        setting1 = v.findViewById(R.id.setting1);
        setting2 = v.findViewById(R.id.setting2);

        settingFrame.animate()
                .alpha(1f)
                .setDuration(3000)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        getInfo();
                    }
                })
                .start();

        return v;
    }

    public void getInfo(){
        DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference("users").child(SetPreferences.databaseKey);
        userinfo.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User info = task.getResult().getValue(User.class);
                List<Room> rooms = info.getRooms();
                int roomcnt = 0;
                int bedcnt = 0;
                for(Room r : rooms){
                    List<Bed> beds = r.getBeds();
                    for(Bed b : beds){
                        if(b.isAttach()){
                            parent.getProfile().setRoomindex("" + roomcnt);
                            parent.getProfile().setBedindex("" + bedcnt);
                        }
                        bedcnt++;
                    }
                    bedcnt = 0;
                    roomcnt++;
                }
                parent.setInfo(info);
                if(info.getDepartFloor() == -1){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setDepartFloor();
                        }
                    }, 1500);
                }else{
                    successLogin();
                }
            }
        });
    }

    public void setDepartFloor(){
        setting1.animate()
                .setDuration(1500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(0f)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        setting2.animate()
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(1500)
                                .alpha(1f)
                                .start();
                        HomeDepartFloorTXT.animate()
                                        .setStartDelay(2500)
                                        .withStartAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                HomeDepartFloorTXT.setVisibility(View.VISIBLE);
                                                HomeDepartFloorTXT.requestFocus();
                                                InputMethodManager imm = getContext().getSystemService(InputMethodManager.class);
                                                imm.showSoftInput(HomeDepartFloorTXT, InputMethodManager.SHOW_IMPLICIT);
                                            }
                                        })
                                        .start();
                    }
                })
                .start();
    }

    public void successLogin(){
        settingDepartStepTXT.setText("데이터를 읽어왔습니다!\n홈 화면으로 이동할께요!");
        setting1.animate()
                .setDuration(1500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(0f)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        setting2.animate()
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(1500)
                                .alpha(1f)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                parent.changeFragment(HomeActivity.FRAGMENT_INFO);
                                            }
                                        }, 1000);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }
}