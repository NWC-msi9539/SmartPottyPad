package nwc.hardware.smartpottypad.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.WifiProfile;
import nwc.hardware.smartpottypad.fragments.home.detail.CheckWifiFragment;

public class WifiScanAdapter extends RecyclerView.Adapter<WifiScanAdapter.MyViewHolder>{
    private final String TAG = "HomeRoomAdapter";
    private List<ScanResult> items;
    private Context mContext;
    private Drawable wifi1;
    private Drawable wifi2;
    private Drawable wifi3;
    private Drawable wifi4;
    private HomeActivity parent;

    public WifiScanAdapter(List<ScanResult> items, Context mContext, HomeActivity parent) {
        this.items = items;
        this.mContext = mContext;
        wifi1 = mContext.getDrawable(R.drawable.wifi_1);
        wifi2 = mContext.getDrawable(R.drawable.wifi_2);
        wifi3 = mContext.getDrawable(R.drawable.wifi_3);
        wifi4 = mContext.getDrawable(R.drawable.wifi_4);
        this.parent = parent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);
        return myViewHolder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ScanResult item = items.get(position);
        if(!item.SSID.isEmpty()){
            holder.name_txt.setText(item.SSID);
            if(item.level < -30 && item.level >= -50){
                holder.power_IMG.setImageDrawable(wifi4);
            }else if(item.level < -50 && item.level >= -60){
                holder.power_IMG.setImageDrawable(wifi3);
            }else if(item.level < -60 && item.level >= -70){
                holder.power_IMG.setImageDrawable(wifi3);
            }else if(item.level < -70 && item.level >= -80){
                holder.power_IMG.setImageDrawable(wifi2);
            }else{
                holder.power_IMG.setImageDrawable(wifi1);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CheckWifiFragment.wifiProfile = new WifiProfile(item.SSID, null);
                    parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_WIFI);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ScanResult item){
        if(!items.contains(item)) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }
    }

    public List<ScanResult> getItems(){
        return items;
    }

    public void removeItem(ScanResult item){
        if(items.contains(item)){
            int index = items.indexOf(item);
            items.remove(item);
            notifyItemRemoved(index);
        }
    }

    public void removeItem(int position){
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void resetItems(){
        items.clear();
        notifyDataSetChanged();
    }

    public ScanResult getItem(int position){
        return items.get(position);
    }

    public void setItems(List<ScanResult> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name_txt;
        public ImageView power_IMG;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.name_txt);
            power_IMG = itemView.findViewById(R.id.power_IMG);
        }
    }

}
