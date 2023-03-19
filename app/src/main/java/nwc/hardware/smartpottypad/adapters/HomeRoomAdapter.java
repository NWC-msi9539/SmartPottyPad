package nwc.hardware.smartpottypad.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.Bed;
import nwc.hardware.smartpottypad.datas.Room;
import nwc.hardware.smartpottypad.fragments.home.HomeFragment;

public class HomeRoomAdapter extends RecyclerView.Adapter<HomeRoomAdapter.MyViewHolder>{
    private final String TAG = "HomeRoomAdapter";
    private List<Room> items;
    private Context mContext;
    private int departFloor = 0;

    private boolean isAnim = false;
    private HomeActivity parent;

    public HomeRoomAdapter(List<Room> items, Context mContext, int departFloor, HomeActivity parent) {
        this.items = items;
        this.mContext = mContext;
        this.departFloor = departFloor;
        this.parent = parent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.roomitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);
        return myViewHolder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room room = items.get(position);
        HomeBedAdapter adapter = new HomeBedAdapter(room.getBeds(), mContext, parent);
        holder.roomNoTXT.setText("" + ((100 * departFloor) + (position + 1)));
        holder.bedRCC.setAdapter(adapter);
        holder.addBedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On Click addBedBTN Size --> " + room.getBeds().size());
                room.getBeds().add(new Bed(room.getBeds().size() + 1, room.getIndex() - 1));
                adapter.setBeds(room.getBeds());
            }
        });
    }

    private void startShakeAnimation(View view){
        Animation shakeAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake_animation);
        view.startAnimation(shakeAnimation);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(isAnim){
            startShakeAnimation(holder.RoombodyLayout);
        }else{
            if(holder.RoombodyLayout.getAnimation() != null){
                holder.RoombodyLayout.clearAnimation();
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder.RoombodyLayout.getAnimation() != null){
            holder.RoombodyLayout.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addRoom(Room room){
        if(!items.contains(room)) {
            items.add(room);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void onAnim(){
        isAnim = true;
        notifyDataSetChanged();
    }

    public void offAnim(){
        isAnim = false;
        notifyDataSetChanged();
    }

    public boolean isAnim(){
        return isAnim;
    }

    public List<Room> getRooms(){
        return items;
    }

    public void removeRoom(Room room){
        if(items.contains(room)){
            int index = items.indexOf(room);
            items.remove(room);
            notifyItemRemoved(index);
        }
    }

    public void removeRoom(int position){
        items.remove(position);
        for(int i=position; i<items.size(); i++){
            Room room = items.get(i);
            room.setIndex(room.getIndex() - 1);
            items.set(i, room);
        }
        HomeFragment.info.setRoomCount(items.size());
        notifyDataSetChanged();
    }

    public void resetRoom(){
        items.clear();
        notifyDataSetChanged();
    }

    public Room getItem(int position){
        return items.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout RoombodyLayout;
        public TextView roomNoTXT;
        public RecyclerView bedRCC;
        public ImageButton addBedBTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNoTXT = itemView.findViewById(R.id.roomNoTXT);
            addBedBTN = itemView.findViewById(R.id.AddBedBTN);
            RoombodyLayout = itemView.findViewById(R.id.RoombodyLayout);
            bedRCC = itemView.findViewById(R.id.bedRCC);
            bedRCC.setHasFixedSize(true);
            bedRCC.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

}
