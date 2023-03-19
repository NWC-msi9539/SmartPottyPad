package nwc.hardware.smartpottypad.fragments.home.detail;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.util.ArrayList;
import java.util.List;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.adapters.WifiScanAdapter;

public class SearchingWifiFragment extends Fragment {

    private TextView status;
    private ImageButton closeBTN_wifi;
    private LottieAnimationView ltv;

    private RecyclerView wifiRCC;
    private SwipeRefreshLayout SEARCHING_deviceLayout;

    private ProgressBar progressBar;

    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;
    private List<ScanResult> wifiList = new ArrayList<>();

    private WifiScanAdapter adapter;
    private HomeActivity parent;


    public SearchingWifiFragment(HomeActivity parent){
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_searching_wifi, container, false);
        wifiManager = (WifiManager) parent.getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        status = v.findViewById(R.id.statusTXT_wifi);

        ltv = v.findViewById(R.id.wifiLTV);
        ltv.setAnimation(R.raw.wifi_disconnected);
        ltv.setRepeatMode(LottieDrawable.RESTART);
        ltv.setRepeatCount(LottieDrawable.INFINITE);
        ltv.playAnimation();

        progressBar = v.findViewById(R.id.progressBar);

        SEARCHING_deviceLayout = v.findViewById(R.id.SEARCHING_deviceLayout);
        SEARCHING_deviceLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.resetItems();
                status.setText("와이파이를 검색 중 입니다.");
                progressBar.setVisibility(View.VISIBLE);
                wifiManager.startScan();
                SEARCHING_deviceLayout.setRefreshing(false);
            }
        });

        wifiRCC = v.findViewById(R.id.wifiRCC);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(wifiRCC.getContext());
        wifiRCC.setLayoutManager(layoutManager1);

        closeBTN_wifi = v.findViewById(R.id.closeBTN_wifi);

        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    Log.d(TAG, "Searching Success!!");
                    status.setText("검색을 완료 했습니다.");
                    progressBar.setVisibility(View.GONE);
                    adapter.setItems(wifiManager.getScanResults());
                } else {
                    // 실패한 경우 다시 스캔 요청
                    Log.d(TAG, "Searching False!!");
                    wifiManager.startScan();
                }
            }
        };

        adapter = new WifiScanAdapter(wifiList, getContext(), parent);
        wifiRCC.setAdapter(adapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        parent.registerReceiver(wifiScanReceiver, intentFilter);

        wifiManager.startScan();
        return v;
    }


}