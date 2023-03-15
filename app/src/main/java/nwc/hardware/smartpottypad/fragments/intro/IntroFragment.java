package nwc.hardware.smartpottypad.fragments.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;
import java.util.Map;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.activities.IntroActivity;
import nwc.hardware.smartpottypad.tasks.LoginCheckTask;
import nwc.hardware.smartpottypad.tasks.SetPreferences;
import nwc.hardware.smartpottypad.utils.CryptoUtil;

public class IntroFragment extends Fragment {
    private final String TAG = "IntroFragment";

    private IntroActivity parent;

    private ImageView logo;
    private TextView status;
    private LoginCheckTask task;
    private String ID = "";
    private String PIN = "";
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    public IntroFragment(IntroActivity introActivity){
        parent = introActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro, container, false);
        logo = v.findViewById(R.id.logoIMG);
        status = v.findViewById(R.id.Intro_statusTXT);

        task  = new LoginCheckTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                SetPreferences setPreferences = SetPreferences.getInstance();
                String loginInfo = setPreferences.getLoginInfo();
                if(!loginInfo.isEmpty()){
                    String data = CryptoUtil.DecryptText(Base64.decode(loginInfo, Base64.DEFAULT));

                    String[] datas = data.split(" ");
                    ID = datas[0];
                    PIN = datas[1];

                    Log.d(TAG, "ID --> " + ID + ", PASS --> " + PIN +  ", AUTOLOGIN --> " + setPreferences.isAutoLogin());
                }else{
                    Log.d(TAG, "Preference is NULL!!!!");
                }

                return setPreferences.isAutoLogin();
            }

            @Override
            protected void onPostExecute(Boolean unused) {
                super.onPostExecute(unused);
                if(unused){
                    status.setText("로그인 정보를 확인했어요! 연결 중 입니다!");
                    users.orderByChild("id").equalTo(ID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Map<String, Map<String, String>> value = (Map<String, Map<String, String>>) task.getResult().getValue();

                            if(value != null){
                                Log.d(TAG, "EXIST --> " + value);
                                SetPreferences.databaseKey = (String)(((Map<?, ?>) task.getResult().getValue()).keySet().iterator().next());
                                Log.d(TAG, "EXIST Key --> " + SetPreferences.databaseKey);
                                Iterator<Map<String, String>> iterator = value.values().iterator();
                                Map<String, String> userData = iterator.next();
                                Log.d(TAG, "USERDATA --> ID : " + userData.get("ID") + ", PIN : " + userData.get("PIN"));
                                if(userData.get("pin").equals(PIN)){
                                    status.setText("로그인에 성공했습니다.");

                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(getContext(), HomeActivity.class);
                                            startActivity(intent);
                                            parent.finish();
                                        }
                                    }, 2500);
                                }else{
                                    status.setText("저장된 로그인 정보가 올바르지 않습니다.\n로그인 화면으로 이동할께요!");
                                    SetPreferences.getInstance().reset();
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            parent.changeFragment(IntroActivity.FRAGMENT_LOGIN);
                                        }
                                    }, 2500);
                                }
                            }else{
                                status.setText("저장된 로그인 정보가 올바르지 않습니다.\n로그인 화면으로 이동할께요!");
                                SetPreferences.getInstance().reset();
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        parent.changeFragment(IntroActivity.FRAGMENT_LOGIN);
                                    }
                                }, 2500);
                            }
                        }
                    });
                }else{
                    status.setText("로그인 화면으로 이동할께요!");
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            parent.changeFragment(IntroActivity.FRAGMENT_LOGIN);
                        }
                    }, 2500);
                }
            }
        };

        Handler Logohandler = new Handler(Looper.getMainLooper());
        Logohandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.animate()
                        .alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(3000)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                status.setText("로그인 정보를 확인중이에요!");
                                task.execute();
                            }
                        })
                        .start();
            }
        }, 1200);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}