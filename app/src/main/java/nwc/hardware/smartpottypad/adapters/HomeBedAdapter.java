package nwc.hardware.smartpottypad.adapters;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.Bed;

public class HomeBedAdapter extends RecyclerView.Adapter<HomeBedAdapter.MyViewHolder>{
    private List<Bed> items;
    private Context mContext;
    private FrameLayout HomeDetailFrame;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, a HH:mm");

    private HomeActivity parent;

    public HomeBedAdapter(List<Bed> items, Context mContext, HomeActivity parent) {
        this.items = items;
        this.mContext = mContext;
        this.parent = parent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "FINDING --> " + parent.getRootView());
        HomeDetailFrame = parent.getRootView().findViewById(R.id.HomeDetailFrame);

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.beditem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);
        return myViewHolder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Bed bed = items.get(position);
        holder.BedIndexTXT.setText(bed.getName());
        holder.BedFirstTXT.setText(dateFormat.format(new Date(bed.getFirstUseTime())));
        holder.BedChangeTXT.setText(dateFormat.format(new Date(bed.getLastChangeTime())));
        holder.BedstatusBTN.setImageDrawable(mContext.getDrawable(bed.getTypeToDrawbleID(bed.getAlertType())));
        holder.BedstatusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HomeDetailFrame.getVisibility() == View.GONE){
                    HomeDetailFrame.setVisibility(View.VISIBLE);
                    HomeDetailFrame.animate()
                                .setDuration(1500)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .alpha(1f)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_WIFI);
                                    }
                                })
                                .start();
                }else{
                    HomeDetailFrame.animate()
                            .setDuration(1500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .alpha(0f)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    HomeDetailFrame.setVisibility(View.GONE);
                                }
                            })
                            .start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setBeds(List<Bed> beds){
        items = beds;
        notifyDataSetChanged();
    }

    public void addBed(Bed bed){
        if(!items.contains(bed)) {
            items.add(bed);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void removeBed(Bed bed){
        if(items.contains(bed)){
            int index = items.indexOf(bed);
            items.remove(bed);
            notifyItemRemoved(index);
        }
    }

    public void resetBed(){
        items.clear();
        notifyDataSetChanged();
    }

    public Bed getItem(int position){
        return items.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView BedIndexTXT;
        public TextView BedFirstTXT;
        public TextView BedChangeTXT;
        public ImageButton BedstatusBTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            BedIndexTXT = itemView.findViewById(R.id.BedIndexTXT);
            BedFirstTXT = itemView.findViewById(R.id.bedFirstTXT);
            BedChangeTXT = itemView.findViewById(R.id.bedChangeTXT);
            BedstatusBTN = itemView.findViewById(R.id.bedstatusBTN);
        }
    }

}
