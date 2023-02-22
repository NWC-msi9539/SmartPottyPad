package nwc.hardware.smartpottypad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.Display;
import nwc.hardware.smartpottypad.tasks.LoginCheckTask;

public class IntroActivity extends AppCompatActivity {
    private final String TAG = "IntroActivity";

    private ImageView logo;
    private TextView status;
    private LoginCheckTask task = new LoginCheckTask() {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                File env = new File(getCacheDir(), "userEnv");
                if(!env.exists()){
                    env.createNewFile();
                }
                FileInputStream inputStream = new FileInputStream(env);
                Scanner scanner = new Scanner(inputStream);
                String data = "";
                while(scanner.hasNext()){
                    data += scanner.nextLine();
                }
                inputStream.close();

                Log.d(TAG, "Data --> " + data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean unused) {
            super.onPostExecute(unused);
            if(unused){
                status.setText("로그인 정보를 확인했어요! 연결 중 입니다!");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2500);
            }else{
                status.setText("앱 이용이 처음이신 가봐요! 회원가입 폼으로 이동할께요!");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        logo = findViewById(R.id.logoIMG);
        status = findViewById(R.id.Intro_statusTXT);

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

        Display.createInfo(getApplicationContext());
    }
}