package nwc.hardware.smartpottypad.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import nwc.hardware.bletool.BluetoothPermissionTool;
import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.Display;
import nwc.hardware.smartpottypad.fragments.intro.IntroFragment;
import nwc.hardware.smartpottypad.fragments.intro.LoginFragment;
import nwc.hardware.smartpottypad.fragments.intro.RegisterFragment;
import nwc.hardware.smartpottypad.listeners.OnBackKeyDownListener;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class IntroActivity extends AppCompatActivity {
    private final String TAG = "IntroActivity";
    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1001;

    public final static int FRAGMENT_INTRO = 0;
    public final static int FRAGMENT_REGISTER = 1;
    public final static int FRAGMENT_LOGIN = 2;

    private IntroFragment introFragment;
    private RegisterFragment registerFragment;
    private LoginFragment loginFragment;
    private OnBackKeyDownListener onBackKeyDownListener;

    private BluetoothPermissionTool bluetoothPermissionTool;
    private boolean isPermission = false;
    private boolean pause = false;
    private PowerManager powerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introFragment = new IntroFragment(this);
        registerFragment = new RegisterFragment(this);
        loginFragment = new LoginFragment(this);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        Display.createInfo(getApplicationContext());
        SetPreferences.getInstance(getApplicationContext());

        bluetoothPermissionTool = new BluetoothPermissionTool(this){
            @Override
            public boolean onRequestPermissionScanResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                return super.onRequestPermissionScanResult(requestCode, permissions, grantResults);
            }

            @Override
            public boolean onRequestPermissionLocationResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                boolean result = super.onRequestPermissionLocationResult(requestCode, permissions, grantResults);
                if(result) {
                    isPermission = true;
                    Log.d("TESTING", "Permission ON34");

                    if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            AlertDialog.Builder dlg = new AlertDialog.Builder(IntroActivity.this);
                            dlg.setTitle("예외허용"); //제목
                            dlg.setMessage("데이터의 안전한 저장을 위해 배터리 최적화 예외를 설정해주세요."); // 메시지
                            //버튼 클릭시 동작
                            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                                            startActivity(intent);
                                        }
                                    }
                            );
                            dlg.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    //토스트 메시지
                                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                                    Toast.makeText(getApplicationContext(),"데이터의 안전한 저장을 위해 배터리 최적화 예외를 설정해주세요.",Toast.LENGTH_SHORT).show();
                                }
                            });
                            dlg.setCancelable(false);
                            dlg.show();
                        } else {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                        }
                    }else{
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                    }
                }
                return result;
            }
        };

        if(bluetoothPermissionTool.checkPermission()){
            isPermission = true;
            Log.d("TESTING", "Permission ON12");
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(IntroActivity.this);
                    dlg.setTitle("예외허용"); //제목
                    dlg.setMessage("데이터의 안전한 저장을 위해 배터리 최적화 예외를 설정해주세요."); // 메시지
                    //버튼 클릭시 동작
                    dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("TESTING", "요청 액티비티를 엽니다.");
                                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                                    startActivity(intent);
                                }
                            }
                    );
                    dlg.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            //토스트 메시지
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                            Toast.makeText(getApplicationContext(),"데이터의 안전한 저장을 위해 배터리 최적화 예외를 설정해주세요.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.setCancelable(false);
                    dlg.show();
                } else {
                    Log.d("TESTING", "지원하지 않는 요청입니다.");
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                }
            }else{
                Log.d("TESTING", "화이트 리스트에 등록되어있습니다.");
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
            }
        }
    }

    public void changeFragment(int idx){
        if(idx == FRAGMENT_INTRO){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_REGISTER){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, registerFragment).commitAllowingStateLoss();
        }else if(idx == FRAGMENT_LOGIN){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, loginFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(onBackKeyDownListener != null){
                onBackKeyDownListener.onBackKeyDown();
            }
        }
        return false;
    }

    public void setOnKeyDownListener(OnBackKeyDownListener listener){
        this.onBackKeyDownListener = listener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == BluetoothPermissionTool.SCAN_PERMISSION_CODE){
            if (!((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
                Toast.makeText(this, "Peripheral navigation permission are required to use Bluetooth.", Toast.LENGTH_SHORT).show();
            } else {
                if (this.checkSelfPermission(BluetoothPermissionTool.LOCATION_permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                        this.checkSelfPermission(BluetoothPermissionTool.LOCATION_permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(BluetoothPermissionTool.LOCATION_permissions, BluetoothPermissionTool.LOCATION_PERMISSION_CODE);
                }
            }
        }else{
            if (!((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
                Toast.makeText(this, "Location permission are required to use Bluetooth.", Toast.LENGTH_SHORT).show();
            }else{
                isPermission = true;
                Log.d("TESTING", "Permission ON12");
                if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(IntroActivity.this);
                        dlg.setTitle("예외허용"); //제목
                        dlg.setMessage("데이터의 안전한 저장을 위해 배터리 최적화 예외를 설정해주세요."); // 메시지
                        //버튼 클릭시 동작
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                                        startActivity(intent);
                                    }
                                }
                        );
                        dlg.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                //토스트 메시지
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                                Toast.makeText(getApplicationContext(),"데이터의 안전한 저장을 위해 배터리 최적화 예외를 설정해주세요.",Toast.LENGTH_SHORT).show();
                            }
                        });
                        dlg.setCancelable(false);
                        dlg.show();
                    } else {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                    }

                }else{
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.IntroFrame, introFragment).commitAllowingStateLoss();
                }
            }
        }
    }

}