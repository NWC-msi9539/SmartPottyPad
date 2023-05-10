package nwc.hardware.smartpottypad.fragments.home.detail;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.Adapters.SearchingDeviceAdapter;
import nwc.hardware.Interfaces.OnGattListener;
import nwc.hardware.bletool.BluetoothGeneralTool;
import nwc.hardware.bletool.BluetoothPermissionTool;
import nwc.hardware.bletool.BluetoothSearchingTool;
import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.DeviceProfile;
import nwc.hardware.smartpottypad.fragments.home.HomeFragment;

public class SearchingBluetoothFragment extends Fragment {

    private BluetoothSearchingTool bluetoothSearchingTool;
    private BluetoothPermissionTool bluetoothPermissionTool;
    private BluetoothGeneralTool bluetoothGeneralTool;

    private RecyclerView RCC;
    private TextView statusText;

    private SearchingDeviceAdapter adapter;
    private Timer timer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private HomeActivity parent;

    private DeviceProfile profile;
    private float Origindegree;
    private int cnt = 0;
    private Timer connectTimer;
    private FrameLayout HomeDetailFrame;

    public SearchingBluetoothFragment(HomeActivity parent){
        this.parent = parent;
    }

    @Override
    public void onResume() {
        super.onResume();

        profile = parent.getProfile();

        Log.d(TAG, "ROOM INDEX --> " + profile.getRoomindex());
        Log.d(TAG, "Bed INDEX --> " + profile.getBedindex());
        Log.d(TAG, "userkey --> " + profile.getUserkey());
        Log.d(TAG, "SSID --> " + profile.getWifiProfile().getSsid());
        Log.d(TAG, "PASS --> " + profile.getWifiProfile().getPassword());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.resetItem();
                timer.cancel();
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!bluetoothSearchingTool.isScanning()){
                                    if (progressBar.getVisibility() == View.GONE) {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                    statusText.setText("기기를 검색 중 입니다.");
                                    bluetoothSearchingTool.startScan(parent);
                                }
                            }
                        });
                    }

                    @Override
                    public boolean cancel() {
                        return super.cancel();
                    }
                };
                timer.schedule(timerTask,0,1000);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        List<BluetoothDevice> devices = new ArrayList<>();
        adapter = new SearchingDeviceAdapter(devices, getContext()) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.device_item, viewGroup, false);
                MyViewHolder myViewHolder = new MyViewHolder(linearLayout) {
                    @Override
                    public void setContents() {
                        textViews.put("name", itemView.findViewById(R.id.devicename_txt));
                        textViews.put("address",itemView.findViewById(R.id.devicename_address));

                        itemView.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(View view) {
                                BluetoothDevice device = getItem(getAdapterPosition());
                                AlertDialog.Builder dlg = new AlertDialog.Builder(parent);
                                dlg.setTitle("연결"); //제목
                                dlg.setMessage(device.getName() + ", 연결 하시겠습니까?"); // 메시지
                                //버튼 클릭시 동작
                                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        statusText.setText("연결 중 입니다.");
                                                        progressBar.setVisibility(View.GONE);
                                                        timer.cancel();
                                                        if(bluetoothSearchingTool.isScanning()) {
                                                            bluetoothSearchingTool.stopScan(parent);
                                                        }
                                                    }
                                                });

                                                bluetoothGeneralTool.connect(device, parent, false);
                                            }

                                        }
                                );
                                dlg.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        //토스트 메시지
                                        Toast.makeText(parent,"연결을 취소 하였습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dlg.setCancelable(false);
                                dlg.show();
                            }
                        });
                    }
                };
                return myViewHolder;
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                BluetoothDevice device = getItem(position);
                holder.textViews.get("name").setText("" + device.getName());
                holder.textViews.get("address").setText("" + device.getAddress());
            }
        };


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(RCC.getContext());
        RCC.setLayoutManager(layoutManager1);
        RCC.setAdapter(adapter);

        bluetoothPermissionTool = new BluetoothPermissionTool(parent);
        bluetoothSearchingTool = new BluetoothSearchingTool() {
            @SuppressLint("MissingPermission")
            @Override
            public void onScanResult(BluetoothDevice bluetoothDevice) {
                Log.d("TESTING", "FOUND DEVICE ----> " + bluetoothDevice.getName());
                adapter.addDevice(bluetoothDevice);
            }
        };

        bluetoothGeneralTool = BluetoothGeneralTool.getInstance(new OnGattListener() {
            @Override
            public void connectionStateConnecting(BluetoothGatt bluetoothGatt) {
                statusText.setText("연결 중 입니다.");
                timer.cancel();
                if(bluetoothSearchingTool.isScanning()) {
                    bluetoothSearchingTool.stopScan(parent);
                }
            }

            @Override
            public void connectionStateConnected(BluetoothGatt bluetoothGatt) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText("연결 되었습니다.");
                    }
                });
            }

            @Override
            public void connectionStateDisconnected(BluetoothGatt bluetoothGatt) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText("연결이 끊겼습니다.");
                        timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void run() {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!bluetoothSearchingTool.isScanning()){
                                            if (progressBar.getVisibility() == View.GONE) {
                                                progressBar.setVisibility(View.VISIBLE);
                                            }
                                            statusText.setText("기기를 검색 중 입니다.");
                                            bluetoothSearchingTool.startScan(parent);
                                        }
                                    }
                                });
                            }

                            @Override
                            public boolean cancel() {
                                return super.cancel();
                            }
                        };
                        timer.schedule(timerTask,0,100);
                    }
                });
            }

            @Override
            public void discoveredServices() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        bluetoothGeneralTool.setMAIN(14, -1);
                        adapter.resetItem();
                        statusText.setText("서버와 연결 중...");
                    }
                });
                Log.d("TESTING","연결 됌");

                Origindegree = new Float(HomeFragment.info.getRooms()
                        .get(Integer.parseInt(profile.getRoomindex()))
                        .getBeds()
                        .get(Integer.parseInt(profile.getBedindex()))
                        .getDegree());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                        .child(profile.getUserkey())
                        .child("rooms")
                        .child(profile.getRoomindex())
                        .child("beds")
                        .child(profile.getBedindex());

                connectTimer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        cnt++;
                        if(cnt >= 30){
                            cnt = 0;
                            statusText.setText("서버와 연결이 실패하였습니다.");
                            connectTimer.cancel();
                        }
                        reference.child("degree").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                double degree = (double)task.getResult().getValue();
                                if(!String.format("%.1f", Origindegree).equals(String.format("%.1f", degree))){
                                    cnt = 0;
                                    statusText.setText("서버와 연결 되었습니다.");
                                    connectTimer.cancel();
                                    connectTimer = null;
                                    HomeDetailFrame.animate()
                                            .setDuration(1000)
                                            .setInterpolator(new AccelerateDecelerateInterpolator())
                                            .alpha(0f)
                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_WIFI);
                                                    HomeDetailFrame.setVisibility(View.GONE);
                                                }
                                            })
                                            .start();
                                    bluetoothGeneralTool.close();
                                }
                            }
                        });
                    }
                };
                connectTimer.schedule(task, 0, 1000);
            }

            @Override
            public void characteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                Log.d("TESTING","(MAINACTIVITY)Write Success !! --> [" + bluetoothGattCharacteristic.getUuid() + "] " + new String(bluetoothGattCharacteristic.getValue()));
            }

            @Override
            public void characteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {

            }

            @Override
            public void characteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {

            }

            @Override
            public void readRssi(BluetoothGatt bluetoothGatt, int rssi, int status) {

            }
        });

        if(bluetoothGeneralTool.getGatt() == null){
            if (bluetoothPermissionTool.checkPermission()) {
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                statusText.setText("기기를 검색 중 입니다.");
                Log.d("TESTING", "Permission ON");
                bluetoothSearchingTool.startScan(parent);
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!bluetoothSearchingTool.isScanning()){
                                    if (progressBar.getVisibility() == View.GONE) {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                    statusText.setText("기기를 검색 중 입니다.");
                                    bluetoothSearchingTool.startScan(parent);
                                }
                            }
                        });
                    }

                    @Override
                    public boolean cancel() {
                        return super.cancel();
                    }
                };
                timer.schedule(timerTask, 0, 100);
            } else {
                Log.d("TESTING", "Permission OUT");
                Toast.makeText(parent, "블루투스 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
            }
        }else{
            statusText.setText("연결 되었습니다.");
            progressBar.setVisibility(View.GONE);
            BluetoothDevice device = bluetoothGeneralTool.getGatt().getDevice();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_searching_bluetooth, container, false);
        HomeDetailFrame = container.getRootView().findViewById(R.id.HomeDetailFrame);
        progressBar = v.findViewById(R.id.progressBar_bluetooth);
        statusText = v.findViewById(R.id.statusTXT_bluetooth);

        swipeRefreshLayout = v.findViewById(R.id.SEARCHING_deviceLayout_bt);
        RCC = v.findViewById(R.id.deviceRCC);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(connectTimer != null){
            connectTimer.cancel();
            connectTimer = null;
        }
    }
}